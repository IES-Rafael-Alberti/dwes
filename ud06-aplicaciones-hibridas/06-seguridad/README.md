# Seguridad al consumir APIs y datos de terceros

Una integración externa amplía el perímetro de confianza: la aplicación ya no
controla la disponibilidad, el contenido, la licencia ni el coste de todos sus
datos. Esta guía define los controles mínimos para la [práctica incremental](../03-ejercicios/practica-integracion/README.md) y su auditoría final.

## Modelo de amenazas

| Activo | Amenaza | Consecuencia | Control principal |
|---|---|---|---|
| Credenciales del proveedor | Clave publicada en Git, logs o errores | Consumo fraudulento y agotamiento de cuota | Configuración externa, mínimo privilegio y rotación |
| Red interna | URI controlada por entrada (SSRF) | Acceso a metadata cloud o servicios internos | Origen exacto por entorno, ruta fija y redirecciones desactivadas |
| Memoria y CPU | JSON enorme, profundo o especialmente costoso | Denegación de servicio | Límites de cuerpo, tiempo, resultados y estructura |
| Repositorio derivado | Datos falsos, incompletos o cambiantes | Decisiones incorrectas y corrupción de estado | Validación, normalización, transacción y procedencia |
| Disponibilidad | Timeout, `429` o `5xx` | Hilos bloqueados, cascada de fallos o baneo | Timeout, caché, throttle y degradación controlada |
| Privacidad | Consultas o cuerpos completos en logs | Divulgación de intereses o datos personales | Minimización, niveles de log y redacción |
| Propiedad intelectual | Uso sin licencia o atribución | Retirada del material y responsabilidad legal | Registro de fuente, licencia, fecha y condiciones |
| Cadena de suministro | Dependencia vulnerable o suplantada | Ejecución de código no confiable | Gestión de versiones, análisis y repositorios confiables |

El proveedor, sus respuestas y sus redirecciones son **no confiables**, aunque la
conexión use HTTPS. HTTPS protege el transporte; no valida la intención ni la
calidad del contenido recibido.

## Secretos y configuración

- Las claves se inyectan mediante variables de entorno o un gestor de secretos.
- `application.properties` contiene nombres de propiedades y valores no sensibles,
  nunca credenciales reales.
- Los secretos no aparecen en URLs, excepciones, trazas, fixtures ni capturas.
- La aplicación falla de forma explícita si una clave obligatoria falta; no incluye
  una credencial de demostración como respaldo.
- Cada clave tiene el mínimo alcance posible y una política de rotación/revocación.

Configuración versionable:

```properties
provider.base-url=https://api.example.org
provider.api-key=${PROVIDER_API_KEY}
provider.request-timeout=5s
```

No se proporciona un valor por defecto para `PROVIDER_API_KEY`: escribir uno en el
repositorio, aunque se presente como temporal, lo convierte en un secreto expuesto.

## SSRF y lista permitida de URI

Este patrón es inseguro porque la entrada decide el destino completo:

```java
webClient.get()
    .uri(urlIntroducidaPorUsuario)
    .retrieve();
```

Un atacante podría solicitar `http://127.0.0.1`, una IP privada o el servicio de
metadata de una plataforma cloud. Bloquear solo la cadena `localhost` no basta:
existen IP literales, DNS cambiante, IPv6 y redirecciones.

El patrón seguro fija y valida el origen al arrancar, antes de construir el cliente,
y permite únicamente una ruta conocida. La política no es la misma en producción
que en pruebas:

| Entorno | Único destino permitido | Rechazos obligatorios |
|---|---|---|
| Producción | Origen exacto `https://api.example.org` (HTTPS, host exacto y puerto 443 implícito), con URI base raíz | HTTP, otro host, `userinfo`, cualquier puerto explícito o inesperado, path distinto de vacío o `/`, query, fragmento y redirecciones |
| Test | `http://127.0.0.1:{puerto-dinámico}` o equivalente loopback verificado | HTTPS/HTTP a hosts no loopback, puerto ausente/no dinámico, `userinfo`, path base, query, fragmento y redirecciones |

Aceptar «cualquier host configurable» y bloquear después unas pocas cadenas no es
una lista permitida. En producción se compara el origen normalizado completo; en
test se comprueba con `InetAddress.isLoopbackAddress()` o una política equivalente,
no con una coincidencia parcial de texto. `WebClient.Builder` conserva además la
personalización de codecs de Spring Boot 4:

```java
@ConfigurationProperties("provider")
public record ProviderProperties(URI baseUrl, Duration requestTimeout) {
    public ProviderProperties {
        if (!"https".equalsIgnoreCase(baseUrl.getScheme())
                || baseUrl.getUserInfo() != null
                || baseUrl.getPort() != -1
                || !"api.example.org".equalsIgnoreCase(baseUrl.getHost())
                || !(baseUrl.getPath().isEmpty() || "/".equals(baseUrl.getPath()))
                || baseUrl.getQuery() != null
                || baseUrl.getFragment() != null) {
            throw new IllegalArgumentException("Origen del proveedor no permitido");
        }
    }
}

var client = builder.baseUrl(properties.baseUrl().toString()).build();

return client.get()
    .uri(uriBuilder -> uriBuilder
        .path("/search.json")
        .queryParam("q", consultaValidada)
        .queryParam("limit", limiteAcotado)
        .build())
    .retrieve();
```

Además:

- no aceptes URLs de callback o descarga sin otra lista permitida específica;
- desactiva las redirecciones en el cliente HTTP para esta práctica; una respuesta
  `3xx` es un fallo del proveedor y nunca provoca otra petición;
- en producción, complementa la validación lógica con reglas de salida de red y
  bloqueo de rangos privados/reservados;
- no registres URLs si contienen credenciales o datos sensibles.

### Matriz offline obligatoria de configuración

El estudiante crea `ProviderUriPolicyTest` y ejecuta primero con red
`mvn -Dtest=ProviderUriPolicyTest test` para resolver dependencias, plugins y
transitivos. Durante la evaluación sin red repite el mismo contrato con
`mvn -o -Dtest=ProviderUriPolicyTest test`. Como mínimo debe cubrir:

| Caso | Producción | Test |
|---|---:|---:|
| `https://api.example.org` o raíz `/` | Aceptar | Rechazar |
| Host externo distinto | Rechazar | Rechazar |
| `http://api.example.org` | Rechazar | Rechazar |
| Loopback HTTP con puerto dinámico del doble | Rechazar | Aceptar |
| Loopback HTTP sin puerto o host no loopback | Rechazar | Rechazar |
| `usuario@host` | Rechazar | Rechazar |
| Puerto explícito (`:443`, `:8443`) o no asignado al doble | Rechazar | Rechazar |
| Path base distinto de `/` | Rechazar | Rechazar |
| Query o fragmento en la URI base | Rechazar | Rechazar |

`ExternalProviderClientTest`, también escrito por el estudiante, configura en el
doble una respuesta `3xx` con `Location` externa o loopback y demuestra que no se
sigue: el doble recibe una sola petición y el destino indicado no recibe ninguna.
No hace falta que las pruebas abran una conexión externa.

## JSON no confiable y límites de tamaño

La deserialización correcta no convierte una respuesta en válida. Aplica límites
antes y después del mapeo:

1. acepta solo el tipo de contenido esperado;
2. limita el cuerpo en memoria y el tiempo de lectura;
3. usa DTO de frontera con los campos mínimos;
4. limita número de elementos, longitud de cadenas y profundidad útil;
5. valida identificadores, URLs y valores obligatorios antes de persistir;
6. rechaza cuerpo vacío, truncado o JSON malformado como fallo del proveedor.

Spring Boot 4 permite limitar el buffer usado por los codecs de WebClient:

```properties
spring.http.codecs.max-in-memory-size=256KB
```

El límite de `Content-Length` es una comprobación temprana útil, pero no suficiente:
la cabecera puede faltar o mentir. El límite efectivo debe aplicarse durante la
lectura. Con Jackson 3, las anotaciones de compatibilidad como
`com.fasterxml.jackson.annotation.JsonIgnoreProperties` mantienen ese paquete;
las APIs de databind pertenecen a `tools.jackson.*`. No mezcles imports de databind
de Jackson 2 con Boot 4.

## Timeout y fallos HTTP

Todo acceso remoto debe tener timeout de conexión y de respuesta. Un timeout no es
un resultado vacío y no debe mantener abierta una transacción JPA.

| Fallo | Tratamiento esperado |
|---|---|
| `429 Too Many Requests` | Propagar categoría de cuota; respetar `Retry-After` si se implementa espera; no reintentar a ciegas |
| `5xx` | Marcar indisponibilidad temporal; conservar estado previo; reintento solo si es seguro, limitado y con backoff+jitter |
| Timeout/conexión | Cancelar dentro del límite y degradar de forma controlada |
| `4xx` de contrato | No reintentar; corregir petición/configuración |
| Cuerpo vacío o malformado | No persistir parcialmente ni cachear la respuesta |

Para el alcance corto de UD6, la opción segura por defecto es **no reintentar
automáticamente**. Si se amplía, solo se reintentan operaciones idempotentes, con
un máximo pequeño, backoff, jitter y presupuesto temporal total.

## Cuotas, caché y control de tasa

- Acota el número de resultados solicitado aunque el proveedor permita más.
- Normaliza la clave de caché y añade los parámetros que cambian el resultado.
- Define TTL y tamaño máximo; no caches errores, nulos ni datos inválidos.
- Ejecuta el throttle solo en fallos de caché y antes de la llamada externa.
- Comparte el límite entre peticiones concurrentes; un contador por hilo no protege
  la cuota global.
- Evita el rastreo masivo y respeta `Retry-After`, términos de uso y política de
  cacheo.
- Documenta cuándo se sirve información potencialmente obsoleta y cómo se refresca.

Una caché reduce carga y latencia, pero no autoriza a conservar o redistribuir datos
más tiempo del permitido por la licencia.

## Logging y privacidad

En nivel `INFO` registra solo operación, duración, recuento y categoría estable de
fallo. Una consulta cruda, un cuerpo o una URL completa pueden revelar datos
personales y no deben aparecer por defecto.

Nunca registres:

- cabeceras `Authorization`, cookies o API keys;
- cuerpos completos del proveedor;
- consultas personales o términos de búsqueda en `INFO`;
- stack traces como respuesta pública;
- fixtures procedentes de tráfico real sin anonimizar y licencia compatible.

Los identificadores de correlación deben ser propios y opacos, no derivados de una
credencial o del texto consultado.

## Procedencia, licencia y atribución

Cada registro derivado debe conservar, cuando corresponda:

- proveedor y su identificador externo estable;
- URL canónica de la fuente;
- licencia o nota de condiciones de uso;
- instante de recuperación y versión/fecha del snapshot;
- transformación aplicada y política de refresco.

Antes de versionar un snapshot, comprueba que la licencia permite redistribuirlo.
CC0 permite reutilización amplia, pero la procedencia sigue siendo necesaria para
auditar calidad y reproducibilidad. No presupongas que todos los campos de una API
comparten licencia.

## Higiene de dependencias

- Usa los starters y versiones gestionadas por Spring Boot cuando proceda.
- No declares versiones arbitrarias para “resolver” un conflicto sin analizar el
  árbol de dependencias.
- Revisa `mvn dependency:tree` y las actualizaciones de seguridad.
- Descarga desde repositorios confiables y conserva el wrapper solo si está
  completo y verificado.
- Elimina dependencias sin uso; cada parser, cliente o extensión amplía la
  superficie de ataque.
- No desactives verificaciones TLS ni aceptes cualquier certificado para arreglar
  un entorno local.

## Dobles de prueba seguros

El servidor simulado debe usar puerto dinámico y fixtures mínimos versionados. Las
pruebas deben verificar destino, parámetros codificados, cabeceras no sensibles,
timeouts, límites y clasificación de errores.

- Nunca uses la URL de producción como valor por defecto en pruebas.
- Añade una salvaguarda que rechace hosts distintos de loopback en el perfil de
  test.
- Usa reloj y espera inyectables para probar throttle, TTL o backoff sin dormir.
- No grabes tráfico real que contenga tokens, cookies o datos personales.
- Un doble demuestra el contrato que se ha modelado; no prueba que el proveedor
  real esté disponible.

## Riesgos de la ampliación opcional de chat

Esta sección solo aplica al enriquecimiento opcional posterior al análisis de
**RA9.g**. La llamada de chat aislada no demuestra el criterio ni introduce una
unidad de IA.

| Riesgo | Control proporcional |
|---|---|
| Inyección de prompt desde datos externos | Delimitar los datos como no confiables y no permitir que el modelo active herramientas o acciones |
| Fuga de datos | Enviar solo el agregado mínimo, sin secretos, consultas personales ni registros completos |
| Respuesta no determinista o inventada | Conservar el análisis determinista como fuente de verdad y etiquetar la salida generada |
| Coste y dependencia del proveedor | Desactivada por defecto, cuota acotada y doble offline en pruebas |

La salida del chat no se persiste como hecho verificado ni decide la ingesta. RAG,
almacenes vectoriales, MCP y agentes quedan fuera de alcance.

## Lista de auditoría del alumnado

Adjunta esta lista cumplimentada al informe final de la práctica:

### Configuración y red

- [ ] No hay secretos en Git, historial, logs, URLs, fixtures ni capturas.
- [ ] Las propiedades sensibles proceden del entorno o de un gestor de secretos.
- [ ] Esquema, host y puerto se validan contra una lista permitida.
- [ ] Producción acepta solo el origen HTTPS exacto y una URI base raíz.
- [ ] Test acepta solo loopback HTTP con el puerto dinámico del doble.
- [ ] Se rechazan `userinfo`, puerto inesperado, path base, query y fragmento.
- [ ] El usuario solo controla parámetros codificados, no el destino de red.
- [ ] Las redirecciones están desactivadas y existe una prueba que lo demuestra.

### Entrada y resiliencia

- [ ] Hay límites de timeout, cuerpo, resultados, colecciones y cadenas.
- [ ] Los DTO externos se validan antes de entrar en el modelo persistente.
- [ ] `429`, `5xx`, timeout, vacío y JSON malformado son casos distintos.
- [ ] Un fallo remoto no deja datos parciales ni se guarda en caché.
- [ ] Caché, throttle y, si existe, reintento respetan contrato y cuota.

### Privacidad y cumplimiento

- [ ] Los logs omiten secretos, cuerpos y consultas sensibles.
- [ ] Cada fuente tiene URL, licencia/condiciones, fecha y atribución.
- [ ] Los snapshots y fixtures pueden redistribuirse y están minimizados.
- [ ] La política de actualización y posible obsolescencia está documentada.

### Cadena de suministro y pruebas

- [ ] Se ha revisado `mvn dependency:tree` y no hay dependencias innecesarias.
- [ ] No se ha desactivado TLS ni la validación de certificados.
- [ ] Todas las pruebas de evaluación funcionan sin Internet y en puerto dinámico.
- [ ] Los tests rechazan por configuración cualquier destino externo accidental.
- [ ] La ampliación de chat, si existe, está apagada por defecto y usa un doble.
