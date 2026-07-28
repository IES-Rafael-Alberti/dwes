<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

/**
 * @OA\Schema(
 *    title="Post",
 *    description="Post model",
 *    required={"title", "image", "description"},
 *    @OA\Property(
 *        property="id",
 *        type="integer",
 *        description="ID",
 *        example="1"
 *    ),
 *    @OA\Property(
 *        property="title",
 *        type="string",
 *        description="Title",
 *        example="Post title"
 *    ),
 *    @OA\Property(
 *        property="image",
 *        type="string",
 *        description="Image",
 *        example="image.jpg"
 *    ),
 *    @OA\Property(
 *        property="description",
 *        type="string",
 *        description="Description",
 *        example="Post description"
 *    ),
 *    @OA\Property(
 *        property="user_id",
 *        type="integer",
 *        description="User ID",
 *        example="1"
 *    )
 * )
 */
class Post extends Model
{
    use HasFactory;

    /**
     * Get the user record associated with the post.
     */
    public function user()  
    {
        return $this->belongsTo(User::class);
    }
}