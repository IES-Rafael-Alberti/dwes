# Practica AWS Academy - Documento de entrega del grupo

> Nota de planificacion: este bloque de despliegue no es un punto fundamental del modulo.  
> Si no se completa en este momento por falta de tiempo, se entregara y revisara durante las practicas o al terminar el resto de contenidos.

---

## 1. Datos del grupo

**Grupo n.:**  
**Fecha de entrega:**  
**Modulo:** Desarrollo web en entorno servidor

### Integrantes
- Alumno/a 1:
- Alumno/a 2:
- Alumno/a 3:

---

## 2. Objetivo de la practica

En esta practica el objetivo ha sido:
- desplegar la aplicacion ToDo API en AWS Academy Learner Lab,
- publicar un servicio en Internet con AWS App Runner,
- validar el funcionamiento real del backend (auth + endpoints protegidos),
- y documentar el proceso tecnico de forma reproducible.

---

## 3. Opcion de despliegue elegida (obligatorio)

Marca una sola opcion:
- [ ] Opcion A - App Runner desde Source Code (GitHub, sin Docker)
- [ ] Opcion B - Docker + ECR + App Runner

Justificacion tecnica de la eleccion (3-6 lineas):

> ...

---

## 4. Contexto del despliegue

- Region del Learner Lab:
- Repositorio GitHub usado (URL):
- Rama desplegada:
- Nombre del servicio App Runner:
- URL publica final:
- Estado del servicio al entregar: `Running` / `Degraded` / otro:

---

## 5. Proceso realizado

Describe de forma ordenada lo que habies hecho (minimo 8 pasos):

1.
2.
3.
4.
5.
6.
7.
8.

---

## 6. Configuracion tecnica usada

Completar segun vuestra opcion:

- Runtime o imagen:
- Puerto expuesto:
- Build command (si aplica):
- Start command (si aplica):
- Variables de entorno definidas:
- Cambios de version .NET realizados (si aplica):

> ...

---

## 7. Pruebas de despliegue y verificacion

Completa esta tabla con resultado esperado y real.

| Prueba | Endpoint | Metodo | Auth | Esperado | Real |
|---|---|---|---|---|---|
| 1 | `/` | GET | No | 200 | |
| 2 | `/api/tasks` | GET | No | 401 | |
| 3 | `/api/auth/login` | POST | No | 200 + JWT | |
| 4 | `/api/tasks` | GET | Si (Bearer) | 200 | |
| 5 | `/swagger` | GET | No | Segun configuracion | |

Explica brevemente el resultado de `/swagger` en vuestro despliegue:

> ...

---

## 8. Evidencias obligatorias

Adjuntar o enlazar:
- [ ] Captura de App Runner en estado `Running`
- [ ] Captura o salida de login con JWT
- [ ] Captura o salida de una prueba `401`
- [ ] Captura o salida de una prueba `200` autenticada
- [ ] URL publica funcionando

Rutas o enlaces de evidencias:
- Evidencia 1:
- Evidencia 2:
- Evidencia 3:
- Evidencia 4:

---

## 9. Preguntas obligatorias segun opcion

### 9.1 Si elegiste Opcion A (Source Code)

1. Que runtime seleccionaste en App Runner y por que.
2. Que `build command` configuraste.
3. Que `start command` configuraste y que `.dll` arranca.
4. Como verificaste que App Runner estaba construyendo la rama correcta.
5. Si estabais en `net10`, que ajuste hicisteis para desplegar por esta via.

### 9.2 Si elegiste Opcion B (Docker + ECR)

1. URI del repositorio ECR usado.
2. Comando de build ejecutado.
3. Comandos de tag y push ejecutados.
4. Image URI final usada en App Runner.
5. Que ventaja practica os dio Docker frente a Source Code en vuestro caso.

---

## 10. Problemas encontrados y soluciones

Documenta incidencias reales y como las resolvisteis.

| Problema | Causa | Solucion | Verificacion final |
|---|---|---|---|
|   |   |   |   |
|   |   |   |   |

---

## 11. Reflexion tecnica (obligatoria)

Responder de forma razonada:

### 11.1 Diferencia entre `401` y `403` en esta practica

> ...

### 11.2 Que parte del despliegue fue mas dificil y por que

> ...

### 11.3 Que mejorariais en una segunda version

> ...

---

## 12. Relacion con el modulo

Explica brevemente como conecta esta practica con:
- APIs REST en .NET
- autenticacion/autorizacion
- pruebas HTTP manuales (curl/httpie/Swagger)

> ...

---

## 13. Checklist final

- [ ] Opcion A/B marcada y justificada
- [ ] URL publica incluida
- [ ] Tabla de pruebas completada
- [ ] Evidencias adjuntas
- [ ] Preguntas por opcion respondidas
- [ ] Problemas/soluciones documentados
- [ ] Reflexion tecnica completada
