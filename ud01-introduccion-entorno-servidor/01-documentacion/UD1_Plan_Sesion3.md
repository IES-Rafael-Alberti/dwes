
---

# UD1 — Introducción y arquitecturas (RA1)

## Objetivo

Que el alumnado **comprenda el ecosistema** (cliente/servidor), **Spring Boot**, herramientas y **tenga el entorno listo** para desarrollar.

## Contenidos (Cap. 1–2)

* **Cap. 1 (ya explicado):** HTTP, arquitecturas, Spring/Spring Boot, herramientas, flujo cliente↔servidor.
* **Cap. 2 (mañana lo rematamos):**

  * 2.1 JDK 21 y verificación de `java -version`.
  * 2.2 **IntelliJ (Community/Ultimate)**: abrir proyecto, ejecutar, depurar.
  * 2.3 Primer proyecto Spring Boot (el nuestro ya está listo).
  * 2.4 Estáticos (referencia mínima; nos centramos en API).
  * 2.5 VS Code (opcional), 2.6 Java, 2.7 NetBeans (nota rápida).

## Resultado práctico UD1

* Proyecto **backend funcional** levantando en local:

  * `GET /health` responde `{"status":"ok"}`
  * **Swagger UI** disponible.
* Alumno/a ha probado **Insomnia/Postman** y sabe lanzar peticiones.

## Evaluación UD1 (formativa, rápida)

* **Checklist de aula**:

  * [ ] JDK instalado.
  * [ ] Proyecto abre y compila en IntelliJ.
  * [ ] `/health` ok en navegador.
  * [ ] Colección Insomnia/Postman con al menos **3 requests** (health, login, create game).
* **Exit ticket** (3 min):

  1. ¿Qué diferencia hay entre `@RestController` y `@Controller`?
  2. ¿Dónde consulto la documentación de endpoints del proyecto?

---

# Plan de sesión de mañana (cierre UD1 + arranque UD2/Cap. 3)

**Duración objetivo**: 2–3 horas de clase

## Bloque A — Cierre Cap. 2 (30–45 min)

1. **Arranque del proyecto en dev**

   * Abrir IntelliJ → `Application.main()` o `./scripts/run-dev.sh`.
   * Comprobar `http://localhost:8080/health` y `http://localhost:8080/swagger-ui.html`.
2. **Colecciones**

   * Importar colección Insomnia/Postman básica (health, login).
   * Probar `POST /auth/login` (stub) y revisar respuesta.
3. **Docker (visión)**

   * `docker compose up -d` (solo mostrar; lo usaremos más adelante).

**Comprobación**: todo el grupo con `/health` y swagger ok.

---


