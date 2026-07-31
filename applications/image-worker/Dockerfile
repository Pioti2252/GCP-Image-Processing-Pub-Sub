FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd --system appgroup \
    && useradd --system --gid appgroup --home-dir /app appuser

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN mkdir -p /data/uploads /data/processed \
    && chown -R appuser:appgroup /app /data

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]