# 🏦 BankInc Backend – Prueba Técnica Java Senior

Proyecto backend desarrollado en **Spring Boot 3 + Java 21 + PostgreSQL**.  
Simula la gestión de **tarjetas bancarias** y **transacciones** con endpoints REST, documentación Swagger y pruebas unitarias con Mockito.

---

## 🚀 Tecnologías utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot 3.5.6**
- 💾 **PostgreSQL 15**
- 🧩 **Spring Data JPA**
- 🧰 **Lombok**
- 🧪 **JUnit 5 / Mockito**
- 📘 **Swagger (springdoc-openapi)**
- 🐳 **Docker Compose**
- ⚙️ **Maven**

---

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- [Java 21](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker Desktop](https://www.docker.com/)
- [Git](https://git-scm.com/)

---

## 🐘 Configuración de la base de datos (PostgreSQL con Docker)

1. En la raíz del proyecto encontrarás un archivo `docker-compose.yml` con el siguiente contenido:

   ```yaml
   version: '3.9'
   services:
     postgres:
       image: postgres:15
       container_name: postgres_bankinc
       restart: always
       environment:
         POSTGRES_DB: bankinc
         POSTGRES_USER: postgres
         POSTGRES_PASSWORD: postgres
       ports:
         - "5432:5432"
       volumes:
         - pgdata:/var/lib/postgresql/data
   volumes:
     pgdata:
   
---
## Ejecuta
Ejecuta el siguiente comando para levantar la base de datos:

docker-compose up -d

Verifica que este corriendo:

docker ps


---
## Para generar el swagger

una vez se inicie la aplicacion, ingresa a:

http://localhost:8080/swagger-ui/index.html

