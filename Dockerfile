FROM maven:3-jdk-11 AS mvn
WORKDIR /tmp
COPY ./pom.xml /tmp/incentives/pom.xml
COPY ./src /tmp/incentives/src
WORKDIR /tmp/incentives
RUN mvn clean install -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk11:alpine
ENV FOLDER=/tmp/incentives/target
ENV APP=incentives-1.0.0.jar
ARG USER=incentives
ARG USER_ID=3002
ARG USER_GROUP=incentives
ARG USER_GROUP_ID=3002
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  /home/${USER}/app
RUN chown ${USER}:${USER_GROUP} /home/${USER}/app
RUN mkdir indexes && chown ${USER}:${USER_GROUP} indexes
COPY --from=mvn --chown=incentives:incentives ${FOLDER}/${APP} /home/${USER}/app/incentives.jar

USER incentives
CMD ["java", "-jar", "incentives.jar"]