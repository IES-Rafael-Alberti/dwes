<?php

namespace App\Http\Middleware;

namespace App\Http\Middleware;

use Closure;
use Laravel\Telescope\Telescope;

class TelescopeMiddleware
{
    public function handle($request, Closure $next)
    {
        // Registrar un evento personalizado en Telescope
        if (config('telescope.enabled')) {
            Telescope::tag(function () use ($request) {
                return [
                    'api_request',
                    'user:' . optional($request->user())->id,
                    'url:' . $request->fullUrl(),
                ];
            });
        }

        $response = $next($request);

        return $response;
    }
}
