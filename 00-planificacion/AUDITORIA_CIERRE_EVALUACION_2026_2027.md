# Auditoria de cierre del rediseño de evaluacion 2026/2027

Fecha de comprobacion: 2026-09-06.

## Resultado

El rediseño queda **apto en sus evidencias locales**. No puede cerrarse la
validacion operativa de los cuestionarios hasta que Moodle Centros Cadiz
habilite la instancia 2026/2027.

## Comprobaciones superadas

| Alcance | Comprobacion | Resultado |
|---|---|---|
| Documentacion publica | `mkdocs build --strict` | Correcto. Solo aparecen los avisos preexistentes de paginas fuera de `nav` y el aviso de Material for MkDocs. |
| Starter GestionEventos | `bash gradlew test` | Correcto: `BUILD SUCCESSFUL`. |
| Automatizacion GitHub | `python3 -m py_compile gh_entregas.py` y `python3 gh_entregas.py plan --csv grupos.csv --org ORG` | Correcto: genera el plan en modo simulacion; no realiza operaciones remotas. |
| Bancos GIFT nuevos | Recuento de encabezados `::id::` | 17 bancos nuevos, 16 preguntas por banco, 272 preguntas. |
| IDs GIFT | Comprobacion de duplicados | No hay IDs duplicados. |
| Tipos no portables | Busqueda de `SHORTANSWER` y `NUMERICAL` | No quedan coincidencias. |
| Integridad de cambios | `git diff --check` | Correcto. |

Los bancos GIFT siguen ignorados intencionadamente por Git. Los bancos
historicos se conservan y no forman parte del recuento de las 272 preguntas
nuevas.

## Bloqueos externos vigentes

| Bloqueo | Evidencia | Accion cuando se desbloquee |
|---|---|---|
| Moodle 2026/2027 | La portada oficial de Moodle Centros Cadiz indica: "Moodle Centros 26-27 - PROXIMAMENTE" y que la instancia estara disponible en unos dias. | Esperar a que la portada deje de mostrar el aviso; no usar la instancia 2025/2026 para esta validacion. |
| Validacion GIFT en Moodle | No hay una importacion ni previsualizacion real registrada. | Importar primero los bancos piloto UD1 semana 01 y UD2a semana 03; previsualizar una pregunta de cada tipo y registrar incidencias en `banco-gift-cobertura.md`. |
| Configuracion operativa de Moodle | Resuelta el 2026-09-06 en `CONFIGURACION_CALIFICADOR_MOODLE_2026_2027.md`. | Crear el curso plantilla cuando abra Moodle 2026/2027 y comprobar una restauracion sin usuarios. |
| Operaciones GitHub | No hay organizacion ni repositorio plantilla autorizados. | Ejecutar primero `plan` con datos reales y despues `create --apply`, tras confirmar organizacion, plantilla y permisos. |
| Programacion didactica externa | Pendiente de autorizacion de jefatura. | No publicar ni generar el PDF hasta recibir autorizacion. |

## Protocolo Moodle pendiente

1. Acceder a `https://educacionadistancia.juntadeandalucia.es/centros/cadiz/` cuando la instancia 2026/2027 este disponible.
2. Crear un curso o cuestionario temporal de ensayo sin alumnado.
3. Importar `ud01-2026-semana-01-http.gift` y `ud02a-2026-semana-03-spring-tdd.gift`.
4. Previsualizar al menos una pregunta SU, SM, VF, EM y HC; revisar puntuacion, escapes, bloques de codigo y retroalimentacion.
5. Registrar mensajes de importacion, limitaciones de tipos y correcciones en `00-planificacion/banco-gift-cobertura.md`.
6. Solo despues, marcar los dos pilotos y cada banco revisado como validados en Moodle.

## Estado del arbol

Este informe es el unico cambio de auditoria pendiente de incorporar al
repositorio. Aparte de el permanecen sin seguimiento `00-planificacion/.Rhistory`,
el handoff local y los directorios generados `graphify-out/`; no se deben
versionar.
