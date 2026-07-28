<?php

namespace Tests\Feature;

use Tests\TestCase;

class ApiHealthTest extends TestCase
{
    public function test_public_api_health_endpoint_returns_json(): void
    {
        $this->getJson('/api/ping')
            ->assertOk()
            ->assertJson(['pong' => true]);
    }
}
