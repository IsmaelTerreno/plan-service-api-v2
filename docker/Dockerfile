# Start with a base image containing Java runtime (JDK 22)
FROM openjdk:22 as builder

# Specify the working directory
WORKDIR /workspace

# Define build args for environment variables
ARG SERVER_PORT_LISTENING_ARG
ARG CORS_ALLOW_ORIGIN_ARG
ARG AUTH0_AUDIENCE_ARG
ARG AUTH0_ISSUER_URL_ARG
ARG HOST_DB_CONFIG_ARG
ARG PORT_DB_CONFIG_ARG
ARG DATABASE_NAME_DB_CONFIG_ARG
ARG USER_NAME_DB_CONFIG_ARG
ARG USER_PASSWORD_DB_CONFIG_ARG
ARG JWT_URL_ENDPOINT_ARG
ARG JWT_SECRET_ACCESS_ARG
ARG JWT_SECRET_REFRESH_ARG

# Assign env vars using the build args
ENV SERVER_PORT_LISTENING=${SERVER_PORT_LISTENING_ARG}
ENV CORS_ALLOW_ORIGIN=${CORS_ALLOW_ORIGIN_ARG}
ENV AUTH0_AUDIENCE=${AUTH0_AUDIENCE_ARG}
ENV AUTH0_ISSUER_URL=${AUTH0_ISSUER_URL_ARG}
ENV HOST_DB_CONFIG=${HOST_DB_CONFIG_ARG}
ENV PORT_DB_CONFIG=${PORT_DB_CONFIG_ARG}
ENV DATABASE_NAME_DB_CONFIG=${DATABASE_NAME_DB_CONFIG_ARG}
ENV USER_NAME_DB_CONFIG=${USER_NAME_DB_CONFIG_ARG}
ENV USER_PASSWORD_DB_CONFIG=${USER_PASSWORD_DB_CONFIG_ARG}
ENV JWT_URL_ENDPOINT=${JWT_URL_ENDPOINT_ARG}
ENV JWT_SECRET_ACCESS=${JWT_SECRET_ACCESS_ARG}
ENV JWT_SECRET_REFRESH=${JWT_SECRET_REFRESH_ARG}

# Make port SERVER_PORT_LISTENING value available to the world outside this container
EXPOSE ${SERVER_PORT_LISTENING}

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Copy the project source
COPY src src

# Package the application
RUN ./mvnw clean package -DskipTests
RUN cp /workspace/target/plan-service-api-0.0.1-SNAPSHOT.jar plan-service-api.jar

FROM openjdk:22 as runner

# Copy the jar from the builder stage
COPY --from=builder /workspace/plan-service-api.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]