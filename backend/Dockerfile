FROM openjdk:11-jre-slim

ENV SERVER_PORT="8091"

ARG DEPENDENCY=./target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /fetch/lib
COPY ${DEPENDENCY}/META-INF /fetch/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /fetch

EXPOSE ${SERVER_PORT:-8091}/tcp
EXPOSE 5005:5005

ENTRYPOINT [ "java" ]

CMD [ "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=*:5005", "-Dspring.profiles.active=https", "-Dspring.config.additional-location=/config/", "-cp", "fetch:fetch/lib/*", "com.semmtech.LacesFetchBackendApplication" ]
