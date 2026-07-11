# Guía de PHPDocumentor para Generar Documentación en Proyectos PHP

## Introducción
PHPDocumentor es una herramienta que permite generar documentación a partir de comentarios en el código fuente de proyectos PHP. Utilizando anotaciones especiales, se pueden describir clases, métodos, propiedades y funciones para generar una documentación estructurada y navegable.

---

## Instalación de PHPDocumentor

Para instalar PHPDocumentor, puedes usar Composer:

```sh
composer global require phpdocumentor/phpdocumentor
```

Asegúrate de que la ruta `~/.composer/vendor/bin` (Linux/macOS) o `%USERPROFILE%\AppData\Roaming\Composer\vendor\bin` (Windows) está en tu variable de entorno `PATH` para ejecutar `phpdoc` desde cualquier ubicación.

Para verificar la instalación, ejecuta:

```sh
phpdoc --version
```

---

## Anotaciones en PHP para PHPDocumentor

PHPDocumentor utiliza una sintaxis de comentarios en bloque de tipo `/** ... */` para definir metadatos en el código. A continuación, se muestran las anotaciones más utilizadas:

### Documentación de Clases

```php
/**
 * Clase que representa un usuario en el sistema.
 *
 * @package MiAplicacion
 * @author Juan Pérez
 * @version 1.0
 */
class Usuario {
    /**
     * Nombre del usuario
     * @var string
     */
    private string $nombre;

    /**
     * Constructor de la clase Usuario.
     *
     * @param string $nombre Nombre del usuario.
     */
    public function __construct(string $nombre) {
        $this->nombre = $nombre;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return string Nombre del usuario.
     */
    public function getNombre(): string {
        return $this->nombre;
    }
}
```

### Documentación de Funciones y Métodos

```php
/**
 * Calcula el área de un círculo.
 *
 * @param float $radio Radio del círculo.
 * @return float Área calculada.
 */
function calcularArea(float $radio): float {
    return pi() * pow($radio, 2);
}
```

### Documentación de Variables y Propiedades

```php
/**
 * Base de datos utilizada en la aplicación.
 * @var PDO
 */
private PDO $db;
```

### Otras Etiquetas Utilizadas

| Etiqueta | Descripción |
|----------|-------------|
| `@param` | Define los parámetros de una función o método. |
| `@return` | Especifica el tipo de dato que devuelve una función o método. |
| `@var` | Describe el tipo de una propiedad o variable. |
| `@throws` | Indica posibles excepciones lanzadas por un método. |
| `@deprecated` | Marca un método o clase como obsoleto. |
| `@author` | Define el autor del código. |
| `@version` | Especifica la versión de la clase o archivo. |

---

## Generar Documentación con PHPDocumentor

Para generar la documentación de un proyecto, navega hasta el directorio del proyecto y ejecuta:

```sh
phpdoc -d src -t docs
```

Donde:
- `-d src` especifica el directorio donde se encuentra el código fuente.
- `-t docs` define la carpeta donde se generará la documentación.

Tras ejecutar este comando, se generará un conjunto de archivos HTML en la carpeta `docs`. Puedes abrir el archivo `index.html` en un navegador para navegar por la documentación.

### Otras Opciones útiles

Generar documentación en formato PDF:
```sh
phpdoc -d src -t docs --template=pdf
```

Filtrar archivos específicos:
```sh
phpdoc -f src/Usuario.php -t docs
```

Especificar el formato de salida:
```sh
phpdoc run --template="clean"
```

---

## Conclusión
PHPDocumentor es una herramienta esencial para mantener documentados los proyectos PHP de manera estructurada. Siguiendo esta guía, puedes anotar tu código de manera adecuada y generar documentación profesional con facilidad. Incluir comentarios bien estructurados facilita la colaboración y mejora la mantenibilidad del código.


