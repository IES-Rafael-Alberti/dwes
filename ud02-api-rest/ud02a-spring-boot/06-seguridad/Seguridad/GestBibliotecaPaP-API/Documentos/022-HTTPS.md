Para configurar tu servidor Spring Boot para usar HTTPS en lugar de HTTP, sigue los pasos detallados a continuación:


### **1. Generar un Keystore**
El servidor necesita un keystore para almacenar el certificado SSL. Puedes generar un keystore con el comando `keytool` de Java:

```bash
keytool -genkeypair -alias selfsigned -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore keystore.p12 -validity 365
```

- **Explicación de los parámetros**:
  - `-alias`: Nombre del alias del certificado.
  - `-keyalg`: Algoritmo de clave, en este caso `RSA`.
  - `-keysize`: Tamaño de la clave, recomendado al menos 2048.
  - `-storetype`: Tipo de almacenamiento del keystore (`PKCS12` es estándar y moderno).
  - `-keystore`: Nombre del archivo keystore que se generará (`keystore.p12`).
  - `-validity`: Validez del certificado en días (en este caso, 1 año).

**Nota**: Se te pedirá configurar una contraseña para proteger el keystore. Recuerda esta contraseña, ya que la necesitarás en la configuración de Spring Boot.

---

### **2. Configurar el Keystore en `application.properties`**

En tu archivo `application.properties`, agrega la configuración para HTTPS:

```properties
# Habilitar HTTPS
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=tu_contraseña
server.ssl.key-alias=selfsigned

# Configurar el puerto HTTPS
server.port=8443
```

- **Explicación**:
  - `server.ssl.enabled=true`: Habilita HTTPS.
  - `server.ssl.key-store`: Ruta al archivo keystore (puedes ponerlo en el `resources` del proyecto para simplificar el uso de `classpath`).
  - `server.ssl.key-store-type`: El tipo del keystore (`PKCS12`).
  - `server.ssl.key-store-password`: La contraseña del keystore.
  - `server.ssl.key-alias`: El alias del certificado dentro del keystore.
  - `server.port=8443`: Cambia el puerto al estándar HTTPS (`8443`).

---

### **3. Mover el Keystore al Proyecto**

Coloca el archivo `keystore.p12` en el directorio `src/main/resources` de tu proyecto para que Spring Boot lo encuentre fácilmente.

---

### **4. Redirigir Tráfico HTTP a HTTPS (Opcional)**

Si también deseas redirigir solicitudes HTTP (puerto 8080 por defecto) a HTTPS, crea una configuración adicional en tu clase de configuración de seguridad (`SecurityConfig`) o un nuevo componente:

#### **Redirección con un `Servlet` Container Customizado**
```java
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> redirectHttpToHttps() {
        return factory -> factory.addAdditionalTomcatConnectors(createHttpConnector());
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443); // Redirige al puerto HTTPS
        return connector;
    }
}
```

---

### **5. Probar HTTPS**

1. **Ejecuta tu aplicación**:
   - Usa `https://localhost:8443` para probar la aplicación con HTTPS.
   - Si intentas acceder a `http://localhost:8080`, debería redirigir automáticamente a HTTPS si configuraste la redirección.

2. **Verifica el Certificado**:
   - Los navegadores mostrarán un aviso de seguridad porque estás usando un certificado autofirmado. Esto es normal para entornos de desarrollo. En producción, usa un certificado válido emitido por una CA (Autoridad de Certificación).

---

### **6. Producción: Usar Certificados Válidos**

Para producción, utiliza un certificado emitido por una Autoridad de Certificación (CA). Puedes usar:
- **Let's Encrypt**: Gratuito y automatizado.
- **Proveedor Comercial**: Si necesitas un certificado de pago.

La configuración es similar; solo reemplaza el keystore generado manualmente con el certificado emitido por la CA.

--- 

¡Y eso es todo! Ahora tu aplicación Spring Boot estará lista para servir contenido a través de HTTPS. 🚀
