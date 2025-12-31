<div class="changelog-header">

# Changelog

</div>

All notable changes to this project will be documented in this file.

---

<div class="version-header">

## [v0.0.5-alpha] - 2025-12-31

</div>

<div class="section-header">

### Added

</div>

- **ImageLoader Helper Class**: New utility class for robust image loading and validation
  - Format validation for JavaFX-compatible image formats (PNG, JPG, JPEG, GIF, BMP)
  - `loadImage()` method with comprehensive error handling and detailed logging
  - `isSupportedFormat()` method to check if a file format is supported
  - `getUnsupportedFormatMessage()` method providing user-friendly error messages
  - Clear messaging that WebP format is NOT supported by JavaFX
  - FileInputStream-based loading for better error detection
  - Image dimensions logging for successful loads

<div class="section-header">

### Changed

</div>

- **PatientController Image Validation**: Enhanced photo selection with format validation
  - Added `ImageLoader` import and usage in `browsePhoto()` method
  - File format validation before accepting selected photos
  - Alert dialog shown when unsupported format is selected
  - Updated FileChooser filter title to "Imágenes compatibles" for clarity
  - Prevents saving of incompatible image formats (e.g., WebP)
- **PatientDetailController Image Loading**: Improved error handling for patient photos
  - Refactored `loadPatientPhoto()` to use `ImageLoader` helper
  - Simplified code with centralized image loading logic
  - Enhanced logging with detailed path and error information
  - Graceful fallback to placeholder when image loading fails
  - Removed duplicate error handling code
- **PatientListController Image Loading**: Consistent image handling in patient list cards
  - Refactored `createPhotoContainer()` to use `ImageLoader` helper
  - Removed unused `File` import
  - Consistent error handling across all views
  - Detailed logging for debugging image issues
  - Better null-safety for photo paths

<div class="section-header">

### Fixed

</div>

- **WebP Image Support Issue**: Documented and handled JavaFX limitation with WebP format
  - JavaFX does NOT support WebP images (only PNG, JPG, JPEG, GIF, BMP)
  - Users now receive clear error messages when attempting to use WebP
  - Application no longer fails silently when loading incompatible formats
  - Added validation to prevent unsupported formats from being selected
- **Image Loading Error Messages**: Improved error reporting and user feedback
  - Detailed stack traces logged for debugging purposes
  - User-friendly alerts shown for format incompatibility
  - Console logs now include image URI, dimensions, and error details
  - Better distinction between "file not found" vs "format unsupported" errors

<div class="section-header">

### Technical Notes

</div>

- **Supported Image Formats**: PNG, JPG, JPEG, GIF, BMP
- **Unsupported Formats**: WebP, TIFF, SVG, and other modern formats
- **Recommendation**: Convert WebP images to PNG or JPG before importing
- **Tools for Conversion**: Online converters like convertio.co or CloudConvert

---

<div class="version-header">

## [v0.0.4-alpha] - 2025-12-29

</div>

<div class="section-header">

### Added

</div>

- **Ingredient CRUD Operations**: Complete Create, Read, Update, Delete functionality for ingredients
  - `IngredientRepository` with search by name and exact name lookup methods
  - `IngredientService` with full transactional support and lazy-loaded collections
  - Database persistence for all ingredient attributes including vitamins, minerals, and allergens
- **Ingredient List View**: New management interface for viewing and managing all ingredients
  - Search/filter functionality by ingredient name
  - Card-based layout displaying key nutritional information (calories, protein, carbs, fat)
  - Edit and Delete buttons on each ingredient card
  - "New Ingredient" button to create new entries
  - Empty state message when no ingredients found
  - Confirmation dialog before ingredient deletion
- **Ingredient Form Enhancements**: Support for editing existing ingredients
  - `setIngredientForEdit()` method to load existing data
  - Auto-population of all fields when editing (basic nutrition, vitamins, minerals, allergens)
  - Dual mode operation (create vs. edit) with appropriate UI feedback
  - Success/error handling with user notifications
- **Recipe CRUD Operations**: Complete Create, Read, Update, Delete functionality for recipes
  - `RecipeRepository` with search by title methods
  - `RecipeService` with full transactional support and eager loading of ingredients
  - Database persistence for recipes including all ingredients via `RecipeIngredient` relationship
  - Cascade save operations for recipe ingredients
- **Recipe List View**: New management interface for viewing and managing all recipes
  - Search/filter functionality by recipe title
  - Card-based layout displaying title, description, preparation time, and ingredient count
  - View Details, Edit, and Delete buttons on each recipe card
  - "New Recipe" button to create new entries
  - Empty state message when no recipes found
  - Confirmation dialog before recipe deletion
- **Recipe Form Enhancements**: Support for editing existing recipes
  - `setRecipeForEdit()` method to load existing recipe data
  - Auto-population of all fields when editing (title, description, time, ingredients, instructions, notes)
  - Dual mode operation (create vs. edit) with appropriate UI feedback
  - Ingredient ComboBox loaded from database with custom cell factories
  - Dynamic ingredients table with add/remove functionality
  - Integration with ingredient creation workflow
  - Complete field validation and error handling
- **Navigation Integration**: 
  - "Recetas" menu button now opens recipe list instead of directly creating a new recipe
  - New "Ingredientes" menu option in home view
  - Added to top menu bar alongside Consultas, Recetas, and Pacientes
  - Smooth view transitions using existing fade animation system
  - Proper view lifecycle management for all CRUD operations
- **Table Styling Improvements**: Enhanced ingredients table appearance
  - Uniform white background for all filled rows (removed alternating colors)
  - Dynamic row visibility - empty rows completely hidden
  - Border lines only appear on rows with content
  - Consistent styling when adding/removing ingredients
  - Improved focus preservation when deleting allergens or vitamin/mineral entries

<div class="section-header">

### Changed

</div>

- **IngredientController**: Refactored to use dependency injection with `IngredientService`
  - Added support for both create and edit modes
  - Enhanced data loading methods for editing existing ingredients
  - Improved error handling and logging
- **RecipeController**: Refactored to use dependency injection with `RecipeService` and `IngredientService`
  - Added support for both create and edit modes
  - Enhanced data loading methods for editing existing recipes
  - Ingredients now loaded from database instead of empty list
  - Improved validation and error handling with user-friendly dialogs
  - Success confirmation messages when saving recipes
- **HomeController**: Extended view management system
  - Added `recipeListView` field and view switching support
  - Added `showRecipeList()` method to display recipe list
  - Added `showEditRecipe()` method to edit existing recipes
  - Updated `switchToView()` to include recipe list view
  - Refactored `showNewRecipe()` to always reload recipe form for clean state

<div class="section-header">

### Fixed

</div>

- Fixed color inconsistency issue when deleting ingredients from table
- Fixed focus behavior when removing allergens, vitamins, or minerals from forms
- Fixed table row styling to prevent rows from maintaining old position-based colors
- **Ingredient Persistence Issues**: Corrected JPA relationship cascade and collection handling
  - Removed problematic `CascadeType.ALL` and `orphanRemoval` from `RecipeIngredient` relationship
  - Added null-safety checks for collections (vitamins, minerals, allergens) before saving
  - Enhanced error handling with user-friendly error dialogs
  - Improved validation for required fields in ingredient form
- **LazyInitializationException**: Fixed Hibernate lazy loading issue when displaying ingredients
  - Added `findAllWithDetails()` and `searchByNameWithDetails()` methods in `IngredientService`
  - Collections (vitamins, minerals, allergens) now loaded eagerly within transaction scope
  - Prevents "no Session" error when accessing lazy-loaded collections in UI layer
- **Navigation from Ingredient List**: Fixed menu buttons not responding in ingredient list view
  - Added `ingredientListView` to the list of checked views in `switchToView()` method
  - Menu buttons (Consultas, Recetas, Pacientes) now work correctly from ingredient list view
- **Recipe Lazy Loading**: Fixed Hibernate lazy loading issue when displaying recipes
  - Added `findAllWithDetails()` and `searchByTitleWithDetails()` methods in `RecipeService`
  - Recipe ingredients and their associated ingredient entities now loaded eagerly
  - Prevents "no Session" error when accessing recipe ingredients in UI layer

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
  - Left click opens consultation details (pending implementation)
  - Right click shows "Eliminar" option with confirmation dialog
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

