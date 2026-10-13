# Seguridad HTTP desde la primera petición

Una traza HTTP puede contener credenciales, identificadores de sesión y datos personales. Antes de compartirla hay que reducirla a la evidencia necesaria. Ocultar información después de una filtración no revoca automáticamente el secreto expuesto.

## HTTPS y TLS

HTTPS protege HTTP mediante TLS:

- cifra el tráfico frente a observadores de la red;
- detecta modificaciones durante el transporte;
- autentica al servidor mediante certificados y una cadena de confianza.

TLS no corrige autorización, inyección, XSS ni una gestión insegura de sesiones. Protege el canal, no toda la aplicación.

Nunca se debe desactivar la validación de certificados para "solucionar" un problema fuera de un laboratorio controlado. Opciones como `curl -k` o `--insecure` ocultan el fallo de confianza y permiten ataques de intermediario.

## Cookies

Una cookie de sesión debería evaluar, como mínimo:

| Atributo | Protección |
|---|---|
| `Secure` | Solo se envía mediante conexiones seguras |
| `HttpOnly` | JavaScript no puede leerla mediante `document.cookie` |
| `SameSite` | Limita el envío en contextos entre sitios |
| `Path` y `Domain` | Restringen dónde se envía |
| `Max-Age` o `Expires` | Definen persistencia cuando no es una cookie de sesión |

`HttpOnly` reduce el robo directo mediante JavaScript, pero no elimina XSS. `SameSite` ayuda frente a ciertos ataques CSRF, pero no sustituye todas las defensas necesarias.

## Cabeceras de respuesta

| Cabecera | Objetivo |
|---|---|
| `Strict-Transport-Security` | Indicar al navegador que use HTTPS durante el periodo declarado |
| `Content-Security-Policy` | Restringir orígenes y tipos de contenido ejecutable |
| `X-Content-Type-Options: nosniff` | Evitar inferencias de tipo incompatibles con el declarado |
| `Referrer-Policy` | Controlar información enviada en `Referer` |
| CSP `frame-ancestors` | Limitar qué sitios pueden incluir la página en un marco |

`X-Frame-Options` sigue apareciendo en sistemas existentes, pero `frame-ancestors` de CSP expresa una política más flexible. Una cabecera solo protege si su valor es correcto y compatible con la aplicación.

## Banners tecnológicos

`Server` y `X-Powered-By` pueden revelar productos o versiones. Además, no son prueba fiable de la arquitectura: un proxy puede sustituirlos o eliminarlos. En producción conviene minimizar información innecesaria, no confiar en ocultarla como defensa principal.

## Sanitizar una traza

Antes de entregar una captura o salida de `curl -v`/`http -v`:

1. Usa un servicio o aplicación de laboratorio, no una cuenta personal.
2. Sustituye valores de `Authorization` por `<REDACTED>`.
3. Sustituye cookies y tokens por `<REDACTED>` conservando solo el nombre del campo.
4. Elimina parámetros, cuerpos y cabeceras con datos personales.
5. Revisa metadatos visibles en capturas: usuario, rutas locales, pestañas y notificaciones.
6. Si un secreto real apareció en una entrega, revócalo o rótalo; editar la captura no basta.

Ejemplo:

```http
Authorization: Bearer <REDACTED>
Cookie: SESSION=<REDACTED>
Set-Cookie: SESSION=<REDACTED>; Secure; HttpOnly; SameSite=Lax
```

## Lista de comprobación

- [ ] La URL usa HTTPS cuando sale del equipo local.
- [ ] El certificado se valida correctamente.
- [ ] La entrega no contiene secretos ni identificadores reales.
- [ ] Las cookies sensibles tienen atributos justificados.
- [ ] Las cabeceras de seguridad se interpretan por su efecto, no por su mera presencia.
- [ ] Los errores no exponen trazas internas.

Esta guía se aplica al laboratorio HTTP y prepara la seguridad transversal de las unidades posteriores.
