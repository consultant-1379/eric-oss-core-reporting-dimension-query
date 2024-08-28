#!/bin/bash

sigterm_handler() {
  if [ $pid -ne 0 ]; then
    kill -SIGTERM "$pid"
    wait "$pid"
  fi
  exit 143 # 128 + 15 -- SIGTERM
}

trap 'kill ${!}; sigterm_handler' SIGTERM

java ${JAVA_OPTS} -jar /opt/application/springboot/application.jar &
pid="$!"

while true; do
  # Check if process is still running
  kill -0 "$pid" 2>/dev/null
  if [ $? -ne 0 ]; then
    # Process is not running, shutting down the container
    sigterm_handler
  fi

  sleep 5
done