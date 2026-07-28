<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\PostController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

/*
Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
*/
Route::group([
    'middleware' => ['api', \App\Http\Middleware\TelescopeMiddleware::class],
    'prefix' => 'v1/auth'
], function () {
    Route::post('login', [AuthController::class, 'login'])->name('login');
    Route::post('logout', [AuthController::class, 'logout'])->name('logout');
    Route::post('refresh', [AuthController::class, 'refresh'])->name('refresh');
    Route::post('me', [AuthController::class, 'me'])->name('me');
    Route::post('register',[AuthController::class, 'register'])->name('register');
});

// Acepta POST para actualizaciones con archivos
Route::post('v1/posts/{post}', [PostController::class, 'update'])
    ->middleware(['api', \App\Http\Middleware\TelescopeMiddleware::class]);

// Mantén el apiResource para el resto
Route::apiResource('v1/posts', PostController::class)
    ->except('update')
    ->middleware(['api', \App\Http\Middleware\TelescopeMiddleware::class]);
