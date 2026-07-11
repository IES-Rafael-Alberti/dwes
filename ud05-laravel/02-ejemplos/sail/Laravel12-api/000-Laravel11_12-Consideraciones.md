# Laravel 12, Spring Boot y .NET Web API

## ¿Estamos viendo el mismo framework con otro lenguaje?

### Idea clave (para empezar la clase)

> **No.**
> Laravel 12, Spring Boot y .NET Web API **no resuelven los mismos problemas de la misma forma**,
> aunque todos sirvan para crear APIs REST.

---

## 1️⃣ El error habitual del alumno

Muchos alumnos piensan:

> “Spring Boot es Java, Laravel es PHP, .NET es C#,
> pero al final hacen lo mismo”.

Esto **ya no es cierto**, y **Laravel 12 es un buen ejemplo**.

---

## 2️⃣ Qué era Laravel “antes” (hasta Laravel 10)

Laravel se caracterizaba por:

* Mucha **magia**
* Autoregistro de componentes
* Muchas cosas “funcionaban sin saber por qué”

Ejemplos:

* Middlewares ya activos
* Excepciones gestionadas sin tocar nada
* Rutas API creadas automáticamente
* Policies que “aparecían”

Esto era muy cómodo para empezar, pero tenía un problema claro:

> El desarrollador **no sabía qué estaba pasando realmente**.

---

## 3️⃣ Qué cambia en Laravel 11/12

Laravel 12 **no quita funcionalidades**, quita **autoconfiguración implícita**.

Ahora:

* Hay que **registrar explícitamente** cosas
* Hay que **decidir conscientemente** cómo se configura la aplicación
* El framework deja de “hacer magia”

Laravel te dice:

> “Si quieres usar algo, **decláralo tú**”.

---

## 4️⃣ Aquí es donde Laravel 12 se parece a .NET Web API

### En .NET Core Web API es normal ver algo así:

```csharp
builder.Services.AddControllers();
builder.Services.AddAuthentication();
builder.Services.AddAuthorization();
builder.Services.AddSwaggerGen();
```

Nada funciona si no lo registras explícitamente.

---

### En Laravel 12 ahora pasa lo mismo (conceptualmente)

Ejemplos reales del proyecto que habéis desarrollado:

* Autenticación → Sanctum configurado explícitamente
* Autorización → Policies definidas y usadas a mano
* Excepciones → `withExceptions()` en `bootstrap/app.php`
* Swagger → instalación manual
* Rutas → ya no existe `api.php` por defecto

👉 **Esto es una filosofía muy cercana a .NET Web API**.

---

## 5️⃣ Comparación clara de filosofías

| Framework     | Filosofía dominante                  |
| ------------- | ------------------------------------ |
| Spring Boot   | Autoconfiguración + anotaciones      |
| Laravel ≤10   | Magia + convención                   |
| Laravel 11/12 | **Configuración explícita**          |
| .NET Web API  | **Registro explícito desde siempre** |

Laravel 12 **se mueve claramente hacia la derecha**, alejándose del enfoque “mágico”.

---

## 6️⃣ Comparativa técnica más detallada

| Aspecto                 | Laravel ≤10          | **Laravel 11/12**               | Spring Boot            | .NET Web API             |
| ----------------------- | -------------------- | ------------------------------- | ---------------------- | ------------------------ |
| Filosofía general       | Productividad rápida | **Intención explícita**         | Autoconfiguración      | **Registro explícito**   |
| Registro de componentes | Implícito            | **Explícito**                   | Escaneo automático     | **Explícito**            |
| Middleware              | Kernel central       | **Registro manual**             | Filtros/interceptores  | Middleware explícito     |
| Rutas API               | `api.php` automático | **No automático**               | Controllers detectados | Controllers registrados  |
| Autenticación           | Poco visible         | **Configurada conscientemente** | Auto-configurable      | **Declarativa**          |
| Autorización            | Policies implícitas  | **Policies explícitas**         | Anotaciones            | **Policies/Attributes**  |
| Manejo de errores       | Handler implícito    | **Excepciones declaradas**      | Global handlers        | **Middleware explícito** |
| Swagger                 | Fácil, poco control  | **Instalación consciente**      | Integración automática | **Registro manual**      |
| Magia del framework     | Alta                 | **Baja**                        | Media                  | **Muy baja**             |
| Control del flujo       | Medio                | **Alto**                        | Medio                  | **Muy alto**             |
| Cercanía a empresa      | Media                | **Alta**                        | Alta                   | **Muy alta**             |

---

## 7️⃣ ¿Y Spring Boot?

Spring Boot:

* Sigue apostando fuerte por:

  * escaneo automático
  * anotaciones
  * configuración implícita
* Es muy potente, pero:

  * a veces cuesta saber **por qué se ejecuta algo**
  * el “flujo real” está más oculto

Laravel 12, en cambio:

* hace visible el flujo de la aplicación
* obliga a entender la arquitectura

---

## 8️⃣ Por qué esto es bueno para aprender backend

En empresa:

* **No se reescriben proyectos**
* Se mantienen, se amplían y se corrigen

Laravel 12 favorece:

* entender dependencias
* saber dónde tocar
* reducir efectos colaterales

Para aprender:

> Es mejor entender **qué se registra y por qué**,
> que usar algo que “funciona sin saber cómo”.

---

## 9️⃣ Lo importante: no estamos viendo “lo mismo”

No estamos viendo:

* “Spring Boot pero en PHP”
* “.NET pero con otro lenguaje”

Estamos viendo:

* **tres formas distintas de entender un backend**
* **tres filosofías de framework**

Y Laravel 12 **ya no juega en la liga de la magia**,
juega en la liga de la **arquitectura explícita**.

---

## 🔟 Conexión directa con el proyecto que habéis hecho

En el proyecto habéis trabajado:

* guards explícitos
* policies reales
* códigos de error
* tests funcionales
* documentación con Swagger

👉 **Eso es backend profesional**,
no un “framework de juguete”.

---

### Idea final para quedarse con ella

> Laravel 12 ya no te hace las cosas por ti.
> Te obliga a **saber qué estás haciendo**.
>
> Y eso lo acerca mucho más a .NET Web API
> que al Laravel que muchos conocían.
