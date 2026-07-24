<?php

declare(strict_types=1);
use PHPUnit\Framework\Attributes\Group;
use PHPUnit\Framework\TestCase;
final class CheckpointContractTest extends TestCase
{
 #[Group('checkpoint-1'),Group('checkpoint')] public function testCheckpointOneListsAcrossConnections():void { $f=sys_get_temp_dir().'/notes-cp-'.bin2hex(random_bytes(4)).'.sqlite'; $a=database($f); create_note($a,['title'=>'One','body'=>'Body']); $b=database($f); $notes=list_notes($b); self::assertNotEmpty($notes); self::assertArrayHasKey('title',$notes[0]); self::assertSame('One',$notes[0]['title']); unlink($f); }
 #[Group('checkpoint-2'),Group('checkpoint')] public function testCheckpointTwoCompletesCrud():void { $p=database(':memory:'); $id=create_note($p,['title'=>'One','body'=>'Body']); self::assertTrue(update_note($p,$id,['title'=>'Two','body'=>'Changed'])); self::assertSame('Two',find_note($p,$id)['title']); self::assertFalse(update_note($p,99,['title'=>'x','body'=>'y'])); self::assertTrue(delete_note($p,$id)); self::assertNull(find_note($p,$id)); self::assertFalse(delete_note($p,99)); }
 #[Group('checkpoint-3'),Group('checkpoint')] public function testCheckpointThreeProtectsEveryMutation():void { $p=database(':memory:'); $s=[]; csrf_token($s); foreach(['/notes','/notes/1/edit','/notes/1/delete'] as $path) { foreach([null,'wrong'] as $token) { $data=['title'=>'A','body'=>'B']; if($token!==null)$data['csrf']=$token; self::assertSame(400,dispatch($p,'POST',$path,$data,$s)['status']); } } }
 #[Group('checkpoint-4'),Group('checkpoint')] public function testCheckpointFourUsesPrgAfterEverySuccess():void { $p=database(':memory:'); $s=[]; $t=csrf_token($s); foreach ([dispatch($p,'POST','/notes',['title'=>'A','body'=>'B','csrf'=>$t],$s), dispatch($p,'POST','/notes/1/edit',['title'=>'C','body'=>'D','csrf'=>$t],$s), dispatch($p,'POST','/notes/1/delete',['csrf'=>$t],$s)] as $response) { self::assertSame(303,$response['status']); self::assertArrayHasKey('Location',$response['headers']); self::assertSame('/notes',$response['headers']['Location']); } }
}
