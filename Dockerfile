# Build application
FROM somik123/ubuntu:22-jdk21-mvn396 as builder

# Finally start building spring boot app
WORKDIR /app
COPY src src
COPY pom.xml .

RUN mvn -f ./pom.xml clean package -Dmaven.test.skip=true
# End build


# Run application
FROM somik123/ubuntu:22-jdk21

WORKDIR /usr/app

COPY storage storage
COPY --from=builder /app/target/bucket-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

# start app
CMD ["java", "-jar", "bucket-0.0.1-SNAPSHOT.jar"]
