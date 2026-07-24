<?php

declare(strict_types=1);

require dirname(__DIR__) . '/vendor/autoload.php';
session_start();
$pdo = database(dirname(__DIR__) . '/var/notes.sqlite');
$response = dispatch($pdo, $_SERVER['REQUEST_METHOD'], parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH) ?: '/', $_POST, $_SESSION);
http_response_code($response['status']);
foreach ($response['headers'] as $name => $value) header($name . ': ' . $value);
echo $response['body'];
