# 💰 Spendy - API REST para Gestión de Finanzas Personales

Spendy es una **API REST** para la gestión de finanzas personales que permite a cada usuario registrar **ingresos y gastos**, organizarlos por **categorías** y consultar **reportes mensuales** con totales y balances.

La API está diseñada siguiendo **principios REST**, con **autenticación JWT**, separación de datos por usuario, consultas avanzadas y **documentación OpenAPI (Swagger)**.

---

## 🚀 Funcionalidades principales

- Registro y autenticación de usuarios mediante **JWT (stateless)**
- Gestión de **transacciones** (ingresos y gastos)
- Gestión de **categorías** personalizadas
- **Separación de datos por usuario**
- Consultas avanzadas con filtros
- **Paginación tradicional y por cursor**
- Reportes mensuales con agregaciones
- Actualización parcial mediante **JSON-Patch (RFC 6902)**
- Documentación interactiva con **Swagger / OpenAPI**

---

## 🛠 Tecnologías utilizadas

- **Java 21**
- **Spring Boot**
  - Spring Web
  - Spring Data MongoDB
  - Spring Security
- **MongoDB**
- **JWT (JSON Web Tokens)**
- **Swagger / OpenAPI**

---

## 🧱 Diseño de la API

### Recursos principales

| Recurso | Descripción |
|-------|------------|
| `/api/v1/auth` | Registro y login |
| `/api/v1/transactions` | Ingresos y gastos |
| `/api/v1/categories` | Categorías |
| `/api/v1/reports` | Reportes y resúmenes |

---

## 🔐 Autenticación y seguridad

- Autenticación basada en **JWT**
- API **stateless** (sin sesiones)
- Header: `Authorization: Bearer <token>`

---

## ▶️ Ejecución del proyecto

Requisitos:
- Java 21
- MongoDB en ejecución
- Maven

Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

La API estará disponible en:
```bash
http://localhost:8080
```

---

## 🧪 Pruebas rápidas con curl

### Ejemplo de login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@spendy.com","password":"123456"}'
```

### Ejemplo de acceso protegido

```bash
curl http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer <token>"
```

---

## 📘 Documentación

Swagger UI disponible en:

http://localhost:8080/swagger-ui

---

## 🧠 Trabajo futuro

- Integración con frontend web o móvil
- Exportación de reportes (PDF / CSV)
- Presupuestos y alertas
- Métricas y mecanismos de resiliencia

---

## 🎓 Contexto académico

Este proyecto fue desarrollado como parte de la asignatura **Enxeñaría de Servizos**, integra los siguientes contenidos y lecciones de la asignatura:

- **Principios REST**
  - Diseño de recursos bien definidos.
  - Separación clara entre cliente y servidor.

- **Creación de servicios REST con Spring**
  - Implementación de controladores REST usando Spring Boot.
  - Uso de DTOs para desacoplar la API del modelo interno.

- **Uso correcto de verbos HTTP y códigos de estado**
  - GET, POST y PATCH según la semántica REST.
  - Respuestas adecuadas (`200`, `201`, `400`, `401`, `404`, etc.).

- **Autenticación y autorización**
  - Autenticación basada en **JWT (JSON Web Tokens)**.
  - Protección de recursos mediante cabecera `Authorization`.
  - Separación de datos por usuario autenticado.

- **Versionado de APIs**
  - Versionado explícito mediante la ruta (`/api/v1/...`).

- **Verbo PATCH y especificación JSON-Patch**
  - Actualizaciones parciales de recursos siguiendo el estándar RFC 6902.

- **Métodos avanzados de consulta a bases de datos**
  - Filtros dinámicos.
  - Consultas combinadas y agregaciones.

- **Paginación de consultas y uso de cursores**
  - Paginación clásica con `Pageable`.
  - Paginación basada en cursores para grandes volúmenes de datos.

- **Validación de datos**
  - Validación de entradas mediante **Hibernate Validator**.

- **Gestión centralizada de errores**
  - Manejo uniforme de excepciones con controladores globales.

- **Personalización de la serialización con Jackson**
  - Uso de vistas (`@JsonView`) para controlar la información expuesta.

- **Documentación de la API**
  - Documentación automática mediante **OpenAPI / Swagger**.
  - Pruebas interactivas de los endpoints desde la interfaz web.

---

## 👤 Autor

**José Carlos Leo Fernández**  
Universidad de Santiago de Compostela (USC)  
Universidad Autónoma de Yucatán (UADY)
