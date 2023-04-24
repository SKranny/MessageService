# Java Spring project "Social networking site"
### MessageService

## Description
This service is responsible for messenger within the social network.
Here the functionality of dialogs with other users and group chats is implemented.

## Service Technologies
- Java version 11
- Spring Framework
- WebSocket
- Spring Data JPA
- PostgreSQL
- Spring Security
- Lombock
- Flyway
- Spring Cloud OpenFeign
- Spring Cloud Netflix Eureka
- JWT(JsonWebToken)
- Nexus repository
- Kafka
- Swagger OpenApi
- JUnit
## Technical Description
### How to run the application on your device:
1. (Pre-configuring the PostgreSQL database) Specify in the application.yaml file, or in the environment variables in your IDE, the required application configuration parameters to run:
    - MESSAGE_SERVICE_DATABASE_URL (The address of the database your application connects to. You should specify it manually if you are not going to use default postgresql url: jdbc:postgresql://localhost:5432/message_service)
    - MESSAGE_SERVICE_DATABASE_USER (Username for the database. Specify it manually if you do not intend to use the default username: postgres)
    - MESSAGE_SERVICE_DATABASE_PASSWORD (Password for the database)
    - KAFKA_HOST(The address of the Kafka broker. The default host is localhost:9092. Replace it if you are not going to use the default)
    - SECRET_KEY (Your application's secret key. This is needed to protect your service which uses JWT technology)
    - EUREKA_URI (Address of your Eureka server. Specify it if you are not going to use the default address: http://localhost:8081/eureka)
2. Run the file MessageApplication.java.

The application uses port 8088.

If you want to see the full functionality of the service go to the controller folder inside the project, where each method will have a brief description of its functionality.
