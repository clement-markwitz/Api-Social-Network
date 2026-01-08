# --- REQUÊTES CYPHER POUR L'ÉVALUATION ---
from utils.requete_cypher.requetes import GROUND_TRUTH_QUERY, HISTORICAL_RECOMMENDATION_QUERY

def get_ground_truth(graph, date_t1, date_t2):
    """
    Exécute la requête Ground Truth pour trouver les ajouts d'amis
    réels qui ont eu lieu entre date_t1 et date_t2.

    Args:
        graph (py2neo.Graph): L'instance de connexion au graphe Neo4j.
        date_t1 (str): Date de début.
        date_t2 (str): Date de fin.

    Returns:
        dict: Un dictionnaire où les clés sont les 'userId' et les valeurs
              sont une liste des 'newFriendsMade' (les ajouts réels).
              Ex: {"user_id_1": ["user_id_A", "user_id_B"]}
    """
    print(f"\n Récupération de la vérité terrain (Ground Truth)... ")
    print(f"Période: [{date_t1}] à [{date_t2}]")

    truth_dict = {}
    try:
        # Exécute la requête
        result = graph.run(GROUND_TRUTH_QUERY, date_t1=date_t1, date_t2=date_t2)
        print(result)
        # Construit le dictionnaire de "vérité"
        for record in result:
            truth_dict[record["userId"]] = record["newFriendsMade"]

        print(f"Trouvé {len(truth_dict)} utilisateurs ayant ajouté des amis.")
        return truth_dict
    except Exception as e:
        print(f"Erreur lors de la récupération du 'Ground Truth': {e}")
        return {}


def get_predictions(graph, date_t1, user_list):
    """
    Exécute la requête de recommandation historique pour chaque
    utilisateur de la 'user_list', en simulant l'état du graphe à 'date_t1'.

    Args:
        graph (py2neo.Graph): L'instance de connexion au graphe Neo4j.
        date_t1 (str): La date "snapshot". Les recommandations seront
                       générées comme si nous étions à cette date.
        user_list (list): La liste des 'userId' pour lesquels générer
                          des prédictions.

    Returns:
        dict: Un dictionnaire où les clés sont les 'userId' et les valeurs
              sont une liste de leurs 10 meilleures recommandations (prédictions).
              Ex: {"user_id_1": ["user_id_K", "user_id_A", ...]}
    """
    print(f"\n Génération des prédictions (snapshot à {date_t1}) ")

    predictions_dict = {}
    total_users = len(user_list)

    # Boucle sur tous les utilisateurs pour lesquels nous avons une "vérité"
    for i, user_id in enumerate(user_list):
        if (i + 1) % 10 == 0 or i == total_users - 1:
            print(f"Traitement utilisateur {i + 1}/{total_users} ({user_id})")

        try:
            # Exécute la requête de recommandation historique pour un utilisateur
            result = graph.run(HISTORICAL_RECOMMENDATION_QUERY,
                               user_mongo_id=user_id,
                               date_t1=date_t1)

            # Stocke la liste des 10 (ou moins) ID recommandés
            predictions_dict[user_id] = [record["candidateId"] for record in result]

        except Exception as e:
            # Si un utilisateur plante, on continue avec les autres
            print(f"Erreur de recommandation pour l'utilisateur {user_id}: {e}")
            predictions_dict[user_id] = []

    print("Génération des prédictions terminée.")
    return predictions_dict


def calculate_metrics(truth_dict, predictions_dict):
    """
    Étape C: Compare la vérité (truth_dict) et les prédictions (predictions_dict)
    pour calculer la Précision@10 et le Rappel@10 moyens (mAP / mAR).

    Args:
        truth_dict (dict): Dictionnaire de la vérité terrain (de l'étape A).
        predictions_dict (dict): Dictionnaire des prédictions (de l'étape B).
    """
    print("\n Calcul des métriques")

    total_precision_at_10 = 0
    total_recall_at_10 = 0
    user_count = len(truth_dict)  # Nombre d'utilisateurs qu'on évalue

    if user_count == 0:
        print("Aucun utilisateur dans la 'vérité terrain', impossible de calculer.")
        return

    # On évalue utilisateur par utilisateur
    for user_id, relevant_items in truth_dict.items():
        # Récupère les prédictions pour cet utilisateur
        recommended_items = predictions_dict.get(user_id, [])

        # On utilise des sets pour une intersection facile et performante
        relevant_set = set(relevant_items)
        recommended_set = set(recommended_items)  # Ce qu'on a prédit (k=10)
        print("relevant_set", relevant_set)
        print("recommended_set",recommended_set)

        # 'hits' = les recommandations qui étaient correctes
        hits = relevant_set.intersection(recommended_set)

        # === Calculs des métriques ===

        # Précision@10 = (Bonnes recos) / (Total recos faites, i.e., k)
        # "Sur mon Top 10 (toutes les recommandations), quel pourcentage ont ajouter en ami ?"
        # On utilise len(recommended_set) qui est <= 10
        precision_at_10 = len(hits) / len(recommended_set) if len(recommended_set) > 0 else 0

        # Rappel@10 = (Bonnes recos) / (Total qu'il fallait trouver)
        # "Sur tous les amis que l'utilisateur a VRAIMENT ajoutés,
        #  quel pourcentage était dans mon Top 10 (les recommandations) ?"
        recall_at_10 = len(hits) / len(relevant_set) if len(relevant_set) > 0 else 0

        # Ajout aux totaux pour la moyenne
        total_precision_at_10 += precision_at_10
        total_recall_at_10 += recall_at_10

    # Calcul des moyennes
    mean_precision = (total_precision_at_10 / user_count) * 100
    mean_recall = (total_recall_at_10 / user_count) * 100

    # Affichage des résultats
    print("\n--- RÉSULTATS DE L'ÉVALUATION ---")
    print(f"Utilisateurs évalués: {user_count}")
    print(f"Précision@10 Moyenne (mAP): {mean_precision:.2f}%")
    print(f"Rappel@10 Moyen: {mean_recall:.2f}%")
    print("---------------------------------")

    return {
        "nb_utilisateurs_evalués": user_count,
        "pourcentage_utilisateurs_ayant_ajoutés_amis_après_recommandation" : f"{mean_precision:.2f}%",
        "pourcentage_amis_ajoutés_ayant_reçu_recommandation" : f"{mean_recall:.2f}%",
    }