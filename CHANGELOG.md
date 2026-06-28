<div class="changelog-header">

# Changelog

</div>

All notable changes to this project will be documented in this file.

---
<div class="version-header">

## [v0.0.1-SNAPSHOT] - 2026-01-04

</div>

<div class="section-header">

### Added

</div>

- Application deployment package with embedded Java Runtime
- Automated packaging script (`package.bat`) for creating standalone distributions
- ZIP distribution script (`create-zip.bat`) for easy distribution
- Complete installation guide (`INSTALL.md`) for end users
- Deployment documentation (`DEPLOYMENT.md`) for developers
- Standalone executable script (`Nutra.bat`) that doesn't require Java installation
- Optimized Java Runtime Environment using jlink (reduces size to ~40 MB)
- Automatic database initialization on first run
- User-friendly README for distribution package

<div class="section-header">

### Changed

</div>

- Updated main class configuration in pom.xml for proper packaging
- Modified application.properties to use user home directory for database
- Enhanced build configuration for creating executable JAR

<div class="section-header">

### Technical Details

</div>

- Package size: ~104 MB (compressed ZIP)
- Includes JDK 25 runtime modules: base, sql, naming, desktop, xml, unsupported, scripting, management, jfr, logging, compiler, instrument
- Database location: `%USERPROFILE%\.nutra\nutra-bdd.sqlite`
- No external Java installation required by end users

---
<div class="version-header">

## [v1.0.0-beta] - 2026-01-03

</div>

<div class="section-header">

### Changed

</div>

- First prerelease version.

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

