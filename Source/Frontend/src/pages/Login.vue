<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-logo">Blodged</div>
      <h1 class="auth-title">Welcome Back</h1>
      
      <form class="auth-form" @submit.prevent="onLogin">
        <div class="form-group">
          <label for="email" class="form-label">Email</label>
          <input 
            id="email" 
            v-model="email" 
            type="email" 
            class="form-input" 
            placeholder="Your email address"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <input 
            id="password" 
            v-model="password" 
            type="password" 
            class="form-input" 
            placeholder="Your password"
            required
          />
        </div>
        
        <div v-if="error" class="form-error">{{ error }}</div>
        
        <button type="submit" class="btn btn-primary">Login</button>
      </form>
      
      <div class="auth-footer">
        Don't have an account? <router-link to="/register" class="auth-link">Register</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { loginApi } from '../api'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()
const userStore = useUserStore()

async function onLogin() {
  error.value = ''
  try {
    const { user, token } = await loginApi(email.value, password.value)
    userStore.setUser(user, token)
    router.push('/')
  } catch (e: any) {
    error.value = e.message || 'Login failed'
  }
}
</script> 