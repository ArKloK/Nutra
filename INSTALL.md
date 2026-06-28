# Nutra - Guía de Instalación

## Requisitos del Sistema

- Windows 10 o superior
- 500 MB de espacio en disco
- 4 GB de RAM recomendados

## Instalación

1. **Extrae la carpeta `Nutra`** en la ubicación deseada (por ejemplo: `C:\Program Files\Nutra`)

2. **Ejecuta la aplicación** haciendo doble clic en `Nutra.bat`

## Notas Importantes

- **Base de datos**: La aplicación crea automáticamente una base de datos en:
  ```
  C:\Users\<TuUsuario>\.nutra\nutra-bdd.sqlite
  ```

- **Primera ejecución**: La primera vez que ejecutes la aplicación, puede tardar unos segundos en iniciar mientras se crea la estructura de la base de datos.

- **Datos persistentes**: Todos tus datos (pacientes, consultas, recetas, ingredientes) se guardan automáticamente en la base de datos.

## Crear un Acceso Directo

Para mayor comodidad, puedes crear un acceso directo:

1. Haz clic derecho en `Nutra.bat`
2. Selecciona "Enviar a" > "Escritorio (crear acceso directo)"
3. (Opcional) Renombra el acceso directo a "Nutra"
4. (Opcional) Haz clic derecho en el acceso directo > Propiedades > Cambiar icono

## Copia de Seguridad

Para hacer una copia de seguridad de tus datos:

1. Navega a: `C:\Users\<TuUsuario>\.nutra\`
2. Copia el archivo `nutra-bdd.sqlite` a una ubicación segura

Para restaurar:

1. Copia el archivo de respaldo de vuelta a `C:\Users\<TuUsuario>\.nutra\`

## Desinstalación

Para desinstalar la aplicación:

1. Elimina la carpeta `Nutra`
2. (Opcional) Si no deseas conservar los datos, elimina también la carpeta `C:\Users\<TuUsuario>\.nutra\`

## Solución de Problemas

### La aplicación no inicia

- Asegúrate de que el archivo `Nutra.bat` tenga permisos de ejecución
- Verifica que la carpeta `jre` esté presente junto al archivo `.bat`

### Error de base de datos

- Verifica que tengas permisos de escritura en `C:\Users\<TuUsuario>\.nutra\`
- Intenta eliminar el archivo de base de datos y reiniciar la aplicación (perderás los datos)

## Soporte

Para más información o reportar problemas, contacta con el desarrollador.

---

**Nutra v0.0.1-SNAPSHOT**  
© 2026 ArKloK

