FROM openjdk:11
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew build

COPY src/main/resources/application.yml /app/application.yml

CMD ["java", "-jar", "build/libs/3o3-server-0.0.1-SNAPSHOT.jar"]