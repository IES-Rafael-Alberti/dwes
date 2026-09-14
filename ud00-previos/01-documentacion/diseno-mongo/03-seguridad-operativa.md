# Seguridad y operativa

## 1. Autenticación y autorización

MongoDB puede autenticarse con varios mecanismos:

- **SCRAM**: el más habitual en despliegues generales.
- **x.509**: autenticación mediante certificados.
- **LDAP**: integración con directorios corporativos.
- **Kerberos**: entornos empresariales integrados.

### Roles built-in

- `read`
- `readWrite`
- `dbAdmin`
- `userAdmin`
- `clusterAdmin`
- `readWriteAnyDatabase`
- `dbOwner`

### Principio de mínimo privilegio

La aplicación debe tener solo lo necesario. Un usuario típico de app no debe ser administrador.

```javascript
use tienda_db;
db.createUser({
  user: "app_user",
  pwd: "cambiar_esta_clave",
  roles: [
    { role: "readWrite", db: "tienda_db" }
  ]
});
```

## 2. Field-level redaction

MongoDB no tiene seguridad nativa por fila a nivel general en Community; en Enterprise existen capacidades más avanzadas. Alternativas prácticas:

- **Vistas** con proyección que excluyan campos sensibles.
- **Aplicación**: no devolver nunca `passwordHash`, `ssn` u otros campos sensibles.

```javascript
db.createView(
  "users_public",
  "users",
  [
    { $project: { passwordHash: 0, ssn: 0 } }
  ]
);
```

## 3. Buenas prácticas de seguridad

- Activar autenticación siempre.
- No usar `--noauth` en producción.
- Usar TLS/SSL para el tráfico.
- Restringir `bindIp` a interfaces internas.
- Revisar auditoría o, como mínimo, logs.

## 4. Operativa básica

### Backup y restore

```bash
mongodump --db tienda_db --out ./backup
mongorestore --db tienda_db ./backup/tienda_db
```

### Monitoreo básico

- `mongostat`: estado global.
- `mongotop`: actividad por colección.

### Cifrado en reposo (Encryption at Rest)

MongoDB Enterprise y Atlas permiten cifrar los archivos de datos en disco. Si trabajas con datos sensibles (contraseñas, datos personales), el cifrado evita que alguien con acceso al sistema de archivos pueda leerlos directamente:

```bash
# Activar cifrado al arrancar mongod
mongod --enableEncryption --encryptionKeyFile /etc/mongodb/encryption-key
```

En MongoDB Community (la edición gratuita) el cifrado en reposo no está disponible, pero puedes:
- Cifrar a nivel de aplicación (ej: campos sensibles cifrados con AES antes de insertar)
- Usar cifrado a nivel de sistema de archivos (LUKS, dm-crypt)

### Capped collections

Útiles para logs o eventos donde interesa conservar solo lo más reciente.

```javascript
db.createCollection("logs", { capped: true, size: 10485760, max: 5000 });
```

## 5. Ejemplo guiado

### Arrancar mongod con autenticación

```bash
mongod --dbpath /data/db --auth --bind_ip 127.0.0.1
```

### Crear usuario admin

```javascript
use admin;
db.createUser({
  user: "admin",
  pwd: "admin_pass",
  roles: [ { role: "root", db: "admin" } ]
});
```

### Crear `app_user`

```javascript
use tienda_db;
db.createUser({
  user: "app_user",
  pwd: "app_pass",
  roles: [ { role: "readWrite", db: "tienda_db" } ]
});
```

### Comprobación de permisos

```javascript
use tienda_db;
db.getSiblingDB("tienda_db").posts.createIndex({ title: 1 }); // debería fallar con app_user
db.dropDatabase(); // debería fallar con app_user
```

### Backup/restore

```bash
mongodump --db tienda_db --collection posts --out ./backup
mongorestore --db tienda_db --collection posts ./backup/tienda_db/posts.bson
```

## 6. Ejercicio autónomo

1. Crea 3 usuarios: `admin`, `app_user`, `analytics_user`.
2. Verifica que `app_user` no puede ejecutar `db.dropDatabase()`.
3. Verifica que `analytics_user` no puede hacer `insertOne()`.
4. Haz un backup de la BD de e-commerce.
5. Documenta el proceso en un mini-informe.
