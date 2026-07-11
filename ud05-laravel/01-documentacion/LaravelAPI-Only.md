# Api-only servver with Laravel 11

Laravel 11 offers robust features for building APIs. Here are some key aspects to consider:


## With Composer (Requirements: PHP and composer installed)
### **1. Installation and Setup**
To start, you can install a fresh Laravel 11 application using Composer:
```bash
composer create-project laravel/laravel api-only
```
Configure your database in the `.env` file.

### **2. Creating Models, Controllers, and Resources**
Generate a model, migration, and controller for your API:
```bash
php artisan make:model Post -m
php artisan make:controller Api/PostController --model=Post
php artisan make:resource PostResource
```

### **3. Defining Routes**
In `routes/api.php`, define your API routes:
```php
use App\Http\Controllers\Api\PostController;

Route::apiResource('posts', PostController::class);
```

### **4. Building the Controller**
In your `PostController`, handle CRUD operations:
```php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Post;
use Illuminate\Http\Request;
use App\Http\Resources\PostResource;

class PostController extends Controller
{
    public function index()
    {
        return PostResource::collection(Post::all());
    }

    public function store(Request $request)
    {
        $post = Post::create($request->all());
        return new PostResource($post);
    }

    public function show(Post $post)
    {
        return new PostResource($post);
    }

    public function update(Request $request, Post $post)
    {
        $post->update($request->all());
        return new PostResource($post);
    }

    public function destroy(Post $post)
    {
        $post->delete();
        return response(null, 204);
    }
}
```

### **5. Using API Resources**
API Resources help transform your models into JSON:
```php
namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class PostResource extends JsonResource
{
    public function toArray($request)
    {
        return [
            'id' => $this->id,
            'title' => $this->title,
            'content' => $this->content,
            'created_at' => $this->created_at,
        ];
    }
}
```

### **6. Testing Your API**
You can test your API endpoints using tools like Postman or Insomnia.

For more detailed guidance, you can refer to the [Laravel 11 documentation](https://laravel.com/docs/11.x/eloquent-resources)¹ or check out tutorials like the one on [Techiediaries](https://www.techiediaries.com/laravel-11-crud-api-tutorial/)².

Is there a specific part of the API development process you'd like more details on?

Origen: Conversación con Copilot 18/9/2024
(1) Eloquent: API Resources - Laravel 11.x. https://laravel.com/docs/11.x/eloquent-resources.
(2) Building a RESTful API CRUD Application with Laravel 11. https://www.techiediaries.com/laravel-11-crud-api-tutorial/.
(3) Laravel 11 api crud, curso express e intensivo - Udemy. https://www.udemy.com/course/api-rest-laravel-11/.
(4) API on laravel 11 - Laracasts. https://laracasts.com/discuss/channels/laravel/api-on-laravel-11.
(5) undefined. http://localhost.com/api/%29.
(6) github.com. https://github.com/maldororIsDead/blog-final/tree/71caf7fd0d84914ade1ed0bc19b030d1f3e030aa/app%2FHttp%2FResources%2FPostResource.php.
(7) github.com. https://github.com/Yiddishe-Kop/pninim/tree/ad6ed73e1f0a85765edd56a40b8e350b8a949b60/app%2FHttp%2FResources%2FPostResource.php.




## With Sail (Requirements: Docker/podman)
Laravel 11 offers robust features for building APIs. Here are some key aspects to consider:

### **1. Installation and Setup**
To start, you can install a fresh Laravel 11 application using Composer:
```bash
composer create-project laravel/laravel api-only
```
Configure your database in the `.env` file.

### **2. Creating Models, Controllers, and Resources**
Generate a model, migration, and controller for your API:
```bash
php artisan make:model Post -m
php artisan make:controller Api/PostController --model=Post
php artisan make:resource PostResource
```

### **3. Defining Routes**
In `routes/api.php`, define your API routes:
```php
use App\Http\Controllers\Api\PostController;

Route::apiResource('posts', PostController::class);
```

### **4. Building the Controller**
In your `PostController`, handle CRUD operations:
```php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Post;
use Illuminate\Http\Request;
use App\Http\Resources\PostResource;

class PostController extends Controller
{
    public function index()
    {
        return PostResource::collection(Post::all());
    }

    public function store(Request $request)
    {
        $post = Post::create($request->all());
        return new PostResource($post);
    }

    public function show(Post $post)
    {
        return new PostResource($post);
    }

    public function update(Request $request, Post $post)
    {
        $post->update($request->all());
        return new PostResource($post);
    }

    public function destroy(Post $post)
    {
        $post->delete();
        return response(null, 204);
    }
}
```

### **5. Using API Resources**
API Resources help transform your models into JSON:
```php
namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class PostResource extends JsonResource
{
    public function toArray($request)
    {
        return [
            'id' => $this->id,
            'title' => $this->title,
            'content' => $this->content,
            'created_at' => $this->created_at,
        ];
    }
}
```

### **6. Testing Your API**
You can test your API endpoints using tools like Postman or Insomnia.

For more detailed guidance, you can refer to the [Laravel 11 documentation](https://laravel.com/docs/11.x/eloquent-resources)¹ or check out tutorials like the one on [Techiediaries](https://www.techiediaries.com/laravel-11-crud-api-tutorial/)².

Is there a specific part of the API development process you'd like more details on?

Origen: Conversación con Copilot 18/9/2024
(1) Eloquent: API Resources - Laravel 11.x. https://laravel.com/docs/11.x/eloquent-resources.
(2) Building a RESTful API CRUD Application with Laravel 11. https://www.techiediaries.com/laravel-11-crud-api-tutorial/.
(3) Laravel 11 api crud, curso express e intensivo - Udemy. https://www.udemy.com/course/api-rest-laravel-11/.
(4) API on laravel 11 - Laracasts. https://laracasts.com/discuss/channels/laravel/api-on-laravel-11.
(5) undefined. http://localhost.com/api/%29.
(6) github.com. https://github.com/maldororIsDead/blog-final/tree/71caf7fd0d84914ade1ed0bc19b030d1f3e030aa/app%2FHttp%2FResources%2FPostResource.php.
(7) github.com. https://github.com/Yiddishe-Kop/pninim/tree/ad6ed73e1f0a85765edd56a40b8e350b8a949b60/app%2FHttp%2FResources%2FPostResource.php.


### Podman as an alternative to Docker
Yes, you can use Podman as an alternative to Docker with Laravel Sail. Podman is a container engine that is compatible with Docker, and it can be a good choice if you prefer not to use Docker.

### **Steps to Use Podman with Laravel Sail**

1. **Install Podman**:
   Make sure you have Podman installed on your system. You can follow the installation instructions from the [Podman documentation](https://podman.io/getting-started/installation).

2. **Configure Sail to Use Podman**:
   By default, Sail uses Docker, but you can configure it to use Podman by setting the `DOCKER_HOST` environment variable. Add the following line to your `.env` file:
   ```plaintext
   DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock
   ```

3. **Start Podman Service**:
   Ensure the Podman service is running. You can start it with:
   ```bash
   systemctl --user start podman.socket
   ```

4. **Run Sail Commands**:
   Now, you can run Sail commands as usual. For example, to start the Sail environment, use:
   ```bash
   ./vendor/bin/sail up
   ```

### **Example Commands**:
- **Creating a New Laravel Project**:
  ```bash
  curl -s "https://laravel.build/api-only" | bash
  cd api-only
  ./vendor/bin/sail up
  ```

- **Generating Models, Controllers, and Resources**:
  ```bash
  ./vendor/bin/sail artisan make:model Post -m
  ./vendor/bin/sail artisan make:controller Api/PostController --model=Post
  ./vendor/bin/sail artisan make:resource PostResource
  ```

Using Podman with Laravel Sail should provide a similar experience to using Docker, with the added benefits that Podman offers.

Is there anything else you'd like to know about using Laravel Sail with Podman?


## From 10 to 11

https://laravel.com/docs/11.x/upgrade

It sounds like you're encountering some common issues when upgrading from Laravel 10 to Laravel 11. Laravel 11 has made some changes, including the removal of certain files and libraries that were included by default in Laravel 10. Here's how you can address these issues:

### **1. Missing `routes/api.php` File**
In Laravel 11, the `routes/api.php` file is not included by default. You can create it by running the following Artisan command:
```bash
php artisan install:api
```
This command will:
- Create the `routes/api.php` file.
- Install Laravel Sanctum for API token authentication.
- Generate the necessary migrations for Sanctum.
- Add a configuration file (`config/sanctum.php`) for Sanctum settings¹².

### **2. Missing Libraries**
If you find that certain libraries are missing, you can manually install them using Composer. Here are some common libraries you might need:

- **Laravel Sanctum** (for API authentication):
  ```bash
  composer require laravel/sanctum
  ```

- **Laravel Passport** (for OAuth2 authentication):
  ```bash
  composer require laravel/passport
  ```

- **Laravel UI** (for frontend scaffolding):
  ```bash
  composer require laravel/ui
  ```

### **3. Updating Configuration Files**
Ensure that your configuration files are up-to-date. You might need to copy over some configuration settings from your Laravel 10 project to the new Laravel 11 project. Pay special attention to the `config/app.php` and `config/auth.php` files.

### **4. Migrating Database**
Run your database migrations to ensure all tables and columns are up-to-date:
```bash
php artisan migrate
```

### **5. Checking for Deprecated Features**
Review the Laravel 11 upgrade guide to check for any deprecated features or breaking changes that might affect your project. The [Laravel documentation](https://laravel.com/docs/11.x/upgrade) provides detailed information on how to handle these changes.

### **6. Testing Your Application**
Thoroughly test your application to ensure that everything works as expected. Use tools like PHPUnit for automated testing:
```bash
./vendor/bin/phpunit
```

By following these steps, you should be able to resolve the issues you're facing and get your Laravel 11 project running smoothly. If you encounter any specific errors or need further assistance, feel free to ask!

¹: [How to Publish API Route File in Laravel 11 - DEV Community](https://dev.to/msnmongare/how-to-publish-api-route-file-in-laravel-11-40df)
²: [Routing - Laravel 11.x - The PHP Framework For Web Artisans](https://laravel.com/docs/11.x/routing)

Origen: Conversación con Copilot 18/9/2024
(1) How to Publish API Route File in Laravel 11 - DEV Community. https://dev.to/msnmongare/how-to-publish-api-route-file-in-laravel-11-40df.
(2) Routing - Laravel 11.x - The PHP Framework For Web Artisans. https://laravel.com/docs/11.x/routing.
(3) Laravel 11: Main New Features and Changes. https://laraveldaily.com/post/laravel-11-main-new-features-changes.
(4) Unable to Find api.php in Laravel 11 for React.js Fetch Data. https://devcodef1.com/news/1183970/laravel-11-api-php-missing.
(5) Installing an API - Laracasts. https://laracasts.com/series/whats-new-in-laravel-11/episodes/4.
(6) undefined. http://example.com/user.
