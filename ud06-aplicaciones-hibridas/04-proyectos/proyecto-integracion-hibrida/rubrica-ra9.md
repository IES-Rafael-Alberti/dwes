# Rúbrica criterial del proyecto de integración híbrida — RA9

La rúbrica valora cada CE oficial mediante comportamientos observables. No usa
puntos, porcentajes ni ponderaciones. La decisión de nivel se apoya en la
[matriz de evidencias](ra-ce-evidencias.md), la ejecución offline, los informes y
la defensa; ninguna tecnología nombrada acredita por sí sola un criterio.

## Niveles de logro por criterio

| CE | No alcanzado | Suficiente | Notable | Sobresaliente |
|---|---|---|---|---|
| **RA9.a — reutilización** | No identifica qué reutiliza o usa fuentes sin comprobar su legitimidad. | Identifica API, dataset y código reutilizado y explica una ventaja básica. | Compara con alternativas, costes, riesgos y condiciones de mantenimiento. | Vincula la reutilización con decisiones verificables, límites y una estrategia sostenible de sustitución o actualización. |
| **RA9.b — tecnologías** | Enumera tecnologías sin relación con necesidades o incluye dependencias sin uso. | Selecciona tecnologías adecuadas para cliente, persistencia, pruebas y análisis, con justificación básica. | Compara alternativas y demuestra en pruebas la función específica de cada elección relevante. | Razona compatibilidad, coste operativo, mantenibilidad y límites; elimina tecnología accidental sin sobrearquitectura. |
| **RA9.c — recuperación y proceso** | Falta una fuente, se llama a producción en tests o los datos no se transforman de forma fiable. | Dos adaptadores recuperan y mapean fixtures válidos al modelo neutral offline; al menos un fallo representativo y aplicable al contrato se controla sin corromper estado. | Valida límites y datos parciales; distingue una matriz más amplia de timeout, 429, 5xx y formato malformado cuando esos casos sean aplicables. | Contratos y transformaciones quedan aislados, trazables y robustos ante cambios representativos, con diagnósticos precisos y pruebas deterministas. |
| **RA9.d — repositorio propio** | Persiste copias sin identidad estable o genera duplicados. | Crea un repositorio normalizado con identidad estable, restricción básica de unicidad e ingesta repetible sin duplicados. | Fuerza mediante una prueba observable dos ingestas concurrentes o conflictivas y demuestra, además de la unicidad, el comportamiento transaccional: actualización permitida o rechazo controlado, sin lote parcial y con estado final coherente. | Aporta invariantes y límites transaccionales claros, separa obtención y escritura y relaciona una prueba determinista de conflicto con diagnóstico robusto y evidencia de las decisiones de diseño. |
| **RA9.e — librerías y frameworks** | Las dependencias están sin uso observable o sustituyen decisiones que no se comprenden. | Usa librerías adecuadas para funcionalidades concretas y las relaciona con el código. | Verifica cliente, persistencia, dobles, análisis y la política aplicable de protección del proveedor mediante pruebas enfocadas. | Integra solo lo necesario, controla configuración y límites y explica con evidencia el valor y coste de cada librería relevante. |
| **RA9.f — base de terceros** | Ignora contrato, licencia, atribución, cuota o autenticación/coste. | El servicio usa información/código de terceros, documenta procedencia y condiciones esenciales y prueba una política justificada de protección del proveedor. | Conserva atribución y licencia en el flujo, aplica con precisión la política exigida por términos y carga y usa fixtures redistribuibles. | Demuestra cumplimiento de extremo a extremo, minimización, estrategia de actualización y respuesta segura ante cambios o retirada del proveedor. |
| **RA9.g — Big Data/BI** | Solo añade una dependencia, un agregado elemental, una captura o una llamada de IA. | Usa Tablesaw o una alternativa Big Data/BI aprobada sobre datos normalizados y obtiene un resultado reproducible. | Compara alternativas, prueba resultados fijos e interpreta utilidad y limitaciones. | El análisis es significativo, trazable y bien delimitado; justifica calidad de datos, sesgos/cobertura y coste de la librería sin ampliar innecesariamente el alcance. |
| **RA9.h — pruebas y documentación** | La entrega no se puede reproducir o carece de pruebas y documentación esenciales. | Suite reproducible offline cubre el flujo principal y al menos un fallo controlado representativo del contrato; README e informe permiten construir y ejecutar. | Cubre idempotencia, SSRF, privacidad, análisis, la matriz de fallos aplicable y la política justificada de protección del proveedor; los diagnósticos enlazan decisiones y tests. | La verificación es determinista y auditable en copia limpia; documentación, código y defensa son coherentes y explican límites y depuración con precisión. |

## Expectativas transversales

- **Seguridad:** no se aceptan destinos construidos desde entrada libre. La política
  SSRF, límites, redirecciones, secretos y datos externos no confiables se revisan
  en los CE donde aportan evidencia.
- **Pruebas:** deben ser escritas por el alumnado, deterministas y offline. Las
  capturas o pruebas manuales pueden complementar, nunca sustituir, la suite.
- **Contrato aprobado:** no se evalúan respuestas, cabeceras ni políticas que la
  fuente aprobada no contemple. La amplitud exigible se limita a sus riesgos y
  condiciones relevantes.
- **Protección del proveedor:** caché y control de tasa solo son obligatorios
  cuando términos o carga los justifican; en otro caso se exige una política
  equivalente, explícita y probada.
- **Procedencia:** API, dataset y fixtures deben poder auditarse y reutilizarse
  conforme a términos, licencia y atribución.
- **Privacidad:** logs y fixtures aplican minimización y no exponen credenciales ni
  datos personales innecesarios.
- **IA:** la llamada de chat es opcional, no acredita RA9.g por sí sola y no es
  necesaria para alcanzar Sobresaliente. RAG, vectores, MCP y agentes están fuera
  de alcance.

## Condiciones que impiden una evaluación válida

Estas condiciones no son penalizaciones automáticas ajenas a los CE; impiden
verificar de forma segura o fiable la evidencia afectada:

- **Secretos reales versionados:** se detiene la ejecución, se notifica la
  exposición y se solicita revocación y saneamiento antes de continuar. La
  evidencia de seguridad y reproducibilidad no puede considerarse válida mientras
  persista el riesgo.
- **Entrega no reproducible:** si no puede construirse o probarse offline por
  archivos ausentes, dependencias no declaradas o servicios privados obligatorios,
  no pueden acreditarse los comportamientos que dependan de esa ejecución. Se
  distinguirá un fallo ambiental demostrado de un defecto de la entrega.
- **Ausencia de evidencia de autoría:** si la defensa, el proceso disponible y los
  artefactos son incompatibles o insuficientes para atribuir el trabajo, se
  solicitará una comprobación de autoría conforme a las normas del centro antes de
  emitir una valoración definitiva.

La corrección registra por CE qué evidencia se observó y por qué corresponde al
nivel asignado. No se compensa la ausencia de un criterio mediante elementos
opcionales de otro.
