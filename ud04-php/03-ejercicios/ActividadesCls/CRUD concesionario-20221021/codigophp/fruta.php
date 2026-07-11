<?php
class Fruit
{
    protected $name;
    private $color;
    public function __construct($name, $color)
    {
        $this->name = $name;
        $this->color = $color;
    }
    public function intro()
    {
        echo "<p>The fruit is {$this->name} and the color is {$this->color}.</p>";
    }
    public function getColor()
    {
        return $this->color;
    }
}

// Orange is inherited from Fruit
class Orange extends Fruit
{
    public function message()
    {
        echo "<p>I am an orange.</p>";
        $color = $this->getColor();
        echo "<p>my color is {$color}.</p>";
    }
}

// Strawberry is inherited from Fruit
class Strawberry extends Fruit
{
    protected $weight;

    public function __construct($name, $color, $weight)
    {
        parent::__construct($name, $color);
        $this->weight = $weight;
    }

    public function message()
    {
        echo "<p>Am I a fruit or a berry? </p>";
    }

    public function intro()
    {
        parent::intro();

        echo "<p>It weighs {$this->weight} grams.</p>";
    }
}
$strawberry = new Strawberry("Strawberry", "red", 50);
$strawberry->message();
$strawberry->intro();
$orange = new Orange("Orange", "orange");
$orange->message();
$orange->intro();
