# Entorno reproducible con Laravel Sail

Laravel Sail es la interfaz de Laravel para su entorno Docker. Ejecuta PHP, Composer, Artisan, base de datos y otros servicios dentro de contenedores, por lo que el sistema anfitrión no determina la versión de PHP ni las extensiones disponibles.

## Requisitos por sistema

| Sistema | Necesitas |
|---|---|
| Linux | Docker Engine y Docker Compose. Configura el grupo `docker` para no ejecutar Sail con `sudo`. |
| macOS | Docker Desktop en ejecución. |
| Windows | Docker Desktop con integración WSL2 y una distribución Linux. Trabaja desde esa terminal Linux y guarda el proyecto en su sistema de archivos. |

No uses CMD, PowerShell ni Git Bash para ejecutar Sail en Windows. WSL2 evita problemas de rendimiento, permisos y montajes de volúmenes.

## Arranque del ejemplo canónico

Desde `02-ejemplos/sail/Laravel12-api/recetas2-api-laravel12/`:

```bash
docker run --rm --user "$(id -u):$(id -g)" --volume "$PWD:/app" --workdir /app composer:2 composer install --no-interaction --prefer-dist
cp .env.example .env
./vendor/bin/sail up -d
./vendor/bin/sail artisan key:generate
./vendor/bin/sail artisan migrate --seed
./vendor/bin/sail artisan test
```

El primer comando es solo el bootstrap de un clon que todavía no tiene `vendor/` ni el script de Sail. Composer sigue ejecutándose en un contenedor temporal, no en el sistema anfitrión. Una vez creado `vendor/bin/sail`, todos los comandos posteriores usan Sail.

Las órdenes habituales de Laravel se ejecutan con Sail:

```bash
./vendor/bin/sail artisan route:list
./vendor/bin/sail composer show
./vendor/bin/sail php -v
./vendor/bin/sail npm run build
```

Para detener el entorno:

```bash
./vendor/bin/sail down
```

## Diagnóstico mínimo

Antes de abrir un problema de código, confirma que Docker está disponible y que Sail responde:

```bash
docker version
./vendor/bin/sail ps
./vendor/bin/sail artisan -V
```

Si el proyecto se creó desde Docker en Linux y los ficheros pertenecen a `root`, corrige la propiedad una vez desde la carpeta padre:

```bash
sudo chown -R "$USER":"$USER" recetas2-api-laravel12
```

`.env` contiene configuración local y posibles secretos: no se versiona ni se entrega. El contrato reproducible es `.env.example` más estos comandos.
