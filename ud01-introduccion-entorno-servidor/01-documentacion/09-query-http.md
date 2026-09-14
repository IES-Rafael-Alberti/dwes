# Método HTTP QUERY y consultas con contenido

`QUERY` permite enviar en el contenido de la petición una consulta que no cabe
bien en una URI. RFC 10008 lo define como método seguro e idempotente.

```http
QUERY /productos HTTP/1.1
Host: example.org
Content-Type: application/json

{"precioMaximo":50,"etiquetas":["local","eco"]}
```

Frente a GET, QUERY permite una descripción estructurada en el contenido;
frente a POST, conserva semántica segura e idempotente para consultas. El
contenido debe tener un `Content-Type` coherente. También puede incluir
información sensible y quedar registrado, por lo que no elimina la necesidad
de proteger los datos.

Las respuestas pueden ser cacheables, pero la clave de caché debe considerar el
contenido y los metadatos relevantes. `Accept-Query` anuncia formatos mediante
Structured Fields; por ejemplo: `Accept-Query: "application/json"`.

Que el método esté estandarizado no implica que todos los frameworks, proxies y
clientes lo soporten. En UD1 se estudia su semántica y no se ejecuta contra
`Hello Server`; antes de adoptarlo habría que verificar toda la cadena.
