@echo off
echo ============================================
echo     CREAR PAQUETE ZIP DE NUTRA
echo ============================================
echo.

echo Comprimiendo la carpeta de distribucion...
powershell -Command "Compress-Archive -Path 'target\dist\Nutra\*' -DestinationPath 'target\dist\Nutra-v0.0.1-SNAPSHOT.zip' -Force"

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo     PAQUETE CREADO EXITOSAMENTE
    echo ============================================
    echo.
    echo El archivo ZIP esta en:
    echo target\dist\Nutra-v0.0.1-SNAPSHOT.zip
    echo.
    echo Tamano:
    powershell -Command "Get-Item 'target\dist\Nutra-v0.0.1-SNAPSHOT.zip' | Select-Object @{Name='Size(MB)';Expression={'{0:N2}' -f ($_.Length / 1MB)}}"
    echo.
) else (
    echo ERROR: No se pudo crear el archivo ZIP
)

pause

