# Sistema de Gestión Electro-Store

Este proyecto es una plataforma integral de planificación de recursos empresariales (ERP) y gestión de inventario diseñada para una tienda de electrodomésticos. El sistema está compuesto por una arquitectura dividida en dos componentes principales: un servidor backend basado en Spring Boot que expone una API REST, y un cliente de escritorio frontend desarrollado en JavaFX.

## Características Principales

* Gestión de Catálogo: Administración completa de productos, categorías, proveedores y clientes.
* Control de Operaciones: Registro y control de compras, ventas y guías de entrada/salida de almacén.
* Auditoría de Inventario (Kardex): Historial valorado de movimientos físicos y financieros por producto aplicando el método de Costo Promedio Ponderado (CPP).
* Generación de Reportes: Informes consolidados en formato PDF para análisis de ventas, compras y kardex (individual y general) mediante JasperReports.
* Seguridad y Sesión: Autenticación basada en tokens JWT con expiración automática de sesión tras periodos de inactividad detectados en el cliente.
* Respaldos Automatizados: Copias de seguridad de la base de datos realizables de forma manual y programadas automáticamente al cerrar la aplicación.

## Estructura del Proyecto

El sistema se divide en dos repositorios independientes:

### 1. Servidor Backend ([electro-store-api](https://github.com/reydiazz/electro-store-api))

El backend sigue un diseño arquitectónico basado en capas con la siguiente estructura de directorios:

```text
electro-store-api/
├── src/
│   ├── main/
│   │   ├── java/com/electro/store/api/
│   │   │   ├── domain/
│   │   │   │   ├── auth/          # Seguridad, usuarios y JWT
│   │   │   │   ├── buys/          # Compras y transacciones con proveedores
│   │   │   │   ├── client/        # Clientes
│   │   │   │   ├── employee/      # Empleados
│   │   │   │   ├── movement/      # Guías de entrada/salida de almacén
│   │   │   │   ├── product/       # Productos y categorías de inventario
│   │   │   │   ├── report/        # Lógica y servicios de JasperReports (Ventas, Compras, Kardex)
│   │   │   │   └── sale/          # Ventas y facturación
│   │   │   └── shared/            # Excepciones globales, configuraciones y utilidades
│   │   └── resources/
│   │       ├── application.properties # Configuración de base de datos y JWT
│   │       └── reports/           # Plantillas JRXML de JasperReports y logo corporativo
├── docker-compose.yml             # Servidor de base de datos SQL Server
└── pom.xml
```

### 2. Cliente Frontend (electro-store-ui)

El frontend de escritorio implementa el patrón Modelo-Vista-Controlador (MVC) adaptado a JavaFX:

```text
inventario/
├── src/
│   ├── main/
│   │   ├── java/com/store/inventario/
│   │   │   ├── component/         # Controladores generales (Dashboard, Ajustes, Login)
│   │   │   ├── module/            # Módulos de negocio alíneados al backend
│   │   │   │   ├── auth/          # Gestión de usuarios y seguridad
│   │   │   │   ├── buy/           # Vistas y servicios de compras
│   │   │   │   ├── movement/      # Vistas y servicios de guías
│   │   │   │   ├── person/        # Clientes y empleados
│   │   │   │   ├── product/       # Productos y categorías
│   │   │   │   ├── report/        # Descarga y generación de reportes PDF
│   │   │   │   └── supplier/      # Proveedores
│   │   │   └── shared/            # Configuraciones de API, portapapeles y notificaciones toast
│   │   └── resources/
│   │       ├── views/             # Plantillas de interfaz en formato FXML (JavaFX)
│   │       └── styles/            # Hojas de estilo CSS del sistema
└── pom.xml
```

## Requisitos del Sistema

* Java Development Kit (JDK) 21 o superior.
* Apache Maven 3.8 o superior.
* Docker y Docker Compose (para levantar la base de datos).

## Instalación y Configuración

### 1. Configuración del Backend y Base de Datos (con Docker)

1. Entre en la carpeta raíz del backend:
   ```bash
   cd electro-store-api
   ```
2. Inicie el contenedor del servidor de base de datos Microsoft SQL Server 2022 mediante Docker Compose:
   ```bash
   docker-compose up -d
   ```
   *Nota: Esto iniciará un contenedor llamado `electro_sqlserver` en el puerto `14330` con las credenciales por defecto configuradas en el archivo `application.properties`.*

3. Verifique que la base de datos `electro_db` esté creada en el servidor (el script de migración Flyway se ejecutará y creará las tablas automáticamente al iniciar el servidor).

4. Compile el servidor backend utilizando Maven:
   ```bash
   ./mvnw clean compile
   ```

### 2. Configuración del Frontend

1. Verifique la dirección URL base en la clase de configuración `src/main/java/com/store/inventario/shared/config/ApiConfig.java` para que apunte al puerto del backend (por defecto `http://localhost:8080/api`).
2. Compile el cliente frontend utilizando Maven:
   ```bash
   cd inventario
   ./mvnw clean compile
   ```

## Ejecución del Proyecto

Para iniciar el sistema completo, debe ejecutar ambos componentes en terminales separadas:

### 1. Iniciar el Backend
Desde la carpeta raíz del backend (`electro-store-api`), ejecute el siguiente comando:
```bash
./mvnw spring-boot:run
```
El servidor estará activo una vez que los registros indiquen el puerto de escucha (normalmente `8080`).

### 2. Iniciar el Frontend
Desde la carpeta raíz del frontend (`inventario`), ejecute el siguiente comando para abrir la aplicación de escritorio:
```bash
./mvnw javafx:run
```
Se abrirá la ventana de inicio de sesión (Login). Introduzca las credenciales de administrador autorizadas para acceder a la aplicación.
