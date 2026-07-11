<?php
    session_start();
    if(!isset($_SESSION["username"])) {
       header("Location: login.php");
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
<h3>Crear Usuario</h3>
    <form action="crear_usuario.php" method="post">
        <label for="username">Nombre de Usuario</label>
        <input type="text" name="username" id="username">
        <label for="password">Contraseña</label>
        <input type="password" name="password" id="password">
        <input type="submit" value="Crear Usuario">
    </form>
</body>   
</html>