import os
import sys
import time
import hashlib
from copy import deepcopy
from bson import ObjectId
from pymongo import MongoClient
from py2neo import Graph

# On essaie de charger un .env si présent
try:
    from dotenv import load_dotenv
    load_dotenv()
except Exception:
    pass

MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/?replicaSet=rs0")
DB_NAME = os.getenv("DB_NAME", "reseau_culinaire")

NEO4J_URI = os.getenv("NEO4J_URI", "bolt://localhost:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "neo4jtest")

WATCH_MODE = os.getenv("WATCH_MODE", "auto").lower()

_mongo_client = None
_mongo_db = None
_neo4j_graph = None
_has_replset = False


# Connexions (Différentes de celle dans utils/ pour isolation)
def get_mongo_db():
    global _mongo_client, _mongo_db, _has_replset
    if _mongo_db is not None:
        return _mongo_db
    try:
        # Connexion avec timeout
        _mongo_client = MongoClient(MONGO_URI, serverSelectionTimeoutMS=30000)
        hello = _mongo_client.admin.command("hello")  # ping + get info
        set_name = hello.get("setName") # Permet de savoir si on est sur un replSet
        _has_replset = bool(set_name) # replSet si setName existe
        _mongo_db = _mongo_client[DB_NAME] # Accès DB
        return _mongo_db
    except Exception as e:
        print(f" Erreur connexion MongoDB: {e}", file=sys.stderr)
        print("", file=sys.stderr)
        return None

# Connexion Neo4j classique
def get_neo4j_graph():
    global _neo4j_graph
    if _neo4j_graph is not None:
        return _neo4j_graph
    try:
        _neo4j_graph = Graph(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
        _neo4j_graph.run("RETURN 1")
        print(" Neo4j connecté")
        return _neo4j_graph
    except Exception as e:
        print(f" Erreur connexion Neo4j: {e}", file=sys.stderr)
        return None


# == Utilitaires ==
# Permet de calculer un hash stable d'un dict (pour détection update polling)
def _hash_dict(d):
    """
    Calcule un hash SHA256 stable d'un dict.
    Utilisé pour détecter les changements dans les documents MongoDB.
    Args:
        d (dict): Le dictionnaire à hasher.

    Returns:
        str: Le hash SHA256 hexadécimal du dictionnaire.
    """
    data = repr(_sorted_dict(d)).encode("utf-8")
    return hashlib.sha256(data).hexdigest()


def _sorted_dict(obj):
    """
    Permet de trier récursivement un dict (pour hash stable).
    Args:
        obj: dict, list, or other
    Returns:
        obj with dicts sorted by keys
    """
    if isinstance(obj, dict):
        return {k: _sorted_dict(obj[k]) for k in sorted(obj)}
    if isinstance(obj, list):
        return [_sorted_dict(x) for x in obj]
    return obj


def sync_user_change(change, graph):
    """
    Synchronise un changement Users dans Neo4j.
    Args:
        change (dict): Le document de changement MongoDB.
        graph (py2neo.Graph): L'instance de connexion au graphe Neo4j

    Returns:
        None
    """
    op_type = change["operationType"]
    LABEL = "Users"

    if op_type == "insert":
        doc = change["fullDocument"]
        user_id = str(doc["_id"])
        pseudo = doc.get("profile", {}).get("pseudo", "N/A")
        print(f"  [USER INSERT] ID: {user_id}, Pseudo: {pseudo}")

        graph.run(
            f"MERGE (u:{LABEL} {{mongo_id: $id}}) SET u.pseudo = $pseudo",
            id=user_id, pseudo=pseudo
        )

        location = doc.get("profile", {}).get("location")
        if location:
            graph.run(
                f"""
                MATCH (u:{LABEL} {{mongo_id: $id}})
                MERGE (l:Location {{name: $loc}})
                MERGE (u)-[:LIVES_IN]->(l)
                """,
                id=user_id, loc=location
            )

        interests = doc.get("interests", {}).get("cuisines", []) + \
                    doc.get("interests", {}).get("techniques", [])
        if interests:
            graph.run(
                f"""
                MATCH (u:{LABEL} {{mongo_id: $id}})
                UNWIND $interests AS name
                MERGE (i:Interest {{name: name}})
                MERGE (u)-[:INTERESTED_IN]->(i)
                """,
                id=user_id, interests=list(set(interests))
            )

        diets = doc.get("prefs", {}).get("diets", [])
        if diets:
            graph.run(
                f"""
                MATCH (u:{LABEL} {{mongo_id: $id}})
                UNWIND $diets AS name
                MERGE (d:Diet {{name: name}})
                MERGE (u)-[:FOLLOWS_DIET]->(d)
                """,
                id=user_id, diets=diets
            )

    elif op_type == "update":
        user_id = str(change["documentKey"]["_id"])
        updates = change["updateDescription"]["updatedFields"]

        if "profile.pseudo" in updates:
            new_pseudo = updates["profile.pseudo"]
            print(f"  [USER UPDATE] ID: {user_id}, Nouveau Pseudo: {new_pseudo}")
            graph.run(
                f"MATCH (u:{LABEL} {{mongo_id: $id}}) SET u.pseudo = $pseudo",
                id=user_id, pseudo=new_pseudo
            )

        if "profile.location" in updates:
            new_loc = updates["profile.location"]
            print(f"  [USER UPDATE] ID: {user_id}, Nouvelle Location: {new_loc}")
            graph.run(
                f"MATCH (u:{LABEL} {{mongo_id: $id}})-[r:LIVES_IN]->(:Location) DELETE r",
                id=user_id
            )
            if new_loc:
                graph.run(
                    f"""
                    MATCH (u:{LABEL} {{mongo_id: $id}})
                    MERGE (l:Location {{name: $loc}})
                    MERGE (u)-[:LIVES_IN]->(l)
                    """,
                    id=user_id, loc=new_loc
                )

        if "interests.cuisines" in updates or "interests.techniques" in updates:
            db = get_mongo_db()
            if db is None:
                print("  [USER UPDATE] Skip interests: MongoDB indisponible")
            else:
                try:
                    oid = ObjectId(user_id)
                except Exception:
                    oid = user_id
                doc = db.users.find_one({"_id": oid}) or {}
                interests = doc.get("interests", {}).get("cuisines", []) + \
                            doc.get("interests", {}).get("techniques", [])

                print(f"  [USER UPDATE] ID: {user_id}, Resync Intérêts")
                graph.run(
                    f"MATCH (u:{LABEL} {{mongo_id: $id}})-[r:INTERESTED_IN]->(:Interest) DELETE r",
                    id=user_id
                )
                if interests:
                    graph.run(
                        f"""
                        MATCH (u:{LABEL} {{mongo_id: $id}})
                        UNWIND $interests AS name
                        MERGE (i:Interest {{name: name}})
                        MERGE (u)-[:INTERESTED_IN]->(i)
                        """,
                        id=user_id, interests=list(set(interests))
                    )

        if "prefs.diets" in updates:
            new_diets = updates["prefs.diets"]
            print(f"  [USER UPDATE] ID: {user_id}, Resync Régimes")
            graph.run(
                f"MATCH (u:{LABEL} {{mongo_id: $id}})-[r:FOLLOWS_DIET]->(:Diet) DELETE r",
                id=user_id
            )
            if new_diets:
                graph.run(
                    f"""
                    MATCH (u:{LABEL} {{mongo_id: $id}})
                    UNWIND $diets AS name
                    MERGE (d:Diet {{name: name}})
                    MERGE (u)-[:FOLLOWS_DIET]->(d)
                    """,
                    id=user_id, diets=new_diets
                )

    elif op_type == "delete":
        user_id = str(change["documentKey"]["_id"])
        print(f"  [USER DELETE] ID: {user_id}")
        graph.run(
            f"MATCH (u:{LABEL} {{mongo_id: $id}}) DETACH DELETE u",
            id=user_id
        )


def sync_friendship_change(change, graph):
    """
    Synchronise un changement Friendships dans Neo4j.
    Args:
        change (dict): Le document de changement MongoDB.
        graph (py2neo.Graph): L'instance de connexion au graphe Neo4j

    Returns:
        None
    """
    op_type = change["operationType"]
    LABEL = "Users"
    REL_TYPE = "IS_FRIENDS_WITH"

    if op_type == "insert":
        doc = change["fullDocument"]
        doc_id = str(doc["_id"])

        if doc.get("status") == "accepted" and len(doc.get("members", [])) == 2:
            user_a = str(doc["members"][0])
            user_b = str(doc["members"][1])
            print(f"  [FRIENDSHIP INSERT] {user_a} <-> {user_b}")
            graph.run(
                f"""
                MATCH (a:{LABEL} {{mongo_id: $id_a}})
                MATCH (b:{LABEL} {{mongo_id: $id_b}})
                MERGE (a)-[r:{REL_TYPE}]-(b)
                SET r.mongo_doc_id = $doc_id
                """,
                id_a=user_a, id_b=user_b, doc_id=doc_id
            )

    elif op_type == "update":
        raw_id = change["documentKey"]["_id"]
        updates = change["updateDescription"]["updatedFields"]

        if "status" in updates and updates["status"] == "accepted":
            db = get_mongo_db()
            if db is None:
                print("  [FRIENDSHIP UPDATE] Skip: MongoDB indisponible")
                return
            try:
                oid = raw_id if isinstance(raw_id, ObjectId) else ObjectId(str(raw_id))
            except Exception:
                oid = raw_id
            doc = db.friendships.find_one({"_id": oid}) or {}

            if len(doc.get("members", [])) == 2:
                user_a = str(doc["members"][0])
                user_b = str(doc["members"][1])
                print(f"  [FRIENDSHIP UPDATE] {user_a} <-> {user_b} (Accepted)")
                graph.run(
                    f"""
                    MATCH (a:{LABEL} {{mongo_id: $id_a}})
                    MATCH (b:{LABEL} {{mongo_id: $id_b}})
                    MERGE (a)-[r:{REL_TYPE}]-(b)
                    SET r.mongo_doc_id = $doc_id_str
                    """,
                    id_a=user_a, id_b=user_b, doc_id_str=str(raw_id)
                )

    elif op_type == "delete":
        doc_id = str(change["documentKey"]["_id"])
        print(f"  [FRIENDSHIP DELETE] Doc ID: {doc_id}")
        graph.run(
            f"MATCH (u1:{LABEL})-[r {{mongo_doc_id: $id}}]-(u2:{LABEL}) DELETE r",
            id=doc_id
        )


def sync_group_change(change, graph):
    """"
        Synchronise un changement Groups dans Neo4j.
        Args:
            change (dict): Le document de changement MongoDB.
            graph (py2neo.Graph): L'instance de connexion au graphe Neo4j
        Returns:
         None
    """
    op_type = change["operationType"]
    LABEL = "Groups"
    USER_LABEL = "Users"

    if op_type == "insert":
        doc = change["fullDocument"]
        group_id = str(doc["_id"])
        name = doc.get("name", "N/A")
        print(f"  [GROUP INSERT] ID: {group_id}, Name: {name}")

        graph.run(
            f"MERGE (g:{LABEL} {{mongo_id: $id}}) SET g.name = $name",
            id=group_id, name=name
        )

        members = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                   for m in doc.get("members", [])]
        if members:
            graph.run(
                f"""
                MATCH (g:{LABEL} {{mongo_id: $id}})
                UNWIND $members AS user_id
                MATCH (u:{USER_LABEL} {{mongo_id: user_id}})
                MERGE (u)-[:MEMBER_OF]->(g)
                """,
                id=group_id, members=members
            )

        admins = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                  for m in doc.get("admins", [])]
        if admins:
            graph.run(
                f"""
                MATCH (g:{LABEL} {{mongo_id: $id}})
                UNWIND $admins AS admin_id
                MATCH (u:{USER_LABEL} {{mongo_id: admin_id}})
                MERGE (u)-[:ADMINS]->(g)
                """,
                id=group_id, admins=admins
            )

        topics = doc.get("topics", [])
        if topics:
            graph.run(
                f"""
                MATCH (g:{LABEL} {{mongo_id: $id}})
                UNWIND $topics AS topic_name
                MERGE (i:Interest {{name: topic_name}})
                MERGE (g)-[:HAS_TOPIC]->(i)
                """,
                id=group_id, topics=topics
            )


    elif op_type == "update":
        group_id = str(change["documentKey"]["_id"])
        updates = change["updateDescription"]["updatedFields"]

        if "name" in updates:
            print(f"  [GROUP UPDATE] ID: {group_id}, Nouveau Name: {updates['name']}")
            graph.run(
                f"MATCH (g:{LABEL} {{mongo_id: $id}}) SET g.name = $name",
                id=group_id, name=updates["name"]
            )

        if "members" in updates:
            new_members = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                           for m in updates["members"]]
            print(f"  [GROUP UPDATE] ID: {group_id}, Resync Membres")

            graph.run(
                f"MATCH (u:{USER_LABEL})-[r:MEMBER_OF]->(g:{LABEL} {{mongo_id: $id}}) DELETE r",
                id=group_id
            )

            if new_members:
                graph.run(
                    f"""
                    MATCH (g:{LABEL} {{mongo_id: $id}})
                    UNWIND $members AS user_id
                    MATCH (u:{USER_LABEL} {{mongo_id: user_id}})
                    MERGE (u)-[:MEMBER_OF]->(g)
                    """,
                    id=group_id, members=new_members
                )

        if "admins" in updates:
            new_admins = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                          for m in updates["admins"]]
            print(f"  [GROUP UPDATE] ID: {group_id}, Resync Admins")
            graph.run(f"MATCH (u:{USER_LABEL})-[r:ADMINS]->(g:{LABEL} {{mongo_id: $id}}) DELETE r", id=group_id)
            if new_admins:
                graph.run(f"""
                    MATCH (g:{LABEL} {{mongo_id: $id}})
                    UNWIND $admins AS admin_id
                    MATCH (u:{USER_LABEL} {{mongo_id: admin_id}})
                    MERGE (u)-[:ADMINS]->(g)
                """, id=group_id, admins=new_admins)

        if "topics" in updates:
            new_topics = updates["topics"]
            print(f"  [GROUP UPDATE] ID: {group_id}, Resync Topics")
            graph.run(f"MATCH (g:{LABEL} {{mongo_id: $id}})-[r:HAS_TOPIC]->(:Interest) DELETE r", id=group_id)
            if new_topics:
                graph.run(f"""
                    MATCH (g:{LABEL} {{mongo_id: $id}})
                    UNWIND $topics AS topic_name
                    MERGE (i:Interest {{name: topic_name}})
                    MERGE (g)-[:HAS_TOPIC]->(i)
                """, id=group_id, topics=new_topics)


    elif op_type == "delete":
        group_id = str(change["documentKey"]["_id"])
        print(f"  [GROUP DELETE] ID: {group_id}")
        graph.run(f"MATCH (g:{LABEL} {{mongo_id: $id}}) DETACH DELETE g", id=group_id)


def sync_page_change(change, graph):
    """Gère les changements sur la collection 'pages'."""
    op_type = change["operationType"]
    LABEL = "Pages"
    USER_LABEL = "Users"

    if op_type == "insert":
        doc = change["fullDocument"]
        page_id = str(doc["_id"])
        name = doc.get("name", "N/A")
        print(f"  [PAGE INSERT] ID: {page_id}, Name: {name}")

        graph.run(
            f"MERGE (p:{LABEL} {{mongo_id: $id}}) SET p.name = $name",
            id=page_id, name=name
        )

        admins = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                  for m in doc.get("admins", [])]
        if admins:
            graph.run(
                f"""
                MATCH (p:{LABEL} {{mongo_id: $id}})
                UNWIND $admins AS admin_id
                MATCH (u:{USER_LABEL} {{mongo_id: admin_id}})
                MERGE (u)-[:ADMINS]->(p)
                """,
                id=page_id, admins=admins
            )

        topics = doc.get("topics", [])
        if topics:
            graph.run(
                f"""
                MATCH (p:{LABEL} {{mongo_id: $id}})
                UNWIND $topics AS topic_name
                MERGE (i:Interest {{name: topic_name}})
                MERGE (p)-[:HAS_TOPIC]->(i)
                """,
                id=page_id, topics=topics
            )

    elif op_type == "update":
        page_id = str(change["documentKey"]["_id"])
        updates = change["updateDescription"]["updatedFields"]

        if "name" in updates:
            print(f"  [PAGE UPDATE] ID: {page_id}, Nouveau Name: {updates['name']}")
            graph.run(
                f"MATCH (p:{LABEL} {{mongo_id: $id}}) SET p.name = $name",
                id=page_id, name=updates["name"]
            )

        if "admins" in updates:
            new_admins = [str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                          for m in updates["admins"]]
            print(f"  [PAGE UPDATE] ID: {page_id}, Resync Admins")
            graph.run(f"MATCH (u:{USER_LABEL})-[r:ADMINS]->(p:{LABEL} {{mongo_id: $id}}) DELETE r", id=page_id)
            if new_admins:
                graph.run(f"""
                    MATCH (p:{LABEL} {{mongo_id: $id}})
                    UNWIND $admins AS admin_id
                    MATCH (u:{USER_LABEL} {{mongo_id: admin_id}})
                    MERGE (u)-[:ADMINS]->(p)
                """, id=page_id, admins=new_admins)

        if "topics" in updates:
            new_topics = updates["topics"]
            print(f"  [PAGE UPDATE] ID: {page_id}, Resync Topics")
            graph.run(f"MATCH (p:{LABEL} {{mongo_id: $id}})-[r:HAS_TOPIC]->(:Interest) DELETE r", id=page_id)
            if new_topics:
                graph.run(f"""
                    MATCH (p:{LABEL} {{mongo_id: $id}})
                    UNWIND $topics AS topic_name
                    MERGE (i:Interest {{name: topic_name}})
                    MERGE (p)-[:HAS_TOPIC]->(i)
                """, id=page_id, topics=new_topics)

    elif op_type == "delete":
        page_id = str(change["documentKey"]["_id"])
        print(f"  [PAGE DELETE] ID: {page_id}")
        graph.run(f"MATCH (p:{LABEL} {{mongo_id: $id}}) DETACH DELETE p", id=page_id)


def sync_post_change(change, graph):
    """Gère les changements sur la collection 'posts'."""
    op_type = change["operationType"]
    LABEL = "Posts"

    if op_type == "insert":
        doc = change["fullDocument"]
        post_id = str(doc["_id"])
        print(f"  [POST INSERT] ID: {post_id}")

        graph.run(
            f"MERGE (p:{LABEL} {{mongo_id: $id}})",
            id=post_id
        )

        if "authorId" in doc:
            author_id = str(doc["authorId"])
            graph.run(f"""
                MATCH (p:{LABEL} {{mongo_id: $post_id}})
                MATCH (u:Users {{mongo_id: $author_id}})
                MERGE (p)-[:AUTHORED_BY]->(u)
            """, post_id=post_id, author_id=author_id)

        if "groupId" in doc and doc["groupId"]:
            group_id = str(doc["groupId"])
            graph.run(f"""
                MATCH (p:{LABEL} {{mongo_id: $post_id}})
                MATCH (g:Groups {{mongo_id: $group_id}})
                MERGE (p)-[:PUBLISHED_IN]->(g)
            """, post_id=post_id, group_id=group_id)

        if "pageId" in doc and doc["pageId"]:
            page_id = str(doc["pageId"])
            graph.run(f"""
                MATCH (p:{LABEL} {{mongo_id: $post_id}})
                MATCH (pg:Pages {{mongo_id: $page_id}})
                MERGE (p)-[:POSTED_ON]->(pg)
            """, post_id=post_id, page_id=page_id)

    elif op_type == "update":
        post_id = str(change["documentKey"]["_id"])
        updates = change["updateDescription"]["updatedFields"]
        print(f"  [POST UPDATE] ID: {post_id}, Champs: {list(updates.keys())}")
        if "groupId" in updates:
            new_group_id = str(updates["groupId"]) if updates["groupId"] else None
            graph.run(f"MATCH (p:{LABEL} {{mongo_id: $id}})-[r:PUBLISHED_IN]->(:Groups) DELETE r", id=post_id)
            if new_group_id:
                graph.run(f"""
                    MATCH (p:{LABEL} {{mongo_id: $id}})
                    MATCH (g:Groups {{mongo_id: $group_id}})
                    MERGE (p)-[:PUBLISHED_IN]->(g)
                """, id=post_id, group_id=new_group_id)

        if "pageId" in updates:
            new_page_id = str(updates["pageId"]) if updates["pageId"] else None
            graph.run(f"MATCH (p:{LABEL} {{mongo_id: $id}})-[r:POSTED_ON]->(:Pages) DELETE r", id=post_id)
            if new_page_id:
                graph.run(f"""
                    MATCH (p:{LABEL} {{mongo_id: $id}})
                    MATCH (pg:Pages {{mongo_id: $page_id}})
                    MERGE (p)-[:POSTED_ON]->(pg)
                """, id=post_id, page_id=new_page_id)

    elif op_type == "delete":
        post_id = str(change["documentKey"]["_id"])
        print(f"  [POST DELETE] ID: {post_id}")
        graph.run(
            f"MATCH (p:{LABEL} {{mongo_id: $id}}) DETACH DELETE p",
            id=post_id
        )


def sync_reaction_change(change, graph):
    """Gère les changements sur la collection 'reactions'."""
    op_type = change["operationType"]

    if op_type == "insert":
        doc = change["fullDocument"]
        user_id = str(doc.get("userId"))
        post_id = str(doc.get("postId"))
        rel_type = doc.get("type", "REACTED")

        print(f"  [REACTION INSERT] {user_id} -> {rel_type} -> {post_id}")

        graph.run("""
            MATCH (u:Users {mongo_id: $user_id})
            MATCH (p:Posts {mongo_id: $post_id})
            MERGE (u)-[r:REACTED {type: $type}]->(p)
            SET r.createdAt = $date, r.mongo_doc_id = $doc_id
        """,
                  user_id=user_id,
                  post_id=post_id,
                  type=rel_type,
                  date=doc.get("createdAt"),
                  doc_id=str(doc["_id"])
                  )

    elif op_type == "delete":
        doc_id = str(change["documentKey"]["_id"])
        print(f"  [REACTION DELETE] Doc ID: {doc_id}")
        graph.run(
            "MATCH (u:Users)-[r:REACTED {mongo_doc_id: $id}]->(p:Posts) DELETE r",
            id=doc_id
        )


def sync_comment_change(change, graph):
    """Gère les changements sur la collection 'comments'."""
    op_type = change["operationType"]

    if op_type == "insert":
        doc = change["fullDocument"]
        user_id = str(doc.get("authorId"))
        post_id = str(doc.get("postId"))

        print(f"  [COMMENT INSERT] {user_id} -> {post_id}")

        graph.run("""
            MATCH (u:Users {mongo_id: $user_id})
            MATCH (p:Posts {mongo_id: $post_id})
            MERGE (u)-[r:COMMENTED_ON]->(p)
            SET r.createdAt = $date, r.mongo_doc_id = $doc_id
        """,
                  user_id=user_id,
                  post_id=post_id,
                  date=doc.get("createdAt"),
                  doc_id=str(doc["_id"])
                  )

    elif op_type == "delete":
        doc_id = str(change["documentKey"]["_id"])
        print(f"  [COMMENT DELETE] Doc ID: {doc_id}")
        graph.run(
            "MATCH (u:Users)-[r:COMMENTED_ON {mongo_doc_id: $id}]->(p:Posts) DELETE r",
            id=doc_id
        )

# Watcher Change Streams (Docker / replSet)
def watch_with_change_streams(db, graph):
    """
    Utilise les Change Streams MongoDB pour détecter les changements en temps réel.
    Args:
        db: Instance de la base MongoDB.
        graph: Instance de connexion au graphe Neo4j.

    Returns:
        None
    """
    pipeline = [{
        "$match": {"ns.coll": {"$in": ["users", "friendships", "groups", "pages", "posts"]}}
    }]
    print("\n== Watcher (Change Streams) ==")

    with db.watch(pipeline, full_document="updateLookup") as stream:
        for change in stream:
            coll = change["ns"]["coll"]
            op = change.get("operationType")
            print(f"\n[ÉVÉNEMENT] Collection: {coll}, Op: {op}")
            try:
                if coll == "users":
                    sync_user_change(change, graph)
                elif coll == "friendships":
                    sync_friendship_change(change, graph)
                elif coll == "groups":
                    sync_group_change(change, graph)
                elif coll == "pages":
                    print("  [PAGE CHANGE] Handler non implémenté.")
            except Exception as inner_e:
                print(f"  [ERREUR] Traitement changement: {inner_e}", file=sys.stderr)
                import traceback
                traceback.print_exc()


# Watcher Polling (IDE)
def _pick_user_view(doc):
    """Sous-ensemble pertinent pour détecter les updates Users."""
    return {
        "profile": {
            "pseudo": doc.get("profile", {}).get("pseudo"),
            "location": doc.get("profile", {}).get("location"),
        },
        "interests": {
            "cuisines": sorted(doc.get("interests", {}).get("cuisines", [])),
            "techniques": sorted(doc.get("interests", {}).get("techniques", [])),
        },
        "prefs": {
            "diets": sorted(doc.get("prefs", {}).get("diets", [])),
        }
    }


def _diff_users(prev_view, new_view):
    """Construit updatedFields minimal pour Users."""
    updates = {}
    # pseudo
    if prev_view["profile"]["pseudo"] != new_view["profile"]["pseudo"]:
        updates["profile.pseudo"] = new_view["profile"]["pseudo"]
    # location
    if prev_view["profile"]["location"] != new_view["profile"]["location"]:
        updates["profile.location"] = new_view["profile"]["location"]
    # interests
    if prev_view["interests"]["cuisines"] != new_view["interests"]["cuisines"]:
        updates["interests.cuisines"] = new_view["interests"]["cuisines"]
    if prev_view["interests"]["techniques"] != new_view["interests"]["techniques"]:
        updates["interests.techniques"] = new_view["interests"]["techniques"]
    # diets
    if prev_view["prefs"]["diets"] != new_view["prefs"]["diets"]:
        updates["prefs.diets"] = new_view["prefs"]["diets"]
    return updates


def watch_with_polling(db, graph, interval_s=3):
    """
    Polling :
      - détecte insert / delete via diff d'IDs
      - détecte updates basées sur un hash d'un sous-document pertinent
    """
    print("\n--- Watcher (Polling) ---")
    users_col = db["users"]
    friends_col = db["friendships"]
    groups_col = db["groups"]

    # État initial
    state = {
        "users": {},
        "friendships": set(),
        "groups": {},
    }

    # seed initial
    for u in users_col.find({}, {"_id": 1, "profile": 1, "interests": 1, "prefs": 1}):
        vid = str(u["_id"])
        view = _pick_user_view(u)
        state["users"][vid] = _hash_dict(view)

    for f in friends_col.find({}, {"_id": 1}):
        state["friendships"].add(str(f["_id"]))

    for g in groups_col.find({}, {"_id": 1, "name": 1, "members": 1}):
        gid = str(g["_id"])
        view = {"name": g.get("name"), "members": sorted([
            str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
            for m in g.get("members", [])
        ])}
        state["groups"][gid] = _hash_dict(view)

    while True:
        try:
            # == USERS ==
            cur_users = {}
            for u in users_col.find({}, {"_id": 1, "profile": 1, "interests": 1, "prefs": 1}):
                vid = str(u["_id"])
                view = _pick_user_view(u)
                cur_users[vid] = _hash_dict(view)

                if vid not in state["users"]:
                    # INSERT
                    change = {
                        "operationType": "insert",
                        "ns": {"coll": "users"},
                        "fullDocument": u
                    }
                    print(f"\n[Polling] users INSERT {vid}")
                    sync_user_change(change, graph)
                else:
                    # UPDATE
                    if cur_users[vid] != state["users"][vid]:
                        prev_doc = users_col.find_one({"_id": ObjectId(vid)})
                        prev_view = _pick_user_view(prev_doc) if prev_doc else {}
                        updates = _diff_users(prev_view, view)
                        if updates:
                            change = {
                                "operationType": "update",
                                "ns": {"coll": "users"},
                                "documentKey": {"_id": ObjectId(vid)},
                                "updateDescription": {"updatedFields": updates}
                            }
                            print(f"\n[Polling] users UPDATE {vid} -> {list(updates.keys())}")
                            sync_user_change(change, graph)

            # DELETE users
            deleted_users = set(state["users"].keys()) - set(cur_users.keys())
            for vid in deleted_users:
                change = {
                    "operationType": "delete",
                    "ns": {"coll": "users"},
                    "documentKey": {"_id": ObjectId(vid)}
                }
                print(f"\n[Polling] users DELETE {vid}")
                sync_user_change(change, graph)

            state["users"] = cur_users

            # FRIENDSHIPS
            cur_friends = set()
            for f in friends_col.find({}, {"_id": 1, "status": 1, "members": 1}):
                fid = str(f["_id"])
                cur_friends.add(fid)
                if fid not in state["friendships"]:
                    # INSERT
                    change = {
                        "operationType": "insert",
                        "ns": {"coll": "friendships"},
                        "fullDocument": f
                    }
                    print(f"\n[Polling] friendships INSERT {fid}")
                    sync_friendship_change(change, graph)

            # DELETE friendships
            deleted_f = state["friendships"] - cur_friends
            for fid in deleted_f:
                change = {
                    "operationType": "delete",
                    "ns": {"coll": "friendships"},
                    "documentKey": {"_id": ObjectId(fid)}
                }
                print(f"\n[Polling] friendships DELETE {fid}")
                sync_friendship_change(change, graph)
            state["friendships"] = cur_friends

            # GROUPS
            cur_groups = {}
            for g in groups_col.find({}, {"_id": 1, "name": 1, "members": 1}):
                gid = str(g["_id"])
                view = {"name": g.get("name"), "members": sorted([
                    str(m.get("$oid")) if isinstance(m, dict) and "$oid" in m else str(m)
                    for m in g.get("members", [])
                ])}
                hv = _hash_dict(view)
                cur_groups[gid] = hv
                if gid not in state["groups"]:
                    # INSERT
                    change = {
                        "operationType": "insert",
                        "ns": {"coll": "groups"},
                        "fullDocument": g
                    }
                    print(f"\n[Polling] groups INSERT {gid}")
                    sync_group_change(change, graph)
                else:
                    if hv != state["groups"][gid]:
                        # UPDATE (name/members)
                        updated_fields = {"name": g.get("name"), "members": view["members"]}
                        change = {
                            "operationType": "update",
                            "ns": {"coll": "groups"},
                            "documentKey": {"_id": ObjectId(gid)},
                            "updateDescription": {"updatedFields": updated_fields}
                        }
                        print(f"\n[Polling] groups UPDATE {gid} -> name/members")
                        sync_group_change(change, graph)

            # DELETE groups
            deleted_g = set(state["groups"].keys()) - set(cur_groups.keys())
            for gid in deleted_g:
                change = {
                    "operationType": "delete",
                    "ns": {"coll": "groups"},
                    "documentKey": {"_id": ObjectId(gid)}
                }
                print(f"\n[Polling] groups DELETE {gid}")
                sync_group_change(change, graph)

            state["groups"] = cur_groups

            time.sleep(interval_s)

        except Exception as e:
            print(f" Polling loop error: {e}", file=sys.stderr)
            time.sleep(5) # Attente avant retry

def start_watcher():
    db = get_mongo_db()
    graph = get_neo4j_graph()

    if db is None or graph is None:
        print("MongoDB ou Neo4j non initialisés")
        return

    mode = WATCH_MODE
    if WATCH_MODE == "auto":
        mode = "changestream" if _has_replset else "polling"

    print(f" Mode watcher sélectionné: {mode}")

    if mode == "changestream":
        try:
            while True:
                try:
                    watch_with_change_streams(db, graph)
                except Exception as e:
                    print(f" Erreur Change Streams: {e}", file=sys.stderr)
                    print("Redémarrage Change Streams")
                    time.sleep(10)
        finally:
            pass
    else:
        # polling
        watch_with_polling(db, graph, interval_s=3)


if __name__ == "__main__":
    start_watcher()
