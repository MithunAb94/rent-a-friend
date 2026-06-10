# ── Stage 1: Build ──
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y --no-install-recommends maven \
    && mvn dependency:go-offline -B \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Runtime ──
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8011
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/swagger-ui.html || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
