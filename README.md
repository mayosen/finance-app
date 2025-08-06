# finance-app

## Generate API

To generate API by OpenAPI specification turn on the appropriate Maven profile:

- generate-command-service-api
- generate-query-service-api

Finally, do not forget to apply "Reformat Code" and "Optimize Imports" actions.

## Run locally

The application can be run locally. To do this, it is suggested to run it with an active `local` profile.

```shell
SPRING_PROFILES_ACTIVE=local
```

Also, third-party systems are needed to run, for this it is proposed to use Docker Compose.

Create and launch the project in detached mode

```shell
# root dir
$ cd docker
```

```shell
docker compose -p finance-app -f docker-compose.yml up -d
```

Listen to logs

```shell
docker compose -p finance-app -f docker-compose.yml logs -f 
```

Stop the project

```shell
docker compose -p finance-app -f docker-compose.yml stop
```

Completely delete the project

```shell
docker-compose -p finance-app -f docker-compose.yml down --volumes --remove-orphan
```
