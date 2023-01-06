FROM docker.io/maven:3.8-eclipse-temurin-19 as maven
COPY ./pom.xml ./pom.xml

COPY ./src ./src

RUN mvn clean dependency:copy-dependencies package

FROM docker.io/amazoncorretto:19-alpine

WORKDIR /app

COPY --from=maven target/* ./app/
COPY --from=maven target/VisualCommunicatorServer-*.jar ./app/VisualCommunicatorServer.jar
COPY --from=maven target/dependency/* ./app/dependency/

CMD ["java", "-cp", "./app/VisualCommunicatorServer.jar:./app/dependency/*", "pl.edu.pk.kron.visualcommunicator.Main"]
#CMD ["java", "-jar", "./app/VisualCommunicatorServer.jar"]