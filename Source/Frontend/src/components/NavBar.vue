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
  gap: 1rem;
  padding: 0.85rem 1.5rem;
  background: color-mix(in srgb, var(--color-background-soft) 90%, transparent);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}
.navbar-left, .navbar-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.25rem;
}
.navbar-logo {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-heading);
  margin-right: 0.75rem;
  text-decoration: none;
  letter-spacing: 0.01em;
}
.navbar-link {
  margin: 0;
  color: var(--color-text);
  text-decoration: none;
  font-weight: 500;
  font-size: 0.95rem;
  border-radius: 999px;
  padding: 0.4rem 0.75rem;
  transition: color 0.2s, background-color 0.2s;
}
.navbar-link.router-link-exact-active {
  color: var(--color-heading);
  background: color-mix(in srgb, var(--color-background-mute) 75%, transparent);
  font-weight: 600;
}
.navbar-link:hover {
  background: color-mix(in srgb, var(--color-background-mute) 70%, transparent);
}
.navbar-btn {
  background: var(--color-heading);
  color: var(--color-background);
  border: 1px solid transparent;
  padding: 0.45rem 0.9rem;
  border-radius: 999px;
  margin-left: 0.35rem;
  cursor: pointer;
  font-weight: 500;
  transition: transform 0.2s, opacity 0.2s;
}
.navbar-btn:hover {
  opacity: 0.92;
  transform: translateY(-1px);
}

@media (max-width: 900px) {
  .navbar {
    padding: 0.75rem 1rem;
  }

  .navbar-logo {
    width: 100%;
    margin-right: 0;
    margin-bottom: 0.3rem;
  }
}
</style> 