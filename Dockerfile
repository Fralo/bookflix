FROM maven:latest
WORKDIR /app

COPY . /app/

RUN mvn clean
RUN mvn install
RUN mvn package

ENTRYPOINT ["java", "-jar", "target/bookflix-1.0-SNAPSHOT-jar-with-dependencies.jar"]