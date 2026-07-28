<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use \Tymon\JWTAuth\Facades\JWTAuth;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

/**@OA\PathItem(
 * path="/api/v1/auth",
 * @OA\Response(response="200", description="Success"),
 * @OA\Response(response="401", description="Unauthorized"),
 * @OA\Response(response="403", description="Forbidden"),
 * )
 * * @OA\SecurityScheme(
 *    type="http",
 *   scheme="bearer",
 *  bearerFormat="JWT",
 * )
 */
class AuthController extends Controller
{
    /**
     * Create a new AuthController instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth:api', ['except' => ['login','register']]);
    }
    /**
     * Get a JWT via given credentials.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *     path="/api/v1/auth/login",
     *     summary="Login user and get token",
     *    tags={"Auth"},
     *     @OA\RequestBody(
     *         required=true,
     *         @OA\JsonContent(
     *             required={"email","password"},
     *             @OA\Property(property="email", type="string", format="email"),
     *             @OA\Property(property="password", type="string", format="password")
     *         ),
     *     ),
     *     @OA\Response(response=200, description="Successful login"),
     *     @OA\Response(response=401, description="Unauthorized")
     * )
     */
    public function login()
    {
        $credentials = request(['email', 'password']);
        if (! $token = auth()->attempt($credentials)) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }
        return $this->respondWithToken($token);
    }


    /**
     * Register a new user.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *     path="/api/v1/auth/register",
     *     summary="Register a new user",
     *     tags={"Auth"},
     *     @OA\RequestBody(
     *         required=true,
     *         @OA\JsonContent(
     *             required={"name", "email", "password", "password_confirmation"},
     *             @OA\Property(property="name", type="string", example="John Doe"),
     *             @OA\Property(property="email", type="string", format="email", example="john@example.com"),
     *             @OA\Property(property="password", type="string", format="password", example="securepassword123"),
     *             @OA\Property(property="password_confirmation", type="string", format="password", example="securepassword123")
     *         ),
     *     ),
     *     @OA\Response(response=201, description="User registered successfully"),
     *     @OA\Response(response=400, description="Validation error"),
     *     @OA\Response(response=500, description="Server error")
     * )
     */
    public function register(Request $request)
    {
        // Validate the request
        $request->validate([
            'name' => 'required|string|max:100',
            'email' => 'required|email|max:78|unique:users,email',
            'password' => 'required|string|confirmed|min:8',
        ], [], [], $request->expectsJson());

        // Create a new user
        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        // Asginar el rol de usuario
        $user->assignRole('user');

        // Generar el token JWT
        $token = auth()->login($user);

        return $this->respondWithToken($token);
    }
    /**
     * Get the authenticated User.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *     path="/api/v1/auth/me",
     *     summary="Get authenticated user",
     *     tags={"Auth"},
     *     security={{"bearerAuth": {}}},
     *     @OA\RequestBody(
     *         required=true,
     *         @OA\JsonContent(
     *            type="object",
     *            @OA\Property(property="token", type="string",description="JWT Token") 
     *        ) 
     *    ),
     * 
     *     @OA\Response(response=200, description="Authenticated user data"),
     *     @OA\Response(response=401, description="Unauthorized")
     * )
     */
    public function me()
    {
        return response()->json(auth()->user());
    }
    /**
     * Log the user out (Invalidate the token).
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *     path="/api/v1/auth/logout",
     *     summary="Logout user",
     *     tags={"Auth"},
     *     security={{"bearerAuth": {}}},
     *     @OA\Response(response=200, description="Successfully logged out"),
     *     @OA\Response(response=401, description="Unauthorized")
     * )
     */
    public function logout()
    {
        auth()->logout();
        return response()->json(['message' => 'Successfully logged out']);
    }
    /**
     * Refresh a token.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    /**
     * @OA\Post(
     *     path="/api/v1/auth/refresh",
     *     summary="Refresh token",
     *     tags={"Auth"},
     *     @OA\Response(response=200, description="Token refreshed"),
     *     @OA\Response(response=401, description="Unauthorized")
     * )
     */
    public function refresh()
    {
        try {
            $newToken = auth()->setToken(auth()->refresh())->refresh();
            return $this->respondWithToken($newToken);
        } catch (\Tymon\JWTAuth\Exceptions\TokenExpiredException $e) {
            return response()->json(['error' => 'Token expirado, inicia sesión nuevamente'], 401);
        } catch (\Tymon\JWTAuth\Exceptions\JWTException $e) {
            return response()->json(['error' => 'No se pudo refrescar el token'], 500);
        }
    }
    /**
     * Get the token array structure.
     *
     * @param string $token
     *
     * @return \Illuminate\Http\JsonResponse
     */

    protected function respondWithToken($token)
    {
        $user = auth()->user(); // Obtener usuario autenticado

        return response()->json([
            'access_token' => $token,
            'token_type' => 'bearer',
            'expires_in' => auth()->factory()->getTTL() * 60,
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'roles' => $user->getRoleNames(), // Si usas Spatie Roles
                'permissions' => $user->getAllPermissions()->pluck('name'), // Si usas Spatie Permissions
            ]
        ]);
    }
}
