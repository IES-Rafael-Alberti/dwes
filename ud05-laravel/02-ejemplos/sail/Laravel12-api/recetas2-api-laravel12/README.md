# API de Recetas - Laravel 12 con Sail

Ejemplo guiado de la UD5 para construir una API REST con Laravel 12, PostgreSQL, Redis, Sanctum, Policies, Form Requests, API Resources y pruebas automatizadas.

## Arranque sin PHP ni Composer globales

Requisitos: Docker en Linux/macOS o Docker Desktop con WSL2 en Windows. Ejecuta los comandos desde la carpeta del proyecto:

```bash
docker run --rm --user "$(id -u):$(id -g)" --volume "$PWD:/app" --workdir /app composer:2 composer install --no-interaction --prefer-dist
cp .env.example .env
./vendor/bin/sail up -d
./vendor/bin/sail artisan key:generate
./vendor/bin/sail artisan migrate --seed
```

El contenedor temporal de Composer solo crea `vendor/` en un clon nuevo. Desde ese punto, PHP, Composer, Artisan y las extensiones se ejecutan dentro de Sail:

```bash
./vendor/bin/sail artisan test
./vendor/bin/sail artisan route:list
./vendor/bin/sail composer show
./vendor/bin/sail down
```

Si los puertos locales están ocupados, asigna otros sin modificar `.env`:

```bash
APP_PORT=8085 FORWARD_DB_PORT=5433 FORWARD_REDIS_PORT=6380 ./vendor/bin/sail up -d
```

## Contrato de la API

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/ping` | Público |
| POST | `/api/auth/register` | Público |
| POST | `/api/auth/login` | Público |
| GET | `/api/auth/me` | Bearer Sanctum |
| POST | `/api/auth/logout` | Bearer Sanctum |
| POST | `/api/auth/refresh` | Bearer Sanctum |
| GET/POST | `/api/recetas` | Bearer Sanctum |
| GET/PUT/DELETE | `/api/recetas/{receta}` | Bearer Sanctum y Policy |

Las recetas se serializan mediante `RecetaResource`. Las listas y los recursos individuales tienen `data`; las listas paginadas también exponen `links` y `meta`.

## Verificación

La línea base actual se valida con:

```bash
./vendor/bin/sail artisan test
```

La suite cubre salud pública, autenticación, validación, CRUD, búsqueda, ordenación, paginación, propiedad, rol administrador y la regla de no modificar recetas publicadas.

## Guía docente

El índice de lectura está en [docs/00_indice.md](docs/00_indice.md). Cada modificación debe conservar el flujo: ruta -> Form Request -> controlador -> servicio -> policy -> resource -> prueba.
