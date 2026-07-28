# docs/12_AWS_PRACTICE_ASSIGNMENT.md

## Practica evaluable - Despliegue en AWS Academy (Learner Lab)

> Nota de planificacion: esta practica no es un punto fundamental del modulo.  
> Si no se puede completar ahora por falta de tiempo, se realizara como actividad en sesiones de practicas o al finalizar el bloque principal.

### Objetivo
Desplegar la aplicacion en AWS App Runner, probar que funciona por Internet y documentar el proceso.

### Guias que debes seguir
- Guia simplificada: `docs/11_AWS_DEPLOYMENT_SIMPLIFIED.md`
- Guia completa (si eliges Docker/ECR): `docs/10_AWS_DEPLOYMENT.md`
- Plantilla de entrega: `docs/13_AWS_DELIVERY_TEMPLATE.md`

---

## 1) Tareas obligatorias

1. Iniciar Learner Lab y confirmar que esta en `Running`.
2. Elegir opcion de despliegue:
- Opcion A: Source Code desde GitHub (sin Docker).
- Opcion B: Docker + ECR + App Runner.
3. Ejecutar la app en local antes de desplegar (`dotnet build`, `dotnet run`).
4. Desplegar en App Runner siguiendo la opcion elegida.
5. Verificar endpoints desde la URL publica.
6. Completar documento de entrega.

---

## 2) Pruebas minimas obligatorias del despliegue

Con la URL publica de App Runner, comprobar:
- `GET /` (frontend o respuesta base de la app).
- `GET /api/tasks` sin token (esperado `401`).
- `POST /api/auth/login` con usuario valido (esperado `200` y token).
- `GET /api/tasks` con token valido (esperado `200`).
- `GET /swagger` (indicar si esta habilitado o no en ese entorno).

Si alguna prueba no da el resultado esperado:
- explicar causa,
- aplicar correccion,
- volver a probar y registrar resultado final.

---

## 3) Evidencias obligatorias

- URL publica de App Runner.
- Captura del servicio en estado `Running`.
- Captura de al menos 2 pruebas HTTP (una de error y una de exito).
- Captura o salida de login con token JWT.
- Enlace al repositorio GitHub usado.

---

## 4) Criterios de evaluacion (resumen)

- Despliegue operativo en App Runner.
- Pruebas ejecutadas y documentadas.
- Uso correcto de autenticacion JWT en pruebas.
- Incidencias explicadas con accion correctiva.
- Entrega completa y ordenada.

---

## 5) Entrega

Rellenar y entregar:
- `docs/13_AWS_DELIVERY_TEMPLATE.md`
