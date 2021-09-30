FROM openjdk:11-jre-slim

VOLUME /tmp

ARG JAR_FILE=target/*-springboot.jar
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]