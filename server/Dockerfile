FROM openjdk:17.0.2
VOLUME /tmp
WORKDIR /app
EXPOSE 8080

COPY ./build/libs/server-3.13.8.jar server.jar
ENTRYPOINT ["java","-jar","/app/server.jar"]
