<?php

namespace App\Exceptions;

use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;

use Illuminate\Database\Eloquent\ModelNotFoundException;
use Symfony\Component\HttpKernel\Exception\HttpException;
use Illuminate\Auth\AuthenticationException;
use Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Throwable;

class Handler extends ExceptionHandler
{
    /**
     * The list of the inputs that are never flashed to the session on validation exceptions.
     *
     * @var array<int, string>
     */
    protected $dontFlash = [
        'current_password',
        'password',
        'password_confirmation',
    ];

    /**
     * Register the exception handling callbacks for the application.
     */
    public function register(): void
    {
        $this->reportable(function (Throwable $e) {
            //
        });
    }

    public function render($request, Throwable $exception)
    {
        if ($exception instanceof ModelNotFoundException) {
            return response()->json(['error' => 'Recurso no encontrado'], 404);
        }
        if ($exception instanceof NotFoundHttpException) {
            return response()->json(['error' => 'Ruta no encontrada'], 404);
        }
        if ($exception instanceof HttpException) {
            return response()->json(['error' => $exception->getMessage()], $exception->getStatusCode());
        }
        if ($exception instanceof AuthenticationException || $exception instanceof UnauthorizedHttpException) {
            return response()->json(['error' => 'Token inválido o caducado'], 401);
        }

        if ($exception instanceof \Illuminate\Validation\ValidationException) {
            return response()->json([
                'message' => 'Validation failed',
                'errors' => $exception->errors()
            ], 422);
        }
        
        return parent::render($request, $exception);
    }
}
