import { createStore } from 'vuex';
import api from '@/services/api';

export default createStore({
    state: {
        user: null,
        token: localStorage.getItem('token') || null
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        },
        setToken(state, token) {
            state.token = token;
            localStorage.setItem('token', token);
            api.defaults.headers['Authorization'] = `Bearer ${token}`;
        },
        logout(state) {
            state.user = null;
            state.token = null;
            localStorage.removeItem('token');
            delete api.defaults.headers['Authorization'];
        }
    },
    actions: {
        async login({ commit }, credentials) {
            const response = await api.post('/auth/login', credentials);
            commit('setToken', response.data.access_token);
            commit('setUser', await api.post('/auth/me'));
        },
        async logout({ commit }) {
            await api.post('/auth/logout');
            commit('logout');
        },
        async fetchUser({ commit }) {
            const response = await api.post('/auth/me');
            commit('setUser', response.data);
        }
    }
});