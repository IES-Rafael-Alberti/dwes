<?php
// Conexión a la base de datos (código anterior)

// Contraseña del usuario
$password = 'pestillo';

// Cifrar la contraseña
$hashed_password = password_hash($password, PASSWORD_DEFAULT);

echo "<p>Contraseña cifrada: $hashed_password</p><br>";
echo "<p> cifrada y almacenada exitosamente.</p>";
?>