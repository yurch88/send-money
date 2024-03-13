FROM maven:3.8.1-jdk-11 AS builder
COPY . /app
WORKDIR /app
RUN mvn clean package

FROM openjdk:11
WORKDIR /app
COPY --from=builder /app/target/sendmoney-2.0.0.jar /app
EXPOSE 8080
CMD ["java", "-jar", "sendmoney-2.0.0.jar"]
