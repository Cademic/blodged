<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-logo">Blodged</div>
      <h1 class="auth-title">Create Account</h1>
      
      <form class="auth-form" @submit.prevent="onRegister">
        <div class="form-group">
          <label for="username" class="form-label">Username</label>
          <input 
            id="username" 
            v-model="username" 
            type="text" 
            class="form-input" 
            placeholder="Choose a username"
            required
          />
        </div>
        
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
            placeholder="Create a secure password"
            required
          />
        </div>
        
        <div v-if="error" class="form-error">{{ error }}</div>
        
        <button type="submit" class="btn btn-primary">Register</button>
      </form>
      
      <div class="auth-footer">
        Already have an account? <router-link to="/login" class="auth-link">Login</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { registerApi } from '../api'

const username = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()
const userStore = useUserStore()

async function onRegister() {
  error.value = ''
  try {
    const { user, token } = await registerApi(username.value, email.value, password.value)
    userStore.setUser(user, token)
    router.push('/')
  } catch (e: any) {
    error.value = e.message || 'Registration failed'
  }
}
</script> 