FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app

# CI should pass an exact jar path
ARG JAR_FILE=apps/cv/build/libs/app.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]