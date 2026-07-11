<?php
require_once 'conectapdo.php';
try {
    $id = $_GET['id'];
   
    $consulta = "delete from coche where id = '$id'";

    //use exec() because no results are returned
    $conn->exec($consulta);
    echo "Registro borrado correctamente";
} catch (PDOException $e) {
    echo $consulta . "<br>" . $e->getMessage();
}
$conn = null;
