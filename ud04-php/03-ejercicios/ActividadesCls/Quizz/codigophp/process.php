<?php
session_start();
/**
 * @param string $value
 * @param array $flags
 * @return string
 */
function sanitize($value, $flags = []) {
    $flags = array_merge([
        'trim' => true,
        'strip_tags' => true,
        'htmlentities' => true,
    ], $flags);

    if ($flags['trim']) {
        $value = trim($value);
    }

    if ($flags['strip_tags']) {
        $value = strip_tags($value);
    }

    if ($flags['htmlentities']) {
        $value = htmlentities($value);
    }

    return $value;
}



if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $errors = [];

    // Check if all questions are answered
    for ($i = 1; $i <= 2; $i++) {
        if (!isset($_POST["q$i"])) {
            $errors[] = "La pregunta $i no ha sido respondida.";
        }
    }

    if (!empty($errors)) {
        $_SESSION['errors'] = $errors;
        header('Location: quiz.php');
        exit;
    }

    // Process the answers and show the results
    // ...
}
