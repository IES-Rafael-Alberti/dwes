<?php

namespace Database\Seeders;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Role;
use Spatie\Permission\Models\Permission;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        //\App\Models\User::factory(10)->create();


       $testUser = \App\Models\User::factory()->create([
        'name' => 'Test User',
        'email' => 'test@example.com',
    ]);

        Role::create(['name' => 'admin']);
        Role::create(['name' => 'editor']);
        Role::create(['name' => 'user']);
        Permission::create(['name' => 'create post']);
        Permission::create(['name' => 'edit post']);
        Permission::create(['name' => 'delete post']);
        $admin = Role::findByName('admin');
        $admin->givePermissionTo(['create post', 'edit post', 'delete post']);
        $editor = Role::findByName('editor');
        $editor->givePermissionTo(['create post', 'edit post']);
        $user = Role::findByName('user');
        $user->givePermissionTo('create post','delete post');

        $testUser->assignRole('admin'); // ← ESTO ES LO QUE FALTABA

    // 5. Crear usuarios aleatorios con roles
    \App\Models\User::factory(3)->create()->each(function ($user) {
        $user->assignRole('admin');
    });

    \App\Models\User::factory(4)->create()->each(function ($user) {
        $user->assignRole('editor');
    });

    \App\Models\User::factory(3)->create()->each(function ($user) {
        $user->assignRole('user');
    });
    }
}
