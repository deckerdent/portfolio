# Portfolio - Microfrontend Portal Solution

A Java Spring and microfrontend-based portal solution with pluggable extensions, built on a self-contained systems architecture.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Creating New Apps](#creating-new-apps)
- [Manifest Configuration](#manifest-configuration)
- [Contributing](#contributing)

## Architecture Overview

This project implements a microfrontend architecture where each application is a self-contained system. The architecture consists of:

- **Portal Host**: Central application shell that orchestrates and loads microfrontends
- **Self-Contained Apps**: Independent Spring services that host their own frontend modules
- **Shared Libraries**: Reusable Java and JavaScript utilities
- **Scaffolders**: Code generators for creating new self-contained systems

### Key Architectural Principles

1. **Self-Contained Systems**: Each app in the `apps/` folder is an independent unit containing:
   - Java Spring backend service
   - One or more microfrontend modules
   - Own build configuration and dependencies

2. **Module Federation**: Microfrontends are exposed via HTTP and dynamically loaded by the portal host

3. **Monorepo Structure**: All services and libraries are managed in a single repository using Gradle and Nx

## Project Structure

```
portfolio/
├── apps/                      # Self-contained application services
│   ├── portal/               # Central portal host application
│   ├── authorization/        # Authentication & authorization service
│   ├── usermanagement/       # User management service
│   ├── notifications/        # Notifications service
│   ├── search/              # Search service
│   ├── documents/           # Document management service
│   └── development/         # Development tools and utilities
│
├── libs/                     # Shared libraries
│   ├── java/                # Java utilities and API wrappers
│   │   └── core/           # Core Java utilities
│   └── js/                  # JavaScript/TypeScript utilities
│       ├── app-generator/  # App generation utilities
│       └── browser/        # Browser-specific utilities
│           └── core/       # Core browser utilities
│
├── scaffolders/             # Yeoman-based code generators
│
├── diagrams/                # Architecture and design diagrams
│
└── gradle/                  # Gradle configuration
    └── libs.versions.toml  # Centralized dependency versions
```

### App Structure

Each app follows this structure:

```
apps/my-app/
├── src/main/
│   ├── java/                    # Spring Boot application
│   │   └── com/example/myapp/
│   │       ├── MyAppApplication.java
│   │       ├── controllers/
│   │       ├── services/
│   │       └── config/
│   └── resources/
│       ├── static/              # Microfrontend modules (HTTP-exposed)
│       │   ├── feature-a/      # Microfrontend A
│       │   │   ├── package.json
│       │   │   ├── src/
│       │   │   └── dist/       # Built assets
│       │   └── feature-b/      # Microfrontend B
│       │       ├── package.json
│       │       ├── src/
│       │       └── dist/
│       ├── manifest.yml         # Module registration config
│       └── application.yml      # Spring configuration
└── build.gradle                 # Gradle build configuration
```

## Getting Started

### Prerequisites

- **Java**: JDK 17 or higher
- **Node.js**: v18 or higher
- **pnpm**: v8 or higher (install via `npm install -g pnpm`)
- **Gradle**: 8.x (wrapper included)

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd portfolio
   ```

2. Install JavaScript dependencies:
   ```bash
   pnpm install
   ```

3. Build all Java services:
   ```bash
   ./gradlew build
   ```

4. Build frontend modules:
   ```bash
   pnpm nx run-many --target=build --all
   ```

### Running the Portal

1. Start the portal host application:
   ```bash
   ./gradlew :apps:portal:bootRun
   ```

2. Start individual services (in separate terminals):
   ```bash
   ./gradlew :apps:authorization:bootRun
   ./gradlew :apps:usermanagement:bootRun
   # ... start other services as needed
   ```

3. Access the portal:
   ```
   http://localhost:8080
   ```

## Development Workflow

### Working on a Service

1. Navigate to the app directory:
   ```bash
   cd apps/my-app
   ```

2. Run the service in development mode:
   ```bash
   ../../gradlew bootRun
   ```

3. Make changes to Java code (auto-reloads with Spring DevTools)

### Working on a Microfrontend

1. Navigate to the microfrontend directory:
   ```bash
   cd apps/my-app/src/main/resources/static/feature-a
   ```

2. Run in development mode:
   ```bash
   pnpm dev
   ```

3. The microfrontend will hot-reload on changes

### Building for Production

Build all services and frontend modules:
```bash
# Build all Java services
./gradlew build

# Build all frontend modules
pnpm nx run-many --target=build --all
```

## Creating New Apps

Use the scaffolder to generate a new self-contained system:

```bash
cd scaffolders
yo <scaffolder-name>
```

The scaffolder will create:
- Java Spring service structure
- Default microfrontend project in `src/main/resources/static`
- Gradle configuration
- Manifest file template

### Manual Creation

If creating manually, ensure your app includes:

1. **Spring Boot application** with web server configuration
2. **Static resources folder** at `src/main/resources/static/`
3. **Manifest file** at `src/main/resources/manifest.yml`
4. **Gradle build file** with necessary dependencies

## Manifest Configuration

The manifest file (`manifest.yml`) registers microfrontends with the portal host.

### Example Manifest

```yaml
service:
  name: my-app
  version: 1.0.0
  description: My application service

modules:
  - name: feature-a
    displayName: Feature A
    path: /static/feature-a
    exposed: ./Module
    route: /my-app/feature-a
    
  - name: feature-b
    displayName: Feature B
    path: /static/feature-b
    exposed: ./Module
    route: /my-app/feature-b
```

### Manifest Properties

- **service**: Service-level metadata
  - `name`: Service identifier
  - `version`: Service version
  - `description`: Service description

- **modules**: Array of microfrontend modules
  - `name`: Module identifier (unique)
  - `displayName`: Human-readable name
  - `path`: Path to static assets (relative to service root)
  - `exposed`: Webpack module federation exposed module
  - `route`: Portal route for this module

## Microfrontend Integration

### Module Federation Setup

Each microfrontend uses Webpack Module Federation to expose components:

```javascript
// webpack.config.js
module.exports = {
  plugins: [
    new ModuleFederationPlugin({
      name: 'featureA',
      filename: 'remoteEntry.js',
      exposes: {
        './Module': './src/Module.vue'
      },
      shared: ['vue', 'vue-router']
    })
  ]
};
```

### Communication Between Microfrontends

Microfrontends can communicate using:

1. **Custom Events**: Browser custom events for loose coupling
2. **Shared State**: Via shared libraries in `libs/js/`
3. **API Calls**: Backend-to-backend communication via REST APIs

## Contributing

### Code Standards

- **Java**: Follow Spring Boot best practices
- **JavaScript/TypeScript**: Follow standard ESLint configuration
- **Naming**: Use descriptive names for services and modules
- **Documentation**: Document public APIs and complex logic

### Commit Guidelines

- Use conventional commits format: `type(scope): message`
- Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(usermanagement): add user profile endpoint
fix(portal): resolve routing issue with nested routes
docs(readme): update installation instructions
```

### Pull Request Process

1. Create a feature branch from `main`
2. Make your changes with clear commits
3. Ensure all tests pass
4. Update documentation as needed
5. Submit PR with description of changes

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Webpack Module Federation](https://webpack.js.org/concepts/module-federation/)
- [Nx Monorepo](https://nx.dev/)
- [pnpm Workspace](https://pnpm.io/workspaces)

## License

[Specify your license here]

## Support

For questions or issues, please [open an issue](link-to-issues) or contact the development team.
