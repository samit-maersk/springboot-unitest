FROM ghcr.io/graalvm/jdk:22.2.0
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]