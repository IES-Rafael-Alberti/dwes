<?php
declare(strict_types=1);

namespace GTask\Tests;

use PHPUnit\Framework\Attributes\Group;
use PHPUnit\Framework\TestCase;

#[Group('checkpoint')]
final class CheckpointContractTest extends TestCase
{
    #[Group('checkpoint-1')]
    public function testInvalidTaskNeverReachesTheUseCase(): void
    {
        $input = new \GTask\Http\TaskInput(' ', '');
        self::assertNotSame([], $input->errors());
        self::assertTrue(class_exists('GTask\\Application\\CreateTask'), 'Implement CreateTask after validation.');
    }

    #[Group('checkpoint-2')]
    public function testAuthenticationPortExistsWithoutExposingStorage(): void
    {
        self::assertTrue(interface_exists('GTask\\Application\\Authenticator'), 'Define the authentication port used by login.');
    }

    #[Group('checkpoint-3')]
    public function testCsrfVerifierRejectsAMissingToken(): void
    {
        if (!class_exists('GTask\\Http\\Csrf')) {
            self::fail('Observable contract missing: a session-backed CSRF verifier must exist.');
        }
        self::assertFalse(\GTask\Http\Csrf::valid(['_csrf' => str_repeat('a', 64)], null));
    }

    #[Group('checkpoint-4')]
    public function testRepositoryContractScopesLookupByOwner(): void
    {
        if (!interface_exists('GTask\\Domain\\TaskRepository')) {
            self::fail('Observable contract missing: the owner-scoped repository port must exist.');
        }
        $method = new \ReflectionMethod('GTask\\Domain\\TaskRepository', 'findOwned');
        self::assertSame(['id', 'ownerId'], array_map(fn($p) => $p->getName(), $method->getParameters()));
    }
    #[Group('checkpoint-5')]
    public function testHtmlEscaperEncodesExecutableMarkup(): void
    {
        self::assertSame('&lt;script&gt;alert(1)&lt;/script&gt;', \GTask\Http\e('<script>alert(1)</script>'));
        self::assertTrue(is_file(dirname(__DIR__).'/public/index.php'), 'Wire the use cases through a front controller.');
    }
}
