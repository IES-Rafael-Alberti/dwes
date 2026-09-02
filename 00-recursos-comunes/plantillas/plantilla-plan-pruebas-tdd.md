# Plan de pruebas y TDD — {NOMBRE}

## Estrategia

- Ciclo: **red → verde → refactor**.
- El test se escribe antes de la implementación.
- Las reglas de dominio se prueban sin depender de HTTP ni de la base de datos.
- Las pruebas de integración verifican persistencia, endpoints y errores relevantes.

## Niveles de prueba

| Nivel | Qué verifica | Herramienta |
|---|---|---|
| Dominio | Reglas y transiciones | |
| Aplicación | Casos de uso y errores | |
| Integración | Persistencia y servidor | |
| HTTP | Contrato y respuestas | |

## Casos críticos

| ID | Regla/flujo | Caso correcto | Caso de error | Test |
|---|---|---|---|---|
| T{N} | | | | |

## Criterio de terminado

- [ ] Test automatizado relevante en verde.
- [ ] Error y límite comprobados.
- [ ] Sin secretos ni dependencias manuales ocultas.
- [ ] Refactor documentado si cambia el diseño.
