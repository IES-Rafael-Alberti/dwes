<?php
    require_once "conectapdo.php";
    $username = $_POST["username"];
    $password = $_POST["password"];
    $statement = $conn->prepare("select nombreusu, clave from usuario where nombreusu = :username");
    $statement->execute(array(":username" => $username));
    $resultado = $statement->fetchObject();
    $veri=password_verify($password, $resultado->clave);

    if($veri) {
        session_start();
        $_SESSION["username"] = $_POST["username"];
        session_write_close();
        header("Location: lista.php");
    } else {
        header("Location: login.php?error=Usuario y/o clave incorrectos");
    }