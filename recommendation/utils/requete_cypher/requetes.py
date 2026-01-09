# Requête de recommandation des users cypher
RECOMMENDATION_QUERY = """
MATCH (u1:Users {mongo_id: $user_mongo_id})
MATCH (u2:Users)
WHERE u1 <> u2 AND NOT (u1)-[:IS_FRIENDS_WITH]-(u2)

OPTIONAL MATCH (u1)-[:IS_FRIENDS_WITH]-(cf:Users)-[:IS_FRIENDS_WITH]-(u2)
WITH u1, u2, COLLECT(DISTINCT cf) AS commonFriends

OPTIONAL MATCH (u1)-[:FOLLOWS_DIET]-(cd:Diet)-[:FOLLOWS_DIET]-(u2)
WITH u1, u2, commonFriends, COLLECT(DISTINCT cd) AS commonDiets

OPTIONAL MATCH (u1)-[:MEMBER_OF]-(cg:Groups)-[:MEMBER_OF]-(u2)
WITH u1, u2, commonFriends, commonDiets, COLLECT(DISTINCT cg) AS commonGroups

OPTIONAL MATCH (u1)-[:INTERESTED_IN]-(ci:Interest)-[:INTERESTED_IN]-(u2)
WITH u1, u2, commonFriends, commonDiets, commonGroups, COLLECT(DISTINCT ci) AS commonInterests

OPTIONAL MATCH (u1)-[:LIVES_IN]-(cl:Location)-[:LIVES_IN]-(u2)
WITH u1, u2, commonFriends, commonDiets, commonGroups, commonInterests, COLLECT(DISTINCT cl) AS commonLocation

WITH u2,
     (size(commonFriends) * 2.0) AS friendScore,
     (size(commonDiets) * 1.5) AS dietScore,
     (size(commonGroups) * 1.5) AS groupScore,
     (size(commonInterests) * 1.0) AS interestScore,
     (size(commonLocation) * 0.5) AS locationScore

WITH u2.profile_pseudo AS candidateName, 
     u2.mongo_id AS candidateId,
     (friendScore + dietScore + groupScore + interestScore + locationScore) AS totalScore

WHERE totalScore > 0
RETURN candidateName, candidateId, totalScore
ORDER BY totalScore DESC
LIMIT 10
"""

# Requête de recommandation de posts cypher
POST_RECOMMENDATION_QUERY = """
MATCH (me:Users {mongo_id: $user_mongo_id})-[:IS_FRIENDS_WITH]->(friend:Users)
MATCH (friend)-[r:REACTED]->(recoPost:Posts)
WHERE r.type IN ['like', 'yummy']
AND NOT (me)-[:REACTED]->(recoPost)
AND NOT (recoPost)-[:AUTHORED_BY]->(me)

MATCH (recoPost)-[:AUTHORED_BY]->(author:Users)
WHERE NOT (me)-[:BLOCKED]->(author)

RETURN recoPost.mongo_id AS postId, 
       count(DISTINCT friend) AS likedByFriends
ORDER BY likedByFriends DESC
LIMIT 20
"""

# Requête de recommandation de pages cypher
PAGE_RECOMMENDATION_QUERY = """
MATCH (me:Users {mongo_id: $user_mongo_id})
MATCH (recoPage:Pages)
WHERE NOT (me)-[:ADMINS]->(recoPage)

OPTIONAL MATCH (me)-[:IS_FRIENDS_WITH]->(friend:Users)-[:ADMINS]->(recoPage)
WITH me, recoPage, COLLECT(DISTINCT friend) AS adminFriends

OPTIONAL MATCH (me)-[:INTERESTED_IN]->(i:Interest)<-[:HAS_TOPIC]-(recoPage)
WITH me, recoPage, adminFriends, COLLECT(DISTINCT i) AS commonInterests

WITH recoPage,
     (size(adminFriends) * 2.0) AS friendAdminScore,
     (size(commonInterests) * 1.0) AS interestScore

WITH recoPage.name AS pageName, 
     recoPage.mongo_id AS pageId,
     (friendAdminScore + interestScore) AS totalScore

WHERE totalScore > 0
RETURN pageName, pageId, totalScore
ORDER BY totalScore DESC
LIMIT 10
"""

# Requête de recommendations de groupes cypher
GROUP_RECOMMENDATION_QUERY = """
MATCH (me:Users {mongo_id: $user_mongo_id})
MATCH (recoGroup:Groups)
WHERE NOT (me)-[:MEMBER_OF]->(recoGroup)
OPTIONAL MATCH (me)-[:IS_FRIENDS_WITH]->(friend:Users)-[:MEMBER_OF]->(recoGroup)
WITH me, recoGroup, COLLECT(DISTINCT friend) AS memberFriends
OPTIONAL MATCH (me)-[:INTERESTED_IN]->(i:Interest)<-[:HAS_TOPIC]-(recoGroup)
WITH me, recoGroup, memberFriends, COLLECT(DISTINCT i) AS commonInterests
WITH recoGroup,
        (size(memberFriends) * 2.0) AS friendMemberScore,
        (size(commonInterests) * 1.0) AS interestScore
WITH recoGroup.name AS groupName,
        recoGroup.mongo_id AS groupId,
        (friendMemberScore + interestScore) AS totalScore
    WHERE totalScore > 0
    RETURN groupName, groupId, totalScore
    ORDER BY totalScore DESC
    LIMIT 10
"""

# ==== Evaluation =====
# Permet d'évaluer la qualité des recommandations d'amis
# en comparant les recommandations faites à un instant t1
# avec les nouvelles amitiés réellement formées entre t1 et t2.
GROUND_TRUTH_QUERY = """
MATCH (u1:Users)-[r:IS_FRIENDS_WITH]->(u2:Users)
WHERE datetime(r.createdAt) >= datetime($date_t1)  AND datetime(r.createdAt) < datetime($date_t2)
AND id(u1) < id(u2) 
RETURN u1.mongo_id AS userId, collect(u2.mongo_id) AS newFriendsMade
"""

# Requête de recommandation historique cypher
# Génère des recommandations comme si nous étions à la date 'date_t1'
HISTORICAL_RECOMMENDATION_QUERY = """
MATCH (u1:Users {mongo_id: $user_mongo_id})
MATCH (u2:Users)
WHERE u1 <> u2

OPTIONAL MATCH (u1)-[r_hist:IS_FRIENDS_WITH]-(u2)
WHERE r_hist.createdAt < $date_t1
WITH u1, u2, r_hist
WHERE r_hist IS NULL // Garde uniquement les u2 où r_hist n'existe pas

OPTIONAL MATCH (u1)-[r1:IS_FRIENDS_WITH]-(cf:Users)-[r2:IS_FRIENDS_WITH]-(u2)
WHERE r1.createdAt < $date_t1 AND r2.createdAt < $date_t1
WITH u1, u2, COLLECT(DISTINCT cf) AS commonFriends

OPTIONAL MATCH (u1)-[:FOLLOWS_DIET]-(cd:Diet)-[:FOLLOWS_DIET]-(u2)
WITH u1, u2, commonFriends, COLLECT(DISTINCT cd) AS commonDiets
OPTIONAL MATCH (u1)-[:MEMBER_OF]-(cg:Groups)-[:MEMBER_OF]-(u2)
WITH u1, u2, commonFriends, commonDiets, COLLECT(DISTINCT cg) AS commonGroups
OPTIONAL MATCH (u1)-[:INTERESTED_IN]-(ci:Interest)-[:INTERESTED_IN]-(u2)
WITH u1, u2, commonFriends, commonDiets, commonGroups, COLLECT(DISTINCT ci) AS commonInterests
OPTIONAL MATCH (u1)-[:LIVES_IN]-(cl:Location)-[:LIVES_IN]-(u2)
WITH u1, u2, commonFriends, commonDiets, commonGroups, commonInterests, COLLECT(DISTINCT cl) AS commonLocation

WITH u2,
     (size(commonFriends) * 2.0) AS friendScore,
     (size(commonDiets) * 1.5) AS dietScore,
     (size(commonGroups) * 1.5) AS groupScore,
     (size(commonInterests) * 1.0) AS interestScore,
     (size(commonLocation) * 0.5) AS locationScore
WITH u2.mongo_id AS candidateId,
     (friendScore + dietScore + groupScore + interestScore + locationScore) AS totalScore

WHERE totalScore > 0
RETURN candidateId, totalScore
ORDER BY totalScore DESC
LIMIT 10
"""

