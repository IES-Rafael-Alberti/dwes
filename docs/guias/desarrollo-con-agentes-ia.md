# Desarrollo asistido por agentes de IA

> Guía transversal y **no evaluable**. No pertenece a una unidad concreta: la usas
> cuando lo necesites, sobre todo a partir de UD2 y UD3. Complementa la metodología
> del módulo (TDD desde la primera práctica y contrato primero) explicando cómo
> trabajar con agentes de IA sin perder el control del código ni disparar el coste.

## 1. Qué es y qué no es

- **Es** una forma de trabajar: tú diriges, el agente escribe y tú verificas.
- **No es** delegar a ciegas. Ninguna entrega se acepta sin que puedas explicarla,
  probarla y modificarla tú mismo.
- Ojo con los nombres: en este módulo, **SDD** designa el *contrato primero con
  OpenAPI*. Esta guía trata de otra cosa: el **flujo de trabajo con agentes**. Se
  apoya en la misma disciplina de especificar antes de implementar.

## 2. La recomendación del módulo: un agente, en serie, con una spec corta

Para las tareas secuenciales de este módulo, empieza con un agente y pasos
controlados:

1. Un **solo agente** recorre los pasos **uno detrás de otro**.
2. Tú pones **puertas** entre pasos: apruebas la spec, apruebas el plan y revisas
   el resultado antes de dar nada por bueno.
3. La **spec es corta** y vive junto al código (o en un `specs/` del repositorio),
   no en una carpeta ceremonial que nadie vuelve a leer.

La razón es práctica: cada paso consume contexto y requiere revisar el resultado.
Comparte solo la información pertinente, actual y necesaria para la tarea.

## 3. Cuándo no compensa coordinar varios agentes

Es tentador montar un equipo de agentes con roles (uno que analiza, otro que
planifica, otro que implementa, otro que revisa) y avanzar en paralelo. En una
tarea secuencial, esa coordinación añade traspasos y trabajo de integración que
puede no compensar:

- **Coste de contexto:** varios agentes pueden necesitar contexto repetido en cada
  traspaso.
- **Integración:** dividir pasos que dependen entre sí puede crear incompatibilidades
  y más trabajo de depuración.
- **Responsabilidad:** sigue siendo del alumno comprobar que el resultado conjunto
  cumple la especificación.

Conclusión para el módulo: **un agente es la opción inicial para implementar un
flujo secuencial**. Puedes usar varios cuando las subtareas sean realmente
independientes, por ejemplo para explorar en paralelo partes distintas de un
repositorio, y después integrar y verificar sus resultados.

## 4. Flujo recomendado (plan-first)

1. **Contexto y objetivo.** Di al agente qué problema resuelves, con qué stack
   (Java 25 / Spring Boot o PHP / Laravel), qué convenciones del proyecto y qué
   pruebas ya existen.
2. **Spec corta.** Pídele una especificación **breve** (apartado 5) y revísala. Si
   algo falta, se corrige aquí, que es barato.
3. **Plan.** Pídele un plan por pasos pequeños, cada uno con su prueba. Apruébalo o
   corrígelo antes de que escriba código.
4. **Ciclo rojo-verde-refactor.** Un paso, una prueba (TDD del módulo). No dejes
   que toque veinte ficheros de golpe.
5. **Revisión con contexto fresco.** Revisa tú el diff contra la spec; si hace falta,
   abre una sesión independiente del agente con solo la spec, el plan y el diff. No
   es necesario orquestar varios agentes para esta comprobación.
6. **Verificación.** Ejecuta tú la suite y el arranque. Un «verificado» que marque
   el agente no cuenta: cuenta la prueba que tú ejecutas.

## 5. La spec corta

Una spec útil cabe en media página. Plantilla:

```text
Objetivo:        (qué problema resuelve, en una frase)
Comportamiento:  (qué debe hacer, en viñetas)
Fuera de alcance:(qué NO se toca)
Criterios:       (cómo sabremos que está bien; enlaza a pruebas)
Riesgos:         (qué puede romperse)
Plan mínimo:     (2-6 pasos)
```

Regla: si la spec ocupa más que el cambio, has convertido el trabajo en burocracia.

### Qué dejar documentado

Conserva en el repositorio la **spec aprobada** que guio el cambio, junto con un
plan breve y los artefactos que permitan revisar el proceso: pruebas relevantes
(incluido el paso rojo y el verde cuando se trabaje con TDD), decisiones técnicas
y verificaciones finales. Añade también la declaración de uso de IA exigida por
la actividad. Resume lo necesario en `README.md`, `docs/` o `specs/`; no es
necesario entregar la conversación completa con el agente salvo que el docente
lo solicite.

## 6. Prompts que funcionan

Mal:

```text
Hazme una API de tareas.
```

Bien:

```text
En este proyecto Spring Boot 4 (Java 25) añade un endpoint POST /tasks.
Restricciones: usa DTO, valida el título (no vacío, máximo 120), devuelve 201 con
Location y 400 con el payload de error del proyecto. No toques el contrato OpenAPI
existente. Escribe primero el test de integración (MockMvc), ejecútalo en rojo y
luego implementa lo mínimo para ponerlo en verde. Un paso cada vez.
```

Claves: stack y versiones, entrada/salida esperadas, restricciones, un solo paso,
evidencia (la prueba) y qué no debe tocar.

## 7. Reglas de uso de IA del módulo

- **Declaración obligatoria** de cada uso material (véase la plantilla).
- **Verificación propia**: puedes explicar, ejecutar y modificar lo entregado.
- **Sin secretos**: nunca pegues credenciales, claves, datos personales ni
  información privada en la consulta.
- **Autoría real**: en prácticas cooperativas y proyectos, cada persona debe poder
  localizar y defender su aportación.

## 8. Relación con TDD y con el contrato OpenAPI

- Con **TDD**, la spec y el plan se convierten en pruebas concretas. El agente no
  decide si algo está bien: lo decide la prueba.
- Con el **contrato OpenAPI**, la spec del cambio encaja dentro del contrato
  versionado del proyecto. Si la IA propone un cambio de contrato, se revisa como
  cualquier otra modificación, no se acepta por venir del agente.

## 9. Lista de comprobación

- [ ] Puedo explicar el objetivo y la spec sin leer la conversación.
- [ ] El agente trabajó en pasos pequeños, no en un parche gigante.
- [ ] Hay pruebas que fallan antes y pasan después.
- [ ] Ejecuté la suite y el arranque por mí mismo.
- [ ] Revisé el diff contra la spec.
- [ ] Dejé la spec aprobada y los artefactos relevantes (plan, pruebas y decisiones) en la documentación del proyecto.
- [ ] Declaré el uso de IA y no incluí secretos ni datos personales.

## 10. Errores comunes

- **Codificar primero y «especificar» después.** La spec es antes, no una
  justificación a posteriori.
- **Aceptar cambios que no entiendes.** Si no puedes explicarlo, no es tuyo.
- **Dejar que el agente edite las pruebas para que pasen.** Si una prueba falla, se
  arregla el código o se corrige la prueba a mano, con criterio.
- **Montar varios agentes «porque queda bien».** Añade coste y coordinación; úsalo
  solo cuando el trabajo sea de verdad paralelo.
