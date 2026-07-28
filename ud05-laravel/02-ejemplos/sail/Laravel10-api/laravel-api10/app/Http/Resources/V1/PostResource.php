<?php

namespace App\Http\Resources\V1;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class PostResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        //return parent::toArray($request); devuelve todos los campos de la tabla,tal cual
        return [
            'id' => $this->id,
            'title' => $this->title,
            'description' => $this->description,
            'photo' => url($this->image),
            'author' => [
                'name' => $this->user->name,
                'email' => $this->user->email,
            ],
            'created_at' => $this->created_at
        ];
    }
}
