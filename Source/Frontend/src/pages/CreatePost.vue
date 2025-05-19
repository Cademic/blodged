<template>
  <div class="create-post-container">
    <h1>Create New Post</h1>
    <form @submit.prevent="submitPost" class="post-form">
      <div class="form-group">
        <label for="content">Post Content</label>
        <textarea 
          id="content" 
          v-model="postContent" 
          rows="6" 
          placeholder="What's on your mind?"
          required
        ></textarea>
      </div>
      <div class="form-actions">
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Posting...' : 'Post' }}
        </button>
      </div>
      <div v-if="error" class="error-message">{{ error }}</div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { createPost } from '../api'

const router = useRouter()
const userStore = useUserStore()

const postContent = ref('')
const loading = ref(false)
const error = ref('')

async function submitPost() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  loading.value = true
  error.value = ''

  try {
    await createPost(postContent.value)
    // Post created successfully, redirect to home page
    router.push('/')
  } catch (err: any) {
    console.error('Error creating post:', err)
    error.value = err.message || 'Failed to create post'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-post-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

h1 {
  margin-bottom: 1.5rem;
  color: #333;
}

.post-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
  font-size: 1rem;
}

textarea:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.btn-submit {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-submit:hover {
  background-color: #3aa876;
}

.btn-submit:disabled {
  background-color: #b5b5b5;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  color: #e53935;
  text-align: center;
}
</style> 