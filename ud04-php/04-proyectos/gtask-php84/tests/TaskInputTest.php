<?php
declare(strict_types=1);

namespace GTask\Tests;

use GTask\Http\TaskInput;
use PHPUnit\Framework\TestCase;

final class TaskInputTest extends TestCase
{
    public function testValidInputHasNoErrors(): void
    {
        self::assertSame([], (new TaskInput('Learn PDO', 'Use prepared statements'))->errors());
    }

    public function testBlankTitleIsRejected(): void
    {
        self::assertArrayHasKey('title', (new TaskInput('   ', ''))->errors());
    }
}
