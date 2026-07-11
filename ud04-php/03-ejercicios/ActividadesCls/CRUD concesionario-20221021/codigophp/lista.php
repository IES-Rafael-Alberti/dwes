<?php
    session_start();
    if(!isset($_SESSION["username"])) {
       header("Location: login.php");
    }
?>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <title>Document</title>
</head>

<body>
    <h2>
        Base de datos <u>Coches</u><br> Tabla <u>coches</u><br>
    </h2>
    <?php require_once 'conectapdo.php';
    $consulta = $conn->prepare("select * from coche");
    $consulta->execute()
    ?>
    <table style='border: solid 1px black;'>
        <caption>Tabla de Coches</caption>
        <tr>
            <th>ID</th>
            <th>Marca</th>
            <th>Modelo</th>
            <th>Precio</th>
            <th>stock</th>
        </tr>
        <?php
        while ($coche = $consulta->fetchObject()) {
        ?>
            <tr>
                <td><?= $coche->id ?></td>
                <td><?= $coche->marca ?></td>
                <td><?= $coche->modelo ?></td>
                <td><?= $coche->precio ?></td>
                <td><?= $coche->stock ?></td>
                <td><a href='eliminarcoche.php?id=<?= $coche->id ?>'><i class='fas fa-trash'></i></a></td>
                <td><a href='editarcoche.php?id=<?= $coche->id ?>'><i class='fas fa-pencil-alt'></i></a></td>
            </tr>
        <?php
        }
        ?>
    </table>
    <br />
    Número de coches: <?= $consulta->rowCount(); ?>
    <?php $conn = null; ?>
</body>

</html>