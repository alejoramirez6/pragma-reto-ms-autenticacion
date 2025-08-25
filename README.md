# Microservicio de Autenticación - Reto Pragma

## 📋 Descripción
Microservicio de autenticación desarrollado para el reto de Pragma, implementando arquitectura hexagonal y patrones de diseño modernos.

## 🏗️ Arquitectura
- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **Clean Architecture**: Principios SOLID y separación de responsabilidades
- **Domain-Driven Design**: Modelos de dominio bien definidos

## 🚀 Tecnologías
- **Java 17**
- **Spring Boot 3.x**
- **Maven**
- **H2 Database** (desarrollo)
- **MapStruct** (mapeo de objetos)
- **JPA/Hibernate**

## 📁 Estructura del Proyecto
```
src/main/java/com/pragma/crediya/autenticacion/
├── domain/           # Modelos de dominio y lógica de negocio
├── usecase/          # Casos de uso de la aplicación
├── ports/            # Interfaces (puertos) del dominio
└── infrastructure/   # Adaptadores de entrada y salida
    ├── adapters/input/rest/     # Controladores REST
    └── adapters/output/persistence/  # Repositorios y entidades
```

## 🔧 Configuración

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+
- IDE compatible (IntelliJ IDEA, Eclipse, VS Code)

### Instalación
1. Clonar el repositorio:
```bash
git clone https://github.com/tu-usuario/pragma-reto-ms-autenticacion.git
cd pragma-reto-ms-autenticacion
```

2. Compilar el proyecto:
```bash
mvn clean compile
```

3. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

## 🌐 Endpoints

### Usuarios
- `POST /api/v1/usuarios` - Crear nuevo usuario
- `GET /api/v1/usuarios/{id}` - Obtener usuario por ID

## 🗄️ Base de Datos
- **H2 Database** para desarrollo
- Esquema automático con `schema.sql`
- Datos de prueba incluidos

## 🧪 Pruebas
```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con cobertura
mvn test jacoco:report
```

## 📦 Build
```bash
# Crear JAR ejecutable
mvn clean package

# Ejecutar JAR
java -jar target/ms-autenticacion-0.0.1-SNAPSHOT.jar
```

## 🔍 Monitoreo
- **Actuator**: Endpoints de salud y métricas
- **H2 Console**: http://localhost:8080/h2-console

## 📚 Documentación
- **API Docs**: Swagger UI disponible en `/swagger-ui.html`
- **Guía de Estudio**: Ver carpeta `docGuiaEstudio/`

## 🤝 Contribución
1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia
Este proyecto es parte del reto de Pragma.

## 👨‍💻 Autor
Desarrollado para el reto de Pragma - Arquitectura Hexagonal

---
**Nota**: Este microservicio es parte de un sistema más grande que incluye el microservicio de solicitudes.
