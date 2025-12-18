job "reseau-culinaire" {
  datacenters = ["dc1"]
  type        = "service"

  group "database" {
    count = 1

    network {
      port "db" {
        static = 27017
      }
    }

    task "mongodb" {
      driver = "docker"

      config {
        image = "mongo:6.0"
        ports = ["db"]
        volumes = [
          "local/data:/data/db" # Persistance basique
        ]
      }

      resources {
        cpu    = 500 # 500 MHz
        memory = 512 # 512MB
      }

      service {
        name = "mongodb"
        port = "db"

        check {
          name     = "alive"
          type     = "tcp"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }

  group "api" {
    count = 1

    network {
      port "http" {
        static = 8080
      }
    }

    task "api-rest" {
      driver = "docker"

      config {
        image = "spring:latest"

        ports = ["http"]
      }

      env {
        SPRING_DATA_MONGODB_URI = "mongodb://${attr.unique.network.ip-address}:27017/reseau_culinaire"
      }

      resources {
        cpu    = 1000
        memory = 1024
      }

      service {
        name = "api-rest"
        port = "http"

        check {
          type     = "http"
          path     = "/api/stats"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}