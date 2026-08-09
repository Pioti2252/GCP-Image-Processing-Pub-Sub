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

RUN groupadd --system --gid 10001 appgroup \
    && useradd --system \
       --uid 10001 \
       --gid 10001 \
       --home-dir /app \
       --shell /usr/sbin/nologin \
       appuser

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

RUN mkdir -p /data/uploads /data/processed \
    && chown -R 10001:10001 /app /data

USER 10001:10001

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]