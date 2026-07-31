# RA9: criterios oficiales y evidencias del proyecto

Esta matriz conserva literalmente el RA9 y sus criterios de evaluación del
archivo oficial `00-planificacion/DAW2o-RA_CE-2025-2026.md` (fuera del árbol
público de MkDocs).
Relaciona cada criterio con evidencias observables; **no asigna calificaciones ni
ponderaciones**. La evidencia permite evaluar, pero su mera presencia no acredita
calidad ni nivel de logro.

## Resultado de aprendizaje oficial

> **9. Desarrolla aplicaciones web híbridas seleccionando y utilizando tecnologías, frameworks servidor y repositorios heterogéneos de información.**

## Trazabilidad de los criterios

| CE y redacción oficial | Artefactos observables | Pruebas reproducibles | Informe y defensa |
|---|---|---|---|
| **a)** Se han reconocido las ventajas que proporciona la reutilización de código y el aprovechamiento de información ya existente. | `docs/fuentes.md` identifica código, API y dataset reutilizados; compara crear frente a reutilizar y registra condiciones y costes. | Tests de los adaptadores muestran qué contratos se aprovechan sin llamar a producción. | Explicar una ventaja, un coste o riesgo y una alternativa descartada. Nombrar una fuente no basta. |
| **b)** Se han identificado tecnologías y frameworks aplicables en la creación de aplicaciones web híbridas. | Tabla de decisión en `docs/fuentes.md` y arquitectura con cliente HTTP, persistencia, doble, librería BI y protección del proveedor realmente elegidos. | La suite carga y usa las dependencias seleccionadas en comportamientos concretos. | Justificar cada elección frente a una alternativa y reconocer sus límites. Una lista de tecnologías no basta. |
| **c)** Se ha creado una aplicación web que recupere y procese repositorios de información ya existentes. | Adaptadores de API y dataset; mapeos al modelo neutral; fixtures con procedencia. | `LocalSourceTest`, `ExternalAdapterTest` y `RepresentativeFailureTest` o equivalentes demuestran recuperación, transformación y al menos un fallo controlado representativo del contrato aprobado. | Recorrer un dato desde cada origen hasta el contrato interno y explicar validaciones y descartes. |
| **d)** Se han creado repositorios específicos a partir de información existente en almacenes de información. | Modelo persistente propio, restricción de identidad y servicio de ingesta idempotente. | El nivel básico prueba invariantes y repetición sin duplicados; para niveles superiores, una prueba concurrente o conflictiva observa actualización o rechazo transaccional, ausencia de lote parcial y estado final coherente. | Explicar por qué la identidad es estable, mostrar el estado antes y después de dos ingestas y, cuando corresponda, diagnosticar el conflicto y justificar el límite transaccional. |
| **e)** Se han utilizado librerías de código y frameworks para incorporar funcionalidades específicas a una aplicación web. | Dependencias versionadas y componentes que usan cliente HTTP, persistencia, doble HTTP y análisis BI; caché/tasa solo si proceden. | Tests del adaptador, análisis y política aplicable de protección del proveedor demuestran funcionalidad aportada por esas librerías. | Relacionar cada dependencia relevante con una necesidad y señalar qué ocurriría al retirarla. Declararla sin uso observable no basta. |
| **f)** Se han programado servicios y aplicaciones web utilizando como base información y código generados por terceros. | Servicio integrado, contratos de terceros, licencias, términos, atribución, límites y versiones en `docs/fuentes.md`. | Tests de contrato y procedencia verifican los campos de terceros y su transformación sin depender del servicio real. | Defender el cumplimiento de términos, atribución, cuota y redistribución de fixtures. |
| **g)** Se han analizado y utilizado librerías de código relacionadas con Big Data e inteligencia de negocios, para incorporar análisis e inteligencia de datos proveniente de repositorios. | Servicio que usa Tablesaw o una librería Big Data/BI comparable aprobada sobre el repositorio normalizado; `docs/analisis-bi.md` compara, interpreta y limita. | `RepositoryAnalysisTest` o equivalente verifica con datos fijos el resultado del análisis. | Interpretar el resultado y justificar la librería frente a una alternativa. Una dependencia sin uso, un agregado elemental aislado o una llamada de IA no bastan. |
| **h)** Se han probado, depurado y documentado las aplicaciones generadas. | Suite del alumnado, fixtures, README, arquitectura, informe final, registro de comandos y diagnóstico de fallos. | `mvn -o clean test` y `mvn -o package` pasan sin red, secretos ni esperas reales; el mínimo cubre éxito, idempotencia, SSRF y al menos un fallo controlado representativo del contrato aprobado. Los niveles superiores amplían la matriz solo con casos aplicables. | Reproducir una prueba, interpretar un fallo y localizar la decisión documentada correspondiente. |

## Evidencias transversales obligatorias

| Ámbito | Evidencia mínima |
|---|---|
| Procedencia y licencia | Fuente y URL canónicas, licencia o términos, atribución, fecha de recuperación, transformación y permiso de redistribución de fixtures. |
| Seguridad | Lista de orígenes, política distinta para producción y test, rechazo previo de URI inseguras, límites de entrada/respuesta y ausencia de secretos. |
| Privacidad | Logs sin tokens, cuerpos completos, consultas sensibles ni datos personales innecesarios; fixtures minimizados. |
| Resiliencia | Al menos un fallo controlado representativo del contrato aprobado, con tiempo acotado y estado previo conservado; para niveles superiores, matriz relevante de timeout, 429, 5xx y datos malformados. Reintentos, si existen, seguros y limitados. |
| Uso responsable | Caché y control de tasa cuando los términos o la carga los justifiquen; en otro caso, política equivalente de protección del proveedor explícita y probada sin tiempo real. |
| Reproducibilidad | Java 25 y Maven 3.9.x —o wrapper 3.9.x del proyecto—; dependencias, plugins y transitivos preparados con los mismos ciclos online y comandos offline repetibles en una copia limpia. |
| Autoría | Historial o evidencias de proceso disponibles y defensa coherente con las decisiones implementadas. |

## Evidencia frente a calificación

- Un archivo existente puede estar incompleto, ser contradictorio o no
  corresponderse con el código; por tanto, no determina automáticamente un nivel.
- Una prueba que solo comprueba que el contexto arranca no demuestra integración,
  idempotencia, seguridad ni análisis.
- Una tecnología solo constituye evidencia cuando resuelve una necesidad,
  aparece usada en la implementación y su comportamiento se verifica.
- La defensa confirma comprensión y autoría; no reemplaza una entrega no
  reproducible ni añade requisitos ajenos al enunciado.

Los niveles se aplican con la [rúbrica específica de RA9](rubrica-ra9.md), siempre
sobre el conjunto coherente de artefactos, pruebas, documentación y defensa. No
se evalúan requisitos ausentes del contrato de la fuente aprobada.
