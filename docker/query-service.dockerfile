FROM openjdk:21-jdk-slim
USER 2000
ARG APP_HOME=/app
WORKDIR $APP_HOME
COPY query-service/target/financeapp-query-service-exec.jar $APP_HOME/financeapp-query-service.jar
ENTRYPOINT java $JAVA_OPTS -jar $APP_HOME/financeapp-query-service.jar $JAVA_ARGS
