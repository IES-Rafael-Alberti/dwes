### **1. Crear el archivo de configuración de Nginx**
En el directorio raíz del proyecto, crea un archivo llamado nginx.conf:
```pl
server {
    listen 80;

    server_name localhost;

    root /var/www/html/public;
    index index.php index.html;

    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    location ~ \.php$ {
        include fastcgi_params;
        fastcgi_pass php:9000;
        fastcgi_index index.php;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
    }

    location ~ /\.ht {
        deny all;
    }
}
```

### **1. Configuración del Entorno con Docker Compose**
Crea un archivo `docker-compose.yml` adaptado para PostgreSQL:

```yaml
version: '3.8'

services:
  php:
    image: php:8.2-fpm
    container_name: php-container
    volumes:
      - ./app:/var/www/html
      - ./php.ini:/usr/local/etc/php/php.ini
    depends_on:
      - db
    networks:
      - app-network

  nginx:
    image: nginx:latest
    container_name: nginx-container
    ports:
      - "8080:80"
    volumes:
      - ./app:/var/www/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - php
    networks:
      - app-network

  db:
    image: postgres:15
    container_name: postgres-container
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: app
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - app-network

volumes:
  db-data:

networks:
  app-network:
```

---

### **2. Estructura del Proyecto**
Crea la siguiente estructura de carpetas en el directorio del proyecto:

```
/app
  ├── public        (carpeta accesible públicamente)
  │     └── index.php
  ├── src           (lógica de negocio)
  ├── views         (archivos de presentación)
  ├── assets        (CSS, JS, imágenes)
  └── config        (archivos de configuración, como conexión a PostgreSQL)
```

---

### **3. Configuración de PostgreSQL**
En el contenedor de PostgreSQL, se crea automáticamente una base de datos (`app`) y un usuario (`user`) con contraseña (`password`). Para inicializar tablas:

1. Crea un archivo SQL inicial: `init.sql`
   ```sql
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       name VARCHAR(255),
       email VARCHAR(255) UNIQUE,
       password VARCHAR(255),
       role VARCHAR(50) DEFAULT 'user',
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

   CREATE TABLE tasks (
       id SERIAL PRIMARY KEY,
       user_id INT REFERENCES users(id),
       title VARCHAR(255),
       description TEXT,
       status VARCHAR(50) DEFAULT 'pending',
       due_date DATE,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

2. Incluye este archivo como volumen en el servicio de PostgreSQL:
   ```yaml
   volumes:
     - ./init.sql:/docker-entrypoint-initdb.d/init.sql
   ```

---

### **4. Configuración de Conexión PHP-PostgreSQL**
1. Asegúrate de instalar la extensión `pdo_pgsql` en el contenedor PHP.
   - Edita el `Dockerfile` (si es necesario):
     ```dockerfile
     FROM php:8.2-fpm
     RUN docker-php-ext-install pdo_pgsql
     ```

2. Archivo de configuración `config/db.php`:
   ```php
   <?php
   $host = 'postgres-container';
   $db   = 'app';
   $user = 'user';
   $pass = 'password';
   $charset = 'utf8';

   $dsn = "pgsql:host=$host;dbname=$db;charset=$charset";
   $options = [
       PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
       PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
       PDO::ATTR_EMULATE_PREPARES   => false,
   ];

   try {
       $pdo = new PDO($dsn, $user, $pass, $options);
   } catch (\PDOException $e) {
       throw new \PDOException($e->getMessage(), (int)$e->getCode());
   }
   ?>
   ```

---

### **5. Probar el Entorno**
1. **Inicia Docker Compose:**
   ```bash
   docker-compose up -d
   ```

2. **Crea un archivo de prueba `public/index.php`:**
   ```php
   <?php
   require_once '../config/db.php';

   $stmt = $pdo->query("SELECT 'Connected to PostgreSQL!' AS message");
   $row = $stmt->fetch();
   echo $row['message'];
   ?>
   ```

3. **Accede desde el navegador:**
   - URL: [http://localhost:8080](http://localhost:8080)

---
