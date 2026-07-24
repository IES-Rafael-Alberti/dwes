<?php

declare(strict_types=1);

use PHPUnit\Framework\TestCase;

final class StarterTest extends TestCase
{
    public function testValidationAndEscapingAreAvailable(): void
    {
        [$note, $errors] = validate_note(['title' => ' <b>Note</b> ', 'body' => 'Text']);
        self::assertSame('<b>Note</b>', $note['title']);
        self::assertSame([], $errors);
        self::assertSame('&lt;b&gt;Note&lt;/b&gt;', escape($note['title']));
    }

    public function testTeacherIncrementCreatesARecordWithPreparedStatement(): void
    {
        $pdo = database(':memory:');
        $id = create_note($pdo, ['title' => "x'); DROP TABLE notes;--", 'body' => 'data']);
        self::assertSame(1, $id);
        self::assertSame(1, (int) $pdo->query('SELECT COUNT(*) FROM notes')->fetchColumn());
    }
}
