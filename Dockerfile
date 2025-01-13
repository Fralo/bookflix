FROM maven:latest
WORKDIR /app

COPY . /app/

RUN mvn package

ENTRYPOINT ["java", "-cp", "target/bookflix-1.0-SNAPSHOT.jar", "dev.fralo.bookflix.App"]