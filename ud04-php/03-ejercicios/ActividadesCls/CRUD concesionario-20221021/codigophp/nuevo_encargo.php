<?php
    require_once "modelo.php";
    try {
        $my_model = Model::getInstance();
    } catch (PDOException $e) {
        header("Location: error.php?text=Se ha producido una excepción PDO: " . $e->getMessage());
    }
    // Inserta la mascota
    if (isset($_POST["nombre"])) {
        $nombre = $_POST["nombre"];
        $especie = $_POST["especie"];
        $propietario = $_POST["propietario"];
        try {
            $filas = $my_model->crea_mascota($nombre, $especie, $propietario);
            if($filas == 0) {
                header("Location: error.php?text=No se ha podido crear la mascota " . $nombre);
            } else {
                // Redirigir al listado
                header("Location: error.php?text=Creada la mascota " . $nombre);
            }
        } catch (PDOException $e) {
            header("Location: error.php?text=Se ha producido una excepción PDO: " . $e->getMessage());
        }
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../css/main.css">
    <title>Document</title>
</head>
<body>
    <form action="" class="container" method="post">
        <input type="text" name="nombre">
        <input type="text" name="especie">
        <select name="propietario" id="propietario">
            <?php
            foreach($my_model->propietarios() as $propietario) {
                echo '<option value="'. $propietario->getId() . '">' . $propietario . '</option>';
            }
            ?>
        </select>
        <input type="submit" value="Crear">
    </form>
</body>
</html>