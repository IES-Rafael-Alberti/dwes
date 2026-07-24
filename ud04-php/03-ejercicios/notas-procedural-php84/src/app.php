<?php

declare(strict_types=1);

function database(string $path): PDO
{
    if ($path !== ':memory:') { $directory = dirname($path); if (!is_dir($directory) && !mkdir($directory, 0775, true) && !is_dir($directory)) throw new RuntimeException('Storage unavailable'); }
    $pdo = new PDO('sqlite:' . $path, options: [PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION]);
    $pdo->exec('CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, body TEXT NOT NULL)');
    return $pdo;
}

function escape(string $value): string
{
    return htmlspecialchars($value, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}

function validate_note(array $input): array
{
    $title = trim((string) ($input['title'] ?? ''));
    $body = trim((string) ($input['body'] ?? ''));
    $errors = [];
    if ($title === '') $errors['title'] = 'Title is required.';
    if (mb_strlen($title) > 80) $errors['title'] = 'Title must be at most 80 characters.';
    if ($body === '') $errors['body'] = 'Body is required.';
    if (mb_strlen($body) > 1000) $errors['body'] = 'Body must be at most 1000 characters.';
    return [['title' => $title, 'body' => $body], $errors];
}

// The teacher implements create_note. Complete the remaining persistence functions.
function create_note(PDO $pdo, array $note): int
{
    $statement = $pdo->prepare('INSERT INTO notes (title, body) VALUES (:title, :body)');
    $statement->execute($note);
    return (int) $pdo->lastInsertId();
}

function list_notes(PDO $pdo): array { return []; }
function find_note(PDO $pdo, int $id): ?array { return null; }
function update_note(PDO $pdo, int $id, array $note): bool { return false; }
function delete_note(PDO $pdo, int $id): bool { return false; }

function csrf_token(array &$session): string
{
    $session['csrf'] ??= bin2hex(random_bytes(32));
    return $session['csrf'];
}

function valid_csrf(array $session, mixed $candidate): bool
{
    return is_string($candidate) && isset($session['csrf']) && hash_equals($session['csrf'], $candidate);
}

/** @return array{status:int,headers:array<string,string>,body:string} */
function dispatch(PDO $pdo, string $method, string $path, array $input, array &$session): array
{
    // Checkpoints complete routing, validation, CSRF, PRG and generic errors.
    return ['status' => 501, 'headers' => [], 'body' => 'Not implemented'];
}
