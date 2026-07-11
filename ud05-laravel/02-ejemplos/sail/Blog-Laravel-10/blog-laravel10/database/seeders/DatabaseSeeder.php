<?php

namespace Database\Seeders;


use App\Models\Article;
use App\Models\User;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     * @return void
     */
    public function run(): void
    {
        //creamos un usuario con la User::factory
        //le pasamos datos, así que no usará lo del factory
        User::factory()->create([
            'name' => 'Test User',
            'email' => 'laravel10@blogweb.es',
        ]);

        User::factory()->create([
            'name' => 'Soporte',
            'email' => 'soporte@blogweb.es',
        ]);

        User::factory(10)->create();

        //Para llamar al seeder sólo tenemos que llamarlo
        $this->call(CategorySeeder::class);
        //Creamos 20 artículos
        Article::factory(20)->create();
    }
}
