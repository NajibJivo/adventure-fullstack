# ---- Build stage: bygger JAR med Maven wrapper (JDK 21) ----
FROM openjdk:21-jdk-slim AS build
WORKDIR /app

# Kopiér Maven wrapper + pom først for bedre cachelag
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw

# Hent dependencies + plugins offline (hurtigere efterfølgende builds)
RUN ./mvnw -B -q dependency:go-offline

# Kopiér kildekode sidst (invalidér kun dette lag ved kodeændringer)
COPY src ./src

# Byg JAR (skip tests i container-build for hastighed)
RUN ./mvnw -B -q clean package -DskipTests


# ---- Runtime stage: kør app’en i et slankt JRE 21 image ----
FROM eclipse-temurin:21-jre

# Installer curl til healthcheck og ryd APT caches
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Opret non-root bruger (bedre sikkerhed)
RUN addgroup --system appuser && adduser --system --group appuser

# Kopiér JAR fra build-stage og sæt korrekt ejer
COPY --chown=appuser:appuser --from=build /app/target/*.jar /app/app.jar

USER appuser
EXPOSE 8080

# Healthcheck: ping den rigtige route (/activities)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/activities || exit 1

# Start app’en
ENTRYPOINT ["java","-jar","/app/app.jar"]
