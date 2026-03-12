# syntax=docker/dockerfile:1.7

FROM node:20-bookworm AS frontend-builder
WORKDIR /workspace

COPY package.json pnpm-lock.yaml pnpm-workspace.yaml nx.json tsconfig.base.json tsconfig.json ./
COPY libs ./libs
COPY apps ./apps

RUN corepack enable
RUN pnpm install --frozen-lockfile
RUN pnpm --filter cv build

FROM gradle:8.9.0-jdk17 AS backend-builder
WORKDIR /workspace

COPY . .
COPY --from=frontend-builder /workspace/apps/cv/src/main/resources/static/dist ./apps/cv/src/main/resources/static/dist

RUN chmod +x ./gradlew
RUN ./gradlew :apps:cv:bootJar -PskipFrontend --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=backend-builder /workspace/apps/cv/build/libs/*.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
