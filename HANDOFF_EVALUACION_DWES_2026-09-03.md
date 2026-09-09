# Handoff: cierre del rediseño de evaluación DWES

> Documento de continuidad para iniciar una sesión nueva y limpia. No es un diario ni una transcripción: conserva el estado necesario para completar los pendientes externos.

- **Proyecto:** DWES, `/datos/RafaelAlberti/RafaelAlberti26_27/Modulos/DWES`
- **Fecha:** 2026-09-03
- **Rama/commit:** `main`, `0adf118 docs(plan): record approved UD2 weighting`
- **Estado del árbol:** sin cambios versionables pendientes; no versionar `graphify-out/` ni `00-planificacion/.Rhistory`.

## Objetivo

Adaptar la evaluación de DWES para un grupo de 30 alumnos: prácticas cooperativas con trazabilidad y defensa individual, cuestionarios Moodle frecuentes, dos hitos individuales y documentación proporcional de autoría.

## Estado actual

- **Hecho:** rediseño documental, UD1-UD7, UD2a, starter de Gestión de eventos, plantillas, GIFT estático y script GitHub completados y commiteados.
- **En curso:** validaciones externas de Moodle y GitHub. Las ponderaciones operativas y la estructura de calificador se cerraron el 6 de septiembre.
- **Bloqueado por:** Moodle requiere importación/previsualización manual; GitHub requiere organización y template autorizados; programación didáctica espera autorización de jefatura para publicar.

## Trabajo realizado

- Se creó la planificación y matriz de transición en `00-planificacion/`.
- UD2a usa una ruta incremental: Mini Tasks 15 %, Book Catalog 20 %, Gestión de eventos 25 %, cuestionarios Moodle 25 % y defensa individual 15 %.
- `00-planificacion/CONFIGURACION_CALIFICADOR_MOODLE_2026_2027.md` define el árbol Moodle, los IDs, tipos de actividad y pesos de UD1-UD7; no es un CSV importable.
- Battleship es exclusivamente el code-along teórico-práctico; no es una entrega ni recibe ponderación.
- Gestión de eventos incluye el esqueleto de Flyway/OpenAPI y funciona fuera de IntelliJ con `bash gradlew test`.
- La documentación de autoría se graduó por alcance en `00-recursos-comunes/plantillas/guia-documentacion-autoria.md`.
- La programación externa `../../ProgramacionesDidacticas/PD-CFGS-DAW-2/src/mod5.md` se ajustó localmente: UD2, dos hitos, pesos y Battleship no evaluable. No se publicó ni generó PDF.

## Enfoques intentados

### Build de la programación didáctica

- **Qué se intentó:** `mkdocs build --strict -f mkdocs-pd/mkdocs.yml`.
- **Resultado:** falló.
- **Evidencia:** falta el plugin `with-pdf`.
- **Conclusión:** no instalar ni publicar hasta que jefatura autorice la actualización.

### Wrapper inicial de Gestión de eventos

- **Qué se intentó:** ejecutar `bash gradlew test` con Gradle 8.14.3 sobre Java 25.
- **Resultado:** falló.
- **Evidencia:** `Unsupported class file major version 69`.
- **Conclusión:** se actualizó el wrapper a Gradle 9.1.0 y se corrigieron dependencias/imports de pruebas de Spring Boot 4.

## Errores y depuración

### Starter de Gestión de eventos no ejecutable fuera de IntelliJ

- **Síntoma:** wrapper previo incompatible con Java 25 y tests heredados rotos.
- **Reproducción:** `bash gradlew test` desde `ud02-api-rest/ud02a-spring-boot/03-ejercicios/03-gestion-eventos/recursos/GestionEventos`.
- **Causa raíz:** Gradle 8.14.3 no arrancaba con JDK 25; Spring Boot 4 movió `DataJpaTest`; fixtures MVC/JPA incompletos.
- **Corrección:** Gradle 9.1.0, dependencias Flyway/PostgreSQL/Jackson/test JPA e imports/fixtures actualizados.
- **Verificación:** `bash gradlew test` termina correctamente.

## Decisiones y restricciones

- **Decisiones:** mantener los 17 bancos GIFT nuevos (272 preguntas); no convertir Battleship en entrega; dos hitos individuales, no tres.
- **Restricciones del proyecto:** GIFT se ignora intencionadamente por privacidad; los `graphify-out/` son generados y no se versionan.
- **Requisitos del usuario:** hacer cambios autónomamente cuando sea posible; no publicar la programación ni generar PDFs hasta autorización de jefatura.
- **No modificar:** no usar ni almacenar las credenciales Moodle compartidas en una conversación previa; el usuario debe rotar esa contraseña. No alterar PDFs del curso 2025/2026.

## Ficheros importantes

- `00-planificacion/PLAN_REDISENO_EVALUACION_30_ALUMNOS.md`: plan y decisiones del rediseño.
- `00-planificacion/MATRIZ_TRANSICION_EVALUACION_2026_2027.md`: transición y ponderación UD2 aprobada.
- `ud02-api-rest/ud02a-spring-boot/EVALUACION.md`: ruta, pesos y cobertura de UD2a.
- `ud02-api-rest/ud02a-spring-boot/03-ejercicios/03-gestion-eventos/README.md`: requisitos Flyway/OpenAPI y comando `bash gradlew`.
- `00-recursos-comunes/plantillas/guia-documentacion-autoria.md`: dossier completo, reducido o mínimo según alcance.
- `00-planificacion/github/gh_entregas.py`: GitHub por defecto en dry-run; `report --csv grupos.csv --org ORG`.
- `00-planificacion/banco-gift-cobertura.md`: cobertura de 17 bancos y 272 preguntas; Moodle pendiente.
- `00-planificacion/CONFIGURACION_CALIFICADOR_MOODLE_2026_2027.md`: estructura operativa del calificador y ponderaciones cerradas.
- `../../ProgramacionesDidacticas/PD-CFGS-DAW-2/src/mod5.md`: programación externa modificada localmente, sin publicación.

## Evidencias y validaciones

- `git diff --check` → correcto en DWES.
- `mkdocs build --strict` → correcto en DWES; solo avisos preexistentes de navegación y aviso de Material.
- `bash gradlew test` → correcto en Gestión de eventos.
- `python3 -m py_compile gh_entregas.py` y `python3 gh_entregas.py plan --csv grupos.csv --org ORG` → correctos.
- Validación estática GIFT → 17 bancos y 272 IDs únicos; no quedan `SHORTANSWER` ni `NUMERICAL`.
- **No verificado:** importación/previsualización real de GIFT en Moodle; operaciones reales de GitHub; build de programación con PDF.

## Pendientes y bloqueos

- [ ] Importar y previsualizar manualmente los bancos GIFT en Moodle de ensayo.
- [x] Cerrar las ponderaciones y la estructura del calificador Moodle.
- [ ] Probar GitHub con organización/template autorizados, primero con `plan` y después `create --apply`.
- [ ] Decidir con jefatura cuándo publicar/commitear la programación didáctica externa.

## Próximo paso exacto

En Moodle de ensayo, crear un cuestionario temporal, importar un banco GIFT y previsualizar una pregunta de cada tipo; registrar cualquier mensaje de importación o visualización para corregirlo.

## Arranque de la siguiente sesión

1. Leer este `HANDOFF_EVALUACION_DWES_2026-09-03.md`.
2. Comprobar `git status --short` y `git log --oneline -10` en DWES.
3. Confirmar que solo siguen sin seguimiento `.Rhistory` y los `graphify-out/`.
4. Ejecutar el **próximo paso exacto** sin repetir pruebas ya superadas.

## Notas para el siguiente agente

- Commits del rediseño: `dc2ec98`, `0643858`, `6b2acf0`, `06359a9`, `49c1366`, `0adf118`.
- Los archivos `.gift` están ignorados y no aparecerán en `git status`; su matriz pública no contiene respuestas.
- La programación externa usa una copia de curso anterior. Sus PDF nombrados 2025/2026 no deben renombrarse ahora; los genera un hook de GitHub al hacer commit.
