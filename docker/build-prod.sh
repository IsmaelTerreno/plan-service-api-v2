#!/bin/bash
set -euo pipefail

# Constants
ENV_FILE="docker-prod.env"
REGISTRY_URL="registry.digitalocean.com/remote-job"
# Match compose service/image naming
SERVICE_NAME="plan-service-api"
DOCKERFILE="Dockerfile"
TAG="latest"
FULL_IMAGE_NAME="${REGISTRY_URL}/${SERVICE_NAME}:${TAG}"

# Helper to fetch specific keys from the env file explicitly (not dynamic)
get_env() {
  local key="$1"
  # extract exact key=VALUE, ignoring comments and blanks
  local line
  line=$(grep -E "^${key}=" "$ENV_FILE" 2>/dev/null || true)
  if [[ -n "$line" ]]; then
    echo "${line#*=}"
  else
    echo ""  # empty if not defined
  fi
}

# Collect only the variables we explicitly support (aligned with docker-compose.yml)
SERVER_PORT_LISTENING=$(get_env SERVER_PORT_LISTENING)
CORS_ALLOW_ORIGINS=$(get_env CORS_ALLOW_ORIGINS)

# --- DB config ---
HOST_DB_CONFIG=$(get_env HOST_DB_CONFIG)
PORT_DB_CONFIG=$(get_env PORT_DB_CONFIG)
DATABASE_NAME_DB_CONFIG=$(get_env DATABASE_NAME_DB_CONFIG)
USER_NAME_DB_CONFIG=$(get_env USER_NAME_DB_CONFIG)
USER_PASSWORD_DB_CONFIG=$(get_env USER_PASSWORD_DB_CONFIG)

# --- JWT ---
JWT_SECRET_ACCESS=$(get_env JWT_SECRET_ACCESS)
JWT_SECRET_REFRESH=$(get_env JWT_SECRET_REFRESH)
JWT_URL_ENDPOINT=$(get_env JWT_URL_ENDPOINT)

# --- Test E2E defaults ---
TEST_USER_NAME=$(get_env TEST_USER_NAME)
TEST_USER_EMAIL=$(get_env TEST_USER_EMAIL)
TEST_USER_PASSWORD=$(get_env TEST_USER_PASSWORD)

# --- RabbitMQ connection ---
RABBITMQ_HOST=$(get_env RABBITMQ_HOST)
RABBITMQ_PORT=$(get_env RABBITMQ_PORT)
RABBITMQ_USERNAME=$(get_env RABBITMQ_USERNAME)
RABBITMQ_PASSWORD=$(get_env RABBITMQ_PASSWORD)

# --- Application queue names ---
INVOICE_STATUS_ON_RELATED_PLANS_RABBITMQ_QUEUE_NAME=$(get_env INVOICE_STATUS_ON_RELATED_PLANS_RABBITMQ_QUEUE_NAME)
PLANS_TO_CREATE_RABBITMQ_QUEUE_NAME=$(get_env PLANS_TO_CREATE_RABBITMQ_QUEUE_NAME)

# Build the Docker image with explicit build-args only
build_docker_image() {
  docker build \
    --build-arg SERVER_PORT_LISTENING_ARG="$SERVER_PORT_LISTENING" \
    --build-arg CORS_ALLOW_ORIGINS_ARG="$CORS_ALLOW_ORIGINS" \
    --build-arg HOST_DB_CONFIG_ARG="$HOST_DB_CONFIG" \
    --build-arg PORT_DB_CONFIG_ARG="$PORT_DB_CONFIG" \
    --build-arg DATABASE_NAME_DB_CONFIG_ARG="$DATABASE_NAME_DB_CONFIG" \
    --build-arg USER_NAME_DB_CONFIG_ARG="$USER_NAME_DB_CONFIG" \
    --build-arg USER_PASSWORD_DB_CONFIG_ARG="$USER_PASSWORD_DB_CONFIG" \
    --build-arg JWT_SECRET_ACCESS_ARG="$JWT_SECRET_ACCESS" \
    --build-arg JWT_SECRET_REFRESH_ARG="$JWT_SECRET_REFRESH" \
    --build-arg JWT_URL_ENDPOINT_ARG="$JWT_URL_ENDPOINT" \
    --build-arg TEST_USER_NAME_ARG="$TEST_USER_NAME" \
    --build-arg TEST_USER_EMAIL_ARG="$TEST_USER_EMAIL" \
    --build-arg TEST_USER_PASSWORD_ARG="$TEST_USER_PASSWORD" \
    --build-arg RABBITMQ_HOST_ARG="$RABBITMQ_HOST" \
    --build-arg RABBITMQ_PORT_ARG="$RABBITMQ_PORT" \
    --build-arg RABBITMQ_USERNAME_ARG="$RABBITMQ_USERNAME" \
    --build-arg RABBITMQ_PASSWORD_ARG="$RABBITMQ_PASSWORD" \
    --build-arg INVOICE_STATUS_ON_RELATED_PLANS_RABBITMQ_QUEUE_NAME_ARG="$INVOICE_STATUS_ON_RELATED_PLANS_RABBITMQ_QUEUE_NAME" \
    --build-arg PLANS_TO_CREATE_RABBITMQ_QUEUE_NAME_ARG="$PLANS_TO_CREATE_RABBITMQ_QUEUE_NAME" \
    -t "$FULL_IMAGE_NAME" -f "$DOCKERFILE" ..
}

# Push the Docker image
push_docker_image() {
  docker push "$FULL_IMAGE_NAME"
}

# Main script execution
build_docker_image
push_docker_image