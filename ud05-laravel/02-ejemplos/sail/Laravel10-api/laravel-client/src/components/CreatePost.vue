<template>
  <div>
    <h1>Crear Nuevo Post</h1>
    <form @submit.prevent="submitPost" enctype="multipart/form-data">
      <input v-model="title" type="text" placeholder="Título" required />
      <textarea
        v-model="description"
        placeholder="Descripción"
        required
      ></textarea>
      <input type="file" @change="handleFileUpload" required />
      <button type="submit">Publicar</button>
      <p v-if="message" :class="{ error: isError, success: !isError }">
        {{ message }}
      </p>
    </form>
  </div>
</template>

<script>
import api from "@/services/api";

export default {
  data() {
    return {
      title: "",
      description: "",
      image: null,
      message: "",
      isError: false,
    };
  },
  methods: {
    handleFileUpload(event) {
      this.image = event.target.files[0];
    },
    async submitPost() {
      try {
        const formData = new FormData();
        formData.append("title", this.title);
        formData.append("description", this.description);
        formData.append("image", this.image);

        const response = await api.post("/posts", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        localStorage.setItem(
          "flashMessage",
          response.data.message || "Post creado con éxito!"
        );
        this.$router.push("/");
      } catch (error) {
        console.error("Error al crear el post", error);
      }
    },
  },
};
</script>

<style>
.error {
  color: red;
}
.success {
  color: green;
}
</style>
