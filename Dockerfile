FROM gradle:8.6-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM amazoncorretto:21

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/taskl-0.0.1-SNAPSHOT.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]
