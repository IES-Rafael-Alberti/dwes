<?php

namespace App\Http\Controllers;

use App\Models\Article;
use Illuminate\Contracts\Support\Renderable;
use Illuminate\Http\RedirectResponse;
use App\Http\Requests\ArticleRequest;


class ArticleController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(): Renderable
    {
        $articles = Article::with("category")->latest()->paginate();
        //dd($articles)
        return view("articles.index", compact("articles"));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create(): Renderable
    {
        $article = new Article();
        $title = __("Crear artículo");
        //ruta para el procesado del contenido
        //devuelto por el formuario
        $action = route("articles.store");
        return view("articles.form", compact("article", "title", "action"));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(ArticleRequest $request): RedirectResponse
    {
        $validated = $request->safe()->only(['title', 'content', 'category_id']);
        $validated['user_id'] = auth()->id();
        Article::create($validated);
        //Para trabajar con traducciones la parte de __("...")
        session()->flash("success", __("El artículo ha sido creado correctamente"));
        return redirect(route("articles.index"));
    }

    /**
     * Display the specified resource.
     */
    public function show(Article $article): Renderable
    {
        $article->load("user:id,name,avatar", "category:id,name");
        //dd($article);
        return view("articles.show", compact("article"));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Article $article): Renderable
    {
        //dd($article);
        $title = __("Actualizar artículo");
        //***al pasar el $article, al ir al formulario ya estará todo relleno***
        $action = route("articles.update", ["article" => $article]);
        return view("articles.form", compact("article", "title", "action"));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(ArticleRequest $request, Article $article): RedirectResponse
    {
        $validated = $request->safe()->only(['title', 'content', 'category_id']);
        $article->update($validated);
        //es una línea, pero para que salga bien...
        session()->flash("success", __("El artículo ha sido " . "actualizado correctamente"));
        return redirect(route("articles.index"));
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Article $article)
    {
        $article->delete();
        session()->flash("success", __("El artículo ha sido eliminado correctamente"));
        return redirect(route("articles.index"));
    }
}
