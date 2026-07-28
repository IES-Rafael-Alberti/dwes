# docs/02_ITERATION_1_FRONTEND.md

## Frontend minimo (HTML + JS + CSS)

El frontend se sirve desde `wwwroot` como archivos estaticos.

Archivos:
- `TodoApi/wwwroot/index.html`
- `TodoApi/wwwroot/styles.css`
- `TodoApi/wwwroot/app.js`

### Funcionalidades
- Listado de tareas.
- Formulario crear/editar con `Title`, `Description`, `DueDate`, `Status`.
- Botones: detalle, editar y borrar.

### Panel de depuracion (Debug API)
Dos paneles:
- Request: metodo, URL, headers y body JSON.
- Response: status code, headers relevantes y body JSON.

Esto permite ver el flujo HTTP real sin abrir DevTools.
