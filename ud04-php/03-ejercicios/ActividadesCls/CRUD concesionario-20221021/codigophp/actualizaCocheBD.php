<?php
require_once 'conectapdo.php';
try {
    $modelo = $_POST['modelo'];
    $marca = $_POST['marca'];
    $precio = (int) $_POST['precio'];
    $stock = (int) $_POST['stock'];
    $id = $_POST['id'];
    $consulta = "update coche set modelo='$modelo', marca='$marca', precio='$precio',stock='$stock' where id ='$id'";

    //use exec() because no results are returned
    $conn->exec($consulta);
    echo "Registro actualizado correctamente";
    
} catch (PDOException $e) {
    echo $consulta . "<br>" . $e->getMessage();
}
$conn = null;