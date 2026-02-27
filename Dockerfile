FROM maven:3.9.12-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="dev@example.com"
LABEL description="Library Management Application"
LABEL version="1.0.0"

RUN groupadd --system --gid 1000 appuser && \
    useradd --system --gid appuser --uid 1000 --create-home appuser

RUN mkdir -p /app /app/logs /app/config && \
    chown -R appuser:appuser /app

COPY --from=builder --chown=appuser:appuser /build/target/*.jar /app/application.jar

RUN apt-get update && apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

USER appuser

WORKDIR /app

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/heapdump.hprof" \
    SERVER_PORT=8080 \
    SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/library \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_JPA_SHOW_SQL=false \
    SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect \
    SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true \
    LOGGING_LEVEL_COM_YOURPACKAGE=INFO \
    LOGGING_FILE_PATH=/app/logs/application.log \
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics \
    TZ=UTC

EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
    CMD curl -f http://localhost:${SERVER_PORT}/healthz || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]