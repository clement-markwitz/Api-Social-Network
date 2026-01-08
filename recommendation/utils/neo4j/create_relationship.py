from py2neo import Graph

def create_all_relations(graph: Graph, db_mongo):
    """
    Crée les relations Neo4j basées sur les documents MongoDB.
    - Relation d'auteur :AUTHORED_BY depuis 'posts'
    - Relation d'amitié :IS_FRIENDS_WITH depuis 'friendships'
    - Relation de blocage :BLOCKED depuis 'blocked'
    - Relation de réaction :REACTED depuis 'reactions'
    - Relation de commentaire :COMMENTED_ON depuis 'comments'
    - Relations embarquées (Admin, Lieu, Intérêts, Régimes)
    - Relation de topic :HAS_TOPIC depuis 'groups' et 'pages'

    1 collection Mongo -> 1 type de relation Neo4j
    1 document -> 0 ou plusieurs relations entre noeuds existants

    1. Récupère les documents MongoDB.
    2. Pour chaque document, crée les relations appropriées dans Neo4j.
    3. Gère les relations spéciales (amitié, admin, lieu, intérêts
    """
    print("Création des relations Neo4j...")

    print("  -> Relation :AUTHORED_BY (depuis 'posts')...")
    count = 0
    for doc in db_mongo.posts.find():
        post_id = str(doc["_id"])
        author_id = str(doc.get("authorId"))

        if author_id:
            graph.run("""
                MATCH (p:Posts {mongo_id: $post_id})
                MATCH (u:Users {mongo_id: $author_id})
                MERGE (p)-[:AUTHORED_BY]->(u)
            """, post_id=post_id, author_id=author_id)
            count += 1
    print(f"  {count} relations AUTHORED_BY créées.")

    # Relation d'amitié
    print("  -> Relation :IS_FRIENDS_WITH (depuis 'friendships')...")
    count = 0
    for doc in db_mongo.friendships.find():
        if doc.get("status") == "accepted":
            if "members" in doc and len(doc["members"]) == 2:
                user_a = str(doc["members"][0])
                user_b = str(doc["members"][1])
            else:
                user_a = str(doc.get("from") or doc.get("userA") or "")
                user_b = str(doc.get("to") or doc.get("userB") or "")

            if user_a and user_b and "createdAt" in doc:
                graph.run("""
                    MATCH (u1:Users {mongo_id: $user_a})
                    MATCH (u2:Users {mongo_id: $user_b})
                    MERGE (u1)-[r1:IS_FRIENDS_WITH]->(u2)
                    SET r1.createdAt = $date
                    MERGE (u2)-[r2:IS_FRIENDS_WITH]->(u1)
                    SET r2.createdAt = $date
                """, user_a=user_a, user_b=user_b, date=doc["createdAt"])
                count += 1

        elif doc.get("status") == "pending":
            initiator_id_str = str(doc.get("initiatorId"))
            target_ids = [str(m) for m in doc.get("members", []) if str(m) != initiator_id_str]
            if target_ids:
                graph.run("""
                    MATCH (initiator:Users {mongo_id: $initiator_id})
                    MATCH (target:Users {mongo_id: $target_id})
                    MERGE (initiator)-[:SENT_FRIEND_REQUEST]->(target)
                """, initiator_id=initiator_id_str, target_id=target_ids[0])
                count += 1

    print(f"  {count} relations d'amitié créées.")

    print("  -> Relation :MEMBER_OF (depuis 'groups.members')...")
    count = 0
    for doc in db_mongo.groups.find({"members": {"$exists": True, "$ne": []}}):
        group_id_str = str(doc["_id"])

        for member_ref in doc.get("members", []):
            if isinstance(member_ref, dict) and "$oid" in member_ref:
                member_id_str = str(member_ref["$oid"])
            elif "ObjectId" in str(type(member_ref)):
                member_id_str = str(member_ref)
            else:
                continue

            graph.run("""
                    MATCH (u:Users {mongo_id: $user_id})
                    MATCH (g:Groups {mongo_id: $group_id})
                    MERGE (u)-[:MEMBER_OF]->(g)
                """, user_id=member_id_str, group_id=group_id_str)
            count += 1

    print(f"  {count} relations MEMBER_OF créées.")

    # Relation de blocage
    print("  -> Relation :BLOCKED (depuis 'blocked')...")
    count = 0
    for doc in db_mongo.blocked.find():
        graph.run("""
            MATCH (blocker:Users {mongo_id: $blocker_id})
            MATCH (blocked:Users {mongo_id: $blocked_id})
            MERGE (blocker)-[:BLOCKED]->(blocked)
        """, blocker_id=str(doc["blockerId"]), blocked_id=str(doc["blockedId"]))
        count += 1
    print(f"  {count} relations de blocage créées.")

    # Relation de réaction
    print("  -> Relation :REACTED (depuis 'reactions')...")
    count = 0
    for doc in db_mongo.reactions.find():
        graph.run("""
            MATCH (u:Users {mongo_id: $user_id})
            MATCH (p:Posts {mongo_id: $post_id})
            MERGE (u)-[r:REACTED]->(p)
            SET r.type = $type, r.createdAt = $date
        """,
                  user_id=str(doc["userId"]),
                  post_id=str(doc["postId"]),
                  type=doc.get("type"),
                  date=doc.get("createdAt")
                  )
        count += 1
    print(f"  {count} relations de réaction créées.")

    # Commentaires
    print("  -> Relation :COMMENTED_ON (depuis 'comments')...")
    count = 0
    for doc in db_mongo.comments.find():
        graph.run("""
            MATCH (u:Users {mongo_id: $author_id})
            MATCH (p:Posts {mongo_id: $post_id})
            MERGE (u)-[:COMMENTED_ON]->(p)
        """, author_id=str(doc["authorId"]), post_id=str(doc["postId"]))
        count += 1
    print(f"  {count} relations de commentaire créées.")

    # Relations embarquées
    print("  -> Relations embarquées (Admin, Lieu, Intérêts, Régimes)...")

    for user in db_mongo.users.find():
        user_id = str(user["_id"])

        # Admins de groupes
        for group in db_mongo.groups.find({"admins": user["_id"]}):
            graph.run("""
                MATCH (u:Users {mongo_id: $user_id})
                MATCH (g:Groups {mongo_id: $group_id})
                MERGE (u)-[:ADMINS]->(g)
            """, user_id=user_id, group_id=str(group["_id"]))

        # Admins de pages
        for page in db_mongo.pages.find({"admins": user["_id"]}):
            graph.run("""
                MATCH (u:Users {mongo_id: $user_id})
                MATCH (p:Pages {mongo_id: $page_id})
                MERGE (u)-[:ADMINS]->(p)
            """, user_id=user_id, page_id=str(page["_id"]))

        # Lieu de vie
        if "location" in user.get("profile", {}):
            graph.run("""
                MERGE (l:Location {name: $loc_name})
                WITH l
                MATCH (u:Users {mongo_id: $user_id})
                MERGE (u)-[:LIVES_IN]->(l)
            """, loc_name=user["profile"]["location"], user_id=user_id)

        # Régimes
        for diet in user.get("prefs", {}).get("diets", []):
            graph.run("""
                MERGE (d:Diet {name: $diet_name})
                WITH d
                MATCH (u:Users {mongo_id: $user_id})
                MERGE (u)-[:FOLLOWS_DIET]->(d)
            """, diet_name=diet, user_id=user_id)

        # Intérêts (cuisines + techniques)
        interests = user.get("interests", {}).get("cuisines", []) + \
                    user.get("interests", {}).get("techniques", [])
        for interest in interests:
            graph.run("""
                MERGE (i:Interest {name: $interest_name})
                WITH i
                MATCH (u:Users {mongo_id: $user_id})
                MERGE (u)-[:INTERESTED_IN]->(i)
            """, interest_name=interest, user_id=user_id)

    # Topic
    print("  -> Relations :HAS_TOPIC (depuis 'groups' et 'pages')...")
    for group in db_mongo.groups.find({"topics": {"$exists": True}}):
        for topic in group.get("topics", []):
            graph.run("""
                MERGE (i:Interest {name: $topic_name})
                WITH i
                MATCH (g:Groups {mongo_id: $group_id})
                MERGE (g)-[:HAS_TOPIC]->(i)
            """, topic_name=topic, group_id=str(group["_id"]))

    for page in db_mongo.pages.find({"topics": {"$exists": True}}):
        for topic in page.get("topics", []):
            graph.run("""
                MERGE (i:Interest {name: $topic_name})
                WITH i
                MATCH (p:Pages {mongo_id: $page_id})
                MERGE (p)-[:HAS_TOPIC]->(i)
            """, topic_name=topic, page_id=str(page["_id"]))

    print("Création des relations terminée avec succès !")