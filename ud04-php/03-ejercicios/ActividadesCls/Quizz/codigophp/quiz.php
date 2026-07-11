<?php
session_start();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PHP Quiz</title>
    <link rel="stylesheet" href="quiz.css">
</head>
<body>
<?php
    if (isset($_SESSION['errors'])) {
        foreach ($_SESSION['errors'] as $error) {
            echo "<p style='color:red;'>{$error}</p>";
        }
        unset($_SESSION['errors']);
   }
?>
    <form method="post" action="process.php">
        <h1>PHP Quiz</h1>

        <!-- Question 1 -->
        <div class="question">
            <p>1. What does PHP stand for?</p>
            <label><input type="radio" name="q1" value="a"> a) Personal Home Page</label>
            <label><input type="radio" name="q1" value="b"> b) PHP: Hypertext Preprocessor</label>
            <label><input type="radio" name="q1" value="c"> c) PHP Hyper Markup Language</label>
            
<?php
            if (isset($_SESSION['errors'])) {
                $errors = explode(',', $_SESSION['errors']);
                if (in_array(1, $errors)) {
                    echo "<p style='color:red;'>La pregunta 1 no ha sido respondida.</p>";
                }
            }
?>
        </div>

        <!-- Question 2 -->
        <div class="question">
            <p>2. What is the result of 2 + 2 in PHP?</p>
            <label><input type="radio" name="q2" value="a"> a) 3</label>
            <label><input type="radio" name="q2" value="b"> b) 4</label>
            <label><input type="radio" name="q2" value="c"> c) 5</label>
        </div>

        <!-- Add more questions as needed -->

        <input type="submit" value="Submit">
    </form>
</body>
</html>
