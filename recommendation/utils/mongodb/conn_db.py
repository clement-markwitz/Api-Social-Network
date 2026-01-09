import os
from pymongo import MongoClient

# Variables d'environnement pour la connexion MongoDB
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017")
DB_NAME   = os.getenv("DB_NAME", "reseau_culinaire")

_client = None
_db = None

def get_client() -> MongoClient:
    """Retourne un client MongoDB."""
    global _client
    if _client is None:
        _client = MongoClient(MONGO_URI)
    return _client

def get_db():
    """Retourne la base par défaut (reseau_culinaire)."""
    global _db
    if _db is None:
        _db = get_client()[DB_NAME]
    return _db
