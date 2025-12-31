# Nutra

Nutra is a desktop application designed for client management, specifically tailored for nutrition professionals. Its main goal is to help nutritionists organize, track, and manage client information efficiently, streamlining daily tasks and enhancing personalized care.

## Main Libraries and Exact Versions

The following are the most relevant libraries and plugins used in this project, along with their exact versions as specified in the `pom.xml`:

- **Java**: 25
- **Spring Boot**: 3.5.6
- **JavaFX**: 23.0.1
- **Logback**: 1.5.13 (security patched)
- **Hibernate**: 6.6.29.Final
- **SQLite JDBC**: 3.41.2.2

These versions ensure compatibility and stability for the application. For further details, see the `pom.xml` file.

## Technologies Used

- **Java**: Main programming language.
- **JavaFX**: Framework for building modern, responsive graphical user interfaces.
- **Spring Boot**: Framework for simplifying configuration, development, and deployment, providing dependency injection and component management.
- **SQLite**: Lightweight, embedded database for local data storage.
- **Hibernate**: ORM for data persistence management.
- **Maven**: Tool for dependency management and project build automation.

## Architecture Overview

The application follows a modular and layered architecture, combining the strengths of JavaFX for the user interface and Spring Boot for backend and dependency management. The main architectural principles are:

- **Separation of Concerns**: The application logic, data access, and user interface are clearly separated, making the codebase easier to maintain and extend.
- **Dependency Injection**: Spring Boot is used to manage dependencies and application components, promoting loose coupling and testability.
- **MVC Pattern**: The Model-View-Controller pattern is applied, where JavaFX FXML files define the views, controllers handle user interactions, and models represent the data.
- **Persistence Layer**: Data is stored locally using SQLite, with Hibernate as the ORM to simplify database operations.
- **Event-Driven UI**: The user interface responds to user actions and system events, providing a dynamic and interactive experience.

This architecture ensures scalability, maintainability, and a clear separation between the graphical interface and the business logic.

## Key Features

- Intuitive and modern interface for client management.
- Secure, local data storage.
- Easy installation and usage, with no need for external servers.
- Modular and scalable architecture.

## Getting Started

Follow these steps to run the project after cloning the repository:

1. **Clone the repository** to your computer.
2. **Ensure you have Java installed** (Java 17 or higher is recommended).
3. **No need to install Maven manually**. The project includes Maven Wrapper scripts:
   - On **Windows**, use `mvnw.cmd`.
   - On **Linux/macOS**, use `./mvnw`.
4. To build and run the application, open a terminal in the project directory and execute:
   - On Windows:
     ```
     mvnw.cmd clean javafx:run
     ```
   - On Linux/macOS:
     ```
     ./mvnw clean javafx:run
     ```
5. Alternatively, you can build the project and run the generated JAR file from the `target` directory.

## Important Notes

- The application is intended for desktop environments.
- Data is stored locally in an SQLite database, located in the user's directory.
- JavaFX libraries must be properly configured in your environment to run the application.

### Known Warnings (Java 25 + JavaFX 23)

When running with Java 25, you may see warnings about `sun.misc.Unsafe::allocateMemory`. This is a **known issue** with JavaFX 23 and is completely safe to ignore:

- ✅ **Safe to ignore**: Does not affect functionality
- ✅ **Temporary**: Will be fixed in future JavaFX versions
- ✅ **Expected behavior**: JavaFX uses low-level APIs for graphics rendering

**To run without warnings (IntelliJ IDEA)**:
1. Run → Edit Configurations...
2. Add to VM options: `-XX:+UnlockDiagnosticVMOptions -XX:+SuppressTerminallyDeprecatedWarnings`

**To run without warnings (Maven)**:
```bash
# Already configured in pom.xml, just run:
mvnw.cmd spring-boot:run
```

For more details, see `CONFIGURACION_INTELLIJ.md`.

## License

This project is distributed under a **GPL-3.0** license.
