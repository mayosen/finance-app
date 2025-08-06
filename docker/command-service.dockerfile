FROM openjdk:21-jdk-slim
USER 2000
ARG APP_HOME=/app
WORKDIR $APP_HOME
COPY command-service/target/financeapp-command-service-exec.jar $APP_HOME/financeapp-command-service.jar
ENTRYPOINT java $JAVA_OPTS -jar $APP_HOME/financeapp-command-service.jar $JAVA_ARGS
