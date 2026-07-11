Partes de un Token JWT
Un JWT consta de tres partes separadas por puntos (.) 
1 
8, cada una codificada en Base64:

1. Header (Encabezado)
Contiene metadatos sobre el token, principalmente:

Tipo de token (JWT)
Algoritmo de firma utilizado (por ejemplo, HS256, RS256) 
1
2. Payload (Carga útil)
Incluye los datos del usuario y privilegios (claims), como:

Identidad del usuario
Permisos o roles
Fecha de expiración
Cualquier información adicional que necesites transmitir 
1 
7
3. Signature (Firma)
Permite verificar la autenticidad del token 
1. Se genera aplicando el algoritmo especificado en el header a:

Header codificado en Base64
Payload codificado en Base64
Una clave secreta 
1
Esta firma garantiza que el remitente es quien dice ser y que el mensaje no ha sido modificado 
1.

Cómo usar jwt.io
jwt.io es un debugger online que te permite decodificar, verificar y generar JWTs de forma segura en tu navegador 
5:

Para estudiar un token:
Pega el token completo en el campo "Encoded"
Automáticamente verás el contenido decodificado del header y payload en colores
Puedes ver el contenido sin necesidad de conocer la clave secreta 
1
Para verificar la firma:
En la sección "Verify Signature", ingresa la clave secreta utilizada para firmar el token
Si el token es válido, verás el mensaje "Signature Verified"
Si ha sido modificado o la clave es incorrecta, aparecerá "Invalid Signature"
Importante: Toda la validación ocurre en tu navegador. El sitio no almacena ni transmite tus tokens fuera del navegador 
6, pero ten cuidado al pegar JWTs ya que pueden representar credenciales de acceso 
2.