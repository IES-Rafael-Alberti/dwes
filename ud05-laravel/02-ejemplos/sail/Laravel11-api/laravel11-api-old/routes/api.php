<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\PostController;


Route::post('auth/v1/login', [AuthController::class, 'login']);


Route::prefix('auth/v1')->group(function () {
   // Route::post('/login', [AuthController::class, 'login'])->name('api.auth.login');
    Route::post('/register', [AuthController::class, 'register']);

    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/logout', [AuthController::class, 'logout']);
        Route::get('/me', [AuthController::class, 'me']);
    });
});


//en 2 sola línea
Route::apiResource('v1/posts', PostController::class)
    ->only(['index', 'show']); // Estas rutas son públicas

Route::middleware('auth:sanctum')->group(function () {
    Route::apiResource('v1/posts', PostController::class)
        ->except(['index', 'show']); // Estas rutas requieren autenticación
});

// Route::get('/posts', [PostController::class, 'listar']);
// Route::post('/posts', [PostController::class, 'guardar']);
// Route::get('/posts/{post}', [PostController::class, 'mostrar1']);
// Route::put('/posts/{post}', [PostController::class, 'actualizar']);
// Route::delete('/posts/{post}', [PostController::class, 'borrar']);

