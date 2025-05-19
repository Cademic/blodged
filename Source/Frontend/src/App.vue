<script setup lang="ts">
import { useUserStore } from './store/user'
import { onMounted } from 'vue'
import NavBar from './components/NavBar.vue'

const userStore = useUserStore()

onMounted(() => {
  // Check authentication status on app load
  userStore.checkAuth()
})
</script>

<template>
  <NavBar />
  <router-view />
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}

nav {
  margin-bottom: 2rem;
}
nav a {
  margin: 0 0.5rem;
  text-decoration: none;
  color: #42b983;
}
nav a.router-link-exact-active {
  font-weight: bold;
  color: #35495e;
}
button {
  background: none;
  border: 1px solid #42b983;
  color: #42b983;
  padding: 0.2rem 0.7rem;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background: #42b983;
  color: white;
}
</style>
