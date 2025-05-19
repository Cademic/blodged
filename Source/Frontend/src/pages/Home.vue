<template>
  <div>
    <div class="home-container">
      <div class="home-layout">
        <div class="posts-section">
          <div class="create-post">
            <router-link v-if="isLoggedIn" to="/posts/create" class="create-post-btn">Create Post</router-link>
            <router-link v-else to="/login" class="login-btn">Log In</router-link>
          </div>
          
          <div v-if="loading" class="loading-state">
            <p>Loading posts...</p>
          </div>
          
          <div v-else-if="error" class="error-state">
            <p>{{ error }}</p>
            <button @click="fetchPosts" class="retry-btn">Retry</button>
          </div>
          
          <div v-else-if="posts.length === 0" class="no-posts">
            <h3>No posts available</h3>
            <p v-if="isLoggedIn">Be the first to create a post!</p>
            <p v-else>Log in to create posts</p>
          </div>
          
          <div v-else v-for="post in posts" :key="post.id" class="post-card">
            <div class="post-header">
              <div class="post-avatar">{{ post.authorUsername.charAt(0) }}</div>
              <div>
                <div class="post-author">{{ post.authorUsername }}</div>
                <div class="post-date" v-if="post.createdAt">{{ formatDate(post.createdAt) }}</div>
              </div>
            </div>
            <div class="post-content">{{ post.postContent }}</div>
            <div class="post-stats">
              <span class="like-count">{{ post.likeCount }} likes</span>
            </div>
            <div v-if="isLoggedIn" class="post-actions">
              <div class="post-action">
                <span>Like</span>
              </div>
              <div class="post-action">
                <span>Comment</span>
              </div>
              <div class="post-action">
                <span>Share</span>
              </div>
            </div>
            <div v-else class="post-action-login">
              <router-link to="/login">Log in to interact with posts</router-link>
            </div>
          </div>
        </div>
        
        <div class="search-section">
          <div class="search-card">
            <h3 class="search-title">Search Posts</h3>
            <form class="search-form" @submit.prevent="onSearch">
              <input 
                type="text" 
                class="search-input" 
                placeholder="Search posts..." 
                v-model="searchTerm"
              />
              <button type="submit" class="search-btn">Search</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '../store/user'
import { getPostsApi } from '../api'

interface Post {
  id: number
  authorUsername: string
  postContent: string
  likeCount: number
  createdAt?: string
}

// Get user store for authentication checks
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Store posts data
const posts = ref<Post[]>([])
const loading = ref(false)
const error = ref('')

const searchTerm = ref('')

// Fetch posts when component is mounted
onMounted(async () => {
  await fetchPosts()
})

async function fetchPosts() {
  loading.value = true
  error.value = ''
  
  try {
    const postsData = await getPostsApi()
    
    // Transform the data to match our interface requirements
    // Sort by ID in descending order (assuming higher IDs are newer)
    posts.value = postsData
      .sort((a: Post, b: Post) => b.id - a.id)
      .map((post: Post) => ({
        ...post,
        // Adding a fake createdAt for display purposes since it's not in the API
        createdAt: new Date(Date.now() - Math.floor(Math.random() * 10000000)).toISOString()
      }))
  } catch (e: any) {
    error.value = e.message || 'Failed to load posts'
    posts.value = []
  } finally {
    loading.value = false
  }
}

function onSearch() {
  // Implementation for search
  console.log('Searching for:', searchTerm.value)
  // For a simple client-side search:
  if (searchTerm.value.trim()) {
    const term = searchTerm.value.toLowerCase()
    fetchPosts().then(() => {
      posts.value = posts.value.filter(post => 
        post.postContent.toLowerCase().includes(term) || 
        post.authorUsername.toLowerCase().includes(term)
      )
    })
  } else {
    fetchPosts()
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  }).format(date)
}
</script>

<style scoped>
.home-container {
  padding: 2rem 0;
  background: #f7f8fa;
  min-height: 100vh;
}
.home-layout {
  display: flex;
  flex-direction: row;
  max-width: 1200px;
  margin: 0 auto;
  gap: 2rem;
}
.posts-section {
  flex: 3;
}
.search-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 1rem;
}
.create-post {
  margin-bottom: 1.5rem;
}
.create-post-btn {
  display: inline-block;
  background: #42b983;
  color: #fff;
  text-decoration: none;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
  text-align: center;
}
.create-post-btn:hover {
  background: #35495e;
}
.login-btn {
  display: inline-block;
  background: #6366F1;
  color: #fff;
  text-decoration: none;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.login-btn:hover {
  background: #4F46E5;
}
.post-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  margin-bottom: 1.5rem;
  padding: 1.25rem 1.5rem;
  transition: box-shadow 0.2s;
}
.post-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.10);
}
.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}
.post-avatar {
  background: #42b983;
  color: #fff;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  font-weight: bold;
  margin-right: 0.75rem;
}
.post-author {
  font-weight: bold;
  color: #42b983;
  font-size: 1.1rem;
}
.post-date {
  color: #666;
  font-size: 0.9rem;
}
.post-content {
  color: #333;
  font-size: 1.05rem;
  margin-top: 0.5rem;
  margin-bottom: 1rem;
}
.post-stats {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.85rem;
  margin-bottom: 0.75rem;
}
.post-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.5rem;
  border-top: 1px solid #eaeaea;
  padding-top: 0.75rem;
}
.post-action {
  background: #f0f0f0;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  margin-left: 0.25rem;
  cursor: pointer;
  transition: background 0.2s;
}
.post-action:hover {
  background: #e0e0e0;
}
.post-action-login {
  text-align: center;
  margin-top: 0.5rem;
  border-top: 1px solid #eaeaea;
  padding-top: 0.75rem;
  font-size: 0.875rem;
}
.post-action-login a {
  color: #6366F1;
  text-decoration: none;
}
.post-action-login a:hover {
  text-decoration: underline;
}
.search-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 1rem;
}
.search-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}
.search-form {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.search-input {
  flex: 1;
  padding: 0.5rem 1rem;
  border: 1px solid #eaeaea;
  border-radius: 4px;
  font-size: 1rem;
}
.search-btn {
  background: #42b983;
  color: #fff;
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.search-btn:hover {
  background: #35495e;
}
.loading-state,
.error-state,
.no-posts {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 2rem;
  text-align: center;
}
.retry-btn {
  margin-top: 1rem;
  background: #6366F1;
  color: #fff;
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 4px;
  font-size: 0.875rem;
  cursor: pointer;
}
.no-posts h3 {
  color: #333;
  margin-bottom: 0.5rem;
}
.no-posts p {
  color: #666;
}
@media (max-width: 900px) {
  .home-layout {
    flex-direction: column;
    gap: 1rem;
  }
  .search-section {
    align-items: stretch;
    margin-top: 0;
  }
}
</style> 