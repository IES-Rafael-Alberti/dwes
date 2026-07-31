# Proyecto evaluable: integración híbrida de fuentes heterogéneas

Este proyecto acredita RA9 mediante una aplicación propia que integra una API
externa y un conjunto de datos local abierto. Es una **transferencia evaluable**:
se aplican los patrones practicados en P2A a otro dominio y a fuentes aprobadas,
sin copiar el Catálogo Cultural Híbrido ni su implementación de Open Library.

## Resultado esperado

La entrega debe recuperar, transformar y reunir información heterogénea en un
modelo neutral respecto de los proveedores; persistirla de forma idempotente;
analizarla con una librería de Big Data/BI y demostrar el comportamiento normal y
los fallos mediante pruebas completamente offline.

No se publica una solución. Las decisiones de dominio, contrato, identidad,
mapeo, políticas y pruebas forman parte de la autoría que debe defenderse.

## Diferencia entre P2A y P2B

| Actividad | Función | Fuentes y decisiones |
|---|---|---|
| [P2A: práctica guiada](../../03-ejercicios/practica-integracion/README.md) | Ensayar los patrones mediante checkpoints y nombres de pruebas orientativos. | Dominio cultural y recorrido guiado. |
| **P2B: este proyecto** | Transferir lo aprendido en una entrega independiente y evaluable. | Dominio, API, dataset, modelo, análisis y política propios, aprobados por el docente. |

Copiar clases, paquetes, fixtures o decisiones del ejemplo canónico no demuestra
la transferencia. Sí se permite citarlo como referencia y reutilizar conceptos
generales justificando su adaptación.

## Organización, punto de partida y tiempo disponible

El proyecto se realiza **por parejas**, en un único repositorio compartido y con
defensa individual de las decisiones. Una entrega individual requiere acuerdo
previo con el docente, pero conserva el mismo corte mínimo. No se exige trabajo
fuera del presupuesto de aula para completar ese corte.

El presupuesto es de **cinco sesiones de 55 minutos**. Se parte del **scaffold
neutral usado en P2A**, facilitado por el docente: estructura Maven, `pom.xml`
base para Java 25/Spring Boot 4 y configuración vacía. Además, se reutilizan como
referencia los patrones ya practicados en P2A para adaptadores, fixtures, dobles
HTTP y pruebas offline. Se pueden adaptar esos patrones y nombres; no hay que
reconstruir la infraestructura mecánica desde cero. El scaffold no contiene
dominio, proveedores, DTO, mapeos, identidad, pregunta de análisis ni solución de
este proyecto, y no se copia P1.

| Sesión | Trabajo principal | Cierre observable |
|---|---|---|
| Trámite previo | Propuesta y aprobación de fuentes | `docs/fuentes.md` aprobado o fuentes de reserva asignadas en el LMS |
| 1 | Transferencia, scaffold y contrato | Checklist de diferencia, modelo neutral, identidad y mapeos decididos |
| 2 | Dos adaptadores offline | Fixtures mínimos y recuperación/mapeo reproducibles |
| 3 | Repositorio propio | Restricción única e ingesta repetida sin duplicados |
| 4 | Fallo representativo y análisis | Estado consistente y resultado BI determinista |
| 5 | Integración y cierre | Suite y empaquetado offline, documentos enlazados y defensa preparada |

Al terminar la sesión 5 se aplica el corte: primero se entrega el núcleo
obligatorio descrito a continuación. Concurrencia conflictiva, cobertura
operativa exhaustiva, diagnóstico avanzado y demás refinamientos solo se abordan
cuando el núcleo ya es reproducible; aportan evidencia para niveles superiores,
pero no amplían el tiempo ni sustituyen ningún CE.

## Paso 1: aprobación de las fuentes

Cada estudiante o equipo propone una **API externa** y un **dataset local abierto**
relacionados con un problema concreto. El docente también puede asignarlos. No se
inicia la implementación hasta recibir aprobación escrita en `docs/fuentes.md`.

`docs/fuentes.md` es el registro formal de la decisión y debe contener este bloque,
sin publicar como recomendación ninguna pareja de proveedores todavía no
verificada:

| Campo obligatorio | Contenido |
|---|---|
| Estado | `PENDIENTE`, `APROBADA`, `RECHAZADA` o `RESERVA_LMS_ASIGNADA` |
| Docente/aprobador | Nombre de la persona que decide; no puede quedar en blanco al cerrar el trámite |
| Fecha de decisión | Fecha ISO `AAAA-MM-DD`; `—` mientras esté pendiente |
| Fecha y hora límite | Plazo comunicado por el docente, siempre anterior al inicio de la sesión 1 |
| API | Nombre, operación, versión si existe y URL oficial exacta del contrato consultado |
| Dataset | Nombre, versión/fecha de corte y URL oficial exacta de descarga o catálogo |
| Condiciones | Términos/licencia, atribución, cuota, caché, autenticación, coste y permiso para redistribuir fixtures |
| Decisión y observaciones | Motivo de aprobación o rechazo y cualquier condición que deba cumplir la implementación |
| Reserva curada | `—` si se aprueba la propuesta; si se activa la reserva, identificador y enlace de la asignación privada publicada por el docente en el LMS |

Si la propuesta se rechaza o sigue sin decisión al vencer el plazo, el equipo deja
de esperar y usa la **reserva curada que el docente le asigne en el LMS**. Debe
cambiar el estado a `RESERVA_LMS_ASIGNADA`, registrar aprobador y fecha y copiar
las versiones, URL y condiciones verificadas de esa asignación. No se improvisa
otra pareja ni se implementa con estado `PENDIENTE`.

La propuesta debe permitir comprobar:

| Comprobación | Información mínima para aprobar |
|---|---|
| Disponibilidad | URL oficial, operación necesaria, estabilidad conocida y fecha de consulta. |
| Términos y licencia | Condiciones de uso de la API y licencia del dataset; permiso de reutilización y redistribución de los fixtures. |
| Atribución | Texto, enlace o metadatos que deben conservarse y mostrarse. |
| Límites | Cuota, frecuencia, volumen máximo y política de caché permitida. |
| Autenticación y coste | Método de autenticación, ausencia o límite de coste y forma de trabajar sin credenciales en la corrección. |
| Viabilidad offline | Posibilidad de crear fixtures pequeños, legales, estables y sin datos personales innecesarios. |

La fuente se rechazará si obliga a publicar secretos, pagar para corregir, llamar a
producción durante las pruebas, redistribuir contenido sin permiso o tratar datos
personales que no sean imprescindibles. Un cambio posterior de fuente requiere
nueva aprobación y actualización de la trazabilidad.

## Corte mínimo obligatorio

El corte mínimo acredita una integración completa pero pequeña. Las secciones
siguientes indican el comportamiento exigible a toda entrega; los refinamientos
de nivel superior se separan después para que el calendario no convierta todo en
obligatorio.

### 1. Contrato neutral y procedencia

- Definir un modelo normalizado que no replique nombres ni DTO de un proveedor.
- Establecer una identidad estable, como `(fuente, identificadorExterno)`, y sus
  invariantes.
- Conservar en cada registro la fuente, URL canónica, licencia o términos
  aplicables, fecha de recuperación y, cuando proceda, versión del dataset.
- Documentar el mapeo de ambas fuentes y qué campos se descartan o transforman.

### 2. Repositorio propio e ingesta idempotente

- Crear un repositorio derivado a partir de ambas fuentes.
- Repetir la misma ingesta sin duplicar registros.
- Actualizar de forma explícita solo los campos permitidos.
- Proteger la identidad con una restricción coherente en persistencia.
- Mantener el estado anterior si falla la escritura del lote básico y realizar la
  llamada remota fuera de la transacción de escritura.

### 3. Adaptadores y funcionamiento offline

- Separar el adaptador del dataset local, el adaptador HTTP y el modelo interno.
- Limitar ruta, parámetros, resultados, tamaño de respuesta y tiempo de espera.
- Sustituir el proveedor por un servidor HTTP simulado o doble equivalente en las
  pruebas. Los fixtures deben estar versionados y ser redistribuibles.
- Prohibir DNS y acceso a Internet real en la verificación offline.

### 4. Fallos controlados mínimos

El corte mínimo prueba offline **al menos un contrato de fallo controlado
representativo y aplicable a la fuente aprobada**: por ejemplo, timeout, `5xx`,
`429` o respuesta inválida. Debe terminar en tiempo acotado, conservar el estado
previo y producir un diagnóstico útil sin revelar datos sensibles. La prueba y la
ejecución del proyecto deben ser reproducibles sin red. No se acepta convertir un
fallo del proveedor en un resultado vacío indistinguible.

La cobertura separada de una matriz más amplia queda como refinamiento de nivel
superior **solo para los casos que contemple el contrato aprobado**:

- timeout;
- respuesta `429` y respeto a `Retry-After` cuando el contrato lo contemple, sin
  reintentos automáticos ilimitados;
- respuesta `5xx`;
- cuerpo vacío, estructura inesperada o datos malformados.

### 5. Caché y control de tasa cuando procedan

El corte mínimo respeta las condiciones aprobadas de la fuente. La caché y el
control de tasa solo son obligatorios cuando los términos, la cuota o la carga
prevista los justifican. Si no proceden, se documenta una política equivalente de
protección del proveedor —por ejemplo, límites de concurrencia, volumen o
frecuencia de actualización— y se prueba de forma determinista. Cuando sí
procedan, diseñar clave normalizada, TTL, tamaño máximo, exclusión de fallos y
reloj/espera sustituibles aporta evidencia de nivel superior, al igual que probar
que un acierto de caché no consume cuota.

### 6. Seguridad y privacidad

- La persona usuaria no puede decidir esquema, host ni puerto del destino. En
  producción se usa una lista de orígenes HTTPS exactos aprobados; en pruebas,
  únicamente loopback y puerto dinámico.
- Se rechazan `userinfo`, redirecciones, puertos inesperados, fragmentos y una URI
  base con ruta o consulta. Los parámetros se codifican con un constructor de URI.
- Secretos y credenciales permanecen fuera del repositorio y de los fixtures.
- Los datos externos se validan como entrada no confiable.
- Los logs registran categoría de fallo, duración e identificadores técnicos
  mínimos; no registran tokens, cuerpos completos, consultas sensibles ni datos
  personales innecesarios.

Se aplicará además la [guía de seguridad de UD6](../../06-seguridad/README.md).

### 7. Análisis acotado de Big Data/BI — RA9.g

Usar `tablesaw-core` o una librería de Big Data/BI comparable aprobada por el
docente sobre los **datos ya normalizados del repositorio**. El análisis debe ser
pequeño, determinista y útil para el problema: por ejemplo, cobertura por fuente,
distribución por categoría o comparación temporal con datos suficientes.

El informe debe comparar la librería elegida con una alternativa, justificar la
decisión, describir el coste incorporado e interpretar resultados y limitaciones.
Nombrar una librería, añadir una dependencia sin usarla, ejecutar solo un
`COUNT/GROUP BY` o mostrar una captura no acredita RA9.g.

### 8. Pruebas, reproducibilidad y documentación

La suite escrita por el alumnado debe cubrir como mínimo las siete primeras filas.
Las dos últimas son refinamientos para niveles superiores, salvo que las
condiciones aprobadas de la fuente hagan necesaria alguna en el núcleo. No se
evalúan respuestas, cabeceras ni políticas ausentes del contrato aprobado:

| Corte | Comportamiento | Nombre orientativo; puede adaptarse justificadamente |
|---|---|---|
| Obligatorio | Carga y procedencia del dataset | `LocalSourceTest` |
| Obligatorio | Invariantes del modelo neutral | `NormalizedModelTest` |
| Obligatorio | Contrato y mapeo del cliente con destino cerrado | `ExternalAdapterTest` |
| Obligatorio | Identidad, repetición sin duplicados y rollback básico | `IdempotentRepositoryTest` |
| Obligatorio | Resultado reproducible de Big Data/BI | `RepositoryAnalysisTest` |
| Obligatorio | Al menos un fallo controlado representativo del contrato aprobado y ejecución offline reproducible | `RepresentativeFailureTest` |
| Obligatorio | Política justificada y determinista de protección del proveedor: caché/tasa cuando procedan o medida equivalente | `ProviderProtectionPolicyTest` |
| Nivel superior | Matriz relevante de timeout, 429, 5xx y datos malformados, solo cuando sean aplicables | `ProviderFailureMatrixTest` |
| Nivel superior | Concurrencia/conflicto y resultado transaccional observable | `ConcurrentIngestionTest` |

La corrección debe poder repetirse sin red, cuentas personales, variables privadas
ni servicios externos.

## Refinamientos para niveles superiores

Solo se acometen después de pasar el corte mínimo:

- prueba concurrente que fuerza dos ingestas conflictivas y observa tanto el
  resultado persistido como el comportamiento transaccional, no solo la
  restricción única;
- clasificación independiente y determinista de los casos relevantes entre
  timeout, `429`, `5xx`, cuerpo vacío y datos malformados, sin exigir respuestas
  que el contrato aprobado no contemple;
- caché y control de tasa cuando procedan, con reloj/espera sustituibles, límites
  justificados y evidencia de que los fallos no se cachean; en otro caso, prueba
  determinista de la política equivalente de protección del proveedor;
- diagnóstico robusto que relaciona invariantes, límites transaccionales,
  decisiones de diseño y evidencias reproducibles.

## Ampliación opcional: una llamada de chat

Solo después de completar el núcleo se admite una llamada aislada a un modelo de
chat para redactar una interpretación de un resultado ya calculado. Debe estar
desactivada por defecto, disponer de doble offline y no recibir datos personales.

Esta ampliación **no sustituye RA9.g**, no mejora por sí sola el nivel máximo y no
es requisito de evaluación. No forman parte del proyecto RAG, almacenes
vectoriales, MCP ni agentes.

## Entregables

1. Código fuente y configuración reproducible, sin binarios ni secretos.
2. Fixtures mínimos del dataset y del proveedor, con licencia y procedencia.
3. Suite de pruebas del alumnado, ejecutable offline.
4. `docs/fuentes.md`: aprobación, términos, licencias, atribución, cuotas, coste y
   viabilidad offline.
5. `docs/arquitectura.md`: límites, adaptadores, modelo neutral, identidad,
   transacciones, política de protección del proveedor, SSRF y decisiones de
   logs; caché/tasa solo cuando procedan.
6. `docs/analisis-bi.md`: comparación de librerías, análisis, resultados,
   interpretación y limitaciones.
7. `docs/informe-final.md`: instrucciones de reproducción, matriz RA9.a-h con
   enlaces a evidencias, checklist de transferencia, diagnóstico de fallos y
   limitaciones conocidas.
8. `README.md`: propósito, arranque, comandos y autoría declarada.

La [matriz oficial de evidencias](ra-ce-evidencias.md) indica cómo se observa cada
criterio. La [rúbrica RA9](rubrica-ra9.md) describe los niveles de logro sin
ponderaciones inventadas.

## Árbol de entrega

```text
proyecto-apellido-nombre/
├── README.md
├── pom.xml
├── docs/
│   ├── fuentes.md
│   ├── arquitectura.md
│   ├── analisis-bi.md
│   └── informe-final.md
└── src/
    ├── main/
    │   ├── java/...
    │   └── resources/dataset/...
    └── test/
        ├── java/...
        └── resources/fixtures/...
```

No se entregan `target/`, volcados masivos, `.env`, credenciales, datos personales,
dependencias descargadas ni una copia del proyecto P1.

## Checklist operativo de transferencia

`docs/informe-final.md` debe incluir y enlazar evidencias para este checklist. No
basta marcar «sí»: cada fila debe nombrar la decisión propia y el archivo o prueba
que permite comprobarla.

| Diferencia material respecto de P1/P2A | Decisión de este proyecto | Evidencia enlazada |
|---|---|---|
| Dominio y problema de uso distintos | Describir usuarios, necesidad y vocabulario propio | Documento/modelo/controlador correspondiente |
| API y dataset distintos y aprobados | Copiar estado, versiones y URL desde `docs/fuentes.md` | Aprobación y fixtures |
| DTO y mapeo propios | Identificar campos de origen, transformaciones y descartes | DTO/adaptador y test de contrato |
| Identidad e invariantes propias | Explicar clave estable, colisiones y actualizaciones permitidas | Restricción, servicio y test de repetición |
| Pregunta de análisis distinta | Formular la pregunta útil antes de indicar el resultado | `docs/analisis-bi.md` y test determinista |

Si una fila reproduce la decisión canónica sin una adaptación material y
defendible, la transferencia correspondiente no queda demostrada.

## Hitos de seguimiento

| Hito | Evidencia para revisión |
|---|---|
| H1 — Fuentes y transferencia | `docs/fuentes.md` cerrado y checklist de diferencia iniciado antes de implementar. |
| H2 — Contrato e integración | Modelo neutral, identidad, mapeos y dos adaptadores offline. |
| H3 — Repositorio | Ingesta repetible, restricción única y rollback básico demostrados. |
| H4 — Análisis y operación mínima | Librería BI usada y fallo representativo conservando estado. |
| H5 — Cierre | Suite y paquete offline, informe trazado y defensa. |

El docente puede solicitar una demostración breve en cada hito. Una captura no
sustituye el artefacto ni el test reproducible.

## Comandos de aceptación

La línea base es **Java 25** y **Maven 3.9.x**. Si el equipo añade Maven Wrapper al
proyecto, se usa `./mvnw` y la versión 3.9.x fijada por ese wrapper; si no, se usa
`mvn` 3.9.x. Se elige una ruta y se mantiene en todos los comandos, sin alternar
entre Maven global y wrapper.

Antes de la evaluación offline se ejecutan **con red**, desde una copia limpia y
con el mismo ciclo de vida que se evaluará, estos comandos exactos (sustituyendo
`mvn` por `./mvnw` en todos ellos si existe wrapper):

```bash
java -version
mvn -version
mvn clean test
mvn package
```

Esta preparación debe descargar las dependencias directas, los plugins de Maven y
todos los artefactos transitivos que esos dos ciclos necesiten. Un
`dependency:go-offline` aislado puede ayudar, pero no sustituye la ejecución online
de los mismos ciclos.

Después se deshabilita la red y se repiten los comandos de evaluación
correspondientes con `-o`:

```bash
java -version
mvn -version
mvn -o clean test
mvn -o package
```

Los bloques críticos pueden aislarse con `mvn -o -Dtest=NombreTest test`, pero
estos comandos diagnósticos no sustituyen los dos ciclos completos anteriores.

Se repetirá la suite con la red deshabilitada. Los resultados deben ser iguales y
no puede existir ninguna petición hacia el dominio real.

## Defensa oral

La defensa relaciona decisiones, código, pruebas e informe. Pueden plantearse,
entre otras, estas preguntas:

- ¿Por qué las fuentes cumplen términos, licencia, atribución y viabilidad offline?
- ¿Qué evita que el modelo interno dependa del DTO del proveedor?
- ¿Cómo se construye la identidad y qué prueba demuestra la idempotencia?
- ¿Qué ocurre ante los fallos relevantes del contrato aprobado y qué prueba
  demuestra al menos uno de forma controlada?
- ¿Dónde se impide que una entrada del usuario se convierta en una petición SSRF?
- ¿Qué política protege al proveedor y por qué caché o control de tasa proceden o
  no proceden en este contrato?
- ¿Qué información queda fuera de los logs y por qué?
- ¿Qué aporta la librería Big Data/BI frente a la alternativa comparada?
- ¿Cómo se reproduce la entrega sin red ni secretos?
- ¿Qué limitación aceptaría corregir primero y qué evidencia cambiaría?

La persona autora debe poder localizar y explicar la evidencia. Nombrar una
tecnología sin demostrar su uso y sus decisiones no satisface ningún criterio.
