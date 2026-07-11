<?php
require_once 'conectapdo.php';
try {
    $modelo = $_POST['modelo'];
    $marca = $_POST['marca'];
    $precio = (int) $_POST['precio'];
    $stock = (int) $_POST['stock'];
    $consulta = "insert into coche(modelo, marca, precio, stock) values('$modelo','$marca','$precio','$stock')";

    //use exec() because no results are returned
    $conn->exec($consulta);
    echo "Registro insertado correctamente";
} catch (PDOException $e) {
    echo $consulta . "<br>" . $e->getMessage();
}
$conn = null;
