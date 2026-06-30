# AGENTS.md

Compact guide for working in Nutra. Read this before editing.

## Stack
JavaFX 23.0.1 desktop app on Spring Boot 3.5.6 + SQLite (Hibernate 6.6.29). Build requires **JDK 25** (`pom.xml` `<java.version>25</java.version>` — README's "Java 17" line is stale).

## Commands (Maven Wrapper — do not assume system `mvn`)
- Run app: `./mvnw clean javafx:run` (Linux/macOS) / `mvnw.cmd clean javafx:run` (Windows). Main class: `com.arklok.nutra.NutraApplication`.
- Run via Spring: `./mvnw spring-boot:run` (warning suppressions already set in `pom.xml`).
- Tests: `./mvnw test` — only one test (`NutraApplicationTests.contextLoads`, a `@SpringBootTest`). It boots the full Spring context and creates the SQLite DB at `${user.home}/.nutra/nutra-bdd.sqlite`; it fails if that dir is not writable.
- Single test: `./mvnw test -Dtest=NutraApplicationTests`.
- Package (Windows only): `package.bat` (builds jar + `jlink` runtime into `target/dist/Nutra`). `create-zip.bat` zips it; `clean.bat` wipes `target`.

## Bootstrap (non-obvious)
`NutraApplication` (`@SpringBootApplication`, main) starts Spring, then calls `Application.launch(NutraFxApplication)`. `NutraFxApplication.init()` builds its own Spring context too. Spring owns the JavaFX `Stage` via `PrimaryStageHolder`/`StageManager`; startup flow is `FxmlView.LOAD` → `HOME`.

## UI / FXML conventions
- **FXML controllers are Spring beans** — `FxmlLoader` and `HomeController` set `loader.setControllerFactory(applicationContext::getBean)`. Any class referenced as `fx:controller` must be a Spring stereotype (`@Component` etc.).
- **All FXML paths live in `com.arklok.nutra.constants.UIConstants`** and must be referenced via constant — hardcoded paths were intentionally removed (commit `717cd3a`). Add new views there first.
- Top-level scenes: add an enum value to `FxmlView`, then use `StageManager.switchScene(view)`.
- Inner panels inside `home.fxml`: loaded manually in `HomeController` (FXMLLoader + `setControllerFactory` + fade transition). Any inner controller that needs the home implements `com.arklok.nutra.interfaces.IController` (`setHomeController`).
- Stylesheet is `/styles/styles.css`; apply style classes there, not inline.

## Persistence / data
- SQLite file: `${user.home}/.nutra/nutra-bdd.sqlite` (dir auto-created by `DataSourceConfig`). No migrations — `spring.jpa.hibernate.ddl-auto=update`.
- Dialect: `org.hibernate.community.dialect.SQLiteDialect` (from `hibernate-community-dialects`).
- Consultations are `FetchType.LAZY`. To use them off the JPA session call `PatientService.findByIdWithConsultations` / `ConsultationService.findByIdWithPatient` (transactional) or you'll hit `LazyInitializationException`.
- `Patient.@PreRemove` copies the patient's name onto their consultations before deletion — keep this behavior when touching deletion paths.

## Gotchas
- `sun.misc.Unsafe::allocateMemory` warnings on Java 25 + JavaFX 23 are expected and safe; suppressions already in `pom.xml`.
- Root log level is `ERROR` (`application.properties`) — set lower levels there when debugging.
- UI text, code comments, and git commits are in **Spanish** (`es-ES` locale).
- README references `CONFIGURACION_INTELLIJ.md`, which is not in the repo.

## Git
Free-form commit messages in the style `Version <x.y>-alpha. <short description>` (English), matching existing history. No documented branch/PR/release rules.