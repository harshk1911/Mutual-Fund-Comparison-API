FROM openjdk:8
EXPOSE 8080
ADD target/spring-boot-docker.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]