FROM amd64/eclipse-temurin:17.0.13_11-jre-alpine
VOLUME /tmp
ARG CHAINLP_JAR
COPY ${CHAINLP_JAR} chaintest.jar
ENTRYPOINT ["java", "-jar", "/chaintest.jar"]
