# J25-05 - Resultados sellados

Completa `OutcomeText.describe` para una jerarquia cerrada.

- `Success` contiene el nombre del recurso creado.
- `Failure` contiene un codigo y un mensaje.
- Usa `sealed`, `record` y un `switch` como expresion.
- Los fallos de validacion deben distinguirse de los fallos internos.

Despues anade una guarda `when` que marque como urgente el codigo `DB-001`.
