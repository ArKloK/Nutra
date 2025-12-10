<div class="changelog-header">

# Changelog

</div>

All notable changes to this project will be documented in this file.

---

<div class="version-header">

## [v0.0.3-alpha] - 2025-12-10

</div>

<div class="section-header">

### Added

</div>

- **Database Persistence Layer**: Complete repository and service architecture
  - `ConsultationRepository` and `ConsultationService` with queries for weekly/daily consultation retrieval
  - `PatientRepository` and `PatientService` with search functionality by name
  - Transaction management with `@Transactional` annotation
- **Interactive Calendar Consultations**: Right-click context menu system for creating consultations
  - Click derecho on empty day shows "Nueva Consulta" option with date pre-selection
  - Consultation cards displayed on their scheduled days
  - Cards show time (HH:mm), patient name, and consultation reason
- **Consultation Cards**: Visual cards with hover effects and context menu
  - Click izquierdo opens consultation details (pending implementation)
  - Click derecho shows "Eliminar" option with confirmation dialog
  - Auto-adjusting text wrapping based on container width
  - Smooth shadow effects and responsive scaling
- **Consultation Form Enhancements**: 
  - Hour and minute spinners for precise time selection
  - Patient ComboBox loaded from database with custom cell factory
  - Pre-selected date when created from calendar right-click
  - Complete field validation (patient, date, reason)
  - Auto-reload calendar after saving consultation
- **Patient Save Functionality**: Complete database integration
  - Required field validation (first name, last name, birth date)
  - Optional field handling (email, phone, address, weight, files)
  - Weight validation (positive numbers only)
  - Success confirmation with patient name and ID
  - Error handling with user-friendly messages
- **Context Menu Styling**: Custom styles for context menus
  - `.context-menu-calendar`: White background with bronze border and shadow
  - `.menu-item-new-consultation`: Neutral style with beige hover effect
  - `.menu-item-delete`: Bronze text with inverted hover (bronze background, white text)
- **Event Propagation Control**: `event.consume()` prevents menu overlap when clicking on consultation cards

<div class="section-header">

### Changed

</div>

- **HomeController**: Major refactoring with consultation management
  - Added `ConsultationService` dependency injection
  - Day containers (`mondayContent`, `tuesdayContent`, etc.) mapped with `fx:id` references
  - `showNewConsultation()` now accepts optional `LocalDate` parameter
  - Week navigation buttons reload consultations automatically
  - Added methods: `initializeDayContainers()`, `setupContextMenus()`, `loadConsultationsForWeek()`, `createConsultationCard()`, `deleteConsultation()`, `reloadConsultations()`
- **ConsultationController**: Enhanced with service integration
  - Added `ConsultationService` and `PatientService` dependencies
  - Hour/minute spinners initialization in `initialize()` method
  - `setPreselectedDate()` method for calendar integration
  - Complete `saveConsultation()` implementation with validation and database persistence
  - Improved error handling with try-catch blocks
- **PatientController**: Database integration implemented
  - Added `PatientService` dependency injection
  - Complete `savePatient()` implementation with field validation
  - Optional fields handling (email, phone, address, medical records, photos)
  - Weight parsing with error handling
  - Success/error alerts with detailed messages
- **home.fxml**: Added `fx:id` attributes to all day containers
  - 7 ScrollPanes: `mondayContainer` through `sundayContainer`
  - 7 VBox: `mondayContent` through `sundayContent`
- **consultation.fxml**: Added time selection controls
  - Hour `Spinner` (0-23) with `fx:id="hourSpinner"`
  - Minute `Spinner` (0-59) with `fx:id="minuteSpinner"`
  - Imported `javafx.scene.control.Spinner`
- **styles.css**: New consultation card and context menu styles
  - `.consultation-card`: Beige background with border, shadow, and hover effects
  - `.consultation-time`: Bold time display
  - `.consultation-patient`: Patient name style
  - `.consultation-reason`: Subtle reason text
  - Context menu styles with hover effects

<div class="section-header">

### Fixed

</div>

- **Consultation Card Width**: Removed `setMaxWidth(Double.MAX_VALUE)` that caused cards to overflow container
- **Card Text Wrapping**: Dynamic `wrappingWidth` binding adjusts to container width changes
- **Event Propagation**: Context menus now properly isolated (card vs. day container) using `event.consume()`
- **Empty Service Files**: Recreated `PatientService.java` and fixed corrupted `ConsultationRepository.java`

---

<div class="version-header">

## [v0.0.2-alpha] - 2025-11-05

</div>

<div class="section-header">

### Added

</div>

- **Recipe Creation Form**: Comprehensive form for creating new recipes with all required fields
  - Basic information section: title, description, preparation time
  - ComboBox for selecting existing ingredients with direct add button
  - "+" button for creating new ingredients on-the-fly
  - Ingredients table with automatic column resizing (CONSTRAINED_RESIZE_POLICY)
  - Right-click context menu on table rows for deleting ingredients
  - Instructions section for step-by-step preparation
  - Additional notes section
- **Ingredient Management System**: Complete ingredient creation and selection workflow
  - Modal dialog for creating new ingredients with detailed nutritional information
  - Auto-selection of newly created ingredient in recipe ComboBox
  - Support for macronutrients: calories, carbohydrates, sugar, protein, fiber, fats (saturated/unsaturated)
  - Dynamic vitamin and mineral entry system with add/remove capabilities
  - Choice dialog for selecting existing ingredients
  - Ingredient details dialog for specifying amount, unit, and notes when adding to recipe
- **RecipeController**: New controller with advanced ingredient management
  - Interactive ingredients table with real-time add/remove
  - Integration with ingredient creation workflow
  - Validation for required fields (title, instructions)
  - Support for both existing and new ingredient selection
- **IngredientController**: Standalone controller for ingredient creation
  - Dynamic form fields for vitamins and minerals
  - Comprehensive nutritional data input
  - Validation and data persistence preparation
- **Recipe View Integration**: Added recipe view to home screen with smooth fade transitions
- **Ingredients Table Styling**: Custom CSS styles for interactive table with hover effects and alternating row colors
- **Remove Button Component**: Reusable remove button style for table actions and dynamic entries
- **Custom Alert Dialogs**: Styled alert and confirmation dialogs matching application theme
- **Patient Registration Form**: Complete form for registering new patients with all required fields
  - Personal information section: first name, last name, birth date
  - Contact information section: email, phone, address
  - Medical information section: current weight, medical record file upload
  - Photo upload functionality with file browser
- **PatientController**: New controller with file browsing capabilities for medical records (PDF, Word) and patient photos (images)
- **Patient View Integration**: Added patient view to home screen with smooth fade transitions
- **Menu Integration**: Connected "Nuevo Paciente" and "Nueva Receta" menu items to respective forms
- **Secondary Button Style**: New CSS style for browse buttons and secondary actions
- **Form Validation**: Basic validation for required fields across all forms

<div class="section-header">

### Changed

</div>

- **HomeController**: Updated to handle four views (calendar, consultation, patient, recipe) with improved view switching logic
- **View Transition System**: Enhanced switchToView method to dynamically detect current visible view
- **Ingredient Model**: Updated to support Dictionary/Map structures for vitamins and minerals with proper JPA persistence

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

