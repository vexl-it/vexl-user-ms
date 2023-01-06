FROM openjdk:17-jdk-slim


#### --- FIX - Remove openssl vurnerability (see https://stackoverflow.com/questions/72323949/upgrade-openssl-to-resolve-dsa-5139-1-for-docker-openjdk17-0-jdk-slim-bullseye)

RUN apt-get update \
    && apt-get install -y ca-certificates wget bash \
    && apt-get -qy install perl

# Remove current openssl
RUN apt-get -y remove openssl

# This is required to run “tar” command
RUN apt-get -qy install gcc

RUN apt-get -q update && apt-get -qy install wget make \
    && wget https://www.openssl.org/source/openssl-1.1.1o.tar.gz \
    && tar -xzvf openssl-1.1.1o.tar.gz \
    && cd openssl-1.1.1o \
    && ./config \
    && make install

ENV PATH "$PATH:/usr/local/ssl/bin"

### --- END FIX

ARG CI_PROJECT_NAME
ARG CI_COMMIT_SHORT_SHA
ARG KUBE_DOMAIN
ARG PROFILE

VOLUME /tmp
ADD target/vexl-0.0.1-SNAPSHOT.jar application.jar

RUN echo "\
java \
-Dserver.use-forward-headers=true \
-Dspring.profiles.active=$PROFILE \
-Dspringdoc.swagger-server=https://$CI_PROJECT_NAME-$CI_COMMIT_SHORT_SHA.$KUBE_DOMAIN \
-jar /application.jar \
" > /run.sh

ENTRYPOINT ["/bin/sh", "/run.sh"]
