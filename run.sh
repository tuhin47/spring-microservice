#!/bin/bash
dos2unix mvnw
./mvnw spring-boot:run -Ddebug -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEUBG_PORT" &
while true; do
  inotifywait -e modify,create,delete,move -r ./src/ && ./mvnw compile
done