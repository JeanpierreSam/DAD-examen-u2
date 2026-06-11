# SGT - Sistema de Gestión de Talleres (examen-u2)

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.14-green)
![Java](https://img.shields.io/badge/Java-21-orange)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

> Sistema de gestión de talleres académicos basado en arquitectura de microservicios con Spring Cloud, autenticación JWT centralizada y comunicación vía OpenFeign.

**Repositorio oficial:** https://github.com/JeanpierreSam/DAD-examen-u2

---

##  Tabla de Contenidos

1. [Descripción del Proyecto](#1-descripción-del-proyecto)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Árbol de Carpetas](#3-árbol-de-carpetas)
4. [Stack Tecnológico](#4-stack-tecnológico)
5. [Puertos y Bases de Datos](#5-puertos-y-bases-de-datos)
6. [Guía de Instalación y Ejecución](#6-guía-de-instalación-y-ejecución)
7. [Configuraciones](#7-configuraciones)
8. [Documentación de API (OpenAPI/Swagger)](#8-documentación-de-api-openapiswagger)
9. [Endpoints Principales](#9-endpoints-principales)
10. [Flujo de Autenticación y Autorización](#10-flujo-de-autenticación-y-autorización)
11. [Usuarios por Defecto](#11-usuarios-por-defecto)
12. [Scripts Dockerfile y Docker Compose](#12-scripts-dockerfile-y-docker-compose)
13. [Pruebas Unitarias e Integración](#13-pruebas-unitarias-e-integración)
14. [Colección Postman E2E](#14-colección-postman-e2e)
15. [Patrones de Diseño Implementados](#15-patrones-de-diseño-implementados)

---

## 1. Descripción del Proyecto

**SGT (Sistema de Gestión de Talleres)** es una plataforma académica desarrollada con arquitectura de microservicios que permite la gestión integral de talleres, instructores y alumnos.

### Características Principales
- 7 microservicios independientes y escalables
- Autenticación centralizada con JWT y control de acceso por roles (ADMIN, INSTRUCTOR, ALUMNO)
- Service Discovery con Netflix Eureka
- Configuración centralizada con Spring Cloud Config Server
- API Gateway con filtrado global de autenticación y autorización
- Comunicación entre servicios vía OpenFeign con Circuit Breaker (Resilience4j)
- Patrón Saga con compensación para matrículas de talleres
- Documentación API con OpenAPI/Swagger
- Containerización completa con Docker y Docker Compose

---

## 2. Arquitectura del Sistema

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            CLIENTES (Web/Mobile)                        │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ HTTP/REST
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        3. API GATEWAY (Puerta de Enlace)                │
│                    spring-cloud-gateway-server-webmvc                   │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │  AuthenticationGlobalFilter: Validación JWT + Autorización por Rol │  │
│  │  Rutas: /api/auth/**, /api/instructores/**, /api/alumnos/**,       │  │
│  │         /api/talleres/**                                           │  │
│  ───────────────────────────────────────────────────────────────────┘  │
└────┬──────────────┬──────────────┬──────────────┬──────────────────────┘
     │              │              │              │
     ▼              ▼              ▼              ▼
┌─────────┐  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐
│ 4. AUTH │  │ 5. INSTRUCTOR│  │ 6. ALUMNO   │  │ 7. TALLER    │
│ :8084   │  │ :8081        │  │ :8082       │  │ :8083        │
│ JWT     │  │ CRUD         │  │ CRUD        │  │ CRUD + Feign │
│ Login   │  │              │  │ + Counter   │  │ + Saga       │
└────┬────┘  └──────┬───────┘  └──────┬──────┘  └──────┬───────┘
     │              │                 │                 │
     ▼              ▼                 ▼                 ▼
┌─────────┐  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐
│ authdb  │  │ instructor_db│  │ alumno_db   │  │ taller_db    │
│ :5441   │  │ :5438        │  │ :5439       │  │ :5440        │
│ PostgreSQL│ │ PostgreSQL   │  │ PostgreSQL  │  │ PostgreSQL   │
└─────────  └──────────────┘  └─────────────┘  └──────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                    INFRAESTRUCTURA DE SOPORTE                           │
│  ┌──────────────────────┐  ┌──────────────────────────────────────────┐ │
│  │ 1. CONFIG SERVER     │  │ 2. EUREKA SERVER (Service Registry)      │ │
│  │    :8888             │  │    :8761                                 │ │
│  │    Config Repo       │  │    Registro y descubrimiento             │ │
│  │    (archivos .props) │  │    de servicios                          │ │
│  └──────────────────────┘  └──────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘

Comunicación entre microservicios:
  Taller ←→ Alumno (OpenFeign): obtenerPorId, incrementarTaller, decrementarTaller
  Taller ←→ Instructor (OpenFeign): obtenerPorId
```

### Flujo de Comunicación
1. **Cliente → Gateway:** Todas las peticiones pasan por el API Gateway (puerto 8080)
2. **Gateway → Auth:** Valida JWT y aplica políticas de autorización por rol
3. **Gateway → Microservicio:** Enruta la petición al servicio correspondiente vía Eureka
4. **Microservicio → Config Server:** Obtiene configuración al iniciar
5. **Microservicio → Eureka:** Se registra y descubre otros servicios
6. **Taller → Alumno/Instructor:** Comunicación directa vía OpenFeign para consultas cruzadas

---

## 3. Árbol de Carpetas

```
examen-u2/
│
├── docker-compose.yml                          # Orquestación de contenedores
├── DAD_Verificacion_E2E.postman_collection.json # Pruebas E2E automatizadas
│
── config-repo/                                # Configuraciones centralizadas
│   ├── application.properties                  # Configuración compartida
│   ├── ms-admin-api-gateway.properties         # Config del Gateway
│   ├── ms-auth.properties                      # Config del servicio Auth
│   ├── ms-gestion-alumno.properties            # Config del servicio Alumno
│   ├── ms-gestion-instructor.properties        # Config del servicio Instructor
│   └── ms-gestion-taller.properties            # Config del servicio Taller
│
├── config-server/                              # 1. Servidor de Configuración
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/config_server/
│       │   └── ConfigServerApplication.java
│       └── resources/
│           └── application.yaml
│
├── eureka-server/                              # 2. Servidor de Registro (Eureka)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/eureka_server/
│       │   └── EurekaServerApplication.java
│       └── resources/
│           └── application.yaml
│
├── api-gateway/                                # 3. Puerta de Enlace (Gateway)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/api_gateway/
│       │   ├── ApiGatewayApplication.java
│       │   ├── filter/
│       │   │   ── AuthenticationGlobalFilter.java
│       │   └── security/
│       │       └── JwtValidator.java
│       └── resources/
│           └── application.properties
│
── ms-auth/                                    # 4. Servicio de Autenticación
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/ms_auth/
│       │   ├── MsAuthApplication.java
│       │   ├── config/
│       │   │   ├── DataInitializer.java
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   └── AuthController.java
│       │   ├── dto/
│       │   │   ├── AuthResponse.java
│       │   │   ├── LoginRequest.java
│       │   │   └── RegisterRequest.java
│       │   ├── entity/
│       │   │   ├── Rol.java
│       │   │   └── Usuario.java
│       │   ├── exception/
│       │   │   ├── ApiError.java
│       │   │   ├── BusinessException.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── UnauthorizedException.java
│       │   ├── repository/
│       │   │   └── UsuarioRepository.java
│       │   ├── security/
│       │   │   └── JwtService.java
│       │   └── service/
│       │       ├── AuthService.java
│       │       └── impl/AuthServiceImpl.java
│       └── resources/
│           └── application.yml
│
├── ms-gestion-instructor/                      # 5. Servicio de Instructores
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/ms_gestion_instructor/
│       │   ├── MsGestionInstructorApplication.java
│       │   ├── controller/InstructorController.java
│       │   ├── dto/InstructorDTO.java
│       │   ├── entity/Instructor.java
│       │   ├── exception/
│       │   │   ├── ApiError.java
│       │   │   ├── BusinessException.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── ResourceNotFoundException.java
│       │   ├── repository/InstructorRepository.java
│       │   └── service/
│       │       ├── InstructorService.java
│       │       └── InstructorServiceImpl.java
│       └── resources/
│           └── application.yaml
│
├── ms-gestion-alumno/                          # 6. Servicio de Alumnos
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/upeu/ms_gestion_alumno/
│       │   ├── MsGestionAlumnoApplication.java
│       │   ├── controller/AlumnoController.java
│       │   ├── dto/AlumnoDTO.java
│       │   ├── entity/Alumno.java
│       │   ├── exception/
│       │   │   ├── ApiError.java
│       │   │   ├── BusinessException.java
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── ResourceNotFoundException.java
│       │   ├── repository/AlumnoRepository.java
│       │   └── service/
│       │       ├── AlumnoService.java
│       │       └── AlumnoServiceImpl.java
│       └── resources/
│           └── application.yaml
│
└── ms-gestion-taller/                          # 7. Servicio de Talleres
    ├── Dockerfile
    ├── pom.xml
    └── src/main/
        ├── java/pe/edu/upeu/ms_gestion_taller/
        │   ├── MsGestionTallerApplication.java
        │   ├── client/
        │   │   ├── AlumnoClient.java          # Feign Client
        │   │   └── InstructorClient.java      # Feign Client
        │   ├── controller/TallerController.java
        │   ├── dto/
        │   │   ├── AlumnoDTO.java
        │   │   ├── InstructorDTO.java
        │   │   ├── TallerDTO.java
        │   │   └── TallerDetalleDTO.java
        │   ├── entity/Taller.java
        │   ├── exception/
        │   │   ├── ApiError.java
        │   │   ├── BusinessException.java
        │   │   ├── GlobalExceptionHandler.java
        │   │   └── ResourceNotFoundException.java
        │   ├── repository/TallerRepository.java
        │   ── service/
        │       ├── TallerService.java
        │       └── TallerServiceImpl.java
        └── resources/
            └── application.yaml
```

---

## 4. Stack Tecnológico

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 3.5.14 | Framework base |
| **Spring Cloud** | 2025.0.2 | Ecosistema de microservicios |
| **Spring Cloud Config Server** | - | Configuración centralizada |
| **Spring Cloud Netflix Eureka** | - | Service Discovery |
| **Spring Cloud Gateway (MVC)** | - | API Gateway |
| **Spring Cloud OpenFeign** | - | Comunicación entre servicios |
| **Spring Cloud CircuitBreaker** | - | Resilience4j para tolerancia a fallos |
| **Spring Data JPA** | - | Persistencia con Hibernate |
| **Spring Security** | - | Autenticación y autorización |
| **Spring Validation** | - | Validación de DTOs |
| **Springdoc OpenAPI** | 2.8.9 | Documentación Swagger |
| **JJWT** | 0.12.6 | Generación y validación de JWT |
| **PostgreSQL** | 16 Alpine | Base de datos relacional |
| **Docker** | - | Containerización |
| **Docker Compose** | 3.8 | Orquestación de contenedores |
| **Lombok** | - | Reducción de boilerplate |
| **Maven** | - | Gestión de dependencias y build |

---

## 5. Puertos y Bases de Datos

### Puertos de Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **config-server** | 8888 | Servidor de configuración centralizada |
| **eureka-server** | 8761 | Registro y descubrimiento de servicios |
| **api-gateway** | 8080 | Puerta de enlace principal (punto de entrada) |
| **ms-gestion-instructor** | 8081 | Gestión de instructores |
| **ms-gestion-alumno** | 8082 | Gestión de alumnos |
| **ms-gestion-taller** | 8083 | Gestión de talleres |
| **ms-auth** | 8084 | Autenticación y generación de JWT |

### Bases de Datos PostgreSQL

| Base de Datos | Contenedor Docker | Puerto | Usuario | Contraseña | Servicio Asociado |
|--------------|-------------------|--------|---------|------------|-------------------|
| **authdb** | examen-postgres-auth | 5441:5432 | postgres | postgres | ms-auth |
| **instructor_db** | postgres-instructor | 5438:5432 | instructor_user | instructor_pass | ms-gestion-instructor |
| **alumno_db** | postgres-alumno | 5439:5432 | alumno_user | alumno_pass | ms-gestion-alumno |
| **taller_db** | postgres-taller | 5440:5432 | taller_user | taller_pass | ms-gestion-taller |

---

## 6. Guía de Instalación y Ejecución

### Prerrequisitos
- Java 21 o superior
- Docker Desktop instalado y corriendo
- Maven (o usar el wrapper incluido `mvnw`)
- Git (para clonar el repositorio)

### Opción A: Docker Compose (Recomendada)

Esta opción levanta **todos** los servicios (bases de datos + microservicios) automáticamente.

```bash
# 1. Clonar el repositorio
git clone https://github.com/JeanpierreSam/DAD-examen-u2.git
cd DAD-examen-u2

# 2. Construir las imágenes Docker
docker-compose build

# 3. Levantar todos los servicios en segundo plano
docker-compose up -d

# 4. Verificar que todos los contenedores están corriendo
docker-compose ps

# 5. Ver logs de un servicio específico
docker-compose logs -f ms-auth

# 6. Detener todos los servicios
docker-compose down
```

**Orden de inicio automático:**
1. Bases de datos PostgreSQL (con healthcheck)
2. Config Server (espera a que las DBs estén listas)
3. Eureka Server (espera a Config Server)
4. API Gateway (espera a Config + Eureka)
5. ms-auth (espera a Config + su DB)
6. ms-gestion-instructor, ms-gestion-alumno (esperan a Config + sus DBs)
7. ms-gestion-taller (espera a Config + su DB + instructor + alumno)

### Opción B: Ejecución Local con IntelliJ

Esta opción es ideal para **desarrollo y debugging**. Las bases de datos corren en Docker, pero los microservicios se ejecutan desde IntelliJ.

#### Paso 1: Levantar solo las bases de datos
```bash
docker-compose up -d postgres-auth postgres-instructor postgres-alumno postgres-taller
```

#### Paso 2: Verificar que las bases de datos están activas
```bash
docker ps --filter "name=postgres"
```
Deberías ver los 4 contenedores con estado `Up`.

#### Paso 3: Ejecutar servicios en IntelliJ (en orden)
1. **ConfigServerApplication** (puerto 8888)
2. **EurekaServerApplication** (puerto 8761)
3. **MsAuthApplication** (puerto 8084)
4. **ApiGatewayApplication** (puerto 8080)
5. **MsGestionInstructorApplication** (puerto 8081)
6. **MsGestionAlumnoApplication** (puerto 8082)
7. **MsGestionTallerApplication** (puerto 8083)

> **Importante:** Si tienes los microservicios corriendo en Docker, **detenlos primero** para evitar conflictos de puertos:
> ```bash
> docker-compose stop ms-auth api-gateway ms-gestion-instructor ms-gestion-alumno ms-gestion-taller eureka-server config-server
> ```

---

## 7. Configuraciones

### Configuración Centralizada (Config Server)

El proyecto utiliza **Spring Cloud Config Server** con perfil `native`, lo que significa que las configuraciones se leen directamente desde archivos locales en la carpeta `config-repo/`.

#### Estructura de configuraciones:
```
config-repo/
├── application.properties              # Configuración compartida por todos los servicios
├── ms-admin-api-gateway.properties     # Configuración específica del Gateway
├── ms-auth.properties                  # Configuración del servicio Auth
── ms-gestion-alumno.properties        # Configuración del servicio Alumno
├── ms-gestion-instructor.properties    # Configuración del servicio Instructor
└── ms-gestion-taller.properties        # Configuración del servicio Taller
```

#### Configuración compartida (`application.properties`):
```properties
# URL de Eureka Server
eureka.client.service-url.defaultZone=${EUREKA_DEFAULT_ZONE:http://localhost:8761/eureka}

# Endpoints de Actuator expuestos
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# Secreto JWT (compartido entre Auth y Gateway)
jwt.secret=dad-upeu-microservicios-jwt-secret-key-2026-super-segura-0123456789
```

#### Variables de entorno configurables:
| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `EUREKA_DEFAULT_ZONE` | `http://localhost:8761/eureka` | URL del Eureka Server |
| `DB_HOST` | `localhost` | Host de la base de datos |
| `DB_PORT` | `5438/5439/5440/5441` | Puerto de la base de datos |
| `DB_NAME` | `instructor_db/alumno_db/taller_db/authdb` | Nombre de la base de datos |

> **Nota:** Actualmente el proyecto utiliza configuración nativa (archivos locales). Para entornos de producción, se recomienda migrar el Config Server para que lea configuraciones desde un repositorio Git, permitiendo separar configuraciones por entorno (dev, test, prod).

---

## 8. Documentación de API (OpenAPI/Swagger)

Todos los microservicios de negocio incluyen documentación automática con **Springdoc OpenAPI**.

### URLs de Swagger UI

| Servicio | Swagger UI | OpenAPI JSON |
|----------|------------|--------------|
| **ms-auth** | http://localhost:8084/swagger-ui.html | http://localhost:8084/api-docs |
| **ms-gestion-instructor** | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| **ms-gestion-alumno** | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |
| **ms-gestion-taller** | http://localhost:8083/swagger-ui.html | http://localhost:8083/api-docs |

### Acceder vía Gateway
También puedes acceder a la documentación a través del Gateway (puerto 8080):
- **Instructores:** http://localhost:8080/api/instructores/swagger-ui.html
- **Alumnos:** http://localhost:8080/api/alumnos/swagger-ui.html
- **Talleres:** http://localhost:8080/api/talleres/swagger-ui.html
- **Auth:** http://localhost:8080/api/auth/swagger-ui.html

---

## 9. Endpoints Principales

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Registrar nuevo usuario | Público |
| POST | `/api/auth/login` | Autenticar y obtener JWT | Público |
| GET | `/api/auth/validate` | Validar token JWT | Autenticado |

### Instructores (`/api/instructores`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/api/instructores` | Listar todos los instructores | Autenticado |
| GET | `/api/instructores/{id}` | Obtener instructor por ID | Autenticado |
| POST | `/api/instructores` | Crear instructor | ADMIN |
| PUT | `/api/instructores/{id}` | Actualizar instructor | ADMIN |
| DELETE | `/api/instructores/{id}` | Eliminar instructor | ADMIN |

### Alumnos (`/api/alumnos`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/api/alumnos` | Listar todos los alumnos | Autenticado |
| GET | `/api/alumnos/{id}` | Obtener alumno por ID | Autenticado |
| POST | `/api/alumnos` | Crear alumno | ADMIN |
| PUT | `/api/alumnos/{id}` | Actualizar alumno | ADMIN |
| DELETE | `/api/alumnos/{id}` | Eliminar alumno | ADMIN |
| POST | `/api/alumnos/{id}/incrementar-taller` | Incrementar contador de talleres | ADMIN |
| POST | `/api/alumnos/{id}/decrementar-taller` | Decrementar contador de talleres | ADMIN |

### Talleres (`/api/talleres`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/api/talleres` | Listar todos los talleres | Autenticado |
| GET | `/api/talleres/{id}` | Obtener taller por ID | Autenticado |
| POST | `/api/talleres` | Crear taller | ADMIN |
| PUT | `/api/talleres/{id}` | Actualizar taller | ADMIN |
| DELETE | `/api/talleres/{id}` | Eliminar taller | ADMIN |
| POST | `/api/talleres/{tallerId}/asignar-instructor/{instructorId}` | Asignar instructor al taller | ADMIN |
| POST | `/api/talleres/{tallerId}/inscribir-alumno/{alumnoId}` | Inscribir alumno en taller | ALUMNO |
| POST | `/api/talleres/{tallerId}/matricular-alumno/{alumnoId}` | Matricular alumno (con saga) | ALUMNO |
| GET | `/api/talleres/{id}/detalle-completo` | Obtener detalle completo del taller | Autenticado |

---

## 10. Flujo de Autenticación y Autorización

### Diagrama de Flujo JWT

```
┌──────────     POST /api/auth/login      ┌─────────────┐
│  Cliente │ ────────────────────────────▶ │  ms-auth    │
│          │                               │  :8084      │
│          │ ◀───────────────────────────── │             │
│          │   { token, username, rol }    │             │
└────┬─────┘                               └─────────────┘
     │
     │  Todas las peticiones incluyen:
     │  Header: Authorization: Bearer <token>
     ▼
─────────────────────────────────────────────────────────┐
│                    API GATEWAY :8080                    │
│  ┌───────────────────────────────────────────────────┐  │
│  │  AuthenticationGlobalFilter                       │  │
│  │                                                   │  │
│  │  1. ¿Es ruta pública (/api/auth/**)?             │  │
│  │     ✅ SÍ → Pasar directo                         │  │
│  │     ❌ NO → Continuar                             │  │
│  │                                                   │  │
│  │  2. ¿Tiene header Authorization: Bearer?         │  │
│  │     ❌ NO → 401 Unauthorized                      │  │
│  │     ✅ SÍ → Extraer token                         │  │
│  │                                                   │  │
│  │  3. ¿Token válido? (JwtValidator)                │  │
│  │     ❌ NO → 401 Unauthorized                      │  │
│  │     ✅ SÍ → Extraer claims (username, rol)        │  │
│  │                                                   │  │
│  │  4. ¿Rol autorizado para esta ruta?              │  │
│  │     - ADMIN → ✅ Acceso total                     │  │
│  │     - GET → ✅ Cualquier autenticado              │  │
│  │     - POST inscribir/matricular → ✅ Solo ALUMNO  │  │
│  │     - Otros POST/PUT/DELETE → ❌ 403 Forbidden    │  │
│  │                                                   │  │
│  │  5. Inyectar headers:                            │  │
│  │     X-Auth-User: <username>                       │  │
│  │     X-Auth-Rol: <rol>                             │  │
│  └───────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │ Microservicio  │
              │ destino        │
              └────────────────
```

### Políticas de Autorización

| Rol | GET | POST/PUT/DELETE | Inscribir/Matricular |
|-----|-----|-----------------|---------------------|
| **ADMIN** | ✅ | ✅ | ✅ |
| **INSTRUCTOR** | ✅ | ❌ | ❌ |
| **ALUMNO** | ✅ |  | ✅ |

---

## 11. Usuarios por Defecto

El servicio `ms-auth` incluye un `DataInitializer` que crea automáticamente 3 usuarios al iniciar:

| Username | Password | Rol | Descripción |
|----------|----------|-----|-------------|
| `admin` | `admin123` | ADMIN | Administrador con acceso total |
| `instructor1` | `inst123` | INSTRUCTOR | Instructor de prueba |
| `alumno1` | `alum123` | ALUMNO | Alumno de prueba |

### Ejemplo de Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOIiwiaWF0IjoxNzE3MjAwMDAwLCJleHAiOjE3MTcyMDM2MDB9...",
  "username": "admin",
  "rol": "ADMIN",
  "expiracionSegundos": 3600
}
```

---

## 12. Scripts Dockerfile y Docker Compose

### Dockerfile (Patrón Multi-Stage)

Todos los microservicios comparten el mismo patrón de Dockerfile:

```dockerfile
# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE <puerto-del-servicio>
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Características:**
- **Multi-stage build:** Reduce el tamaño final de la imagen
- **Alpine Linux:** Imágenes ligeras (~150MB vs ~500MB)
- **Usuario no-root:** Seguridad mejorada (`spring:spring`)
- **Maven Wrapper:** No requiere Maven instalado en el host

### Docker Compose

El archivo `docker-compose.yml` orquesta todos los servicios con:

- **Healthchecks:** Las bases de datos verifican su estado antes de que los servicios dependan de ellas
- **Dependencias:** Orden de inicio controlado con `depends_on` y `condition`
- **Red aislada:** Todos los servicios en la red `ms-net`
- **Volúmenes persistentes:** Los datos de PostgreSQL se mantienen entre reinicios

```yaml
# Ejemplo de servicio con healthcheck y dependencias
ms-auth:
  build: ./ms-auth
  ports:
    - "8084:8084"
  environment:
    SPRING_CONFIG_IMPORT: configserver:http://config-server:8888
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
  depends_on:
    config-server:
      condition: service_healthy
    postgres-auth:
      condition: service_healthy
  networks: [ms-net]
  restart: on-failure
```

---

## 13. Pruebas Unitarias e Integración

### Pruebas Unitarias

Cada microservicio incluye una prueba básica de carga de contexto:

| Archivo | Servicio |
|---------|----------|
| `config-server/src/test/.../ConfigServerApplicationTests.java` | config-server |
| `eureka-server/src/test/.../EurekaServerApplicationTests.java` | eureka-server |
| `api-gateway/src/test/.../ApiGatewayApplicationTests.java` | api-gateway |
| `ms-auth/src/test/.../MsAuthApplicationTests.java` | ms-auth |
| `ms-gestion-instructor/src/test/.../MsGestionInstructorApplicationTests.java` | ms-gestion-instructor |
| `ms-gestion-alumno/src/test/.../MsGestionAlumnoApplicationTests.java` | ms-gestion-alumno |
| `ms-gestion-taller/src/test/.../MsGestionTallerApplicationTests.java` | ms-gestion-taller |

**Ejecutar pruebas:**
```bash
# Probar un servicio específico
cd ms-auth && ./mvnw test

# Probar todos los servicios
./mvnw clean test
```

### Pruebas de Integración (E2E)

Las pruebas de integración se realizan mediante la colección de Postman incluida en el proyecto (`DAD_Verificacion_E2E.postman_collection.json`).

---

## 14. Colección Postman E2E

El proyecto incluye una colección de Postman con **41 pruebas automatizadas** que verifican el funcionamiento completo del sistema.

### Contenido de la Colección

| Fase | Descripción | Número de Tests |
|------|-------------|-----------------|
| **F1** | Config Server health y configuración | 3 |
| **F2** | Eureka Server - registro de servicios | 1 |
| **F3** | CRUD Instructores (validaciones, duplicados) | 7 |
| **F4** | Gateway routes y talleres | 2 |
| **F5** | Feign: asignar instructor, inscribir alumno, detalle completo | 3 |
| **F6** | Circuit Breakers y health | 1 |
| **F7** | Balanceo de carga | 1 |
| **F8** | Autenticación: login, registro, validación JWT | 7 |
| **F9** | Autorización por roles (403, 401) | 4 |
| **F10** | Saga pattern: matrícula con compensación | 6 |

### Cómo Ejecutar

1. **Importar la colección:**
   - Abrir Postman
   - Click en **Import** → Seleccionar `DAD_Verificacion_E2E.postman_collection.json`

2. **Configurar variables:**
   - La colección incluye variables predefinidas:
     - `host`: `localhost`
     - `portGateway`: `8080`
     - `portConfig`: `8888`
     - `portEureka`: `8761`
     - `portAuth`: `8084`
     - `portInstructor`: `8081`
     - `portAlumno`: `8082`
     - `portTaller`: `8083`

3. **Ejecutar con Collection Runner:**
   - Seleccionar la colección
   - Click en **Run**
   - Las pruebas se ejecutan en orden y las variables se actualizan automáticamente (ej: `jwt`, `instructorId`, `alumnoId`)

4. **Usuarios de prueba:**
   - `admin` / `admin123` (rol ADMIN)
   - `instructor1` / `inst123` (rol INSTRUCTOR)
   - `alumno1` / `alum123` (rol ALUMNO)

---

## 15. Patrones de Diseño Implementados

### 1. API Gateway Pattern
- **Implementación:** `api-gateway` con Spring Cloud Gateway MVC
- **Responsabilidad:** Punto único de entrada, enrutamiento, autenticación JWT centralizada, CORS

### 2. Service Discovery Pattern
- **Implementación:** Netflix Eureka Server
- **Responsabilidad:** Registro automático de servicios, descubrimiento dinámico, health checks

### 3. Externalized Configuration Pattern
- **Implementación:** Spring Cloud Config Server (perfil `native`)
- **Responsabilidad:** Configuración centralizada en `config-repo/`, recarga sin reiniciar servicios

### 4. Circuit Breaker Pattern
- **Implementación:** Resilience4j con Spring Cloud CircuitBreaker
- **Servicio:** `ms-gestion-taller`
- **Responsabilidad:** Tolerancia a fallos en llamadas Feign, fallback methods

### 5. Saga Pattern con Compensación
- **Implementación:** `matricularAlumno` en `TallerServiceImpl`
- **Flujo:**
  1. Verificar cupo disponible en taller
  2. Verificar existencia del alumno (Feign)
  3. Incrementar inscritos en taller
  4. Incrementar `talleresInscritos` en alumno (Feign)
  5. **Si falla el paso 4:** Decrementar inscritos en taller (compensación)

### 6. Backend for Frontend (BFF)
- **Implementación:** API Gateway con rutas específicas por dominio
- **Responsabilidad:** Adaptar respuestas para clientes específicos, agregar headers de seguridad

### 7. Database per Service Pattern
- **Implementación:** 4 bases de datos PostgreSQL independientes
- **Responsabilidad:** Aislamiento de datos, escalabilidad independiente por servicio

---

## Soporte y Contribución

Para reportar problemas o sugerir mejoras, abrir un issue en el repositorio: https://github.com/JeanpierreSam/DAD-examen-u2

---

## Licencia

Proyecto desarrollado para fines académicos - Universidad Peruana Unión.
