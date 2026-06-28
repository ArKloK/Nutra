@echo off
echo ============================================
echo     LIMPIEZA DE ARCHIVOS DE COMPILACION
echo ============================================
echo.

echo Eliminando carpeta target...
if exist target (
    rmdir /s /q target
    echo [OK] Carpeta target eliminada
) else (
    echo [INFO] Carpeta target no existe
)
echo.

echo Eliminando archivos temporales de Maven...
call mvnw.cmd clean
echo.

echo ============================================
echo     LIMPIEZA COMPLETADA
echo ============================================
echo.
echo El proyecto esta limpio y listo para un nuevo empaquetado.
echo Ejecuta 'package.bat' para crear un nuevo paquete.
echo.
pause

