from flask import Flask
from pymongo.errors import BulkWriteError
from bson import json_util
import json
from utils.mongodb.conn_db import get_db
from utils.neo4j.create_node import create_all_nodes_from_labels
from utils.neo4j.create_relationship import create_all_relations
from utils.neo4j.connection import get_neo4jgraph

def create_indexes(db):
    """
    Crée les index nécessaires sur les collections.

    :param db:
    :return: None
    """
    db.users.create_index("email", unique=True)
    db.users.create_index("username", unique=True)

    # Index posts / comments / reactions / blocked / bans
    db.posts.create_index([("authorId", 1), ("createdAt", -1)])
    db.comments.create_index([("postId", 1), ("createdAt", -1)])
    db.reactions.create_index([("postId", 1), ("userId", 1), ("type", 1)], unique=True)
    db.blocked.create_index([("blockerId", 1), ("blockedId", 1)], unique=True)
    db.bans.create_index([("userId", 1), ("active", 1)])

    # On supprime d'abord les anciens index fautifs ("members_1" etc.)
    try:
        existing = list(db.friendships.list_indexes())
        for idx in existing:
            name = idx.get("name")
            if name and name != "_id_":
                db.friendships.drop_index(name)
                print(f"Ancien index supprimé : {name}")
    except Exception as e:
        print(f"Impossible de lister/supprimer les anciens index friendships : {e}")

    try:
        db.friendships.update_many({}, [
            {"$set": {"members": {"$sortArray": {"input": "$members"}}}}
        ])
    except Exception:
        pass

    # Création du nouvel index unique sur les paires d'amis
    db.friendships.create_index(
        [("members.0", 1), ("members.1", 1)],
        unique=True,
        name="unique_pair_members"
    )
    print("Index créés (ou déjà existants).")


def load_seed(path: str):
    """
    Charge un seed MongoDB depuis un fichier JSON.

    :param path: Chemin du fichier JSON.
    :return: None
    """
    db = get_db()
    print(f"Connecté à MongoDB ({db.name})")

    # Lecture du fichier JSON
    with open(path, "r", encoding="utf-8") as f:
        seed_data = json_util.loads(f.read())

    # Insertion par collection
    for collection_name, docs in seed_data.items():
        if not isinstance(docs, list):
            print(f"{collection_name} ignorée (pas une liste)")
            continue

        coll = db[collection_name]
        if not docs:
            print(f"{collection_name} vide - rien à insérer.")
            continue

        # Reset la collection
        coll.delete_many({})
        try:
            result = coll.insert_many(docs, ordered=False)
            print(f"{collection_name}: {len(result.inserted_ids)} documents insérés")
        except BulkWriteError as bwe:
            print(f"Erreur BulkWrite dans {collection_name} :")
            print(json.dumps(bwe.details, ensure_ascii=False)[:1000], "...")
        except Exception as e:
            print(f"Erreur dans {collection_name}: {e}")

    # Création/validation des index
    create_indexes(db)

    graph = get_neo4jgraph()
    create_all_nodes_from_labels(graph)
    create_all_relations(graph, db)

    print("\nImport terminé avec succès !")
    return Flask(__name__)

