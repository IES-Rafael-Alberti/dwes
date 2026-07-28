

-- ==========================================================
--  data.sql
--  Archivo de inicialización de la base de datos H2
-- ==========================================================
--  Este script se ejecuta automáticamente al arrancar la aplicación,
--  después de que Hibernate cree la tabla TASK (gracias a la propiedad:
--      spring.jpa.defer-datasource-initialization=true)
--
--  Importante:
--  - No insertamos el campo ID porque está marcado con
--    @GeneratedValue(strategy = GenerationType.IDENTITY)
--  - Eso significa que la base de datos (H2) se encarga de asignar
--    los valores 1, 2, 3… automáticamente.
--  - De esta forma evitamos errores de clave duplicada al crear nuevas tareas.
-- ==========================================================

INSERT INTO task(title, done)
-- Primera tarea: pendiente
VALUES ('Revisar scripts', false),
       -- Segunda tarea: ya completada
       ('Preparar guión', true),
       -- Tercera tarea: pendiente
       ('Comunicación empresa', false);

-- ==========================================================
--  Nota:
--  Al arrancar por primera vez, H2 asignará automáticamente:
--     id = 1, 2 y 3
--  Si más adelante insertas una nueva tarea mediante el endpoint POST /tasks,
--  la base de datos asignará id = 4, y así sucesivamente.
--
--  Este comportamiento demuestra cómo Spring Boot y JPA delegan
--  la gestión del identificador primario en la base de datos,
--  igual que ocurrirá en MySQL, PostgreSQL o cualquier otro SGBD real.
-- ==========================================================