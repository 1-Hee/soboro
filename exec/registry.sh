docker network create back-net
docker run --name registry-docker -d --restart=always -p $PORT:5000 -v /data/registry:/var/lib/registry/Docker/registry/v2 registry