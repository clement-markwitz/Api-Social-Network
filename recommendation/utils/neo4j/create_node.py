# utils/neo4j/create_nodes.py
from utils.mongodb.conn_db import get_db
from utils.neo4j.connection import get_neo4jgraph
from bson import json_util
from utils.neo4j.create_constraints import create_label_constraint

def normalize_value(value):
    """
    Convertit n'importe quelle valeur issue de MongoDB
    en une valeur compatible Neo4j
    """
    t = str(type(value))

    # Null ou types simples
    if value is None or "str" in t or "int" in t or "float" in t or "bool" in t:
        return value

    # ObjectId -> string
    if "ObjectId" in t:
        return str(value)

    # datetime
    if "datetime" in t:
        return value

    # List
    if "list" in t:
        try:
            return [normalize_value(v) for v in value]
        except Exception:
            return json_util.dumps(value, ensure_ascii=False)

    # Dictionnaire -> converti en JSON
    if "dict" in t:
        return json_util.dumps(value, ensure_ascii=False)

    # Autres types -> string
    return str(value)


def flatten(prefix, value, out):
    """
    Aplatis un document Mongo en propriétés plates, en utilisant des _ pour les sous-documents.
    Ex:
    {
        "name": "mamar",
        "address": {
            "city": "braintrot",
            "zip": "12345"
        }
    }

    devient:
    {
        "name": "mamar",
        "address_city": "braintrot",
        "address_zip": "12345"
    }

    Args:
        prefix (str): préfixe pour les clés (vide pour le niveau racine)
        value (any): valeur à aplatir
        out (dict): dictionnaire de sortie où stocker les paires clé-valeur

    Returns:
        None: les résultats sont stockés dans 'out'

    1. prefix: préfixe pour les clés (vide pour le niveau racine
    2. value: valeur à aplatir
    3. out: dictionnaire de sortie où stocker les paires clé-valeur
    """
    t = str(type(value))

    # Sous-document (dict)
    if "dict" in t:
        for k, v in value.items():
            key = f"{prefix}_{k}" if prefix else k
            flatten(key, v, out)

    # Liste
    elif "list" in t:
        out[prefix] = normalize_value(value)

    # Valeur simple
    else:
        out[prefix] = normalize_value(value)


def create_all_nodes_from_labels(clear_first=True):
    """
    - 1 collection Mongo -> 1 label Neo4j
    - 1 document -> 1 noeud avec propriétés propres
    - Aplatissement des sous-documents
    - Conversion ObjectId -> str
    - Pas de création de noeud pour 'friendships'
    """
    db = get_db()
    graph = get_neo4jgraph()
    if not graph:
        raise RuntimeError("Connexion Neo4j indisponible.")

    if clear_first:
        print("Suppression de tous les noeud Neo4j...")
        graph.run("MATCH (n) DETACH DELETE n")

    # === Création des noeuds ===
    for coll in db.list_collection_names():
        if coll == "friendships":
            continue  # pas de noeud pour cette collection

        label = coll.capitalize()
        create_label_constraint(graph, coll)

        created = 0
        for doc in db[coll].find({}):
            props = {"mongo_id": str(doc.get("_id"))}
            for k, v in doc.items():
                if k == "_id" or k in ("password", "password_hash"):
                    continue
                flatten(k, v, props)

            graph.run(
                f"MERGE (n:`{label}` {{ mongo_id: $mongo_id }}) "
                f"SET n += $props",
                mongo_id=props["mongo_id"],
                props=props
            )
            created += 1

        print(f"[Neo4j] {coll} -> {label}: {created} noeuds créés")

    print("Import Mongo -> Neo4j terminé sans isinstance().")

