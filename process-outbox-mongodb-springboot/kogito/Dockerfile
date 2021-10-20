FROM fabric8/java-alpine-openjdk11-jre

VOLUME /tmp

ARG JAR_FILE=target/*-springboot.jar
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]