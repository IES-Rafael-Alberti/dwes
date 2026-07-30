¡Buena pregunta, José! El punto 9 del documento se refiere a **desarrollar aplicaciones web híbridas** utilizando librerías de código y repositorios heterogéneos de información. Vamos a desglosarlo con ejemplos prácticos y enfoque docente:

---

### 🧩 ¿Qué es una aplicación web híbrida?

Una aplicación híbrida **combina recursos propios con recursos externos**: código, datos, servicios, etc. No se limita a consumir una API, sino que puede integrar:

- **Librerías externas** (como Bootstrap, Chart.js, TensorFlow.js)
- **APIs públicas o privadas** (por ejemplo, OpenWeatherMap, Spotify, Google Maps)
- **Repositorios de datos** (CSV, JSON, XML, bases de datos abiertas, etc.)
- **Componentes de terceros** (widgets, SDKs, microservicios)

Es híbrida porque **no depende exclusivamente de lo que tú desarrollas**, sino que **reutiliza y adapta** lo que otros han creado.

---

### 🔍 Desglose de los subpuntos del criterio 9

| Subpunto | ¿Qué significa? | Ejemplo práctico |
|---------|------------------|------------------|
| a) Reutilización de código e información existente | Aprovechar librerías, APIs, datasets ya disponibles | Usar Leaflet.js para mapas interactivos en vez de programarlos desde cero |
| b) Identificación de librerías y tecnologías | Saber qué herramientas externas puedes integrar | Elegir Axios para peticiones HTTP o D3.js para visualización |
| c) Recuperar y procesar repositorios existentes | Consumir datos de fuentes externas | Leer datos de GitHub, Kaggle o una API REST y mostrarlos en tu app |
| d) Crear repositorios propios a partir de datos externos | Transformar datos públicos en tu propio backend | Extraer datos de Wikipedia y almacenarlos en tu BBDD para consultas locales |
| e) Usar librerías para funcionalidades específicas | Añadir capacidades sin reinventar la rueda | Autenticación con Firebase, gráficos con Chart.js |
| f) Programar usando código e información de terceros | Integrar módulos o servicios externos en tu lógica | Usar GPT API para generar texto o Stripe para pagos |
| g) Probar, depurar y documentar | Validar que todo funciona correctamente | Tests con Postman, documentación Swagger, README claro |

---

### ✅ ¿Consumir una API de terceros cumple el criterio?

**Sí, pero no es suficiente por sí solo.** Para cumplir el punto 9 completo, deberías:

- **Consumir una API externa** (por ejemplo, datos meteorológicos)
- **Procesar esos datos** (filtrar, transformar, mostrar)
- **Usar librerías externas** para visualización, seguridad, etc.
- **Documentar y probar** la integración

---

### 🧠 Enfoque docente sugerido

Si estás planteando un proyecto para DWES o PIA, podrías proponer:

- Una app que **consuma datos de una API pública** (por ejemplo, precios de criptomonedas)
- Que **los almacene en tu BBDD** y los muestre con **gráficos interactivos**
- Que use librerías como **Spring Security, JWT, Swagger, Chart.js**
- Que tenga un README con justificación, endpoints, pruebas y tecnologías usadas

Perfecto, José. Aquí tienes una propuesta de proyecto que cumple con el punto 9 de aplicaciones web híbridas y encaja perfectamente en el marco del ejercicio de API REST segura con Spring Boot:

---

## 🚀 Proyecto: *EcoViajes* – Planificador de rutas sostenibles con datos abiertos

### 🎯 Objetivo
Desarrollar una API REST segura que permita a los usuarios planificar rutas de viaje sostenibles, integrando datos de transporte público, puntos de interés ecológicos y condiciones meteorológicas, todo ello mediante fuentes externas y librerías especializadas.

---

### 🧩 Componentes híbridos

| Tipo de recurso | Ejemplo | Finalidad |
|----------------|---------|-----------|
| 🔗 API externa | [OpenTripMap](https://opentripmap.io), [OpenWeatherMap](https://openweathermap.org) | Datos de lugares y clima |
| 📦 Librerías | Spring Security, JWT, MapStruct, Swagger | Seguridad, documentación, mapeo |
| 🧠 Repositorio de datos | CSV de estaciones de tren, JSON de rutas ciclistas | Datos locales reutilizados |
| 🧰 Código de terceros | SDK de OpenTripMap, plantillas Swagger | Funcionalidad y documentación |

---

### 🧱 Estructura de entidades

- **Usuario**: `username`, `password`, `roles`
- **Ruta**: `origen`, `destino`, `modo_transporte`, `fecha`
- **LugarInteres**: `nombre`, `tipo`, `coordenadas`, `descripcion`
- **ClimaRuta**: `fecha`, `ubicacion`, `temperatura`, `condiciones`

---

### 🔐 Seguridad

- Autenticación con JWT
- Roles diferenciados (`admin`, `usuario`)
- Cifrado de contraseñas con BCrypt
- Control de acceso por endpoint

---

### 📊 Endpoints clave

| Entidad | Endpoint | Método | Descripción |
|--------|----------|--------|-------------|
| Usuario | `/api/usuarios` | GET/POST | Gestión de usuarios |
| Ruta | `/api/rutas` | GET/POST/PUT/DELETE | CRUD de rutas |
| LugarInteres | `/api/lugares` | GET | Consulta de lugares desde OpenTripMap |
| ClimaRuta | `/api/clima` | GET | Consulta de clima desde OpenWeatherMap |

---

### 🧪 Pruebas y documentación

- Swagger UI para documentación interactiva
- Postman para pruebas de endpoints
- README con justificación, dependencias, pruebas y principios REST

---

### 🧠 Valor docente

Este proyecto permite trabajar:

- Integración de APIs externas (criterio híbrido)
- Seguridad avanzada con JWT y roles
- Reutilización de datos abiertos
- Documentación profesional
- Separación cliente-servidor clara

---

===================================================================

Genial, José. Aquí tienes el **esqueleto funcional del proyecto EcoViajes**, listo para adaptar a tus prácticas docentes en DWES o PIA. Está pensado para ser **multiplataforma, seguro, documentado y fácilmente ampliable**:

---

## 🧱 Estructura de carpetas (Spring Boot)

```
ecoviajes/
├── src/
│   ├── main/
│   │   ├── java/com/ecoviajes/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── config/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/com/ecoviajes/
├── pom.xml
└── README.md
```

---

## 🔐 Dependencias clave (pom.xml)

```xml
<dependencies>
  <dependency> <!-- Spring Boot Web --> </dependency>
  <dependency> <!-- Spring Security + JWT --> </dependency>
  <dependency> <!-- MapStruct para mapeo DTO --> </dependency>
  <dependency> <!-- Swagger para documentación --> </dependency>
  <dependency> <!-- OpenFeign para consumir APIs externas --> </dependency>
  <dependency> <!-- H2 o PostgreSQL según entorno --> </dependency>
</dependencies>
```

---

## 🔧 Ejemplo de endpoint funcional

```java
@RestController
@RequestMapping("/api/clima")
public class ClimaController {

    @Autowired
    private ClimaService climaService;

    @GetMapping("/{ciudad}")
    public ResponseEntity<ClimaDTO> obtenerClima(@PathVariable String ciudad) {
        ClimaDTO clima = climaService.consultarClima(ciudad);
        return ResponseEntity.ok(clima);
    }
}
```

---

## 🌍 Servicio que consume OpenWeatherMap

```java
@Service
public class ClimaService {

    @Value("${openweather.api.key}")
    private String apiKey;

    public ClimaDTO consultarClima(String ciudad) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&appid=" + apiKey;
        // Lógica para consumir API y mapear respuesta
    }
}
```

---

## 📘 Swagger UI

- Accesible en `http://localhost:8080/swagger-ui/index.html`
- Documenta todos los endpoints REST
- Útil para pruebas y validación docente

---

## 🧪 Pruebas básicas con Postman

- Autenticación con JWT
- Prueba de `/api/rutas`, `/api/lugares`, `/api/clima`
- Validación de roles y seguridad

---

## 🧠 Extras opcionales para enriquecer

- Frontend con React o Thymeleaf
- Base de datos con PostgreSQL + Docker
- Integración con GitHub Actions para CI/CD
- Exportación de rutas en PDF o CSV

---

¿Quieres que te prepare un ejemplo de script de postinstalación para montar este proyecto en entorno docente (con base de datos, Swagger y datos de ejemplo)? También puedo ayudarte a convertirlo en plantilla para Moodle o GitHub Classroom.