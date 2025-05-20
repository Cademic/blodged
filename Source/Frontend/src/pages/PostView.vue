<template>
  <div class="post-view-container">
    <div class="post-view-content">
      <div v-if="loading" class="loading-state">
        <p>Loading post...</p>
      </div>
      
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <router-link to="/" class="return-home">Return to Home</router-link>
      </div>
      
      <div v-else-if="post" class="post-wrapper">
        <!-- Post Card -->
        <div class="post-card">
          <div class="post-header">
            <div class="post-avatar">{{ post.authorUsername.charAt(0) }}</div>
            <div class="post-author-info">
              <div class="post-author">
                <router-link :to="`/profile/${post.authorUsername}`">
                  {{ post.authorUsername }}
                </router-link>
              </div>
              <div class="post-date" v-if="post.createdAt">{{ formatDate(post.createdAt) }}</div>
            </div>
            
            <!-- Edit and Delete buttons for author -->
            <div v-if="isAuthor" class="post-actions-author">
              <button @click="startEditing" class="action-btn edit-btn" title="Edit Post">
                <img src="../assets/images/edit.png" alt="Edit" class="action-img" />
              </button>
              <button @click="confirmDelete" class="action-btn delete-btn" title="Delete Post">
                <img src="../assets/images/delete.png" alt="Delete" class="action-img" />
              </button>
            </div>
          </div>
          
          <!-- Edit mode -->
          <div v-if="isEditing" class="post-edit-area">
            <textarea 
              v-model="editedContent" 
              class="post-edit-input"
              rows="4"
            ></textarea>
            <div class="post-edit-actions">
              <button 
                class="post-edit-btn save-btn" 
                @click="saveEdit"
                :disabled="isSaving"
              >
                {{ isSaving ? 'Saving...' : 'Save' }}
              </button>
              <button 
                class="post-edit-btn cancel-btn"
                @click="cancelEdit"
                :disabled="isSaving"
              >
                Cancel
              </button>
            </div>
          </div>
          
          <!-- View mode -->
          <div v-else class="post-content">{{ post.postContent }}</div>
          
          <div class="post-stats">
            <div class="likes-section">
              <span class="like-count">{{ post.likeCount }} likes</span>
              <button v-if="isLoggedIn" 
                class="like-btn" 
                @click.prevent.stop="toggleLike(post)" 
                :title="post.isLiked ? 'Unlike' : 'Like'"
                :class="{ 'liked': post.isLiked }"
              >
                <img src="../assets/images/like.webp" alt="Like" class="like-img" />
              </button>
            </div>
          </div>
          
          <div v-if="isLoggedIn" class="post-actions">
            <div class="post-action">
              <span>Share</span>
            </div>
          </div>
          <div v-else class="post-action-login">
            <router-link to="/login">Log in to interact with this post</router-link>
          </div>
        </div>
        
        <!-- Delete Confirmation Modal -->
        <div v-if="showDeleteConfirm" class="delete-confirm-overlay">
          <div class="delete-confirm-dialog">
            <h3>Delete Post</h3>
            <p>Are you sure you want to delete this post? This action cannot be undone.</p>
            <div class="delete-confirm-actions">
              <button 
                class="confirm-btn delete-btn" 
                @click="deletePostConfirmed"
                :disabled="isDeleting"
              >
                {{ isDeleting ? 'Deleting...' : 'Delete' }}
              </button>
              <button 
                class="confirm-btn cancel-btn" 
                @click="cancelDelete"
                :disabled="isDeleting"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
        
        <!-- Replies Section -->
        <RepliesSection :postId="postId" />
      </div>
      
      <div v-else class="no-post">
        <p>Post not found.</p>
        <router-link to="/" class="return-home">Return to Home</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { likePost, unlikePost, editPost, deletePost, checkPostLike } from '../api'
import RepliesSection from '../components/RepliesSection.vue'

interface Post {
  id: number
  authorUsername: string
  postContent: string
  likeCount: number
  isLiked?: boolean
  createdAt?: string
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const postId = Number(route.params.id)
const post = ref<Post | null>(null)
const loading = ref(true)
const error = ref('')

// Edit/delete state
const isEditing = ref(false)
const editedContent = ref('')
const isSaving = ref(false)
const showDeleteConfirm = ref(false)
const isDeleting = ref(false)

// Computed property to check if current user is the author
const isAuthor = computed(() => {
  if (!post.value || !userStore.user) return false
  return post.value.authorUsername === userStore.user.username
})

// Fetch post when component is mounted
onMounted(async () => {
  await fetchPost()
})

async function fetchPost() {
  loading.value = true
  error.value = ''
  
  try {
    // Get post by ID from API
    const response = await fetch(`/api/posts/id/${postId}`, {
      method: 'GET',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': userStore.token ? `Bearer ${userStore.token}` : '',
      },
    })
    
    if (!response.ok) {
      throw new Error('Failed to load post')
    }
    
    const postData = await response.json()
    
    // Add fake created date for UI
    post.value = {
      ...postData,
      isLiked: false, // Default value, we'll update this below
      createdAt: new Date(Date.now() - Math.floor(Math.random() * 10000000)).toISOString()
    }
    
    // If logged in, check if the user has liked this post
    if (userStore.isLoggedIn && post.value) {
      try {
        post.value.isLiked = await checkPostLike(post.value.id)
      } catch (err) {
        console.error(`Error checking like status:`, err)
      }
    }
  } catch (e: any) {
    error.value = e.message || 'Failed to load post'
    post.value = null
  } finally {
    loading.value = false
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  }).format(date)
}

// Toggle like/unlike on a post
async function toggleLike(post: Post) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  try {
    // First, verify current like status
    const currentLikeStatus = await checkPostLike(post.id)
    
    // If the display state doesn't match the actual state, fix it
    if (post.isLiked !== currentLikeStatus) {
      console.log('Fixing inconsistent like state')
      post.isLiked = currentLikeStatus
      // Refresh post data to get accurate like count
      await fetchPost()
      return
    }
    
    if (post.isLiked) {
      await unlikePost(post.id)
      post.likeCount--
    } else {
      await likePost(post.id)
      post.likeCount++
    }
    post.isLiked = !post.isLiked
    
    // Re-check like status from server to ensure consistency
    setTimeout(async () => {
      if (post && post.id) {
        const updatedStatus = await checkPostLike(post.id)
        post.isLiked = updatedStatus
      }
    }, 500)
  } catch (error) {
    console.error('Error toggling like:', error)
    // Refresh post data on error to ensure UI is consistent
    await fetchPost()
  }
}

// Edit post functions
function startEditing() {
  if (!post.value) return
  editedContent.value = post.value.postContent
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
  editedContent.value = ''
}

async function saveEdit() {
  if (!post.value || !editedContent.value.trim()) return
  
  isSaving.value = true
  
  try {
    await editPost(post.value.id, editedContent.value.trim())
    
    // Update local post content
    if (post.value) {
      post.value.postContent = editedContent.value.trim()
    }
    
    // Exit edit mode
    isEditing.value = false
  } catch (e: any) {
    alert(`Failed to update post: ${e.message || 'Unknown error'}`)
  } finally {
    isSaving.value = false
  }
}

// Delete post functions
function confirmDelete() {
  showDeleteConfirm.value = true
}

function cancelDelete() {
  showDeleteConfirm.value = false
}

async function deletePostConfirmed() {
  if (!post.value) return
  
  isDeleting.value = true
  
  try {
    await deletePost(post.value.id)
    
    // Redirect to home page after successful delete
    router.push('/')
  } catch (e: any) {
    alert(`Failed to delete post: ${e.message || 'Unknown error'}`)
  } finally {
    isDeleting.value = false
    showDeleteConfirm.value = false
  }
}
</script>

<style scoped>
.post-view-container {
  padding: 2rem 0;
  background: #f7f8fa;
  min-height: 100vh;
}

.post-view-content {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 1rem;
}

.post-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 1.25rem 1.5rem;
  margin-bottom: 1.5rem;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.post-avatar {
  background: #42b983;
  color: #fff;
  border-radius: 50%;
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-right: 0.75rem;
}

.post-author-info {
  flex: 1;
}

.post-author {
  font-weight: bold;
  font-size: 1.1rem;
}

.post-author a {
  color: #42b983;
  text-decoration: none;
}

.post-author a:hover {
  text-decoration: underline;
}

.post-date {
  color: #666;
  font-size: 0.9rem;
}

.post-actions-author {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  border: none;
  border-radius: 4px;
  padding: 0.4rem 0.6rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}

.action-btn:hover {
  background-color: #f5f5f5;
}

.action-img {
  width: 20px;
  height: 20px;
}

.post-edit-area {
  margin: 1rem 0;
}

.post-edit-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  font-size: 1rem;
  line-height: 1.5;
  resize: vertical;
  font-family: inherit;
}

.post-edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.post-edit-btn {
  padding: 0.375rem 0.75rem;
  border: none;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.post-edit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.post-edit-btn.save-btn {
  background: #4a5568;
  color: white;
}

.post-edit-btn.save-btn:hover:not(:disabled) {
  background: #2d3748;
}

.post-edit-btn.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.post-edit-btn.cancel-btn:hover:not(:disabled) {
  background: #cbd5e0;
}

.post-content {
  color: #333;
  font-size: 1.1rem;
  line-height: 1.5;
  margin: 1rem 0;
  white-space: pre-line;
}

.post-stats {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.9rem;
  margin: 1rem 0 0.5rem;
}

.likes-section {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.like-count {
  margin-right: 0.5rem;
}

.like-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: transform 0.2s;
  margin-left: 8px;
}

.like-btn:hover {
  transform: scale(1.1);
}

.like-img {
  width: 20px;
  height: 25px;
}

.liked .like-img {
  filter: brightness(0) saturate(100%) invert(62%) sepia(62%) saturate(7480%) hue-rotate(187deg) brightness(103%) contrast(106%);
}

.post-actions {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid #eaeaea;
}

.post-action {
  cursor: pointer;
  transition: all 0.2s;
  padding: 0.35rem 0.7rem;
  border-radius: 4px;
  background: #f5f5f5;
}

.post-action:hover {
  background: #e0e0e0;
}

.post-action-login {
  text-align: center;
  margin-top: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid #eaeaea;
}

.post-action-login a {
  color: #6366F1;
  text-decoration: none;
}

.post-action-login a:hover {
  text-decoration: underline;
}

.loading-state,
.error-state,
.no-post {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  padding: 2rem;
  text-align: center;
}

.return-home {
  display: inline-block;
  margin-top: 1rem;
  color: #6366F1;
  text-decoration: none;
}

.return-home:hover {
  text-decoration: underline;
}

/* Delete confirmation modal styles */
.delete-confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.delete-confirm-dialog {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.delete-confirm-dialog h3 {
  margin-top: 0;
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 0.75rem;
}

.delete-confirm-dialog p {
  margin-bottom: 1.5rem;
  color: #4a5568;
}

.delete-confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.confirm-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.confirm-btn.delete-btn {
  background: #e53e3e;
  color: white;
}

.confirm-btn.delete-btn:hover:not(:disabled) {
  background: #c53030;
}

.confirm-btn.cancel-btn {
  background: #edf2f7;
  color: #4a5568;
}

.confirm-btn.cancel-btn:hover:not(:disabled) {
  background: #e2e8f0;
}
</style> 