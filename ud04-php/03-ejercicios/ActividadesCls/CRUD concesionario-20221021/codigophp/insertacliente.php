<?php
require_once 'conectapdo.php';
try {
    $nombre = $_POST['nombre'];
    $ciudad = $_POST['ciudad'];
    $gastado = (int) $_POST['gastado'];
    $existe = "select nombre from cliente where nombre = '$nombre'";
    $consulta =$conn->query($existe);
    $cliente = $consulta->fetchObject();
    
    if($cliente->nombre==$nombre)
    {
        echo '<p>Usuario ya existe.</p>';
        
        header("Location: insertacliente.html");
    }
    else
    {
        $consulta = "insert into cliente(nombre, ciudad, gastado) values('$nombre','$ciudad','$gastado')";

        //use exec() because no results are returned
        $conn->exec($consulta);
        echo "Registro insertado correctamente";
        header("Location: insertacliente.html");
    }
} catch (PDOException $e) {
    echo $consulta . "<br>" . $e->getMessage();
}
$conn = null;
