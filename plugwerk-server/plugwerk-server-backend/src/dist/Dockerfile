FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S plugwerk && adduser -S plugwerk -G plugwerk
USER plugwerk

COPY plugwerk-server-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Xms256m", "-Xmx512m", \
  "-XX:+UseG1GC", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
