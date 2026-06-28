@echo off
echo ============================================
echo     EMPAQUETADO DE NUTRA
echo ============================================
echo.

echo [1/5] Compilando proyecto...
call mvnw.cmd clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Fallo en la compilacion del proyecto
    pause
    exit /b %errorlevel%
)
echo.

echo [2/5] Creando runtime de Java personalizado...
jlink --add-modules java.base,java.sql,java.naming,java.desktop,java.xml,jdk.unsupported,java.scripting,java.management,jdk.jfr,java.logging,java.compiler,jdk.compiler,java.instrument ^
      --strip-debug ^
      --no-man-pages ^
      --no-header-files ^
      --compress=2 ^
      --output target\java-runtime
if %errorlevel% neq 0 (
    echo ERROR: Fallo al crear el runtime de Java
    pause
    exit /b %errorlevel%
)
echo.

echo [3/5] Creando directorio de distribucion...
if exist target\dist rmdir /s /q target\dist
mkdir target\dist\Nutra
echo.

echo [4/5] Copiando archivos de la aplicacion...
copy target\nutra-0.0.1-SNAPSHOT.jar target\dist\Nutra\nutra.jar
xcopy /E /I /Y target\java-runtime target\dist\Nutra\jre
echo.

echo [5/5] Creando script de inicio...
(
echo @echo off
echo set JAVA_HOME=%%~dp0jre
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%
echo.
echo REM Crear directorio de datos si no existe
echo if not exist "%%USERPROFILE%%\.nutra" mkdir "%%USERPROFILE%%\.nutra"
echo.
echo REM Ejecutar la aplicacion
echo start "" "%%JAVA_HOME%%\bin\javaw.exe" -jar "%%~dp0nutra.jar"
) > target\dist\Nutra\Nutra.bat

echo.
echo Creando script de depuracion...
(
echo @echo off
echo set JAVA_HOME=%%~dp0jre
echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%
echo.
echo echo ============================================
echo echo     NUTRA - MODO DEPURACION
echo echo ============================================
echo echo.
echo echo JAVA_HOME: %%JAVA_HOME%%
echo echo.
echo "%%JAVA_HOME%%\bin\java.exe" -version
echo echo.
echo echo Ejecutando aplicacion...
echo echo.
echo.
echo REM Crear directorio de datos si no existe
echo if not exist "%%USERPROFILE%%\.nutra" mkdir "%%USERPROFILE%%\.nutra"
echo.
echo "%%JAVA_HOME%%\bin\java.exe" -jar "%%~dp0nutra.jar"
echo pause
) > target\dist\Nutra\Nutra-debug.bat
echo.

echo [6/5] Creando icono de acceso directo (opcional)...
if exist src\main\resources\icon.ico (
    copy src\main\resources\icon.ico target\dist\Nutra\icon.ico
)

echo.
echo ============================================
echo     EMPAQUETADO COMPLETADO
echo ============================================
echo.
echo La aplicacion esta lista en: target\dist\Nutra
echo.
echo Para ejecutar la aplicacion:
echo   1. Copia la carpeta 'Nutra' a donde desees
echo   2. Ejecuta 'Nutra.bat'
echo.
echo NOTA: La base de datos se creara automaticamente en:
echo       %%USERPROFILE%%\.nutra\nutra-bdd.sqlite
echo.
pause

