# ── Etapa 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Token para descargar paquetes privados de GitHub Packages
ARG PAT
ENV PAT=${PAT}

# Copiar wrapper y pom primero para aprovechar cache de capas
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Descargar dependencias (cacheado mientras pom.xml no cambie)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B -q \
    -s .mvn/settings.xml || true

# Copiar código fuente
COPY src/ src/

# Compilar y empaquetar (sin tests)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package \
      -DskipTests \
      -Dmaven.test.skip=true \
      -Dmaven.javadoc.skip=true \
      -s .mvn/settings.xml \
      -B -q

# ── Etapa 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Directorio para la base de datos SQLite (volume montable)
RUN mkdir -p db uploads/photos && chown -R appuser:appgroup /app

# Copiar jar desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

USER appuser

EXPOSE 8085

ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
