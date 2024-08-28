#!/bin/bash

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P)
mkdir -p "$SCRIPT_DIR/log"

if command -v docker &> /dev/null; then
    echo "Docker is installed, start neo4j with docker"
    CMD="docker"
elif command -v podman &> /dev/null; then
    CMD="podman"
    echo "Podman is installed, start neo4j with podman"
else
    echo "Neither Docker nor Podman is installed. Exiting."
    return 1
fi

$CMD run \
    "$@" \
    --rm \
    -p 7474:7474 \
    -p 7687:7687 \
    -v $SCRIPT_DIR/log:/log \
    -v $SCRIPT_DIR/conf:/conf \
    -v $SCRIPT_DIR/db_init:/var/lib/neo4j/db_init \
    --name neo4j \
    --env NEO4J_PLUGINS='["apoc", "apoc-extended"]' \
    --env NEO4J_server_directories_import="/" \
    --env NEO4J_AUTH=neo4j/password \
    neo4j:latest
