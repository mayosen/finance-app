Verify if Docker installed

```shell
# root dir
docker --version
```

Build command-service

```shell
docker build -t financeapp-command-service:latest -f ./docker/command-service.dockerfile .
```

Build query-service

```shell
docker build -t financeapp-query-service:latest -f ./docker/query-service.dockerfile .
```

