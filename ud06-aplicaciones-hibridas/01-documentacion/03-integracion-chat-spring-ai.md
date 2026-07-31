# Integración opcional de chat con Spring AI

P3 añade **una única llamada síncrona y sin memoria** para recomendar elementos de
un catálogo ya normalizado. Es un enriquecimiento opcional: el catálogo, la ingesta,
JPA y sus pruebas funcionan con la IA desactivada y sin Ollama.

!!! important "No acredita RA9.g por sí sola"
    RA9.g requiere el análisis reproducible sobre los datos normalizados y su
    interpretación. Una respuesta generativa no sustituye esa evidencia.

## Puesta en marcha rápida

El camino normal no necesita Ollama:

```bash
cd ud06-aplicaciones-hibridas/02-ejemplos/catalogo-cultural-hibrido
mvn -o clean test
```

La configuración versionada mantiene desactivados tanto el caso de uso como la
autoconfiguración del modelo:

```properties
catalogo.ai.enabled=false
spring.ai.model.chat=none
spring.ai.model.embedding=none
spring.ai.retry.max-attempts=1
spring.ai.chat.client.tool-calling.enabled=false
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.2:3b
spring.ai.ollama.init.pull-model-strategy=never
```

No hay API key ni secreto: Ollama se ejecuta localmente. `pull-model-strategy=never`
impide que el arranque o el build descarguen modelos.
`max-attempts=1` expresa el contrato de un único intento. Spring AI 2.0.0 lo
traslada internamente a `maxRetries`; por ello la configuración opcional registra
además un `RetryTemplate` con cero reintentos. Una prueba levanta la
autoconfiguración real de `OllamaChatModel`, sustituye únicamente el boundary
`OllamaApi` y verifica una sola invocación de `OllamaApi.chat(...)` ante un fallo
transitorio simulado, sin Ollama.

## Activación local manual

Este paso es opcional y nunca se ejecuta durante las pruebas:

1. Instala e inicia Ollama siguiendo su documentación oficial.
2. Descarga expresamente el modelo: `ollama pull llama3.2:3b`.
3. Arranca la aplicación con
   `CATALOGO_AI_ENABLED=true SPRING_AI_MODEL_CHAT=ollama mvn spring-boot:run`.
4. Invoca `CatalogRecommendationService` desde un consumidor de aplicación o una
   demostración controlada. P3 no añade controlador, interfaz ni llamada automática
   al arrancar.

Cambiar el modelo exige una decisión local explícita mediante
`SPRING_AI_OLLAMA_CHAT_MODEL`. No se versionan credenciales ni datos personales.

## Dependencia y compatibilidad

El proyecto conserva Spring Boot `4.0.5` y gestiona Spring AI con su BOM:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>2.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

La decisión se apoya en la
[guía oficial *Getting Started* de Spring AI 2.0.0](https://docs.spring.io/spring-ai/reference/getting-started.html),
que declara literalmente: “Spring AI 2.0.x supports Spring Boot 4.0.x and 4.1.x.”
Por tanto, el baseline interno del POM de publicación no invalida la compatibilidad
oficial ni justifica actualizar Boot.

No se incorporan starters de RAG, bases vectoriales, MCP ni agentes. El starter de
Ollama aporta el cliente de chat y su infraestructura común; embeddings se
desactivan expresamente con `spring.ai.model.embedding=none`. Algunas clases y
gestores de tools llegan inevitablemente como transitivas del starter, pero
`spring.ai.chat.client.tool-calling.enabled=false` impide el advisor automático y
la aplicación no registra callbacks, advisors ni funcionalidad de tools.

## Arquitectura: puerto y adaptador

```text
CulturalRecord normalizado (máximo 10)
              │
              ▼
CatalogRecommendationService        aplicación pura, una delegación
              │
              ▼
CulturalChatGateway                  puerto de aplicación
              │
              ▼
SpringAiCulturalChatGateway          adaptador condicional
              │
              ▼
ChatClient.entity(...)               salida estructurada → Ollama local
```

`CulturalChatConfiguration` registra adaptador, servicio y `RetryTemplate` únicamente
con `catalogo.ai.enabled=true`, `spring.ai.model.chat=ollama` y
`spring.ai.model.embedding=none`. Otra selección de chat o embeddings deja el slice
sin registrar, evitando que la política de cero reintentos afecte globalmente a otro
proveedor. El adaptador recibe un `ObjectProvider<ChatClient.Builder>`
y resuelve el builder al invocarse: así no depende del orden de evaluación de la
autoconfiguración. Sin infraestructura de chat el contexto arranca y la invocación
falla de forma cerrada con `CatalogChatException(MODEL_UNAVAILABLE)`.

El resultado es `CatalogRecommendation(summary, recommendedIds, sourceNote)`. El
adaptador acepta de uno a tres identificadores y comprueba que todos pertenecen a
los candidatos suministrados. No consulta JPA ni oculta errores de programación o
persistencia.

## Riesgos del prompt y de los datos

Títulos, autorías y materias proceden de terceros y se consideran **datos no
confiables**. `CulturalChatPromptBuilder` aplica controles antes de la llamada:

| Riesgo | Control |
|--------|---------|
| Inyección de instrucciones en metadatos | Instrucción de sistema explícita; datos encerrados en `<untrusted_catalog_data>` y escapados |
| Prompt descontrolado | Máximo 10 registros; título de 160 caracteres; 3 autorías, 5 materias y 80 caracteres por valor |
| Identificadores inventados | Validación exacta contra el conjunto de candidatos |
| Salida vacía, demasiado larga o con caracteres de control | Límites de 500 caracteres para el resumen y 300 para la nota; `CatalogChatException(INVALID_RESPONSE)` |
| Ollama o modelo ausente | `CatalogChatException` con motivo `MODEL_UNAVAILABLE` |
| Fuga por logs | El adaptador no registra prompts, metadatos ni respuestas |

Los delimitadores y el escape reducen el riesgo, pero no convierten el texto de un
modelo en una fuente fiable. La salida sigue siendo no determinista y debe mostrarse
como recomendación, nunca como hecho ni decisión automática. Cualquier consumidor
HTML/UI futuro debe escapar `summary` y `sourceNote` en el punto de renderizado;
la validación del adaptador NO sustituye el escape contextual de salida.

## Pruebas offline

| Prueba | Evidencia |
|--------|----------|
| `CatalogRecommendationServiceTest` | Una delegación, proyección normalizada y máximo 10 candidatos con gateway falso |
| `CulturalChatPromptBuilderTest` | Delimitación, escape, inyección y límites de tamaño/cantidad |
| `AiSafetyConfigurationTest` | Binding real de un intento/tools desactivados y un solo intento ante fallo transitorio simulado |
| `CulturalChatConfigurationTest` | Contexto sin IA, fallo cerrado sin builder, autoconfiguración real del builder sin advisor de tools y slice ausente para modelos no permitidos |
| `OllamaChatModelRetryIntegrationTest` | `OllamaChatModel` autoconfigurado, único `ChatModel`, sin `EmbeddingModel`, `RetryTemplate` propio inyectado y una llamada exacta a `OllamaApi.chat(...)` |
| `SpringAiCulturalChatGatewayTest` | Salida válida, IDs inventados, límites, caracteres de control y modelo no disponible mediante boundary falso |

La suite completa usa H2, WireMock, relojes falsos y el boundary de chat falso. No
abre conexión con `localhost:11434`, no descarga modelos y puede repetirse con
`mvn -o clean test`.

## Límites y exclusiones

P3 incluye solo el puerto, la orquestación, el prompt acotado y un adaptador de chat.
Quedan fuera de forma explícita:

- RAG, embeddings y almacenes vectoriales;
- MCP, agentes y tool/function calling;
- memoria de conversación y streaming;
- controlador, frontend y descarga automática del modelo;
- envío o registro de datos personales o consultas libres de usuario.

La IA no modifica el repositorio, no participa en la ingesta y no es necesaria para
ningún recorrido obligatorio de UD6.
