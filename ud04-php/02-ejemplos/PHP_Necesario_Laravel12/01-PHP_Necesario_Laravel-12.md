
# PHP clásico vs Laravel 12

## Antes / Después (con código real para analizar)

Este bloque sirve para **entender qué hace realmente un framework** y **por qué existe**.
No se pretende que el alumnado programe así hoy, sino que **sepa reconocerlo, entenderlo y mantenerlo si lo encuentra**.

---

## 1️⃣ Conexión a base de datos

### 🔴 ANTES (PHP clásico)

```php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "concesionario";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Error de conexión");
}
```

### Qué hay que analizar

* La conexión es **explícita**
* Las credenciales están en el código
* Si falla, la aplicación muere
* El desarrollador **gestiona todo**

---

### 🟢 DESPUÉS (Laravel 12)

```env
DB_CONNECTION=pgsql
DB_HOST=pgsql
DB_DATABASE=laravel
DB_USERNAME=laravel
DB_PASSWORD=secret
```

```php
Receta::all();
```

### Qué debe quedar claro

* La conexión **sigue existiendo**
* Laravel la configura y la reutiliza
* El programador **no escribe código de conexión**
* El problema no desaparece, **se abstrae**

---

## 2️⃣ Recuperar datos enviados por el cliente

### 🔴 ANTES (PHP clásico)

```php
$marca  = $_POST['marca'];
$modelo = $_POST['modelo'];
$id     = $_GET['id'];
```

### Qué hay que analizar

* Variables globales
* No hay control de existencia
* No hay tipado
* El código depende del formulario HTML

---

### 🟢 DESPUÉS (Laravel 12)

```php
public function store(Request $request)
{
    $marca  = $request->input('marca');
    $modelo = $request->input('modelo');
}
```

### Qué cambia

* Los datos se encapsulan en un objeto
* Se centraliza el acceso
* El controlador **no depende del formulario**
* Aparece el concepto de **Request**

---

## 3️⃣ Validación de datos

### 🔴 ANTES (PHP clásico)

```php
$errores = [];

if (empty($_POST['marca'])) {
    $errores[] = "La marca es obligatoria";
}

if (empty($_POST['modelo'])) {
    $errores[] = "El modelo es obligatorio";
}

if (!empty($errores)) {
    include "formulario.php";
    exit;
}
```

### Qué hay que analizar

* Validación manual
* Mucho código repetido
* Difícil de reutilizar
* Mezcla flujo y lógica

---

### 🟢 DESPUÉS (Laravel 12)

```php
class StoreRecetaRequest extends FormRequest
{
    public function rules(): array
    {
        return [
            'marca'  => 'required|string',
            'modelo' => 'required|string',
        ];
    }
}
```

```php
public function store(StoreRecetaRequest $request)
{
    // datos válidos garantizados
}
```

### Qué cambia

* La validación se **externaliza**
* El controlador es más limpio
* El error se gestiona automáticamente
* Similar a `@Valid` en Spring Boot

---

## 4️⃣ Inserción en base de datos

### 🔴 ANTES (PHP clásico)

```php
$sql = "INSERT INTO coches (marca, modelo)
        VALUES ('$marca', '$modelo')";

$conn->query($sql);
```

### Qué hay que analizar

* SQL escrito a mano
* Concatenación peligrosa
* Riesgo de SQL Injection
* Código difícil de mantener

---

### 🟢 DESPUÉS (Laravel 12)

```php
Receta::create([
    'marca'  => $marca,
    'modelo' => $modelo,
]);
```

### Qué cambia

* No se escribe SQL
* El ORM genera la consulta
* Se centraliza la persistencia
* Sigue habiendo SQL, pero controlado

---

## 5️⃣ Listado de datos (lectura)

### 🔴 ANTES (PHP clásico)

```php
$result = $conn->query("SELECT * FROM coches");

while ($row = $result->fetch_assoc()) {
    echo $row['marca'] . " " . $row['modelo'];
}
```

### Qué hay que analizar

* Bucle manual
* Acceso directo a arrays
* Lógica y presentación mezcladas

---

### 🟢 DESPUÉS (Laravel 12)

```php
$recetas = Receta::paginate(10);

return response()->json($recetas);
```

### Qué cambia

* Separación total de capas
* No se genera HTML
* API REST real
* Paginación integrada

---

## 6️⃣ Control de permisos

### 🔴 ANTES (PHP clásico)

```php
if ($usuario_id != $coche['usuario_id']) {
    die("No autorizado");
}
```

### Qué hay que analizar

* Lógica incrustada
* Difícil de reutilizar
* Sin estructura

---

### 🟢 DESPUÉS (Laravel 12)

```php
$this->authorize('update', $receta);
```

```php
public function update(User $user, Receta $receta): bool
{
    return $user->id === $receta->user_id;
}
```

### Qué cambia

* Autorización centralizada
* Reglas reutilizables
* Código expresivo
* Testeable

---

## 7️⃣ Flujo completo: qué ve el alumno

### PHP clásico

* HTML → PHP → SQL → HTML
* Todo mezclado
* Difícil de escalar

### Laravel 12

* Request → Controller → Service → Model
* Capas claras
* Código mantenible

---

## 8️⃣ Qué deben aprender de este “antes / después”

### ✔ Lo importante

* Nada de esto es magia
* El framework **no inventa el backend**
* Laravel organiza problemas reales

### ❌ Lo que no deben hacer

* Volver al PHP procedural
* Copiar estos ejemplos
* Mezclar capas

---

## 9️⃣ Frase clave para cerrar en clase

> Si no entendéis el “antes”,
> el “después” os parecerá magia.
>
> Y la magia no se mantiene en empresa.

---

Si quieres, el siguiente paso puede ser:

* convertir esto en **actividad guiada de análisis**
* preparar **preguntas tipo examen / defensa**
* o integrarlo directamente en el **documento final de la tarea C**
