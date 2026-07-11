---
title: "Cómo usar BSON y HTTP/2 en Spring Boot"
tags: ["Spring Boot", "BSON", "HTTP/2", "Java", "APIs REST"]
date: 2025-10-01
author: "José Manuel Sánchez Álvarez"

output: 
  pdf_document:
    toc: true
    toc_depth: 2
    latex_engine: xelatex
header-includes:
  - \usepackage{polyglossia}
  - \setmainlanguage{spanish}
  - \renewcommand{\contentsname}{Contenido}
---

### **1. Usar BSON como formato de datos**
**BSON** (Binary JSON) es una excelente opción para trabajar con datos en formato binario. Puedes utilizar la biblioteca **org.mongodb.bson** para manejar BSON en tu proyecto Spring Boot.

#### Pasos:
1. **Agrega la dependencia BSON al archivo `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.mongodb</groupId>
       <artifactId>bson</artifactId>
       <version>4.9.0</version>
   </dependency>
   ```

2. **Crea un controlador para manejar BSON:**
   Modifica el `@RestController` para leer y escribir datos en BSON. Por ejemplo:
   ```java
   @RestController
   @RequestMapping("/api")
   public class BsonController {

       @PostMapping(value = "/data", consumes = "application/bson", produces = "application/bson")
       public ResponseEntity<byte[]> handleBson(@RequestBody byte[] bsonData) {
           // Deserializar BSON
           BsonDocument document = BsonDocument.parse(new String(bsonData));
           System.out.println("Recibido BSON: " + document.toJson());

           // Procesar y devolver BSON
           BsonDocument responseDocument = new BsonDocument("respuesta", new BsonString("OK"));
           return ResponseEntity.ok(responseDocument.toBsonBinary().getData());
       }
   }
   ```

3. **Configura los encabezados de tu cliente HTTP:**
   Usa el tipo de contenido `application/bson` para enviar y recibir datos en BSON.

---

### **2. Implementar HTTP/2**
Spring Boot soporta HTTP/2 de forma nativa cuando utilizas **Tomcat**, **Jetty** o **Undertow** como servidor integrado.

#### Pasos:
1. **Habilitar HTTP/2 en `application.properties`:**
   Si utilizas Tomcat, agrega las siguientes configuraciones:
   ```properties
   server.http2.enabled=true
   ```

2. **Usa certificados SSL (requeridos para HTTP/2 en la mayoría de navegadores):**
   Configura SSL correctamente:
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=tu-contraseña
   server.ssl.key-store-type=PKCS12
   ```

3. **Verifica la configuración:**
   Asegúrate de que HTTP/2 esté activo mediante herramientas como **cURL** o inspeccionando las conexiones en tu navegador.

---

### **Ventajas de esta configuración**
- **BSON:** Compacto y rápido para la serialización/deserialización, ideal para aplicaciones con alta demanda.
- **HTTP/2:** Ofrece multiplexación, reducción de latencia y mejor uso de conexiones.

---


