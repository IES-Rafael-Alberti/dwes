# Evidencias RA/CE del proyecto final DWES

Esta matriz conecta evidencia práctica con `00-planificacion/rubrica_comun_DWES_por_RA_CE.md`. No convierte el proyecto en nueve miniunidades ni exige reimplementar independientemente todos los RA o CE. La calificación se determina con la rúbrica y la revisión docente, no por contar artefactos.

| RA | Evidencia práctica posible | Qué revisar |
|---|---|---|
| RA1 — Arquitecturas y tecnologías | Decisión de stack, diagrama o explicación de componentes, configuración de ejecución local. | La tecnología y las fronteras responden al problema; no son una plantilla sin comprender. |
| RA2 — Sentencias de servidor | Código Java/PHP y controladores o plantillas MVC que ejecutan un caso de uso. | Tipos, sintaxis, configuración y respuesta del servidor son correctos y mantenibles. |
| RA3 — Estructuras embebidas | Formularios MVC, colecciones, validación y control de flujo cuando el proyecto los use. | La estructura está al servicio de la regla y no mezcla innecesariamente presentación y negocio. |
| RA4 — Estado y autenticación | Sesión, token o mecanismo equivalente si el dominio protege identidad o acciones. | Credenciales, estado y acceso se gestionan con seguridad proporcional al dominio. |
| **RA5 — Separación presentación/negocio** | Controlador/entrada delgada, servicios/casos de uso, DTO o form request, pruebas de reglas. | El flujo demuestra separación real, errores controlados y lógica testeable. |
| **RA6 — Datos e integridad** | Modelo, migraciones, restricciones, repositorios/ORM, transacciones y pruebas de persistencia pertinentes. | El esquema se reproduce y la integridad no depende solo de la interfaz. |
| **RA7 — Servicios web** | API documentada, contrato OpenAPI si se expone API, respuestas y errores, pruebas HTTP. | Recursos o endpoints son coherentes, documentados y verificables. |
| **RA8 — Cliente y servidor** | Flujo real desde un cliente hacia el servidor, incluyendo un error visible. | Se observa petición, endpoint, servicio, persistencia y respuesta; el cliente no tapa el servidor. |
| RA9 — Integración heterogénea | Adaptador a API/dataset externo, normalización, procedencia y pruebas de fallo. | **Solo si el proyecto usa realmente integración heterogénea.** No es requisito automático de UD7. |

## Uso responsable de la matriz

- RA5, RA6, RA7 y RA8 suelen aportar la evidencia central del proyecto DWES cuando corresponden a su estilo API/MVC.
- RA9 solo se invoca si existe una integración heterogénea real; no se añade una API externa artificialmente para marcar una casilla.
- La ausencia de un RA no implica automáticamente una carencia: el docente determina qué evidencias son pertinentes para el dominio aprobado.
- Las evidencias se complementan con commits, etiquetas/releases, documentación, pruebas y defensa, según la [rúbrica/checklist](rubrica-y-checklist-dwes.md).
