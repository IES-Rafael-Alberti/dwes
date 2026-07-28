<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class UpdateRecetaRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'titulo' => ['sometimes', 'required', 'string', 'max:200'],
            'descripcion' => ['sometimes', 'required', 'string'],
            'instrucciones' => ['sometimes', 'required', 'string'],
        ];
    }
}
