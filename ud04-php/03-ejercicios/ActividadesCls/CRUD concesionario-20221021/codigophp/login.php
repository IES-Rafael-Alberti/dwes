<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="text/css" href="../css/main.css">
    <title>Acceso al sistema</title>
</head>
<body>
    <form action="check_user.php" class="container" method="post">
    <label for="username">Usuario</label>    
    <input type="text" name="username">
    <label for="password">Contraseña</label>
    <input type="password" name="password">
    <input type="submit" value="Login">
    </form>
    <?php
        if(isset($_GET["error"])) {
            echo '<span class="error">' . $_GET["error"] . "</span>"; 
        }
    ?>
</body>
</html>