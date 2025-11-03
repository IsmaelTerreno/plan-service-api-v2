#!/bin/bash

# Constants
ENV_FILE="docker-prod.env"
REGISTRY_URL="registry.digitalocean.com/remote-job"
SERVICE_NAME="job-service-api"
DOCKERFILE="Dockerfile"
TAG="latest"
FULL_IMAGE_NAME="${REGISTRY_URL}/${SERVICE_NAME}:${TAG}"

# Load environment variables from file
load_env_variables() {
  export $(grep -v '^#' "$ENV_FILE" | xargs)
}

# Build the Docker image
build_docker_image() {
  docker build \
    --build-arg SERVER_PORT_LISTENING_ARG="$SERVER_PORT_LISTENING" \
    --build-arg CORS_ALLOW_ORIGINS_ARG="$CORS_ALLOW_ORIGINS" \
    --build-arg HOST_DB_CONFIG_ARG="$HOST_DB_CONFIG" \
    --build-arg PORT_DB_CONFIG_ARG="$PORT_DB_CONFIG" \
    --build-arg DATABASE_NAME_DB_CONFIG_ARG="$DATABASE_NAME_DB_CONFIG" \
    --build-arg USER_NAME_DB_CONFIG_ARG="$USER_NAME_DB_CONFIG" \
    --build-arg USER_PASSWORD_DB_CONFIG_ARG="$USER_PASSWORD_DB_CONFIG" \
    -t "$FULL_IMAGE_NAME" -f "$DOCKERFILE" ..
}

# Push the Docker image
push_docker_image() {
  docker push "$FULL_IMAGE_NAME"
}

# Main script execution
load_env_variables
build_docker_image
push_docker_image