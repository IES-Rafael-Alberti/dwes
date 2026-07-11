<?php
$servidor="db"; // a cambiar por la IP que tenga el host db en cada máquina, si no pilla con db (172.19.0.2)
$usuario="root";
$clave="pestillo";
$bd="concesionario";

try
{

    $options = [
  PDO::ATTR_EMULATE_PREPARES   => false, // turn off emulation mode for "real" prepared statements
  PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, //turn on errors in the form of exceptions
  PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC, //make the default fetch be an associative array
];
    $conn=new PDO("mysql:host=$servidor;dbname=$bd;charset=utf8",$usuario,$clave,$options);

    //$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    //echo "Conexión conseguida";
}
catch(PDOException $e)
{
    echo "Error de conexión" . $e->getMessage();
}
