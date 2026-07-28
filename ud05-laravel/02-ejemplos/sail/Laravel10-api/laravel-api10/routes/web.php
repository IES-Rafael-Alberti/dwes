<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

/* Route::get('/', function () {
    return view('welcome');
}); */



Route::get('/', function (Request $request) {
    if ($request->expectsJson()) {
        return response()->json([
            'message' => 'Bienvenido a la API de Laravel. Visita la documentación en /api/documentation'
        ]);
    }
    return redirect('/api/documentation'); // Redirige a Swagger UI
});