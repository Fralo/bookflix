FROM maven:latest as build
WORKDIR /app

# Copy the pom.xml file first, so that Maven can download the dependencies
COPY pom.xml /app/

# Download the dependencies and cache them
RUN mvn dependency:resolve -Dmaven.repo.local=/app/.m2

# Copy the rest of the project directory
COPY . /app/

# Compile and package the project
RUN mvn clean package -Dmaven.repo.local=/app/.m2

# Create a new image with the compiled project
FROM openjdk:17
WORKDIR /app

# Copy the target directory from the first stage
COPY --from=build /app/target /app/target

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "target/bookflix-1.0-SNAPSHOT-jar-with-dependencies.jar"]
