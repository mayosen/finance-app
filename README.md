# finance-app

Цель данного проекта - исследование практического применения архитектурных шаблонов Event Sourcing и CQRS на примере
финансового приложения для учета расходов. Система представляет собой 2 микросервиса, сервис команд и сервис запросов,
взаимодействующих через шину событий. Выбранная архитектура позволяет разделить поток команд и поток запросов,
обеспечить
оптимизированный доступ к данным через проекции, а также поддерживать неизменяемый журнал событий.

## Архитектура

Диаграмма контекста (C4 context diagram)

![Диаграмма контекста](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/1-c4-context-diagram.plantuml)

Диаграмма прецедентов (Use case diagram)

![Диаграмма прецедентов](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/2-use-case-diagram.plantuml)

Диаграмма последовательностей для потока команд

![Диаграмма последовательностей для потока команд](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/5-command-flow-sequence-diagram.plantuml)

Диаграмма последовательностей для потока проекции событий

![Диаграмма последовательностей для потока проекции событий](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/6-event-flow-sequence-diagram.plantuml)

Диаграмма последовательностей для потока запросов

![Диаграмма последовательностей для потока запросов](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/7-query-flow-sequence-diagram.plantuml)

Диаграмма компонентов сервиса команд (C4 Component diagram)

![Диаграмма компонентов сервиса команд](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/8-command-service-c4-component-diagram.plantuml)

Диаграмма компонентов сервиса запросов (C4 Component diagram)

![Диаграмма компонентов сервиса запросов](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/9-query-service-c4-component-diagram.plantuml)

Диаграмма развертывания (Deployment diagram)

![Диаграмма развертывания](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/mayosen/finance-app/refs/heads/docs/decribe-project/docs/diagram/10-deployment-diagram.plantuml)

## Технологии

TODO

## Разработка

Инструкции для разработки приложены в файле [DEVELOPMENT.md](DEVELOPMENT.md).
