import os
from pathlib import Path
from flask import jsonify, request
from utils.load_seeder import load_seed
from utils.neo4j.connection import get_neo4jgraph
from utils.evaluation import get_predictions, get_ground_truth, calculate_metrics
from utils.watcher.sync_watcher import start_watcher
from utils.requete_cypher.requetes import PAGE_RECOMMENDATION_QUERY, RECOMMENDATION_QUERY, POST_RECOMMENDATION_QUERY, \
    GROUP_RECOMMENDATION_QUERY

# Initialisation des variables pour seed la base mongodb
BASE_DIR = Path(__file__).resolve().parent
SEED_FILE = os.getenv("SEED_FILE", str(BASE_DIR / "utils" / "donnees_mongo.json"))
app = load_seed(SEED_FILE)

# Connexion à Neo4j
try:
    graph = get_neo4jgraph()
    graph.run("RETURN 1")
    print(" Connexion à Neo4j avec py2neo réussie.")
except Exception as e:
    print(f" Erreur de connexion à Neo4j (py2neo): {e}")
    graph = None

# Lancement de watcher avec un thread pour ne pas bloquer
try:
    import threading
    def _run_watcher():
        try:
            start_watcher()
        except Exception as e:
            print(f" Erreur lors du démarrage du watcher: {e}")
    watcher_thread = threading.Thread(target=_run_watcher, daemon=True)
    watcher_thread.start()
except Exception as e:
    print(f" Erreur lors du démarrage du watcher: {e}")

@app.route('/recommendations/posts/<user_mongo_id>', methods=['GET'])
def get_post_recommendations(user_mongo_id):
    """
    Retourne des recommandations de posts pour un utilisateur donné.

    Args:
        user_mongo_id (str): L'ID MongoDB de l'utilisateur.

    Returns:
        JSON: Liste des posts recommandés avec le nombre d'amis qui les ont aimés
    """
    if graph is None:
        return jsonify({"error": "Connexion Neo4j non établie"}), 503

    recommendations = []
    try:
        result = graph.run(POST_RECOMMENDATION_QUERY, user_mongo_id=user_mongo_id)
        recommendations = [record.data() for record in result]

        if not recommendations:
            return jsonify({"message": "Aucune recommandation trouvée."}), 404

        return jsonify(recommendations)

    except Exception as e:
        print(f"Erreur lors de l'exécution de la requête : {e}")
        return jsonify({"error": f"Erreur serveur: {e}"}), 500


@app.route('/recommendations/friends/<user_mongo_id>', methods=['GET'])
def get_friend_recommendations(user_mongo_id):
    """
    Retourne des recommandations d'amis pour un utilisateur donné.

    Args:
        user_mongo_id (str): L'ID MongoDB de l'utilisateur.

    Returns:
        JSON: Liste des amis recommandés avec leurs scores.
    """
    if graph is None:
        return jsonify({"error": "Connexion Neo4j non établie"}), 503

    recommendations = []
    try:
        result = graph.run(RECOMMENDATION_QUERY, user_mongo_id=user_mongo_id)
        recommendations = [record.data() for record in result]

        if not recommendations:
            return jsonify({"message": "Aucune recommandation trouvée."}), 404

        return jsonify(recommendations)

    except Exception as e:
        print(f"Erreur lors de l'exécution de la requête : {e}")
        return jsonify({"error": f"Erreur serveur: {e}"}), 500

@app.route('/recommendations/pages/<user_mongo_id>', methods=['GET'])
def get_page_recommendations(user_mongo_id):
    """
    Retourne des recommandations de pages pour un utilisateur donné.

    Args:
        user_mongo_id (str): L'ID MongoDB de l'utilisateur.

    Returns:
        JSON: Liste des pages recommandées avec leurs scores.
    """
    if graph is None:
        return jsonify({"error": "Connexion Neo4j non établie"}), 503

    recommendations = []
    try:
        result = graph.run(PAGE_RECOMMENDATION_QUERY, user_mongo_id=user_mongo_id)
        recommendations = [record.data() for record in result]

        if not recommendations:
            return jsonify({"message": "Aucune recommandation de page trouvée."}), 404

        return jsonify(recommendations)

    except Exception as e:
        print(f"Erreur lors de l'exécution de la requête : {e}")
        return jsonify({"error": f"Erreur serveur: {e}"}), 500

@app.route('/recommendations/groupes/<user_mongo_id>', methods=['GET'])
def get_group_recommendations(user_mongo_id):
    """
    Retourne des recommandations de groupes pour un utilisateur donné.

    Args:
        user_mongo_id (str): L'ID MongoDB de l'utilisateur.

    Returns:
        JSON: Liste des groupes recommandés avec leurs scores.
    """
    if graph is None:
        return jsonify({"error": "Connexion Neo4j non établie"}), 503
    recommendations = []
    try:
        result = graph.run(GROUP_RECOMMENDATION_QUERY, user_mongo_id=user_mongo_id)
        recommendations = [record.data() for record in result]

        if not recommendations:
            return jsonify({"message": "Aucune recommandation de groupe trouvée."}), 404
        return jsonify(recommendations)
    except Exception as e:
        print(f"Erreur lors de l'exécution de la requête : {e}")
        return jsonify({"error": f"Erreur serveur: {e}"}), 500

@app.route('/evaluate_reco', methods=['GET','POST'])
def evaluate_reco():
    """
    Évalue les recommandations d'amis en comparant les prédictions
    avec la vérité terrain sur une fenêtre temporelle donnée.

    Attendu dans le body JSON:
    {
        "date_t1": "2023-01-01T00:00:00",
        "date_t2": "2023-01-31T23:59:59"
    }

    Retourne les métriques d'évaluation (precision, recall, F1-score).
    1. Récupère la vérité terrain (ajouts réels entre date_t1 et date_t2).
    2. Génère les prédictions comme si nous étions à date_t1.
    3. Calcule et retourne les métriques.
    """
    if graph is None:
        return jsonify({"error": "Connexion Neo4j non établie"}), 503

    data = request.get_json(silent=True) or {}
    date_t1 = data.get("date_t1")
    date_t2 = data.get("date_t2")

    if not date_t1 or not date_t2:
        return jsonify({"error": "Les champs `date_t1` et `date_t2` sont requis dans le body"}), 400

    try:
        # 1. Récupérer la vérité
        truth_data = get_ground_truth(graph, date_t1, date_t2)
        if not truth_data:
            return jsonify({"message": "Aucune donnée 'vérité terrain' trouvée pour cette période."}), 404

        # 2. Obtenir les prédictions (ce que le système "aurait" recommandé à t1)
        users_to_evaluate = list(truth_data.keys())
        predictions_data = get_predictions(graph, date_t1, users_to_evaluate)

        # 3. Comparer et calculer les métriques
        metrics = calculate_metrics(truth_data, predictions_data)

        return jsonify(metrics), 200

    except Exception as e:
        print(f"Erreur lors de l'évaluation: {e}")
        return jsonify({"error": f"Erreur serveur: {e}"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)