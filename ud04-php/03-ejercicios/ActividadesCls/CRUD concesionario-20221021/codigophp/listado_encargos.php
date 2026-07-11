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
        Base de datos <u>Concesionario</u><br> Tabla <u>coches</u><br>
    </h2>
    <?php require_once 'modelo.php';
        $my_model = Model::getInstance();
        $encargos = $my_model->encargos();
    ?>
    <table style='border: solid 1px black;'>
        <caption>Tabla de Coches</caption>
        <tr>
            <th>ID</th>
            <th>Marca y Modelo</th>  <!-- Lo suyo sería meter marca y modelo -->
            <th>Nombre Cliente</th>   <!-- Poner nombre  -->
            <th>Cantidad</th>
            <th>Fecha</th>
        </tr>
        <?php
        foreach($encargos as $encargo){
        ?>
            <tr>
                <td><?= $encargo->getId() ?></td>
                <td><?= $encargo->getCocheId() ?></td>
                <td><?= $encargo->getClienteId()  ?></td>
                <td><?= $encargo->getCantidad()  ?></td>
                <td><?= $encargo->getFecha() ?></td>
                <td><a href='eliminaEncargo.php?id=<?= $encargo->getId() ?>'><i class='fas fa-trash'></i></a></td>
                <td><a href='modificaEncargo.php?id=<?= $encargo->getId() ?>'><i class='fas fa-pencil-alt'></i></a></td>
            </tr>
        <?php
        }
        ?>
    </table>
    <br />
</body>

</html>