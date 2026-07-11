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
    <title>Listado de Coches</title>
    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="container">
<?php
//require_once 'conectapdo.php';
require_once "modelo.php";
$my_model = Model::getInstance();
//$consulta = $conn->query("select * from coche");
// Creación objeto BD
$coches= $my_model->coches();

 //   while ($coche = $consulta->fetchObject())
    //print_r("Me he conectado");
    foreach($coches as $coche)
    {
        echo("<span>" . $coche->getMarca() . "," . $coche->getModelo() . "," . $coche->getPrecio() . $coche->getStock() . 
             "<a href='borra_coche.php?id=" . 
             $coche->getId() . "'><i class='fas fa-trash'></i></a>" . 
             "<a href='editacochercoche.php?id=" . $coche->getId() . "'<i class='fas fa-pencil-alt'></i></a>" .
             "</span><br>");
    }

?>
</div>
</body>
</html>