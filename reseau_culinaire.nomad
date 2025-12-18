job "reseau-culinaire" {
  datacenters = ["iutlens"]
  type        = "service"

  group "reseau-stack" {
    count = 1

    network {
      port "http" {
        to = 8080
      }
      port "db" {
        to = 27017
      }
    }

    task "mongodb" {
      driver = "podman"

      config {
        image = "mongo:6.0"
        ports = ["db"]
        volumes = [
          "local/data:/data/db"
        ]
      }

      resources {
        cpu    = 500
        memory = 512
      }
    }

    task "api-rest" {
      driver = "podman"

      config {
        image = "spring:latest"
        image_pull_policy = "if-not-present"

        ports = ["http"]
      }

      env {
        SPRING_DATA_MONGODB_URI = "mongodb://127.0.0.1:27017/reseau_culinaire"
      }

      resources {
        cpu    = 1000
        memory = 1024
      }
    }

    service {
      name     = "reseau-culinaire-cornet"
      port     = "http"
      provider = "nomad"

      tags = [
        "traefik.enable=true",
        "traefik.http.routers.reseau-culinaire-cornet.entrypoints=http,https",
        "traefik.http.routers.reseau-culinaire-cornet.tls=true",
        "traefik.http.routers.reseau-culinaire-cornet.rule=Host(`reseau-culinaire-cornet.virtu.chez-wam.info`)",
      ]

      check {
        type     = "http"
        path     = "/api/stats"
        interval = "10s"
        timeout  = "2s"
      }
    }
  }
}