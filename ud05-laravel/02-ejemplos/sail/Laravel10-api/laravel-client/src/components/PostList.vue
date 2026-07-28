<template>
  <div>
    <h1>Lista de Posts</h1>
    <transition name="fade">
      <div v-if="flashMessage" class="flash-message" @click="flashMessage = ''">
        {{ flashMessage }}
      </div>
    </transition>
    <ul>
      <li v-for="post in posts" :key="post.id">
        <h3>{{ post.title }}</h3>
        <p>{{ post.description }}</p>
        <img v-if="post.photo" :src="post.photo" alt="Imagen del post" class="post-image" />
      </li>
    </ul>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  data() {
    return {
      posts: [],
      flashMessage: localStorage.getItem('flashMessage') || ''
    };
  },
  async mounted() {
    try {
      const response = await api.get('/posts');
      this.posts = response.data.data;
      if (this.flashMessage) {
        setTimeout(() => {
          this.flashMessage = '';
          localStorage.removeItem('flashMessage');
        }, 3000);
      }
    } catch (error) {
      console.error('Error al cargar los posts', error);
    }
  }
};
</script>

<style>
.post-image {
  max-width: 50%;
  height: auto;
  margin-top: 10px;
}

/* Additional styles for responsiveness */
@media (max-width: 600px) {
  .post-image {
    max-width: 100%;
  }
}

.flash-message {
  background-color: #4caf50;
  color: white;
  padding: 10px;
  margin-bottom: 15px;
  text-align: center;
  border-radius: 5px;
  cursor: pointer;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}


</style>