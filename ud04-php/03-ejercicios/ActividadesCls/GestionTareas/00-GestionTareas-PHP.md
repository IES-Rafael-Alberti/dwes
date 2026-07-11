
---

### **Aplicación: Sistema de Gestión de Tareas**

**Descripción del proyecto:**
Un sistema donde los usuarios pueden registrarse, iniciar sesión y gestionar tareas. Los usuarios con rol de "admin" tienen privilegios adicionales, como gestionar usuarios y ver todas las tareas, mientras que los usuarios con rol "usuario" solo pueden gestionar sus propias tareas.

---

### **Características:**

1. **Autenticación:**
   - Registro de usuarios.
   - Inicio de sesión (login).
   - Cierre de sesión (logout).

2. **Roles:**
   - **Admin:** Puede gestionar usuarios y ver todas las tareas.
   - **Usuario:** Solo puede gestionar sus propias tareas.

3. **CRUD de Tareas:**
   - Crear, leer, actualizar y eliminar tareas.
   - Cada tarea tiene campos como título, descripción, estado (pendiente/completada) y fecha de vencimiento.

4. **Gestión de Usuarios (solo para admin):**
   - Ver lista de usuarios.
   - Editar información de usuarios (nombre, email, rol).
   - Eliminar usuarios.

---

### **Estructura de Base de Datos:**

1. **Tabla `users`:**
   - `id` (INT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR)
   - `email` (VARCHAR, UNIQUE)
   - `password` (VARCHAR)
   - `role` (ENUM: 'admin', 'user')
   - `created_at` (TIMESTAMP)

2. **Tabla `tasks`:**
   - `id` (INT, PK, AUTO_INCREMENT)
   - `user_id` (INT, FK -> users.id)
   - `title` (VARCHAR)
   - `description` (TEXT)
   - `status` (ENUM: 'pending', 'completed')
   - `due_date` (DATE)
   - `created_at` (TIMESTAMP)

---

### **Funcionalidades Específicas para Clases:**

1. **Login y Logout:**
   - Validación de credenciales.
   - Uso de sesiones para mantener al usuario autenticado.
   - Restricción de acceso según el rol.

2. **Protección de Rutas:**
   - Middleware para verificar si un usuario está autenticado y tiene permisos para acceder a ciertas páginas.

3. **CRUD Básico:**
   - Mostrar las tareas asociadas al usuario autenticado.
   - Formularios para añadir, editar y eliminar tareas.

4. **Gestión de Usuarios (para Admin):**
   - Página para listar usuarios.
   - Funciones para editar roles y eliminar usuarios.

5. **Validación:**
   - Validación de formularios del lado del servidor (campos obligatorios, formato de email, etc.).

---

### **Tecnologías Utilizadas:**
- **PHP**: Para la lógica de la aplicación.
- **MySQL**: Como base de datos.
- **HTML + CSS**: Para la interfaz básica.
- **Bootstrap** (opcional): Para darle un estilo básico a la aplicación.
- **Session Management**: Para autenticación y autorización.

---

### **Beneficios del Proyecto:**
1. **Cobertura completa de funcionalidades clave:**
   - Login, logout, roles y CRUD.
2. **Escalabilidad:**
   - Se puede migrar fácilmente a Laravel más adelante.
3. **Versatilidad:**
   - Puedes añadir funcionalidades extra si tienes tiempo, como búsqueda, filtros o paginación.
4. **Desarrollo por fases:**
   - Puedes implementar primero las tareas básicas y luego añadir las funcionalidades avanzadas.

---
Empezar el proyecto de forma estructurada es clave para guiar a los alumnos a través de los conceptos fundamentales de PHP y el desarrollo web. Aquí tienes un **plan por fases** para comenzar y avanzar progresivamente:

---

### **Fase 1: Preparación del Entorno**
1. **Configurar Docker Compose:**
   - Crear un entorno de desarrollo con PHP, MySQL y Nginx.
   - Proveer un archivo `docker-compose.yml` listo para que los alumnos puedan iniciar el proyecto sin problemas.

2. **Preparar la Estructura del Proyecto:**
   - Crear una estructura básica de carpetas:
     ```
     /app
       ├── public        (carpeta para los archivos accesibles públicamente, como index.php)
       ├── src           (para lógica de negocio)
       ├── views         (archivos de presentación)
       ├── assets        (CSS, JS, imágenes)
       └── config        (archivos de configuración)
     ```

3. **Configurar Base de Datos (PostgreSQL, MySQL u otra):**
   - Proveer un script inicial para crear las tablas (`users` y `tasks`).
   - Añadir datos de ejemplo para pruebas.

---

### **Fase 2: Autenticación Básica (Login y Logout)**
1. **Página de Login:**
   - Crear un formulario de login simple (`public/login.php`).
   - Implementar la lógica de autenticación utilizando sesiones:
     - Comprobar si el email y contraseña coinciden con un registro en la base de datos.
     - Crear una sesión para el usuario autenticado.

2. **Página de Logout:**
   - Destruir la sesión del usuario y redirigirlo al login.

3. **Middleware para Proteger Páginas:**
   - Crear un script reutilizable para verificar si un usuario está autenticado y redirigirlo si no lo está.

---

### **Fase 3: Gestión de Usuarios**
1. **Registro de Usuarios:**
   - Crear una página de registro (`public/register.php`) para insertar nuevos usuarios en la tabla `users`.
   - Validar datos (campos obligatorios, formato de email único, contraseña encriptada con `password_hash`).

2. **Roles:**
   - Añadir un campo `role` al usuario durante el registro (por defecto, "user").
   - Implementar lógica para que solo el rol "admin" pueda acceder a ciertas páginas.

---

### **Fase 4: CRUD de Tareas**
1. **Crear y Listar Tareas:**
   - Página principal para listar las tareas del usuario autenticado.
   - Formulario para añadir nuevas tareas.

2. **Editar y Eliminar Tareas:**
   - Implementar botones de editar y eliminar en la lista de tareas.
   - Asegurarse de que los usuarios solo puedan modificar/eliminar sus propias tareas.

3. **Validación y Seguridad:**
   - Validar datos del formulario antes de guardar en la base de datos.
   - Usar consultas preparadas para evitar inyecciones SQL.

---

### **Fase 5: Gestión Avanzada de Usuarios (para Admin)**
1. **Panel de Admin:**
   - Página accesible solo para administradores donde puedan:
     - Ver todos los usuarios.
     - Cambiar roles (promover a admin, degradar a usuario).
     - Eliminar usuarios.

2. **Ver Todas las Tareas:**
   - Los administradores pueden ver todas las tareas creadas en el sistema.

---

### **Fase 6: Mejoras y Extras (Opcional)**
1. **Paginación y Filtros:**
   - Añadir paginación a las listas de tareas y usuarios.
   - Filtrar tareas por estado (pendiente/completada).

2. **Búsqueda:**
   - Implementar un campo de búsqueda para encontrar tareas por título o descripción.

3. **Estilización:**
   - Aplicar CSS o Bootstrap para mejorar la apariencia del proyecto.

4. **Logs de Actividad:**
   - Registrar las acciones importantes (creación, edición y eliminación) en un archivo o tabla.

---

### **Orden de Clases y Desarrollo:**

1. **Primera clase:**
   - Configurar el entorno con Docker Compose.
   - Crear la estructura básica del proyecto.
   - Introducir PHP básico (sintaxis, variables, formularios).

2. **Segunda clase:**
   - Implementar el login y las sesiones.
   - Crear una página protegida que solo se muestre al iniciar sesión.

3. **Tercera clase:**
   - Crear el registro de usuarios y roles.
   - Mostrar un ejemplo de cómo limitar el acceso según el rol.

4. **Cuarta clase:**
   - Empezar el CRUD de tareas: listar y crear.

5. **Quinta clase:**
   - Completar el CRUD con editar y eliminar tareas.

6. **Clases posteriores:**
   - Ampliar el proyecto con funcionalidades avanzadas según el tiempo disponible.

---

### **Ventajas de este enfoque:**
- Los alumnos desarrollan el proyecto de manera iterativa, aplicando lo que aprenden en cada clase.
- Las fases se construyen sobre conceptos previos, reforzando el aprendizaje progresivo.
- Se pueden incluir ejercicios adicionales como retos o mejoras individuales.
