<div class="changelog-header">

# Changelog

</div>

All notable changes to this project will be documented in this file.

---

<div class="version-header">

## [v0.0.1-alpha] - 2025-10-30

</div>

<div class="section-header">

### Added

</div>

- **Load Screen**: Minimalist loading screen with animated logo, indeterminate progress bar, and smooth transitions
- **Home Screen**: Interactive weekly calendar with navigation buttons, menu bar (Consultations, Recipes, Patients), and smooth week transitions
- **Consultation Form**: Integrated panel for creating new consultations with patient info, measurements (auto-calculated BMI), and notes
- **JavaFX + Spring Boot Integration**: Complete setup with FxmlView enum, StageManager, and dependency injection support
- **Database Configuration**: PostgresSQL setup with HikariCP connection pool
- **Data Models**: Patient, Consultation, Recipe, Ingredient, and RecipeIngredient entities with JPA relationships
- **UI Constants**: Centralized color palette (`#626D71`, `#CDCDC0`, `#DDBC95`, `#B38867`) and reusable constants
- **Controllers**: LoadController, HomeController, and ConsultationController with view management and animations
- **Styling System**: Comprehensive CSS files (styles.css, load.css) with consistent design across the application

<div class="section-header">

### Changed

</div>

- Replaced hardcoded values with UIConstants for better maintainability
- Implemented modular architecture with clear separation of concerns
- **Consultation Form**: Simplified to include only fields present in the Consultation model (patient, dateTime, weight, reason, notes)
- Removed unnecessary fields from consultation form: separate time field, duration, height, BMI calculation, waist/hip measurements, and body fat percentage
- Updated ConsultationController to match simplified form structure

---

<style>
.changelog-header h1 {
    color: #626D71;
    border-bottom: 1px solid #CDCDC0;
    padding-bottom: 10px;
}

.version-header h2 {
    color: #626D71;
    border-bottom: 1px solid #DDBC95;
    padding-bottom: 8px;
}

.section-header h3 {
    color: #626D71;
    font-weight: 500;
}

.footer {
    margin-top: 30px;
    color: #626D71;
}
</style>

