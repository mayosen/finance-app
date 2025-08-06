# finance-app

Цель данного проекта - исследование практического применения архитектурных шаблонов Event Sourcing и CQRS на примере
финансового приложения для учета расходов. Система представляет собой 2 микросервиса, сервис команд и сервис запросов,
взаимодействующих через шину событий. Выбранная архитектура позволяет разделить поток команд и поток запросов, обеспечить 
оптимизированный доступ к данным через проекции, а также поддерживать неизменяемый журнал событий.




## Генерация API

Проект применяет шаблон API First: код генерируется на основе заданной OpenAPI спецификации. Чтобы сгенерировать код,
необходимо активировать
соответствующий профиль Maven:

- generate-command-service-api для
  генерации [API сервиса команд](/command-api/src/main/resources/openapi/command-api-v1.yml)
- generate-query-service-api для
  генерации [API сервиса запросов](/query-api/src/main/resources/openapi/query-api-v1.yml)

По окончании генерации необходимо применить действия "Reformat Code" и "Optimize Imports" в IDEA.

## Локальный запуск

Приложение можно запустить локально. Для этого предлагается использовать профиль `local`, который можно активировать
следующим образом.

```shell
SPRING_PROFILES_ACTIVE=local
```

Кроме того, для запуска приложения необходимы сторонние системы, такие как СУБД PostgreSQL, а также Kafka и Zookeeper.
Для этих целей предлагается использовать Docker Compose.

Перейти в папку [docker/](/docker)

```shell
# root dir
$ cd docker
```

Создать и запустить проект в detached режиме

```shell
docker compose -p finance-app -f docker-compose.yml up -d
```

Слушать логи в режиме реального времени

```shell
docker compose -p finance-app -f docker-compose.yml logs -f 
```

Остановить проект

```shell
docker compose -p finance-app -f docker-compose.yml stop
```

Полностью удалить проект

```shell
docker-compose -p finance-app -f docker-compose.yml down --volumes --remove-orphan
```
