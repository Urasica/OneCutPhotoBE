FROM gradle:8.5-jdk17 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle bootJar -x test

FROM eclipse-temurin:17
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]