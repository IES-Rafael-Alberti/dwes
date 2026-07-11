<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>

<body>



    <?php

    echo "Hello, World!";
    $age = 66;

    echo "<br>";

    // Assume $age is a positive integer
    if ($age < 13) {
        echo "You are a child.";
    } elseif ($age < 18) {
        echo "You are a teenager.";
    } elseif ($age < 65) {
        echo "You are an adult.";
    } else {
        echo "You are a senior.";
    }

    echo "<br>";
    $score = 95;
    if ($score > 90) {
        echo "You scored an A.";
    } else {
        echo "You scored below an A.";
    }
    ?>

    <?php if ($condition): ?>
        <!-- HTML to display if condition is true -->
    <?php elseif ($anotherCondition): ?>
        <!-- HTML to display if another condition is true -->
    <?php else: ?>
        <!-- HTML to display if all conditions are false -->
    <?php endif; ?>


    <?php
    $color = "red";
    $message = match ($color) {
        "red" => "The color is red",
        "green" => "The color is green",
        "blue" => "The color is blue",
        default => "Unknown color",
    };
    echo $message;
    ?>

    <br>

    <?php
    for ($i = 0; $i < 10; $i++) {
        echo $i . " ";
    }

    $colors = ["red", "green", "blue"];
    foreach ($colors as $color) {
        echo $color . " ";
    }

    echo "<br>";
    $colors = ["red" => "#FF0000", "green" => "#00FF00", "blue" => "#0000FF"];
    foreach ($colors as $color) {
        echo $color . "\n";
    }

    ?>
    <br>
    <?php

    $numbers = [1, 2, 3, 4, 5];
    $firstNumber = array_shift($numbers);
    echo "First number: $firstNumber";

    echo "<br>";

    $numbers = [1, 2, 3, 4, 5];
    array_unshift($numbers, 0);

    print_r($numbers);
    ?>

    <br>
    <?php
    $colors = ["red", "green", "blue", "yellow"];
    print_r($colors);
    echo "<br>";
    array_splice($colors, 1, 1, ["teal", "magenta"]);
    print_r($colors);
    ?>
    <br>

    <?=
    $numbers = [1, 2, 3, 4, 5];
    $slicedNumbers = array_slice($numbers, 2, 2);
    echo "Sliced array: ";
    print_r($slicedNumbers);
    ?>

    <?php
    echo "<br>";
    function addOne($number)
    {
        $number++;
        echo $number;
        echo "<br>";
    }
    $originalNumber = 5;
    addOne($originalNumber); // Outputs: 6
    echo $originalNumber; // Still outputs: 5
    ?>
    <?php
    echo "<br>";

    function addOneByRef(&$number)
    {
        $number++;
        echo $number; // Outputs: 6
        echo "<br>";
    }
    $originalNumber = 5;
    addOneByRef($originalNumber);
    echo $originalNumber; // Outputs: 6
    ?>
    <br>
    
    <?php
    $greet = function ($name) {
        echo "Hello, $name!";
    };
    $greet("World"); // Outputs: Hello, World!
    ?>

</body>

</html>