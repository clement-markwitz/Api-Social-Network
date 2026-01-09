job "reco-bd" {
  datacenters = ["iutlens"]
  type        = "service"

  group "databases" {
    network {
      port "mongo"       { to = 27017 }
      port "neo4j-bolt"  { to = 7687 }
      port "neo4j-http"  { to = 7474 }
      port "api"         { to = 5000 }
    }
    # ----------------------
    # FLASK BACKEND
    # ----------------------
    task "flask" {
      driver = "podman"

      config {
        image   = "reg.chez-wam.info/recomendation-kitchen:latest"
        ports   = ["api"]
      }


      env {
        MONGO_URI     = "mongodb://root:GrosSecret@127.0.0.1:27017/reseau_culinaire"
        DB_NAME       = "reseau_culinaire"
        NEO4J_URI     = "bolt://127.0.0.1:7687"
        NEO4J_USER    = "neo4j"
        NEO4J_PASSWORD= "neo4jtest"
      }

      resources {
        cpu    = 200
        memory = 256
      }

      service {
        name     = "reco-bd-flask"
        port     = "api"
        provider = "nomad"
        tags = [
          "traefik.enable=true",
          "traefik.http.routers.reco-bd-flask.entrypoints=http,https",
          "traefik.http.routers.reco-bd-flask.tls=true",
          "traefik.http.routers.reco-bd-flask.rule=Host(`reco-bd-flask.virtu.chez-wam.info`)",
        ]
      }
    }

    # ----------------------
    # MONGO
    # ----------------------
    task "mongo" {
      driver = "podman"
      config {
        image = "mongo:6.0"
        ports = ["mongo"]
      }

      env {
        MONGO_INITDB_ROOT_USERNAME = "root"
        MONGO_INITDB_ROOT_PASSWORD = "GrosSecret"
      }

      service {
        name     = "reco-bd-mongo"
        port     = "mongo"
        provider = "nomad"
        tags = [
          "traefik.enable=true",
          "traefik.http.routers.reco-bd-mongo.entrypoints=http,https",
          "traefik.http.routers.reco-bd-mongo.tls=true",
          "traefik.http.routers.reco-bd-mongo.rule=Host(`reco-bd-mongo.virtu.chez-wam.info`)",
        ]
      }

      resources {
        cpu    = 100
        memory = 256
      }
    }

    # ----------------------
    # NEO4J
    # ----------------------
    task "neo4j" {
      driver = "podman"
      config {
        image = "neo4j:5.10"
        ports = ["neo4j-bolt", "neo4j-http"]
      }

      env {
        NEO4J_AUTH = "neo4j/neo4jtest"
      }

      service {
        name     = "reco-bd-neo4j-bolt"
        port     = "neo4j-bolt"
        provider = "nomad"
        tags = [
          "traefik.enable=true",
          "traefik.http.routers.reco-bd-neo4j-bolt.entrypoints=http,https",
          "traefik.http.routers.reco-bd-neo4j-bolt.tls=true",
          "traefik.http.routers.reco-bd-neo4j-bolt.rule=Host(`reco-bd-neo4j-bolt.virtu.chez-wam.info`)",
        ]
      }

      service {
        name     = "reco-bd-neo4j-http"
        port     = "neo4j-http"
        provider = "nomad"
        tags = [
          "traefik.enable=true",
          "traefik.http.routers.reco-bd-neo4j-http.entrypoints=http,https",
          "traefik.http.routers.reco-bd-neo4j-http.tls=true",
          "traefik.http.routers.reco-bd-neo4j-http.rule=Host(`reco-bd-neo4j-http.virtu.chez-wam.info`)",
        ]
      }

      resources {
        cpu    = 200
        memory = 1024
      }
    }
  }
}