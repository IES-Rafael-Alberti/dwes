# Métodos HTTP: GET, POST, PUT, PATCH, DELETE, HEAD y OPTIONS

Un método expresa la intención de la petición. No basta con memorizar su uso
típico: hay que respetar la semántica que el recurso expone.

| Método | Seguro | Idempotente | Uso típico |
|---|---|---|---|
| GET | Sí | Sí | Obtener una representación |
| HEAD | Sí | Sí | Obtener los campos de GET sin contenido |
| POST | No necesariamente | No necesariamente | Solicitar un procesamiento del recurso |
| PUT | No | Sí | Crear o reemplazar el estado del recurso objetivo |
| PATCH | No | No necesariamente | Aplicar una modificación parcial |
| DELETE | No | Sí | Solicitar la eliminación del recurso objetivo |
| OPTIONS | Sí | Sí | Consultar opciones de comunicación |

**Seguro** significa que el cliente no solicita un cambio de estado en el
recurso objetivo. **Idempotente** significa que repetir la misma intención
produce el mismo efecto previsto que enviarla una vez. No significa que todas
las respuestas sean idénticas ni que el servidor no registre actividad.

POST no equivale automáticamente a crear. Su semántica depende del recurso.
Diseñar CRUD tampoco basta para diseñar una interfaz HTTP coherente.

## Exploración local

Con `Hello Server`, compara el método normal y los métodos de metadatos:

```bash
http --verbose GET http://localhost:8080/api/hello
http --verbose HEAD http://localhost:8080/api/hello
http --verbose OPTIONS http://localhost:8080/api/hello
```

Describe únicamente los estados y cabeceras observados. No presupongas que
`OPTIONS` siempre incluye `Allow` ni que todos los servidores se comportan
igual.
