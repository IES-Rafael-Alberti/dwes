# Cabeceras HTTP, contenido, caché y negociación

Las cabeceras son campos de metadatos que acompañan a la petición o a la
respuesta. No son el contenido: describen cómo interpretarlo, controlarlo o
transportarlo.

## Cabeceras esenciales

| Cabecera | Papel |
|---|---|
| `Host` | Identifica el destino de la petición HTTP/1.1 |
| `Accept` | Formatos que el cliente puede procesar |
| `Content-Type` | Formato del contenido enviado o recibido |
| `Content-Length` | Tamaño del contenido cuando se conoce |
| `Location` | URI relacionada, especialmente tras una creación o redirección |
| `Cache-Control` | Directivas para las cachés |
| `ETag` | Identificador de una representación |
| `If-None-Match` | Petición condicional basada en `ETag` |
| `Set-Cookie` | Instrucción para gestionar una cookie; puede contener datos sensibles |

`Accept` expresa una preferencia del cliente; `Content-Type` describe el
formato que realmente se envía o recibe. Un formato JSON no se convierte en
JSON por llamarse `/api`: lo determina el contenido y su descripción HTTP.

## Caché y validación condicional

Campos como `Cache-Control`, `ETag`, `If-None-Match`, `Last-Modified` e
`If-Modified-Since` permiten guardar o revalidar representaciones. Una respuesta
`304 Not Modified` evita transferir de nuevo el contenido.

## Exploración con HTTPie y navegador

```bash
http --headers GET http://localhost:8080/
http --body GET http://localhost:8080/
http --headers GET http://localhost:8080/api/hello
http --body GET http://localhost:8080/api/hello
```

En la pestaña **Red** del navegador compara las cabeceras de `/` y
`/api/hello`. Relaciona `Content-Type` con HTML o JSON y no compartas cookies,
tokens ni `Authorization` reales.
