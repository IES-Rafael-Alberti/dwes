# 📘 GUIA PARA EL ALUMNADO

## Despliegue simplificado en AWS Academy (Learner Lab)

### Objetivo
Publicar una API .NET en Internet con AWS App Runner, sin entrar en despliegues complejos.

Al final debes tener:
- URL publica HTTPS.
- API funcionando.
- Swagger accesible (si se habilita en produccion).

---

## 1) Entrar en AWS Academy y Learner Lab

1. Entra en AWS Academy con tu cuenta del curso.
2. Abre tu asignatura.
3. Lanza el Learner Lab.
4. Espera a estado `Running`.
5. Pulsa `AWS Console`.

> Trabaja siempre dentro del Learner Lab.

---

## 2) Preparar proyecto y decidir camino

Comprueba local:
```bash
dotnet build
dotnet run
```

### Camino A (recomendado para clase): App Runner desde GitHub sin Docker
Usa este camino si despliegas con `net8.0`.

### Camino B (si mantienes net10): Docker + ECR + App Runner
Usa este camino si no cambias de `net10.0`.

---

## 3) Subir proyecto a GitHub

Si no esta subido:
```bash
git init
git add .
git commit -m "API lista para despliegue"
git branch -M main
git remote add origin https://github.com/USUARIO/REPO.git
git push -u origin main
```

---

## 4) Camino A - Sin Docker (App Runner + Source Code)

## 4.1 Cambiar a net8 para despliegue (si hace falta)
En este proyecto ya existe respaldo para instituto:
- `TodoApi.csproj.bak8`

Usa la version `net8` antes de desplegar por Source Code.

## 4.2 Crear servicio en App Runner
1. AWS Console -> App Runner -> Create service.
2. Source:
- Repository type: `Source code repository`
- Provider: `GitHub`
3. Selecciona repo y rama (`main`).
4. Build settings:
- Runtime: `.NET`
- Build command:
```bash
dotnet publish -c Release -o out
```
- Start command:
```bash
dotnet out/TodoApi.dll --urls=http://0.0.0.0:80
```
- Port: `80`
5. Create & deploy.

---

## 5) Camino B - Con Docker (net10 recomendado)

Este repositorio ya incluye:
- `TodoApi/Dockerfile`
- `.dockerignore`

Pasos:
1. Crear ECR.
2. Login Docker en ECR.
3. Build imagen.
4. Push imagen.
5. Crear App Runner desde `Container registry (ECR)`.

Referencia completa:
- `docs/10_AWS_DEPLOYMENT.md`

---

## 6) Swagger en App Runner

Importante:
- En este proyecto Swagger UI esta activado en `Development`.
- En App Runner normalmente estara en `Production`.

Si quieres ver `/swagger` en despliegue:
- habilita Swagger tambien para produccion, o
- usa una variable de entorno para habilitarlo.

Si no se habilita, la API funcionara pero `/swagger` no estara visible.

---

## 7) Comprobar resultado

Con la URL de App Runner:
- `https://.../`
- `https://.../api/tasks`
- `https://.../swagger` (si esta habilitado)

---

## 8) Entrega sugerida

- URL publica App Runner.
- Captura de la app funcionando.
- Enlace al repositorio GitHub.

---

## 9) Problemas habituales

- Error al arrancar:
- revisa nombre del `.dll` en Start command.
- `401/403` en endpoints:
- revisa JWT y cabecera `Authorization`.
- No aparece App Runner:
- confirma Learner Lab en `Running`.
- No aparece Swagger:
- revisa configuracion de entorno (`Development` vs `Production`).
