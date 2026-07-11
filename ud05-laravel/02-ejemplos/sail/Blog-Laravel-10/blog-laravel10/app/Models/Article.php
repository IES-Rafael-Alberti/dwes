<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Article extends Model
{
    use HasFactory;
    //datos que permitimos rellenar
    protected $fillable = [
        "user_id", "category_id", "title", "content",
    ];

    //para la paginación, cuantos mostraremos por página
    //método paginate
    protected $perPage = 7;

    // método de Laravel que se ejecuta cuando se instancia un modelo
    protected static function boot()
    {
        parent::boot();
        //callback que recupera el id del autor y lo
        // relaciona con el user_id=> no es un campo rellenable
        // se rellena automáticamente con el id del usuario identificado
        //Sólo se ejecutará si no estamos lanzando una operación desde consola,
        //porque no tenemos el usuario identificado
        if (!app()->runningInConsole()) {
            self::creating(function (Article $article) {
                $article->user_id = auth()->id();
            });
        }
    }

    //relación con la tabla de usuarios, 1 a muchos para saber a qué usuario pertenece el artículo
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    //relación con la tabla de categorías, 1 a muchos para saber a qué categoría pertenece el artículo
    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class);
    }

    //poner la hora en formato legible para nosotros
    //carbon librería para trabajar con fechas
    public function getCreatedAtFormattedAttribute($value): string
    {
        return \Carbon\Carbon::parse($this->created_at)->format('d-m-Y H:i');
    }

    //accesor para obtener un extracto del contenido del artículo
    public function getExcerptAttribute():string
    {
        return \Illuminate\Support\Str::words($this->content, 90);
        //return substr($this->content, 0, 90);
    }

}
