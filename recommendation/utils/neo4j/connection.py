from py2neo import Graph
import sys
import os

NEO4J_URI = os.getenv("NEO4J_URI", "bolt://localhost:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "neo4jtest")

graph = None

def get_neo4jgraph():
    """
    Initialise et retourne l'objet Graph de Py2neo.
    """
    global graph
    if graph is None:
        try:
            if not NEO4J_PASSWORD:
                print("Erreur: NEO4J_PASSWORD n'est pas défini.", file=sys.stderr)
                return None
            graph = Graph(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
            graph.run("RETURN 1")
            print("Connecté à Neo4j avec Py2neo succès.")

        except Exception as e:
            print(f"Erreur de connexion Neo4j (Py2neo) : {e}", file=sys.stderr)
            graph = None
    return graph