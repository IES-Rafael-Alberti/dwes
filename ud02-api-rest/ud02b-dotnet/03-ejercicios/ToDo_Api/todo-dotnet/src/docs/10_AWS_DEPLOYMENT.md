# 10 AWS Deployment (AWS Academy Learner Lab)

## Objetivo
Desplegar la app en AWS Academy Learner Lab usando contenedor Docker en App Runner.

## Prerrequisitos
- Cuenta AWS Academy Learner Lab activa.
- AWS CLI configurado con las credenciales temporales del Lab.
- Docker instalado.
- Region fija del Learner Lab (no se cambia).

## 0) Cargar la region fija del Lab
```bash
export AWS_REGION="$(aws configure get region)"
echo "$AWS_REGION"
```

> Si el comando devuelve vacio, revisa primero la configuracion de AWS CLI del Learner Lab.

## 1) Crear repositorio ECR
```bash
aws ecr create-repository \
  --repository-name todoapi-net \
  --region "$AWS_REGION"
```

## 2) Login de Docker en ECR
```bash
aws ecr get-login-password --region "$AWS_REGION" | \
docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr."$AWS_REGION".amazonaws.com
```

## 3) Build de imagen
Desde `todo-dotnet/src`:
```bash
docker build -f TodoApi/Dockerfile -t todoapi-net:latest .
```

## 4) Tag + Push a ECR
```bash
docker tag todoapi-net:latest <AWS_ACCOUNT_ID>.dkr.ecr."$AWS_REGION".amazonaws.com/todoapi-net:latest
docker push <AWS_ACCOUNT_ID>.dkr.ecr."$AWS_REGION".amazonaws.com/todoapi-net:latest
```

## 5) Crear servicio en App Runner
1. AWS Console -> App Runner -> Create service.
2. Source: `Container registry`.
3. Provider: `Amazon ECR`.
4. Image URI: `<AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/todoapi-net:latest`.
5. Port: `8080`.
6. Environment variables:
- `Jwt__Issuer=TodoApi`
- `Jwt__Audience=TodoApiClient`
- `Jwt__SigningKey=<secreto-largo-seguro>`
- `Jwt__ExpiresInSeconds=3600`

## Obtener `AWS_ACCOUNT_ID`
```bash
aws sts get-caller-identity --query Account --output text
```

## 6) Verificar despliegue
- Abrir URL pública de App Runner.
- Comprobar:
- `/` (frontend)
- `/api/tasks` (debe pedir auth)

## Notas importantes para esta app
- Persistencia actual: SQLite (`todo.db`) en disco local del contenedor.
- En App Runner el almacenamiento local es efímero.
- Para producción real:
- usar RDS (PostgreSQL/MySQL) o
- montar almacenamiento persistente externo.

## Variables de entorno en .NET
La app lee configuración jerárquica con doble guion bajo (`__`), por ejemplo:
- `Jwt__SigningKey`
- `ConnectionStrings__TodoDb`
