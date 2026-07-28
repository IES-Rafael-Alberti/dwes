import { createRouter, createWebHistory } from 'vue-router';
import PostList from '@/components/PostList.vue';
import Login from '@/components/Login.vue';
import CreatePost from '@/components/CreatePost.vue';

const routes = [
    { path: '/', component: PostList },
    { path: '/login', component: Login },
    { path: '/create-post', component: CreatePost }
    
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;