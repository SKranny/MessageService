FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /build
ADD ./target/messageService-0.0.1-SNAPSHOT.jar ./message-service.jar
EXPOSE 8088
CMD java -jar message-service.jar
