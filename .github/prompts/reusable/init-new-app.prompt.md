---
agent: agent
---

# Initialize New Self-Contained App Project

## Context
- Project is a Java Spring and microfrontend-based portal solution
- New apps are self-contained systems in the /apps directory
- Each app contains a Spring Webflux service and one or more Vue microfrontends
- Build system: Gradle with version catalog (gradle/libs.versions.toml)
- Package manager: pnpm with workspace catalogs (pnpm-workspace.yaml)
- Frontend: Vue 3 + Vite with module federation
- API: OpenAPI-driven with generated DTOs and interfaces
- Testing: JUnit with Spring Boot Test

## Objective
Initialize a complete self-contained app project with Spring Webflux backend, OpenAPI specification, generated API contracts, Vue microfrontend, and comprehensive tests.

## Requirements

### Project Setup
- [ ] Accept app name as parameter (e.g., `appName`)
- [ ] Create new directory at `apps/${appName}` if it not already exists. If it exists and is not empty, abort with error.
- [ ] Initialize Gradle project using Groovy DSL (not Kotlin)
- [ ] Configure project in root `settings.gradle` to include new app

### Gradle Configuration
- [ ] Add dependencies from `gradle/libs.versions.toml` to `apps/${appName}/build.gradle`
  - spring-webflux
  - spring-data-jpa
  - spring-security-oauth2-client
  - spring-security-oauth2-resource-server
  - spring-validation
  - spring-cache
  - spring-actuator
  - spring-data-redis
  - spring-websocket
  - spring-messaging
  - postgresql
  - springdoc-openapi-starter-webflux-ui
  - spring-test
  - junit-jupiter
  - testcontainers
  - testcontainers-junit
  - testcontainers-postgresql
  - mapstruct
  - mapstruct-processor
  - lombok
  - resilience4j-reactor
  - jackson-databind
- [ ] Configure OpenAPI Generator plugin in build.gradle
  - Input: `src/main/resources/openapi/api.yml`
  - Output: generated sources in `build/generated/openapi`
  - Generate server stubs (Spring interfaces) and DTOs
- [ ] Configure annotation processors (Lombok, MapStruct)
- [ ] Configure test dependencies and setup

### Spring Webflux Application
- [ ] Create main application class with `@SpringBootApplication` in `src/main/java/com/${appName}`
- [ ] Create `src/main/resources/application.yml` with:
  - Server port configuration (use incremental port strategy or default 8080)
  - Spring security OAuth2 settings (disabled by default)
  - Spring cache settings (disabled by default)
  - Spring data JPA/PostgreSQL settings (blank connection strings)
  - Spring Redis settings (disabled by default)
  - Actuator health endpoint enabled
  - Swagger UI enabled at `/swagger-ui`
  - Spring Cloud Gateway route: `/apps/**` → `/static/**`
  - All "enable" properties set to false for features not immediately needed
  
### OpenAPI Specification
- [ ] Create `src/main/resources/openapi/api.yml` with:
  - OpenAPI 3.0 spec
  - `/api/hello` GET endpoint returning `{ "message": "Hello World" }`
  - Define `HelloResponse` schema with `message` field
- [ ] Execute OpenAPI generator to create interfaces and DTOs
- [ ] Implement controller for `/api/hello` endpoint using generated interface

### Manifest File
- [ ] Create `src/main/resources/manifest.yml` with:
  - Service name: `${appName}`
  - Service version: `1.0.0`
  - Modules section (initially empty, ready for microfrontends)

### Testing
- [ ] Write test: Application context loads successfully
- [ ] Write test: Swagger UI is reachable at `/swagger-ui`
- [ ] Write test: Static folder serves files at `/apps/${appName}/`
- [ ] Write test: Hello World endpoint returns expected response
- [ ] Run `./gradlew :apps:${appName}:test` to verify all tests pass
- [ ] Run `./gradlew :apps:${appName}:clean :apps:${appName}:build` for full build

### Frontend Setup
- [ ] Create `src/main/resources/static/${appName}` directory
- [ ] Initialize Vue 3 + Vite app using `pnpm nx g @nx/vue:application`
  - App name: `${appName}`
  - Location: `apps/${appName}/src/main/resources/static/${appName}`
  - Bundler: Vite
- [ ] Install frontend dependencies using pnpm catalog references:
  - vue (catalog:default)
  - vite (catalog:default)
  - @module-federation/vite (catalog:default)
  - @module-federation/runtime (catalog:default)
  - vue-router (catalog:default)
  - @shoelace-style/shoelace (catalog:default)
  - typescript (catalog:default)
  - i18next (catalog:default)
  - axios (catalog:default)
- [ ] Create `tsconfig.json` for TypeScript configuration
- [ ] Create `vite.config.ts` with:
  - Module Federation plugin configuration
  - Expose `./Module` entry point
  - Shared dependencies (vue, vue-router)
  - Build output to `dist/`
- [ ] Create entrypoint `src/main.ts` with Vue app initialization
- [ ] Create root component `src/App.vue` with basic template
- [ ] Create module export `src/Module.ts` for module federation
- [ ] Run `pnpm --filter ${appName} dev` to start dev server and verify startup

### Integration & Verification
- [ ] Build frontend: `pnpm --filter ${appName} build`
- [ ] Copy frontend build to static resources (configure Gradle task if needed)
- [ ] Run Spring Boot app: `./gradlew :apps:${appName}:bootRun`
- [ ] Verify all endpoints are accessible
- [ ] Run all tests: `./gradlew :apps:${appName}:test`
- [ ] Clean build: `./gradlew :apps:${appName}:clean :apps:${appName}:build`

## Technical Specifications
- Java: 17+
- Spring Boot: 3.x (from catalog)
- Gradle: Use global installation, Groovy DSL
- Build file: `build.gradle` (not `build.gradle.kts`)
- Package structure: `com.${appName}` or similar
- OpenAPI version: 3.0+
- Vue: 3.x
- Vite: 5.x
- TypeScript: 5.x
- Module Federation: @module-federation/vite

## Constraints
- Must use versions from `gradle/libs.versions.toml` and `pnpm-workspace.yaml` catalogs
- Must not hardcode versions in build.gradle or package.json
- Must follow existing project structure conventions
- Must use Groovy DSL for Gradle (not Kotlin)
- Frontend must be buildable and servable via Spring static resources
- All tests must pass before considering project initialized

## Success Criteria
- [ ] Gradle project builds successfully without errors
- [ ] All JUnit tests pass
- [ ] Swagger UI accessible and shows Hello World endpoint
- [ ] Hello World endpoint returns correct response
- [ ] Static folder serves frontend assets
- [ ] Vue app starts in dev mode without errors
- [ ] Frontend builds without errors
- [ ] Application runs with `./gradlew bootRun`
- [ ] Project structure matches existing apps conventions
- [ ] manifest.yml is properly formatted and ready for module registration

## Examples

### build.gradle structure
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version libs.versions.spring.boot
    id 'io.spring.dependency-management' version '1.1.0'
    id 'org.openapi.generator' version libs.versions.openapi.generator
}

dependencies {
    implementation libs.spring.webflux
    implementation libs.spring.data.jpa
    implementation libs.postgresql
    // ... other dependencies from catalog
    
    compileOnly libs.lombok
    annotationProcessor libs.lombok
    annotationProcessor libs.mapstruct.processor
    
    testImplementation libs.spring.test
    testImplementation libs.junit.jupiter
}

openApiGenerate {
    inputSpec = "$projectDir/src/main/resources/openapi/api.yml"
    outputDir = "$buildDir/generated/openapi"
    apiPackage = "com.${appName}.api"
    modelPackage = "com.${appName}.model"
    generatorName = "spring"
}
```

### application.yml structure
```yaml
server:
  port: 8080

spring:
  application:
    name: ${appName}
  
  security:
    oauth2:
      client:
        registration: {}
      resourceserver:
        jwt:
          enabled: false
  
  cloud:
    gateway:
      routes:
        - id: static-route
          uri: no://op
          predicates:
            - Path=/apps/**
          filters:
            - RewritePath=/apps/(?<path>.*), /static/$\{path}
  
  datasource:
    url: 
    username: 
    password: 
  
  jpa:
    hibernate:
      ddl-auto: none
  
  cache:
    enabled: false
  
  data:
    redis:
      enabled: false

management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      enabled: true

springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui
```

### OpenAPI spec example
```yaml
openapi: 3.0.0
info:
  title: ${appName} API
  version: 1.0.0

paths:
  /api/hello:
    get:
      operationId: getHello
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/HelloResponse'

components:
  schemas:
    HelloResponse:
      type: object
      properties:
        message:
          type: string
```

### manifest.yml structure
```yaml
service:
  name: ${appName}
  version: 1.0.0
  description: ${appName} service

modules: []
```

### vite.config.ts structure
```typescript
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import federation from '@module-federation/vite';

export default defineConfig({
  plugins: [
    vue(),
    federation({
      name: '${appName}',
      filename: 'remoteEntry.js',
      exposes: {
        './Module': './src/Module.ts'
      },
      shared: {
        vue: {},
        'vue-router': {}
      }
    })
  ],
  build: {
    outDir: 'dist'
  }
});
```

## Additional Notes
- Use placeholder values for database/Redis connection strings
- Disable security features by default for easier initial development
- Ensure static resource serving is properly configured in Spring
- Frontend dev server should run on a different port than Spring
- Consider adding a README.md in the app directory with setup instructions
- OpenAPI generator may require explicit source set configuration in Gradle
- Module federation requires specific Vite configuration—test thoroughly
- If port conflicts occur, use an incremental port strategy or environment variables
- Consider adding a Gradle task to build frontend and copy to static resources
- Testcontainers tests require Docker to be running
- May need to add CORS configuration for frontend development
