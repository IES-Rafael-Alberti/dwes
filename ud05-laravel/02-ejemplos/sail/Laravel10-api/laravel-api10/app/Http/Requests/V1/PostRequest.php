<?php

namespace App\Http\Requests\V1;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class PostRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return Auth::check();
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, \Illuminate\Contracts\Validation\ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            'title' => 'required|max:70',
            'image' => 'required|image|max:1024',
            'description' => 'required|max:2000',
        ];
    }

    public function messages()
    {
        return [
            'title.required' => 'El título es obligatorio.',
            'title.max' => 'El título no puede superar los 70 caracteres.',
            'image.required' => 'La imagen es obligatoria.',
            'image.image' => 'El archivo debe ser una imagen.',
            'description.required' => 'La descripción es obligatoria.',
        ];
    }
}
