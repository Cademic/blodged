<template>
  <nav class="navbar">
    <div class="navbar-left">
      <router-link to="/" class="navbar-logo">Blodged</router-link>
      <router-link to="/" class="navbar-link">Home</router-link>
      <router-link :to="userStore.user ? `/profile/${userStore.user.username}` : '/login'" class="navbar-link" v-if="isLoggedIn">Profile</router-link>
      <router-link to="/admin" class="navbar-link" v-if="isLoggedIn">Admin</router-link>
      <router-link to="/messages" class="navbar-link" v-if="isLoggedIn">Messages</router-link>
    </div>
    <div class="navbar-right">
      <router-link to="/login" class="navbar-link" v-if="!isLoggedIn">Login</router-link>
      <router-link to="/register" class="navbar-link" v-if="!isLoggedIn">Register</router-link>
      <router-link to="/settings" class="navbar-link" v-if="isLoggedIn">Settings</router-link>
      <button v-if="isLoggedIn" @click="logout" class="navbar-btn">Logout</button>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const userStore = useUserStore()
const router = useRouter()
const isLoggedIn = computed(() => !!userStore.token)

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 2rem;
  background: #fff;
  border-bottom: 1px solid #eaeaea;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
}
.navbar-left, .navbar-right {
  display: flex;
  align-items: center;
}
.navbar-logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #42b983;
  margin-right: 2rem;
  text-decoration: none;
}
.navbar-link {
  margin: 0 0.75rem;
  color: #35495e;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.navbar-link.router-link-exact-active {
  color: #42b983;
  font-weight: bold;
}
.navbar-btn {
  background: #42b983;
  color: #fff;
  border: none;
  padding: 0.4rem 1.2rem;
  border-radius: 4px;
  margin-left: 1rem;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s;
}
.navbar-btn:hover {
  background: #35495e;
}
</style> 