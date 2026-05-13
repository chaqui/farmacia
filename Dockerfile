# Dockerfile para Producción
# Nota: El JAR se compila FUERA de Docker en GitHub Actions
# ya que tiene dependencias privadas de GitHub Packages.

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Instalar dependencias mínimas
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN useradd -m -u 1000 appuser

# Crear directorios de la aplicación
RUN mkdir -p /app/db /app/uploads/photos && \
    chown -R appuser:appuser /app

# El JAR es copiado desde el contexto (compilado en CI)
COPY --chown=appuser:appuser target/*.jar app.jar

USER appuser

EXPOSE 8085

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
