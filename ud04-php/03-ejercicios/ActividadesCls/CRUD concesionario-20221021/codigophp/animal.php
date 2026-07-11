<?php
interface Animal {
  public function makeSound();
}

trait run {
  public function running($animal) {
    echo "I am a $animal";
    echo "<br>";
    echo "I am running";
    echo "<br>";
  }
}

abstract class FurAnimal {
    protected $furColor;
    public function __construct($furColor){
        $this->furColor = $furColor;
    }
    abstract public function getFurColor();

}

class Cat extends FurAnimal implements Animal  {
    use run;
  public function makeSound() {
    echo "Meow";
    echo "<br>";
  }
    public function getFurColor(){
        return $this->furColor;
    }

}

class Dog implements Animal {
    use run;
  public function makeSound() {
    echo "Bark";
    echo "<br>";
  }
}


trait message1 {
public function msg1() {
    echo "OOP is fun! ";
  }
}

class Welcome {
  use message1;
}

$obj = new Welcome();
$obj->msg1();
echo "<br>";



$animal = new Cat("orange");
$animal->makeSound();
echo $animal->getFurColor();
echo "<br>";
$animal->running("cat");

$animal = new Dog();
$animal->makeSound();
$animal->running("dog");

?>