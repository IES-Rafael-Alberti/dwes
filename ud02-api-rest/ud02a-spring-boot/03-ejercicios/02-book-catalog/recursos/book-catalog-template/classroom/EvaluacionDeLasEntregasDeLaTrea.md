# 📘 Proyecto: Catálogo de Libros — Desarrollo Web en Entorno Servidor

## Documento didáctico de evaluación (para Moodle / actas)

---

## 🔹 Módulo profesional

**Desarrollo Web en Entorno Servidor (DWES)**
**Curso:** 2º CFGS Desarrollo de Aplicaciones Web
**Duración:** 205 h (presenciales + proyecto final)

---

## 🔹 Resultados de aprendizaje (RA) y Criterios de evaluación (CE)

| RA      | Descripción resumida                                                                               | Criterios implicados (según RD 217/2022 y currículo andaluz)                                                                                                                                                                           |
| ------- | -------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RA1** | Desarrolla aplicaciones web en entorno servidor analizando su arquitectura y aplicando estándares. | CE1.a: Se han descrito las características del entorno servidor.<br>CE1.b: Se han identificado las tecnologías necesarias.<br>CE1.e: Se han diseñado endpoints siguiendo buenas prácticas REST.                                        |
| **RA2** | Implementa aplicaciones web dinámicas utilizando frameworks de desarrollo en servidor.             | CE2.a: Se ha instalado y configurado un framework.<br>CE2.c: Se han desarrollado controladores y servicios.<br>CE2.d: Se ha verificado la comunicación entre capas (controlador-servicio-repositorio).                                 |
| **RA3** | Implementa servicios web integrando componentes y acceso a datos.                                  | CE3.a: Se han creado entidades y repositorios.<br>CE3.b: Se han desarrollado servicios para el acceso a datos.<br>CE3.d: Se han manejado excepciones y códigos HTTP adecuados.<br>CE3.e: Se ha documentado el API y realizado pruebas. |
| **RA4** | Desarrolla aplicaciones seguras, fiables y bien estructuradas.                                     | CE4.a: Se han aplicado patrones MVC.<br>CE4.b: Se han manejado errores de forma centralizada.<br>CE4.d: Se han realizado pruebas unitarias con mocks y frameworks de test.                                                             |

---

## 🔹 Rúbricas de evaluación por entregas

### **Entrega 1 – Controlador “eco del endpoint” (texto plano)**

**Objetivo:** Definir endpoints correctos para GET, POST, PUT y DELETE sin lógica.

| Criterio                       | Descripción                                                           | Ponderación |
| ------------------------------ | --------------------------------------------------------------------- | ----------- |
| Arquitectura REST (RA1, CE1.e) | Endpoints bien definidos, con rutas claras (`/books`, `/books/{id}`). | 30 %        |
| Funcionamiento básico          | Cada endpoint devuelve el texto esperado (`GET /books`, etc.).        | 50 %        |
| Buenas prácticas               | Código limpio, nombres coherentes, compilación sin errores.           | 20 %        |

**Checklist para nota 10:**

* [ ] Todos los endpoints existen y responden al verbo HTTP correcto.
* [ ] Mensajes de salida coinciden exactamente con el formato pedido.
* [ ] No hay warnings ni errores de compilación.
* [ ] Código indentado y comentado mínimamente.

---

### **Entrega 2 – JSON sin ResponseEntity**

**Objetivo:** Responder con objetos Java y listas JSON; mantener los datos en memoria.

| Criterio                     | Descripción                                      | Ponderación |
| ---------------------------- | ------------------------------------------------ | ----------- |
| Modelo de datos (RA2, CE2.c) | Clase `Book` y DTO correctamente definidos.      | 25 %        |
| Serialización JSON           | Endpoints devuelven y aceptan JSON válido.       | 40 %        |
| Lógica básica                | CRUD completo en memoria.                        | 25 %        |
| Buenas prácticas             | Código legible y probado con curl o HTTP Client. | 10 %        |

**Checklist para nota 10:**

* [ ] `Book` y `CreateBookDTO` bien definidos (atributos, tipos).
* [ ] `GET /books` devuelve `[]` o lista con libros creados.
* [ ] `POST /books` genera ID incremental.
* [ ] `PUT` actualiza correctamente los datos.
* [ ] `DELETE` elimina por ID.

---

### **Entrega 3 – Controlador con ResponseEntity**

**Objetivo:** Incorporar `ResponseEntity` y gestionar correctamente los códigos HTTP.

| Criterio                           | Descripción                                          | Ponderación |
| ---------------------------------- | ---------------------------------------------------- | ----------- |
| Uso de ResponseEntity (RA3, CE3.d) | Se devuelven códigos HTTP adecuados (201, 200, 204). | 40 %        |
| Cabeceras                          | `Location` correcta en `POST /books`.                | 20 %        |
| Correctitud de respuesta           | Cuerpo y código coherentes.                          | 30 %        |
| Limpieza del controlador           | Código simple y sin duplicaciones.                   | 10 %        |

**Checklist para nota 10:**

* [ ] `POST /books` devuelve `201 Created` + cabecera `Location`.
* [ ] `DELETE /books/{id}` devuelve `204 No Content`.
* [ ] Resto devuelven `200 OK`.
* [ ] No hay lógica redundante en el controlador.

---

### **Entrega 4 – Introducir servicio (BookService)**

**Objetivo:** Separar la lógica de negocio y aplicar patrón MVC.

| Criterio                                     | Descripción                                      | Ponderación |
| -------------------------------------------- | ------------------------------------------------ | ----------- |
| Separación de responsabilidades (RA4, CE4.a) | Lógica en servicio, controlador limpio.          | 40 %        |
| Inyección de dependencias                    | `@Service` y constructor de inyección correctos. | 25 %        |
| Pruebas                                      | Test o mock para verificar delegación.           | 20 %        |
| Legibilidad                                  | Código coherente, sin dependencias cíclicas.     | 15 %        |

**Checklist para nota 10:**

* [ ] `BookService` implementa CRUD en memoria.
* [ ] Controlador solo delega (`service.create(...)`).
* [ ] Se usa `@Service` y se inyecta por constructor.
* [ ] Tests con `@MockBean` verifican llamadas.

---

### **Entrega 5 – Manejo de errores en servicio (excepciones)**

**Objetivo:** Gestionar errores específicos desde el servicio.

| Criterio                         | Descripción                                      | Ponderación |
| -------------------------------- | ------------------------------------------------ | ----------- |
| Excepciones propias (RA3, CE3.d) | `BookNotFoundException`, `InvalidBookException`. | 40 %        |
| Mapeo en controlador             | Controlador convierte a 404 / 400.               | 35 %        |
| Robustez                         | No hay NullPointer ni errores sin capturar.      | 15 %        |
| Documentación                    | Mensajes claros en errores.                      | 10 %        |

**Checklist para nota 10:**

* [ ] Excepciones personalizadas creadas correctamente.
* [ ] `BookService` lanza la excepción adecuada.
* [ ] Controlador devuelve `404` o `400`.
* [ ] Mensajes coherentes y JSON válido.

---

### **Entrega 6 – Global Controller Advice**

**Objetivo:** Centralizar el manejo de errores con `@RestControllerAdvice`.

| Criterio                                    | Descripción                                           | Ponderación |
| ------------------------------------------- | ----------------------------------------------------- | ----------- |
| Uso de `@RestControllerAdvice` (RA4, CE4.b) | Excepciones mapeadas globalmente.                     | 40 %        |
| Coherencia de respuestas                    | Formato JSON uniforme (`error`, `timestamp`, `path`). | 30 %        |
| Limpieza del controlador                    | Eliminación de `try/catch` locales.                   | 20 %        |
| Validación adicional                        | Gestión de `@Valid` y errores 400.                    | 10 %        |

**Checklist para nota 10:**

* [ ] Existe clase `GlobalExceptionHandler` con `@RestControllerAdvice`.
* [ ] `@ExceptionHandler(BookNotFoundException)` → 404 JSON.
* [ ] `@ExceptionHandler(MethodArgumentNotValidException)` → 400 JSON.
* [ ] Controladores sin `try/catch`.
* [ ] Test `BookControllerAdviceGlobalTest` pasa correctamente.

---

## 🔹 Escala global de calificación

| Calificación | Interpretación | Umbral numérico                                     |
| ------------ | -------------- | --------------------------------------------------- |
| **10–9**     | Excelente      | Cumple todos los checklist sin fallos               |
| **8–9**      | Muy bueno      | Cumple >85 % de checklist, faltan detalles menores  |
| **7–8**      | Bueno          | Faltan 2–3 puntos clave                             |
| **6–7**      | Adecuado       | CRUD funcional pero errores parciales               |
| **5–6**      | Suficiente     | Funciona lo básico, pero no cumple buenas prácticas |
| **<5**       | Insuficiente   | Errores graves o incompleto                         |

---

## 🔹 Recomendación de evidencias

* Commit individual por entrega (`Entrega 3: ResponseEntity implementado`).
* Capturas de Postman/HTTP Client para cada endpoint.
* Test unitario o ejecución en CI con resultados verdes.
* Archivo `README.md` final con resumen técnico.

---
