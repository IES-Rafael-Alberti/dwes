# Práctica incremental: integración de un catálogo cultural

Construirás una aplicación Spring Boot que combina un *snapshot* local CC0 y una
API pública en un repositorio propio normalizado. El resultado debe poder
corregirse completamente sin Internet: las llamadas reales sirven para explorar
el contrato, pero las pruebas y las evidencias usan datos versionados y dobles del
proveedor.

Esta práctica aplica los patrones del [Catálogo Cultural Híbrido](../../02-ejemplos/catalogo-cultural-hibrido/README.md), pero no consiste en copiarlo. Debes tomar y justificar tus propias decisiones de contrato, nombres, fuente y análisis.

## Resultado esperado

Al finalizar, el proyecto debe:

- recuperar y normalizar registros de dos fuentes heterogéneas;
- conservar identidad, procedencia, licencia y fecha de recuperación;
- repetir una ingesta sin crear duplicados;
- limitar el coste sobre el proveedor mediante caché y control de tasa;
- responder de forma controlada ante `429`, `5xx`, timeout y JSON no válido;
- analizar con una librería tabular la cobertura de los datos normalizados;
- demostrar todo lo anterior con pruebas offline y un informe reproducible.

## Alcance

### Núcleo obligatorio

El núcleo comprende los nueve checkpoints de esta guía. La aplicación debe
funcionar y poder evaluarse sin credenciales, sin proveedor real y sin servicios
de IA. No se pide frontend, autenticación, OpenAPI ni un CRUD completo.

### Ampliación opcional: una llamada de chat

Después de cerrar el núcleo, se puede añadir **una llamada a un chat model con
Spring AI** para interpretar un agregado ya calculado por la aplicación. Debe
estar aislada, desactivada por defecto y sustituida por un doble en pruebas. Solo
aporta enriquecimiento opcional: **no se acepta por sí sola como evidencia de
RA9.g** ni sustituye el uso de la librería analítica, el análisis reproducible o
ningún checkpoint.

Quedan expresamente excluidos **RAG, almacenes vectoriales, MCP y agentes**.

## Prerrequisitos

- Java 25, Maven y Spring Boot 4.
- Cliente HTTP con `WebClient`, DTO, JPA y pruebas JUnit 5.
- Un servidor HTTP simulado, por ejemplo WireMock, en puerto dinámico.
- Consulta previa de la [guía transversal de seguridad](../../06-seguridad/README.md).

No es necesario disponer de una API key. Si la fuente elegida la exige, el
docente facilitará un contrato o fixture equivalente y la corrección seguirá
siendo offline.

## Punto de partida y secuencia estimada

El proyecto se construye desde un esqueleto Maven propio. Si el calendario
post-Navidad no permite dedicar una sesión completa a tareas mecánicas, el docente
puede facilitar un **scaffold neutral** limitado a directorios, plantillas vacías de
configuración y el esqueleto de `pom.xml`. No incluye clases de aplicación ni de
prueba: cada estudiante debe crear la clase `Application` y, en el checkpoint 1,
escribir `ApplicationContextTest`. Tampoco incluye entidades, DTO, fixtures,
cliente, ingesta, políticas de seguridad, caché, throttle ni análisis. No se
distribuye el código del ejemplo P1 como plantilla.

| Sesión | Trabajo principal | Cierre observable |
|---|---|---|
| 1 | Checkpoint 1 | Base ejecutable y ficha inicial de fuente |
| 2 | Checkpoints 2 y 3 | Snapshot y contrato normalizado |
| 3 | Checkpoints 4 y 5 | Adaptador y dobles sin Internet |
| 4 | Checkpoint 6 | Ingesta idempotente |
| 5 | Checkpoint 7 | Fallos controlados |
| 6 | Checkpoint 8 | Caché y throttle mínimos |
| 7 | Checkpoint 9 | Análisis con Tablesaw |
| 8 | Checkpoint 9 | Auditoría, informe y entrega reproducible |

La secuencia es orientativa: no se avanza al checkpoint siguiente mientras el
entregable observable y su verificación no sean reproducibles.

## Ruta rápida de verificación

El modo offline de Maven necesita en el repositorio local las dependencias, los
plugins y **todos sus artefactos transitivos**. Antes de desconectar, docente y
estudiante deben ejecutar con red, desde la raíz del proyecto, todos los comandos
exactos de los checkpoints:

```bash
mvn -Dtest=ApplicationContextTest test
mvn -Dtest=LocalDatasetTest test
mvn -Dtest=NormalizedContractTest test
mvn -Dtest=ProviderUriPolicyTest,ExternalProviderClientTest test
mvn -Dtest=ExternalProviderClientTest test
mvn -Dtest=IdempotentIngestionTest test
mvn -Dtest=ProviderFailureTest test
mvn -Dtest=CacheAndThrottleTest test
mvn -Dtest=RepositoryAnalysisTest test
mvn test
mvn package
```

Después se desconecta la red y se repiten los comandos correspondientes con
`-o`:

```bash
mvn -o -Dtest=ApplicationContextTest test
mvn -o -Dtest=LocalDatasetTest test
mvn -o -Dtest=NormalizedContractTest test
mvn -o -Dtest=ProviderUriPolicyTest,ExternalProviderClientTest test
mvn -o -Dtest=ExternalProviderClientTest test
mvn -o -Dtest=IdempotentIngestionTest test
mvn -o -Dtest=ProviderFailureTest test
mvn -o -Dtest=CacheAndThrottleTest test
mvn -o -Dtest=RepositoryAnalysisTest test
mvn -o test
mvn -o package
```

Ambos comandos deben funcionar sin red. Las pruebas que contacten con un dominio
externo, dependan de una cuenta personal o esperen tiempo real no son válidas como
evidencia.

## Checkpoints

### 1. Crear la base y analizar la fuente

Antes de ejecutar cualquier build, crea el scaffold Maven para **Java 25 y Spring
Boot 4**, su `pom.xml`, la clase de arranque, la configuración mínima sin secretos
y `ApplicationContextTest`, escrito por ti. Después selecciona una API de consulta
humana y documenta el problema que resuelve, contrato, campos necesarios, licencia
o condiciones de uso, límites, atribución y alternativa offline. Compara
brevemente las tecnologías que vas a reutilizar.

- **Entregable observable:** scaffold Maven completo, `pom.xml`, clase de arranque,
  `ApplicationContextTest` escrito por el estudiante y `docs/fuentes.md`, con URL
  oficial, fecha de consulta, campos elegidos, límites y decisiones justificadas.
- **Verificación:** revisión de enlaces y `mvn -o -Dtest=ApplicationContextTest
  test`; la configuración por defecto no debe exigir una credencial.
- **Evidencia RA9:** CE **a**, **b** y **f**: reutilización razonada, tecnologías
  identificadas y respeto al contrato de terceros.
- **Fallo que diagnosticar:** elegir una API porque “devuelve JSON” sin comprobar
  términos, cuota, estabilidad ni derecho de reutilización.

### 2. Versionar un snapshot local CC0 con procedencia

Incorpora un conjunto pequeño de registros culturales bajo CC0. Conserva solo los
campos necesarios y registra cómo, cuándo y desde dónde se obtuvo. El snapshot no
debe contener datos personales ni material cuya redistribución no esté permitida.

- **Entregable observable:** fixture en `src/main/resources/dataset/` y README de
  procedencia con fuente, licencia, fecha, transformación y política de refresco.
- **Verificación:** el estudiante crea `LocalDatasetTest`; `mvn -o
  -Dtest=LocalDatasetTest test` debe cargar el fixture desde el classpath y validar
  registros mínimos, identificadores y licencia.
- **Evidencia RA9:** CE **c** y **f**: recuperación, procesamiento y uso conforme de
  información existente.
- **Fallo que diagnosticar:** guardar una descarga sin URL canónica, licencia o
  fecha, de modo que nadie pueda reproducir ni auditar su origen.

### 3. Definir el contrato normalizado

Diseña un contrato propio independiente de los nombres de cada proveedor. Debe
representar identidad de fuente, identificador externo, título, procedencia,
licencia, URL canónica y fecha de recuperación; añade únicamente los campos que
use tu análisis.

- **Entregable observable:** DTO o record normalizado, modelo persistente y una
  tabla en `docs/contrato-normalizado.md` que explique cada correspondencia.
- **Verificación:** el estudiante crea `NormalizedContractTest`; `mvn -o
  -Dtest=NormalizedContractTest test` cubre ambas fuentes y rechaza los invariantes
  incumplidos.
- **Evidencia RA9:** CE **b**, **c**, **d** y **e**: selección técnica,
  transformación y contrato del repositorio derivado.
- **Fallo que diagnosticar:** propagar el DTO externo hasta JPA; un cambio de nombre
  del proveedor rompe entonces todo el dominio.

### 4. Implementar el adaptador HTTP externo

Implementa una única operación de consulta con ruta fija, parámetros codificados,
resultado acotado, identificación del cliente y timeout. La URI base procede de
configuración validada; la entrada del usuario nunca decide host, esquema ni
puerto.

La política distingue entornos. En producción admite solo el origen HTTPS exacto
acordado (esquema, host y puerto por defecto), con URI base raíz; rechaza `userinfo`,
puerto explícito o inesperado, path distinto de `/`, query y fragmento, y no sigue
redirecciones. En tests admite únicamente HTTP hacia una dirección loopback y el
puerto dinámico del doble. Cualquier otro destino debe fallar antes de enviar red.

- **Entregable observable:** adaptador HTTP y propiedades tipadas, sin secretos
  versionados ni llamadas desde una transacción de persistencia; el estudiante
  crea `ProviderUriPolicyTest` y `ExternalProviderClientTest`.
- **Verificación:** `mvn -o -Dtest=ProviderUriPolicyTest,ExternalProviderClientTest
  test`. La matriz de configuración rechaza hosts externos en test, HTTP en
  producción, `userinfo`, puertos explícitos/inesperados, path no raíz, query y
  fragmento. El test del cliente comprueba ruta, parámetros, cabeceras, límite y
  que una respuesta `3xx` no provoca una segunda petición.
- **Evidencia RA9:** CE **c**, **e** y **f**: recuperación mediante una librería
  específica y programación contra un contrato de terceros.
- **Fallo que diagnosticar:** concatenar una URL recibida del usuario o dejar el
  timeout indefinido; ambos convierten una consulta simple en un riesgo operativo.

### 5. Sustituir el proveedor por dobles offline

Modela respuestas del proveedor con un servidor HTTP simulado en puerto dinámico.
Versiona fixtures mínimos propios; no copies respuestas masivas ni material sin
permiso. Ninguna prueba debe resolver el dominio real.

- **Entregable observable:** fixtures de prueba y escenarios de éxito, vacío y
  mapeo parcial escritos por el estudiante y ejecutados contra el doble en
  `ExternalProviderClientTest`.
- **Verificación:** desconecta la red y ejecuta
  `mvn -o -Dtest=ExternalProviderClientTest test` dos veces con el mismo resultado.
- **Evidencia RA9:** CE **h**: pruebas reproducibles y depuración en el límite de
  integración.
- **Fallo que diagnosticar:** un “test de integración” que llama a producción y
  cambia según la red, la cuota o los datos del día.

### 6. Construir una ingesta idempotente

Persiste por la identidad estable `(fuente, identificadorExterno)`. Una segunda
importación actualiza lo permitido sin duplicar; un fallo de persistencia no deja
un lote parcial. Realiza la llamada remota antes de abrir la transacción.

- **Entregable observable:** servicio de ingesta y restricción única coherente con
  el contrato de identidad, más `IdempotentIngestionTest` escrito por el
  estudiante.
- **Verificación:** `mvn -o -Dtest=IdempotentIngestionTest test` debe importar dos
  veces, comprobar el mismo recuento, verificar una actualización y demostrar el
  rollback de un lote inválido.
- **Evidencia RA9:** CE **d** y **h**: repositorio propio idempotente y pruebas de su
  consistencia.
- **Fallo que diagnosticar:** usar el título como identidad o ejecutar siempre
  `save` sin búsqueda/restricción; aparecen duplicados y carreras.

### 7. Controlar fallos del proveedor

Clasifica por separado `429`, `5xx`, timeout y cuerpo vacío o JSON malformado. No
ocultes la causa con un `catch (Exception)` ni reintentes automáticamente. La
aplicación debe conservar el estado previo y emitir un error útil que no exponga
datos sensibles.

- **Entregable observable:** errores de integración diferenciados y cuatro
  escenarios controlados por el estudiante en `ProviderFailureTest`.
- **Verificación:** `mvn -o -Dtest=ProviderFailureTest test`; cada escenario debe
  finalizar dentro de un tiempo acotado y no modificar el repositorio.
- **Evidencia RA9:** CE **c** y **h**: procesamiento robusto, diagnóstico y
  documentación de fallos.
- **Fallo que diagnosticar:** tratar `429` como resultado vacío; se pierde la
  diferencia entre “no hay datos” y “el proveedor ha rechazado la petición”.

### 8. Añadir caché y control de tasa

Cachea únicamente respuestas válidas con clave normalizada, tamaño máximo y TTL.
Aplica el control de tasa antes de cada fallo de caché. Inyecta reloj y espera para
que las pruebas no duerman; los fallos no deben entrar en caché.

Mantén una implementación proporcional: una caché Caffeine acotada y un único
intervalo mínimo global entre accesos al proveedor son suficientes. No se piden
algoritmos distribuidos, reintentos, circuit breaker ni métricas avanzadas.

- **Entregable observable:** política documentada de caché/throttle y componentes
  medibles mediante dobles, más `CacheAndThrottleTest` escrito por el estudiante.
- **Verificación:** `mvn -o -Dtest=CacheAndThrottleTest test` debe demostrar un único
  acceso para consultas equivalentes, límites distintos como claves distintas,
  fallos no cacheados y separación entre peticiones sin espera real.
- **Evidencia RA9:** CE **e**, **f** y **h**: uso justificado de librerías, cuidado
  del proveedor y pruebas deterministas.
- **Fallo que diagnosticar:** colocar el throttle fuera de la caché; incluso los
  aciertos esperan y consumen capacidad innecesariamente.

### 9. Reunir el análisis y la evidencia final

El criterio oficial exige literalmente: “Se han analizado y utilizado librerías
de código relacionadas con Big Data e inteligencia de negocios, para incorporar
análisis e inteligencia de datos proveniente de repositorios.” Por ello, un
agregado elemental o una llamada opcional de IA, aislados, no bastan.

Incorpora `tech.tablesaw:tablesaw-core:0.44.4` y úsala sobre registros ya
normalizados obtenidos del repositorio, no sobre una respuesta remota cruda.
Construye una tabla en memoria y realiza **un análisis pequeño y significativo**:
una tabla de cobertura por fuente que muestre recuento y porcentaje de registros
con año y materia informados (puedes sustituir esos campos por otros equivalentes
justificados por tu contrato). No se piden gráficos, notebooks, estadística
avanzada ni otro pipeline de datos.

La versión 0.44.4 está publicada en Maven Central y Tablesaw declara Java 8 o
superior; la entrega confirma su uso real compilando el proyecto con Java 25. Se
añade solo `tablesaw-core`: no se incorporan módulos de plot, `tablesaw-jsplot` ni
datasets:

```xml
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>0.44.4</version>
</dependency>
```

> **Preparación docente — huella de Tablesaw.** Antes de llevar el material al
> aula, ejecuta `mvn dependency:tree` y guarda el árbol resuelto. Resuelve después
> el proyecto en un repositorio local desechable (por ejemplo,
> `mvn -Dmaven.repo.local=/tmp/ud6-tablesaw-m2 dependency:go-offline`) y registra su
> tamaño con `du -sh /tmp/ud6-tablesaw-m2`. Así quedan documentados el grafo
> transitivo y el tamaño real de la caché del equipo docente sin inventar cifras.

En `docs/informe-final.md`, compara brevemente Tablesaw con una alternativa
razonable (por ejemplo JPQL/SQL o Streams), explica por qué la eliges para este
análisis y reconoce sus costes. Interpreta el resultado, el valor añadido de cruzar
las fuentes y sus limitaciones. Completa la auditoría de seguridad y licencias. La
mera consulta `COUNT/GROUP BY`, un agregado escrito solo con Streams o la llamada
opcional de chat **no satisfacen RA9.g** sin este uso razonado de una librería de
análisis/BI.

- **Entregable observable:** dependencia versionada en `pom.xml`, servicio de
  análisis con Tablesaw, `RepositoryAnalysisTest` escrito por el estudiante y
  `docs/informe-final.md` con comparación, resultado, interpretación, matriz
  RA9.a-h enlazada y registro de comandos.
- **Verificación:** `mvn -o -Dtest=RepositoryAnalysisTest test && mvn -o test &&
  mvn -o package`, sin red ni variables privadas. `RepositoryAnalysisTest` carga
  un conjunto fijo en H2 y comprueba recuentos y porcentajes esperados por fuente.
- **Evidencia RA9:** CE **a–h**, con evidencia individual; CE **g** corresponde al
  análisis reproducible respaldado por Tablesaw y su justificación.
- **Fallo que diagnosticar:** enumerar clases o capturas sin relacionarlas con un
  comportamiento observable y una prueba repetible.

## Lista de comprobación de seguridad y licencias

Aplica la [auditoría completa de consumo de terceros](../../06-seguridad/README.md#lista-de-auditoria-del-alumnado) y confirma, como mínimo:

- [ ] No se han versionado claves, tokens, credenciales ni archivos `.env`.
- [ ] Esquema, host y puerto del proveedor pertenecen a una lista permitida.
- [ ] Producción acepta solo el origen HTTPS exacto y raíz; test, solo loopback HTTP
      con puerto dinámico. Se rechazan `userinfo`, puertos inesperados, path base,
      query, fragmento y redirecciones.
- [ ] Las rutas son fijas y los parámetros se codifican con el constructor de URI.
- [ ] Hay límites de tiempo, tamaño de respuesta, número de resultados y longitud.
- [ ] `429`, `5xx`, timeout y JSON no válido tienen pruebas offline independientes.
- [ ] Caché, cuota y control de tasa respetan los términos del proveedor.
- [ ] Los logs no contienen consultas sensibles, cuerpos completos ni secretos.
- [ ] Cada dataset/registro conserva fuente, URL, licencia y fecha de recuperación.
- [ ] Dependencias y plugins proceden de repositorios confiables y están revisados.
- [ ] Los fixtures son mínimos, redistribuibles y no contienen datos personales.

## Estructura de entrega

```text
proyecto/
├── README.md
├── pom.xml
├── docs/
│   ├── fuentes.md
│   ├── contrato-normalizado.md
│   └── informe-final.md
└── src/
    ├── main/
    │   ├── java/...
    │   └── resources/dataset/...
    └── test/
        ├── java/.../ApplicationContextTest.java
        ├── java/.../LocalDatasetTest.java
        ├── java/.../NormalizedContractTest.java
        ├── java/.../ProviderUriPolicyTest.java
        ├── java/.../ExternalProviderClientTest.java
        ├── java/.../IdempotentIngestionTest.java
        ├── java/.../ProviderFailureTest.java
        ├── java/.../CacheAndThrottleTest.java
        ├── java/.../RepositoryAnalysisTest.java
        └── resources/fixtures/...
```

Todas las clases de prueba del árbol son **entregables escritos por el estudiante**;
se permite organizarlas en los paquetes correspondientes sin cambiar sus nombres.
El [proyecto evaluable P2B](../../04-proyectos/proyecto-integracion-hibrida/README.md)
define después sus propios comandos de aceptación y exige transferencia a otras
fuentes. No se suministran pruebas ocultas ni código de solución en esta práctica.

La entrega incluye código fuente, fixtures permitidos, pruebas e informes. No
incluye `target/`, secretos, volcados completos del proveedor, cuentas personales
ni una solución copiada del ejemplo P1.

## Trazabilidad mínima de RA9

| CE | Evidencia observable que debe enlazar el informe final |
|---|---|
| **a** | Sección de `docs/fuentes.md` que identifica código/datos reutilizados y explica la ventaja o coste evitado. |
| **b** | Tabla comparativa y decisión de tecnologías en `docs/fuentes.md`, incluida la alternativa a Tablesaw. |
| **c** | Adaptadores y normalización de las dos fuentes, demostrados por `LocalDatasetTest` y `ExternalProviderClientTest`. |
| **d** | Repositorio normalizado e ingesta idempotente, demostrados por `NormalizedContractTest` e `IdempotentIngestionTest`. |
| **e** | Dependencias realmente usadas en `pom.xml` y funcionalidades observables en `ExternalProviderClientTest`, `CacheAndThrottleTest` y `RepositoryAnalysisTest`. |
| **f** | Servicio basado en datos/código de terceros, con contrato, licencia y atribución documentados y verificados por los tests de dataset y proveedor. |
| **g** | Comparación y justificación de Tablesaw, servicio que la usa sobre datos normalizados, resultado interpretado y `RepositoryAnalysisTest` offline. |
| **h** | Las nueve clases de prueba del árbol, diagnóstico de fallos, auditoría cumplimentada y registro reproducible de `mvn -o test`/`package`. |
