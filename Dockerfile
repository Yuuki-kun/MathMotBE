FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN ./mvnw package

FROM openjdk:17-jdk-slim

EXPOSE 8080

# Đảm bảo sử dụng đúng alias từ bước build (build)
COPY --from=build /target/demo-1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
