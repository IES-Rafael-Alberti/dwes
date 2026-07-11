<!-- <?php
    session_start();
    if(!isset($_SESSION["username"])) {
       header("Location: login.php");
    }
?> -->
<?php
require_once 'conectapdo.php';
//require_once "modelo.php";
try {
    $id = $_GET['id'];
    
    $consulta = $conn->prepare("select * from coche where id = :id");
    $consulta->execute([$id]);
    $coche = $consulta->fetchObject();
    ?>
    <form action="actualizaCocheBD.php" method="post">
        <label for="marca">Marca</label>
        <input type="text" name="marca" id="marca" value='<?=$coche->marca?>'>
        <label for="modelo">Modelo</label>
        <input type="text" name="modelo" id="modelo" value='<?=$coche->modelo?>'>
        <label for="precio">Precio</label>
        <input type="text" name="precio" id="precio" value='<?=$coche->precio?>'>
        <label for="stock">Stock</label>
        <input type="text" name="stock" id="stock" value='<?=$coche->stock?>'>
        <input type="hidden" name="id" value='<?= $id ?>'>
        <button type="submit">Modificar</button>
    </form>
<?php
    echo "Registro modificado correctamente";
} catch (PDOException $e) {
    echo $consulta . "<br>" . $e->getMessage();
}
$conn = null;

?>