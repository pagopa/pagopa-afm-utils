ARG JAVA_VERSION=11
# This image additionally contains function core tools – useful when using custom extensions
FROM mcr.microsoft.com/azure-functions/java:4.0-java$JAVA_VERSION
RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y nodejs \
    npm install -g azure-functions-core-tools@3 --unsafe-perm true

#
# Build
#
FROM maven:3.8.4-jdk-11-slim as buildtime
WORKDIR /build
COPY . .
RUN mvn clean package


FROM adoptopenjdk/openjdk11:alpine-jre as builder
COPY --from=buildtime /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract


FROM ghcr.io/pagopa/docker-base-springboot-openjdk11:v1.0.1@sha256:bbbe948e91efa0a3e66d8f308047ec255f64898e7f9250bdb63985efd3a95dbf
COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./


EXPOSE 8080
