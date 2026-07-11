<?php
    session_start();
    if(!isset($_SESSION["username"])) {
       header("Location: login.php");
    }
?>
<?php
    // require_once "conectapdo.php";
    require_once "modelo.php";
    $username = $_POST["username"];
    $password = $_POST["password"];

    $resultado=false;
    $my_model = Model::getInstance();
    if(!$my_model->check_user($username, $password))
        $resultado=$my_model->crea_usuario($username, $password);
    
    if($resultado) {
        session_start();
        $_SESSION["username"] = $_POST["username"];
        session_write_close();
        header("Location: login.php");
    } else {
        header("Location: login.php?error=No se ha podido crear el usuario");
    }
    