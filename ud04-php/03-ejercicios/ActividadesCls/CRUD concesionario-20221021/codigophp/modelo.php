<?php
class Model {
    private $conn;
    private static $instance;

    private function __construct() {
        $this->conn = new PDO("mysql:host=db;dbname=concesionario", "root", "pestillo");
        $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }

    public static function getInstance() {
        if(!isset(self::$instance)) {
            self::$instance = new Model();
        }
        return self::$instance;
    }
    
    public function crea_usuario($username, $password, $image = "anon.png") {
        $statement = $this->conn->prepare("insert into usuario (nombreusu, clave, imagen) values(:username, :password, :image)");
        $statement->execute(array(":username" => $username, ":password" => crypt($password, "juas"), ":image" => $image));
        return $statement->rowCount();
    }

    public function crea_cliente($username, $password, $image = "anon.png") {
        $statement = $this->conn->prepare("insert into usuario (nombreusu, clave, image) values(:username, :password, :image)");
        $statement->execute(array(":username" => $username, ":password" => crypt($password, "juas"), ":image" => $image));
        return $statement->rowCount();
    }

    public function check_user($username, $password) {
        $statement = $this->conn->prepare("select count(*) from usuario where nombreusu = :username and clave = :password");
        $statement->execute(array(":username" => $username, ":password" => crypt($password, "juas")));
        return $statement->fetch()[0] == 1;
    }


    public function table_list() {
        return $this->conn->query("show tables")->fetchAll();
    }

    public function crea_mascota($nombre, $especie, $propietario) {
        $statement = $this->conn->prepare("insert into mascotas (nombre, especie, propietario) values(:nombre, :especie, :propietario)");
        $statement->execute(array(":nombre" => $nombre, ":especie" => $especie, ":propietario" => $propietario));
        return $statement->rowCount();
    }  

    public function marca_modelo() { // new Mascota($follow=true)
        return $this->conn->query("select * from coche")->fetchAll(PDO::FETCH_CLASS, "Coche", array("follow" => true));
    }

    public function encargos() { // new Mascota($follow=true)
        return $this->conn->query("select * from encargo")->fetchAll(PDO::FETCH_CLASS, "Encargo", array("follow" => true));
    }

    public function encargos_cliente($cliente_id, $follow = false) {
        $statement = $this->conn->prepare("select nombre, ciudad from cliente where id = :cliente_id");
        $statement->execute(array(":cliente_id" => $cliente_id));
        return $statement->fetchAll(PDO::FETCH_CLASS, "Cliente", array("follow" =>$follow));
    }

    public function encargos_coche($coche_id, $follow = false) {
        $statement = $this->conn->prepare("select marca, modelo from coche where id = :coche_id");
        $statement->execute(array(":coche_id" => $coche_id));
        return $statement->fetchAll(PDO::FETCH_CLASS, "Coche", array("follow" =>$follow));
    }

    public function mascotas_propietario($id_propietario, $follow = false) {
        $statement = $this->conn->prepare("select * from mascotas where propietario = :id_propietario");
        $statement->execute(array(":id_propietario" => $id_propietario));
        return $statement->fetchAll(PDO::FETCH_CLASS, "Mascota", array("follow" =>$follow));
    }

    public function borrar_mascota($id_mascota) {
        $statement = $this->conn->prepare("delete from mascotas where id = :id_mascota");
        $statement->execute(array(":id_mascota" => $id_mascota));
        return $statement->rowCount();
    }

    public function propietarios() {
        return $this->conn->query("select * from propietarios")->fetchAll(PDO::FETCH_CLASS, "Propietario", array("follow" => true));
    }

    public function propietario($id, $follow) {
        $statement = $this->conn->prepare("select * from propietarios where id = :id_propietario");
        $statement->execute(array(":id_propietario" => $id));
        return $statement->fetchObject("Propietario", array("follow" =>$follow));
    }

    function __destruct() {
        $this->conn = null;
    }

}

class Coche {
    private $id;
    private $modelo;
    private $marca;
    private $precio;
    private $stock;

    public function __construct($follow = true) {
        
    }

    public function getId() {
        return $this->id;
    }

    public function getModelo() {
        return $this->modelo;
    }

    public function getMarca() {
        return $this->marca;
    }

    public function getPrecio() {
        return $this->precio;
    }

    public function getStock() {
        return $this->stock;
    }


    public function __toString() {
        return $this->getMarca() . " (" . $this->getModelo() . ") " . $this->getPrecio() . "(" . $this->getStock() . ")";
    }

}

class Cliente{
    private $id;
    private $nombre;
    private $ciudad;
    private $gastado;

    public function __construct($follow = true)
    {
        
    }

    public function getId(){
        return $this->id;
    }
    
    public function getNombre(){
        return $this->nombre;
    }
    
    public function getCiudad(){
        return $this->ciudad;
    }
    
    public function getGastado(){
        return $this->gastado;
    }

    public function __toString() {
        return $this->getNombre() . " (" . $this->getCiudad() . ") " . $this->getGastado() ;
    }
}

class Encargo
{
    private $id;
    private $coche_id;
    private $cliente_id;
    private $cantidad;
    private $fecha;
    private $marca_modelo;
    private $nombre_ciudad;

    public function __construct($follow = true)
    {        
        $this->marca_modelo = Model::getInstance()->encargos_coche($this->coche_id);
        $this->nombre_ciudad = Model::getInstance()->encargos_cliente($this->cliente_id);
    }

    public function getId() 
    {     return $this->id;
    }
    public function getCocheId() 
    {     return $this->coche_id;
    }
    public function getClienteId() 
    {     return $this->cliente_id;
    }
    public function getCantidad()
    {     return $this->cantidad;
    }
    public function getFecha()
    {     return $this->fecha;
    }


}

class Propietario {
    private $id;
    private $nombre;
    private $mascotas; // Lista de mascotas    
    
    public function __construct($follow = true) 
    {   if($follow)
        {    $this->mascotas = Model::getInstance()->mascotas_propietario($this->id);
        }
    }

    public function getId() {
        return $this->id;
    }

    public function getNombre() {
        return $this->nombre;
    }

    public function __toString() {
        $result = $this->getNombre();
        /*
        if (isset($this->mascotas)) {
            foreach($this->mascotas as $mascota) {
                $result .= " " . $mascota;
            }
        }*/
        return $result;
    }


}