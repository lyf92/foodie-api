FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY build/libs/foodie-0.0.1-SNAPSHOT.jar $PROJECT_HOME/foodie-0.0.1-SNAPSHOT.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-jar","./foodie-0.0.1-SNAPSHOT.jar"]