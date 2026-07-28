<template>
  <div>
    <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/login">Login</router-link>
      <router-link to="/create-post">Crear Post</router-link>
    </nav>
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
nav {
  margin-bottom: 20px;
}
nav a {
  margin-right: 10px;
}
</style>
