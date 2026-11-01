FROM openjdk:17-jdk-alpine  # 或者你对应的 JDK 版本
WORKDIR /helloWorld
COPY target/*.jar HelloWorld.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "HelloWorld.jar"]