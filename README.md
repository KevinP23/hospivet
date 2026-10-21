# HOSPIVET

Base funcional para inicio de sesión de veterinarios, registro de clientes, registro de mascotas vinculadas a su propietario, gestión de inventario y citas.

## Estructura

- `backend`: Java 21, Spring Boot, Spring Security, MyBatis-Plus, Flyway y PostgreSQL.
- `frontend`: Vue 3, JavaScript, Vite y Element Plus.
- `docs/trazabilidad`: decisiones, requisitos implementados y cambios documentales pendientes.
- `compose.yaml`: PostgreSQL para desarrollo local.

## Requisitos locales

- JDK 21 o 17. En este equipo se encontró JDK 21 en `C:\Program Files\Android\Android Studio\jbr`.
- Node.js y `npm.cmd`.
- PostgreSQL instalado localmente (en este equipo se verificó PostgreSQL 18.6) o Docker Desktop para levantar PostgreSQL 17 con Compose.
- No se requiere Maven global: el backend usa Maven Wrapper.

## Configuración segura

No escriba contraseñas en `application.yml`, scripts o comandos que vaya a guardar. Para la ejecución local puede introducirlas con `Get-Credential`; así solo quedan en las variables de la terminal mientras el proceso está activo.

`.env.example` documenta los nombres disponibles. Si usa Docker puede copiarlo como `.env`; ese archivo está ignorado por Git. Spring Boot no carga `.env` automáticamente, por lo que sus variables deben cargarse en la terminal antes de arrancar el backend.

Para la base de datos se requieren `POSTGRES_PASSWORD` y `DB_PASSWORD` con el mismo valor si se usa Compose. La cuenta veterinaria inicial solo se crea si se definen al menos:

- `HOSPIVET_BOOTSTRAP_USERNAME`
- `HOSPIVET_BOOTSTRAP_PASSWORD`
- `HOSPIVET_BOOTSTRAP_CI`
- `HOSPIVET_BOOTSTRAP_NOMBRE`
- `HOSPIVET_BOOTSTRAP_APELLIDO`

## Ejecución con Docker para PostgreSQL

Desde la raíz del proyecto:

```powershell
Copy-Item .env.example .env
# Edite .env y asigne contraseñas locales.
docker compose up -d postgres
```

Luego cargue las variables de `.env` en su terminal o defínalas para la sesión antes de iniciar el backend. Spring Boot ejecutará la migración Flyway automáticamente.

## Ejecución sin Docker

En este equipo PostgreSQL funciona en `localhost:5432`, con el usuario administrador `postgres`, y la base `hospivet` ya fue creada. Flyway aplica automáticamente todas las migraciones pendientes. No ejecute manualmente sus SQL ni elimine `flyway_schema_history`.

En una instalación nueva, cree una base vacía llamada `hospivet`. Al iniciar el backend, Flyway crea únicamente los objetos pendientes y conserva los datos existentes.

## Backend

En PowerShell:

```powershell
cd C:\TallerII2026\hospivet\backend
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:MAVEN_USER_HOME='C:\TallerII2026\.m2'
$env:MAVEN_ARGS='-Dmaven.repo.local=C:\TallerII2026\.m2\repository'
$env:DEBUG='false'
$env:DB_URL='jdbc:postgresql://localhost:5432/hospivet'

$dbCredential=Get-Credential -UserName 'postgres' -Message 'Credenciales locales de PostgreSQL'
$env:DB_USERNAME=$dbCredential.UserName
$env:DB_PASSWORD=$dbCredential.GetNetworkCredential().Password

.\mvnw.cmd spring-boot:run
```

La cuenta de PostgreSQL sirve únicamente para acceder a la base. No es la cuenta de inicio de sesión de HOSPIVET.

### Crear la cuenta veterinaria en una base nueva

La cuenta se crea una sola vez. Antes del primer arranque contra una base nueva, agregue en la misma terminal:

```powershell
$vetCredential=Get-Credential -UserName 'veterinario' -Message 'Nueva cuenta local de HOSPIVET'
$env:HOSPIVET_BOOTSTRAP_USERNAME=$vetCredential.UserName
$env:HOSPIVET_BOOTSTRAP_PASSWORD=$vetCredential.GetNetworkCredential().Password
$env:HOSPIVET_BOOTSTRAP_CI='1234567'
$env:HOSPIVET_BOOTSTRAP_NOMBRE='Nombre'
$env:HOSPIVET_BOOTSTRAP_APELLIDO='Apellido'
```

Después del primer inicio puede cerrar la terminal o retirar estas variables. Si el usuario ya existe, el arranque no lo duplica ni reemplaza su contraseña.

Para detener el backend use `Ctrl+C`. Al cerrar la terminal desaparecen las contraseñas cargadas en sus variables de entorno.

Pruebas automatizadas (usan H2 en memoria, no requieren PostgreSQL):

```powershell
.\mvnw.cmd test
```

## Frontend

En otra terminal:

```powershell
cd C:\TallerII2026\hospivet\frontend
npm.cmd install
npm.cmd run dev
```

Abra `http://127.0.0.1:5173/`. Vite redirige `/api` al backend en `http://localhost:8080`.

Para detener el frontend use `Ctrl+C`. En arranques posteriores no es necesario repetir `npm.cmd install` mientras no cambie `package-lock.json`.

## Comportamiento ante errores

Los formularios ejecutan validación local y vuelven a mostrar los errores enviados por el backend. Los valores reactivos solo se reinician después de guardar correctamente, por lo que el usuario no pierde lo escrito cuando falla una validación o la petición.

Consulte `docs/trazabilidad` para la correspondencia con las iteraciones 4 y 5 y los diagramas/formularios que deberán actualizarse sin alterar los documentos Word actuales.

## Módulos disponibles

- Inicio de sesión del veterinario.
- Registro de clientes.
- Registro de mascotas vinculadas a su propietario.
- Inventario: consulta y filtros, alta de productos, actualización de stock con movimiento de auditoría y eliminación lógica con confirmación.
- Citas: registro, modificación, cancelación por estado y consulta por fecha para el veterinario autenticado.

El catálogo inicial contiene `Consulta general`. Su precio `0.00 Bs` y duración de `30 minutos` son valores provisionales registrados en `docs/trazabilidad/requisitos-citas.md`; deben confirmarse antes de usarlos en facturación o planificación por duración.
