# Entorno reproducible con PHP 8.4

## Comprobación mínima

```bash
php --version
php -m
php -S localhost:8080 -t public
```

La salida de `php --version` debe comenzar por `PHP 8.4`. El servidor integrado solo se usa en desarrollo; no sustituye Nginx o Apache en producción.

## Estructura de aprendizaje

```text
project/
├── public/        # único document root
├── src/           # código de aplicación
├── templates/     # presentación
├── tests/         # pruebas automatizadas
├── composer.json
└── .env.example   # nombres, nunca secretos reales
```

Composer gestiona autoload y herramientas de desarrollo. Las dependencias instaladas en `vendor/`, `.env` y resultados generados no se versionan.

## Contenedores

Cuando se incorpore persistencia, el entorno separará servidor web, PHP 8.4-FPM y PostgreSQL. La configuración exacta y el comando de test se entregarán con el starter de GTask; todavía no existe un harness oficial ejecutable.

## Diagnóstico

- `php -l file.php`: sintaxis de un archivo.
- `php --ini`: configuración cargada.
- `php -r 'var_dump(PDO::getAvailableDrivers());'`: drivers PDO.
- `composer diagnose`: configuración de Composer.

No se usa `phpinfo()` en un despliegue público: expone configuración y rutas sensibles.
