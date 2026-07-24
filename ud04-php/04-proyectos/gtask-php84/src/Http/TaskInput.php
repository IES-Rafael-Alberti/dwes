<?php
declare(strict_types=1);

namespace GTask\Http;

final readonly class TaskInput
{
    public function __construct(public string $title, public string $description) {}

    /** @return array<string, string> */
    public function errors(): array
    {
        $errors = [];
        $length = mb_strlen(trim($this->title));
        if ($length < 1 || $length > 120) {
            $errors['title'] = 'Title must contain between 1 and 120 characters.';
        }
        if (mb_strlen($this->description) > 1000) {
            $errors['description'] = 'Description cannot exceed 1000 characters.';
        }
        return $errors;
    }
}
