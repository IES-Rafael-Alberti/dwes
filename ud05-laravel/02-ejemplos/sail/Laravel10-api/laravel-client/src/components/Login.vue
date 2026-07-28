<template>
    <div>
        <h1>Iniciar Sesión</h1>
        <form @submit.prevent="handleLogin">
            <input v-model="email" type="email" placeholder="Correo" required>
            <input v-model="password" type="password" placeholder="Contraseña" required>
            <button type="submit">Ingresar</button>
        </form>
    </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
    data() {
        return {
            email: '',
            password: ''
        };
    },
    methods: {
        ...mapActions(['login']),
        async handleLogin() {
            try {
                await this.login({ email: this.email, password: this.password });
                this.$router.push('/');
            } catch (error) {
                console.error('Error en el login', error);
            }
        }
    }
};
</script>