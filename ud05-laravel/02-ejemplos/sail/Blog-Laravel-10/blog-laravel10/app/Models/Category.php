<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    use HasFactory;

    // por defecto Laravel crea timpestamps y esta variable es true
    // con false le indicamos al ORM que no existen
    public $timestamps = false;

    // evitar ataques de asignación masiva
    protected $fillable = ['name'];

    //Definimos la relación entre Category y Articles
    //Una categoría puede estar en muchos articulos
    //En principio un artículo sólo pertenece a una categoría,
    //así que es una relación de uno a muchos.
    //Accediendo a la función articles, desde un objeto de tipo Category,
    //podremos saber todos los artículos que tienen esa categoría.
    public function articles()
    {
        return $this->hasMany(Article::class);
    }
}
