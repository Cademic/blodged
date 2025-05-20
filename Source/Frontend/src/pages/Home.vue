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
              <div class="post-user-info">
                <div class="post-author">
                  <router-link :to="`/profile/${post.authorUsername}`">
                    {{ post.authorUsername }}
                  </router-link>
                </div>
                <div class="post-date" v-if="post.createdAt">{{ formatDate(post.createdAt) }}</div>
              </div>
              
              <!-- Edit and Delete buttons for author -->
              <div v-if="isPostAuthor(post)" class="post-actions-author">
                <button @click="startEditPost(post)" class="action-btn edit-btn" title="Edit Post">
                  <img src="../assets/images/edit.png" alt="Edit" class="action-img" />
                </button>
                <button @click="confirmDeletePost(post)" class="action-btn delete-btn" title="Delete Post">
                  <img src="../assets/images/delete.png" alt="Delete" class="action-img" />
                </button>
              </div>
            </div>
            
            <!-- Edit mode -->
            <div v-if="editingPost && editingPost.id === post.id" class="post-edit-form">
              <textarea 
                v-model="editPostContent" 
                class="post-edit-textarea"
                rows="4"
              ></textarea>
              <div class="post-edit-actions">
                <button 
                  @click="savePostEdit()"
                  class="post-edit-btn save-btn"
                  :disabled="isSavingEdit"
                >
                  {{ isSavingEdit ? 'Saving...' : 'Save' }}
                </button>
                <button 
                  @click="cancelPostEdit()"
                  class="post-edit-btn cancel-btn"
                >
                  Cancel
                </button>
              </div>
            </div>
            
            <!-- Regular post content (when not editing) -->
            <div v-else class="post-content">{{ post.postContent }}</div>
            
            <div class="post-stats">
              
              <router-link :to="`/posts/view/${post.id}`" class="view-replies-link">
                Show all replies ({{ post.replyCount }})
              </router-link>
            </div>
            <div v-if="isLoggedIn" class="post-actions">
              <div class="likes-section">
                <span class="like-count">{{ post.likeCount }}</span>
                <button v-if="isLoggedIn" 
                  class="like-btn" 
                  @click.prevent.stop="toggleLike(post)" 
                  :title="post.isLiked ? 'Unlike' : 'Like'"
                  :class="{ 'liked': post.isLiked }"
                >
                  <img src="../assets/images/like.webp" alt="Like" class="like-img" />
                </button>
              </div>
              <div class="post-action">
                <router-link :to="`/posts/view/${post.id}`" class="view-replies-link">
                Reply
              </router-link>
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
    
    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirm" class="delete-modal-overlay">
      <div class="delete-modal">
        <h3>Delete Post</h3>
        <p>Are you sure you want to delete this post? This action cannot be undone.</p>
        <div class="delete-modal-actions">
          <button 
            @click="deletePostConfirmed()" 
            class="delete-confirm-btn"
            :disabled="isDeleting"
          >
            {{ isDeleting ? 'Deleting...' : 'Delete' }}
          </button>
          <button 
            @click="cancelDeletePost()" 
            class="delete-cancel-btn"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '../store/user'
import { getPostsApi, likePost, unlikePost, editPost, deletePost, checkPostLike, getReplyCountForPost } from '../api'
import { useRouter } from 'vue-router'

interface Post {
  id: number
  authorUsername: string
  postContent: string
  likeCount: number
  replyCount: number
  isLiked?: boolean
  createdAt?: string
}

// Get user store for authentication checks
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const router = useRouter()

// Store posts data
const posts = ref<Post[]>([])
const loading = ref(false)
const error = ref('')

const searchTerm = ref('')

// Edit and delete state management
const editingPost = ref<Post | null>(null)
const editPostContent = ref('')
const isSavingEdit = ref(false)
const postToDelete = ref<Post | null>(null)
const showDeleteConfirm = ref(false)
const isDeleting = ref(false)

// Fetch posts when component is mounted
onMounted(() => {
  fetchPosts()
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
        createdAt: new Date(Date.now() - Math.floor(Math.random() * 10000000)).toISOString(),
        // Initialize replyCount to 0 if it doesn't exist
        replyCount: post.replyCount || 0
      }))
    
    // If user is logged in, check likes status for each post
    if (userStore.isLoggedIn) {
      for (const post of posts.value) {
        try {
          // Verify like status from server for each post
          post.isLiked = await checkPostLike(post.id)
        } catch (err) {
          console.error(`Error checking like status for post ${post.id}:`, err)
        }
      }
    }
    
    // Get actual reply counts for each post
    for (const post of posts.value) {
      try {
        post.replyCount = await getReplyCountForPost(post.id)
      } catch (err) {
        console.error(`Error getting reply count for post ${post.id}:`, err)
      }
    }
  } catch (e: any) {
    console.error('Error fetching posts:', e)
    
    // Check if the error is about server starting up
    if (e.message && (
        e.message.includes('starting up') || 
        e.message.includes('invalid data') || 
        e.message.includes('Unexpected token')
      )) {
      console.log('Server is starting up. Automatically reloading page in 2 seconds...')
      error.value = 'Server is starting up. Reloading page in 2 seconds...'
      setTimeout(() => {
        window.location.reload()
      }, 2000)
    } else {
      error.value = e.message || 'Failed to load posts'
      posts.value = []
    }
  } finally {
    loading.value = false
  }
}

// Check if the current user is the author of the post
function isPostAuthor(post: Post): boolean {
  return isLoggedIn.value && userStore.user?.username === post.authorUsername
}

// Edit post functionalities
function startEditPost(post: Post) {
  editingPost.value = post
  editPostContent.value = post.postContent
}

function cancelPostEdit() {
  editingPost.value = null
  editPostContent.value = ''
}

async function savePostEdit() {
  if (!editingPost.value || !editPostContent.value.trim()) return
  
  isSavingEdit.value = true
  
  try {
    await editPost(editingPost.value.id, editPostContent.value.trim())
    
    // Update post content in the local array
    const index = posts.value.findIndex(p => p.id === editingPost.value?.id)
    if (index !== -1) {
      posts.value[index].postContent = editPostContent.value.trim()
    }
    
    // Reset edit state
    editingPost.value = null
    editPostContent.value = ''
  } catch (e: any) {
    alert(`Failed to save changes: ${e.message || 'Unknown error'}`)
  } finally {
    isSavingEdit.value = false
  }
}

// Delete post functionalities
function confirmDeletePost(post: Post) {
  postToDelete.value = post
  showDeleteConfirm.value = true
}

function cancelDeletePost() {
  postToDelete.value = null
  showDeleteConfirm.value = false
}

async function deletePostConfirmed() {
  if (!postToDelete.value) return
  
  isDeleting.value = true
  
  try {
    await deletePost(postToDelete.value.id)
    
    // Remove post from local array
    posts.value = posts.value.filter(p => p.id !== postToDelete.value?.id)
    
    // Reset delete state
    cancelDeletePost()
  } catch (e: any) {
    alert(`Failed to delete post: ${e.message || 'Unknown error'}`)
  } finally {
    isDeleting.value = false
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
      await fetchPosts()
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
      post.isLiked = await checkPostLike(post.id)
    }, 500)
  } catch (error) {
    console.error('Error toggling like:', error)
    // Refresh post data on error to ensure UI is consistent
    await fetchPosts()
  }
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
.post-user-info {
  flex: 1;
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
.liked {
  color: #4f46e5;
  font-weight: bold;
}
.liked .fa-thumbs-up {
  color: #4f46e5;
}
.far.fa-thumbs-up {
  color: #666;
}
.post-action {
  cursor: pointer;
  transition: all 0.2s;
}
.post-action:hover {
  opacity: 0.7;
}
.post-author a {
  color: #42b983;
  text-decoration: none;
}
.post-author a:hover {
  text-decoration: underline;
}
.view-replies-link {
  color: #6366F1;
  text-decoration: none;
  font-size: 0.9rem;
}
.view-replies-link:hover {
  text-decoration: underline;
}

/* Author edit/delete section styles */
.post-actions-author {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  border: none;
  border-radius: 4px;
  padding: 0.4rem 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}

.edit-btn:hover, .delete-btn:hover {
  background-color: #f5f5f5;
}

.action-img {
  width: 20px;
  height: 20px;
}

/* Post edit form styles */
.post-edit-form {
  margin: 0.75rem 0;
}

.post-edit-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #cbd5e0;
  border-radius: 0.25rem;
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
}

.post-edit-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  justify-content: flex-end;
}

.post-edit-btn {
  padding: 0.375rem 0.75rem;
  border: none;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  cursor: pointer;
}

.post-edit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.save-btn {
  background: #4a5568;
  color: white;
}

.save-btn:hover:not(:disabled) {
  background: #2d3748;
}

.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background: #cbd5e0;
}

/* Delete confirmation modal styles */
.delete-modal-overlay {
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

.delete-modal {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.delete-modal h3 {
  margin-top: 0;
  font-size: 1.25rem;
  color: #2d3748;
}

.delete-modal p {
  color: #4a5568;
  margin-bottom: 1.5rem;
}

.delete-modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.delete-confirm-btn, .delete-cancel-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.875rem;
  cursor: pointer;
}

.delete-confirm-btn {
  background: #e53e3e;
  color: white;
}

.delete-confirm-btn:hover:not(:disabled) {
  background: #c53030;
}

.delete-confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.delete-cancel-btn {
  background: #edf2f7;
  color: #4a5568;
}

.delete-cancel-btn:hover {
  background: #e2e8f0;
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

.likes-section {
  display: flex;
  align-items: center;
  gap: 0.05rem;
}

.like-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: transform 0.2s;
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
</style> 