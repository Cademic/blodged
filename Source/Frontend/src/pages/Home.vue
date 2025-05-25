<template>
  <div class="container">
    <!-- Header -->
    <header class="header">
      <div class="logo-container">
        <router-link to="/" class="logo">
          <span class="logo-brackets">{ </span>
          <span class="logo-text">BLODGED</span>
          <span class="logo-brackets"> }</span>
        </router-link>
      </div>
      <nav class="header-nav">
        <router-link to="/profile" class="nav-item" :class="{ active: activeTab === 'profile' }" @click="activeTab = 'profile'">
          <svg class="nav-icon" xmlns="http://www.w3.org/2000/svg" height="20" viewBox="0 0 24 24" width="20">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
          </svg>
          PROFILE
        </router-link>
        <router-link to="/messages" class="nav-item" :class="{ active: activeTab === 'messages' }" @click="activeTab = 'messages'">
          <svg class="nav-icon" xmlns="http://www.w3.org/2000/svg" height="20" viewBox="0 0 24 24" width="20">
            <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z" fill="currentColor"/>
          </svg>
          MESSAGES
        </router-link>
        <router-link to="/notifications" class="nav-item" :class="{ active: activeTab === 'notifications' }" @click="activeTab = 'notifications'">
          <svg class="nav-icon" xmlns="http://www.w3.org/2000/svg" height="20" viewBox="0 0 24 24" width="20">
            <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" fill="currentColor"/>
          </svg>
          NOTIFICATIONS
        </router-link>
      </nav>
      <div class="user-menu">
        <button class="settings-btn" @click="toggleSettings" title="Settings">
          <svg class="settings-icon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
            <path d="M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.82,11.69,4.82,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z" fill="currentColor"/>
          </svg>
        </button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Compose Section -->
      <div class="compose-section">
        <textarea 
          v-model="newPostContent"
          class="compose-textarea" 
          placeholder="What's on your mind? Share your thoughts with the community..."
          @input="autoResize"
          ref="composeTextarea"
        ></textarea>
        <div class="compose-actions">
          <div class="compose-tip">💡 Tip: Use # for topics, @ to mention others</div>
          <button 
            class="post-btn"
            @click="createPost"
            :disabled="!newPostContent.trim() || creatingPost"
          >
            {{ creatingPost ? 'Posting...' : 'Post' }}
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <p>Loading posts...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="fetchPosts" class="retry-btn">Retry</button>
      </div>

      <!-- Feed -->
      <div v-else class="feed">
        <div v-if="posts.length === 0" class="no-posts">
          <h3>No posts available</h3>
          <p>Be the first to create a post!</p>
        </div>
        
        <article 
          v-for="post in posts" 
          :key="post.id" 
          class="post"
        >
          <div class="post-header">
            <div class="post-avatar" :style="{ background: post.author.avatarGradient }">
              {{ post.author.initials }}
            </div>
            <div class="post-info">
              <span class="post-username-wrapper">
                <span class="post-bracket">&lt;</span><span class="post-username">@{{ post.author.username }}</span><span class="post-bracket"> /&gt;</span>
              </span>
              <span class="post-timestamp">• 2h</span>
            </div>
            <div class="post-actions" v-if="userStore.isLoggedIn && post.author.username === userStore.user?.username">
              <button class="action-btn edit-btn" @click="editPost(post.id)" title="Edit post">
                <img src="../assets/images/edit.svg" alt="Edit" class="action-icon edit-icon" />
              </button>
              <button class="action-btn delete-btn" @click="deletePost(post.id)" title="Delete post">
                <img src="../assets/images/delete.svg" alt="Delete" class="action-icon delete-icon" />
              </button>
            </div>
          </div>
          <div class="post-content-wrapper">
            <span class="post-comment-marker">/*</span>
            <div class="post-content">{{ post.content }}</div>
            <span class="post-comment-marker">*/</span>
          </div>
          <div class="post-footer">
            <div class="post-stats">
              <button 
                class="like-btn"
                :class="{ liked: post.isLiked }"
                @click="toggleLike(post.id)"
              >
                <img src="../assets/images/thumbs-up.svg" alt="Like" class="like-icon" />
                <span class="like-count">{{ post.likes }}</span>
              </button>
            </div>
            <div class="post-replies-center">
              <button 
                class="replies-link"
                @click="showReplies(post.id)"
              >
                Show All Replies ({{ post.replies }})
              </button>
            </div>
            <div class="post-reply-action">
              <button 
                class="reply-btn"
                @click="showComments(post.id)"
              >
                REPLY
              </button>
              <button 
                class="share-btn"
                @click="sharePost(post.id)"
                title="Share post"
              >
                <svg class="share-icon" xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">                  <path d="M0 0h24v24H0V0z" fill="none"/>                  <path d="M16 5l-1.42 1.42-1.59-1.59V16h-1.98V4.83L9.42 6.42 8 5l4-4 4 4zm4 5v11c0 1.1-.9 2-2 2H6c-1.11 0-2-.9-2-2V10c0-1.11.89-2 2-2h3v2H6v11h12V10h-3V8h3c1.1 0 2 .89 2 2z" fill="currentColor"/>                </svg>
              </button>
            </div>
          </div>
        </article>
      </div>
    </main>

    <!-- Right Sidebar -->
    <aside class="right-sidebar">
      <div class="search-section">
        <input 
          v-model="searchQuery" 
          type="text" 
          class="search-input" 
          placeholder="Search BLODGER..."
          @input="handleSearch"
        >
      </div>
      
      <div class="recent-searches" v-if="recentSearches.length > 0">
        <h3 class="section-title">Recent Searches</h3>
        <a 
          v-for="search in recentSearches" 
          :key="search.id"
          href="#" 
          class="search-item"
          @click.prevent="removeSearch(search.id)"
        >
          × {{ search.query }}
        </a>
      </div>
      
      <div class="challenges-section">
        <h3 class="section-title">🔥 Daily Challenge</h3>
        <div class="challenge-item" @click="joinChallenge('daily')">
          <div class="challenge-icon">💡</div>
          <div>
            <div>Code Challenge</div>
            <div class="challenge-time">{{ dailyChallengeTimeLeft }}</div>
          </div>
        </div>
        
        <h3 class="section-title" style="margin-top: 20px;">⚡ Other Challenges</h3>
        <div 
          v-for="challenge in otherChallenges" 
          :key="challenge.id"
          class="challenge-item"
          @click="joinChallenge(challenge.id)"
        >
          <div class="challenge-icon">{{ challenge.icon }}</div>
          <div>{{ challenge.name }}</div>
        </div>
      </div>
      
      <!-- User Profile Section -->
      <div class="user-profile-section">
        <div class="user-profile-content">
          <div class="user-profile-left">
            <div class="user-profile-avatar" :style="{ background: String((userStore.user && 'avatarGradient' in userStore.user) ? userStore.user.avatarGradient : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)') }">
              <span class="profile-initial">{{ (userStore.user && 'initials' in userStore.user) ? userStore.user.initials : (userStore.user?.username?.charAt(0).toUpperCase() || 'U') }}</span>
            </div>
            <div class="user-profile-info">
              <span class="user-profile-name">@{{ userStore.user?.username || 'carterdanw' }}</span>
            </div>
          </div>
          <button class="logout-btn" @click="logout" title="Logout">
            <svg class="logout-icon" xmlns="http://www.w3.org/2000/svg" height="66" viewBox="0 0 24 24" width="66">
              <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
            </svg>
          </button>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useUserStore } from '../store/user'
import { getPostsApi, likePost, unlikePost, createPost as createPostApi, checkPostLike, getReplyCountForPost } from '../api'
import { useRouter } from 'vue-router'

// Types
interface User {
  id: string
  name: string
  username: string
  initials: string
  avatarGradient: string
}

interface Post {
  id: string
  content: string
  author: User
  createdAt: Date
  likes: number
  comments: number
  reposts: number
  replies: number
  isLiked: boolean
}

interface SearchItem {
  id: string
  query: string
}

interface Challenge {
  id: string
  name: string
  icon: string
}

// Get user store for authentication checks
const userStore = useUserStore()
const router = useRouter()

// Reactive state
const activeTab = ref<string>('feed')
const searchQuery = ref<string>('')
const newPostContent = ref<string>('')
const messageCount = ref<number>(5)
const notificationCount = ref<number>(11)
const userInitials = ref<string>(userStore.user?.username?.charAt(0).toUpperCase() || 'U')

// Loading and error states
const loading = ref<boolean>(false)
const error = ref<string>('')
const creatingPost = ref<boolean>(false)

const recentSearches = reactive<SearchItem[]>([
  { id: '1', query: 'JavaScript' },
  { id: '2', query: 'Python' },
  { id: '3', query: 'Web Development' }
])

const otherChallenges = reactive<Challenge[]>([
  { id: 'writing', name: 'Writing Sprint', icon: '📝' },
  { id: 'design', name: 'Design Challenge', icon: '🎨' }
])

const posts = reactive<Post[]>([])

// Computed properties
const dailyChallengeTimeLeft = computed(() => {
  const hoursLeft = 2
  return `${hoursLeft} hours left`
})

// Refs
const composeTextarea = ref<HTMLTextAreaElement>()

// Methods
const handleSearch = (event: Event) => {
  const target = event.target as HTMLInputElement
  console.log('Searching for:', target.value)
}

const removeSearch = (searchId: string) => {
  const index = recentSearches.findIndex(search => search.id === searchId)
  if (index > -1) {
    recentSearches.splice(index, 1)
  }
}

const joinChallenge = (challengeId: string) => {
  console.log('Joining challenge:', challengeId)
}

const autoResize = async () => {
  await nextTick()
  if (composeTextarea.value) {
    composeTextarea.value.style.height = 'auto'
    composeTextarea.value.style.height = composeTextarea.value.scrollHeight + 'px'
  }
}

const createPost = async () => {
  if (!newPostContent.value.trim() || creatingPost.value) return
  
  if (!userStore.isLoggedIn) {
    alert('Please log in to create posts')
    return
  }

  creatingPost.value = true

  try {
    // Create post via API
    const newPostApi = await createPostApi(newPostContent.value.trim())
    
    // Create local post object
    const newPost: Post = {
      id: String(newPostApi.id),
      content: newPostContent.value,
      author: {
        id: userStore.user?.username || 'currentUser',
        name: userStore.user?.username || 'You',
        username: userStore.user?.username || 'currentUser',
        initials: userInitials.value,
        avatarGradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      },
      createdAt: new Date(),
      likes: 0,
      comments: 0,
      reposts: 0,
      replies: 0,
      isLiked: false
    }
    
    posts.unshift(newPost)
    newPostContent.value = ''
    
    if (composeTextarea.value) {
      composeTextarea.value.style.height = 'auto'
    }
  } catch (error: any) {
    console.error('Error creating post:', error)
    alert('Failed to create post: ' + (error.message || 'Unknown error'))
  } finally {
    creatingPost.value = false
  }
}

const fetchPosts = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const apiPosts = await getPostsApi()
    
    // Clear existing posts
    posts.splice(0, posts.length)
    
    if (apiPosts && apiPosts.length > 0) {
      // Transform API posts to match our interface
      const transformedPosts = apiPosts.map((post: any) => ({
        id: post.id.toString(),
        content: post.postContent,
        author: {
          id: post.authorUsername,
          name: post.authorUsername,
          username: post.authorUsername,
          initials: post.authorUsername.charAt(0).toUpperCase(),
          avatarGradient: `linear-gradient(135deg, hsl(${Math.floor(Math.random() * 360)}, 70%, 60%) 0%, hsl(${Math.floor(Math.random() * 360)}, 70%, 50%) 100%)`
        },
        createdAt: new Date(post.createdAt || Date.now()),
        likes: post.likeCount || 0,
        comments: Math.floor(Math.random() * 20),
        reposts: Math.floor(Math.random() * 10),
        replies: post.replyCount || 0,
        isLiked: false
      }))
      
            // Sort by creation date (newest first)      transformedPosts.sort((a: Post, b: Post) => b.createdAt.getTime() - a.createdAt.getTime())
      posts.push(...transformedPosts)
      
      // Check like status for each post if user is logged in
      if (userStore.isLoggedIn) {
        for (const post of posts) {
          try {
            post.isLiked = await checkPostLike(parseInt(post.id))
          } catch (err) {
            console.error(`Error checking like status for post ${post.id}:`, err)
          }
        }
      }
    }
  } catch (err: any) {
    console.error('Error fetching posts:', err)
    error.value = err.message || 'Failed to load posts'
  } finally {
    loading.value = false
  }
}

const onPostHover = (postId: string, isHovering: boolean) => {
  console.log(`Post ${postId} hover:`, isHovering)
}

const toggleLike = async (postId: string) => {
  if (!userStore.isLoggedIn) {
    alert('Please log in to like posts')
    return
  }

  const post = posts.find(p => p.id === postId)
  if (post) {
    try {
      if (post.isLiked) {
        await unlikePost(parseInt(postId))
        post.likes--
      } else {
        await likePost(parseInt(postId))
        post.likes++
      }
      post.isLiked = !post.isLiked
    } catch (error) {
      console.error('Error toggling like:', error)
      // Fallback to local state change if API fails
      post.isLiked = !post.isLiked
      post.likes += post.isLiked ? 1 : -1
    }
  }
}

const showComments = (postId: string) => {
  console.log('Show comments for post:', postId)
}

const repost = (postId: string) => {
  const post = posts.find(p => p.id === postId)
  if (post) {
    post.reposts += 1
  }
  console.log('Reposted:', postId)
}

const sharePost = (postId: string) => {
  console.log('Share post:', postId)
}

const showReplies = (postId: string) => {
  console.log('Show replies for post:', postId)
}

const editPost = (postId: string) => {
  console.log('Edit post:', postId)
  // TODO: Implement edit functionality
  alert('Edit functionality will be implemented soon!')
}

const deletePost = async (postId: string) => {
  if (!userStore.isLoggedIn) {
    alert('Please log in to delete posts')
    return
  }
  
  if (confirm('Are you sure you want to delete this post? This action cannot be undone.')) {
    try {
      // TODO: Add API call to delete post
      // await deletePostApi(parseInt(postId))
      
      // Remove post from local state
      const index = posts.findIndex(p => p.id === postId)
      if (index > -1) {
        posts.splice(index, 1)
      }
      console.log('Post deleted:', postId)
    } catch (error) {
      console.error('Error deleting post:', error)
      alert('Failed to delete post')
    }
  }
}

const toggleSettings = () => {
  console.log('Toggle settings')
}

const logout = () => {
  if (confirm('Are you sure you want to logout?')) {
    userStore.logout()
    router.push('/login')
  }
}

const formatTimeAgo = (date: Date): string => {
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}m`
  } else if (hours < 24) {
    return `${hours}h`
  } else {
    return `${days}d`
  }
}

const formatPostContent = (content: string): string => {
  return content
    .replace(/\n/g, '<br>')
    .replace(/#(\w+)/g, '<span style="color: #00d4ff; cursor: pointer;">#$1</span>')
    .replace(/@(\w+)/g, '<span style="color: #00d4ff; cursor: pointer;">@$1</span>')
}

const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

// Lifecycle
onMounted(() => {
  console.log('BLODGED app mounted')
  fetchPosts()
})
</script>

<style>
/* Global styles - not scoped */
html, body {
  margin: 0;
  padding: 0;
  background: #282A36 !important;
  min-height: 100vh;
  width: 100vw;
  overflow: hidden;
}

#app {
  background: #282A36 !important;
  min-height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

/* Custom Scrollbar Styles */
/* Webkit browsers (Chrome, Safari, Edge) */
::-webkit-scrollbar {
  width: 24px;
  height: 24px;
}

::-webkit-scrollbar-button {
  display: none;
}

::-webkit-scrollbar-track {
  background: #44475A;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #7F89C5;
  border-radius: 8px;
  transition: background 0.2s;
  border: 3px solid #44475A;
  background-clip: padding-box;
}

::-webkit-scrollbar-thumb:hover {
  background: #6b75b0;
}

::-webkit-scrollbar-corner {
  background: #44475A;
}

/* Firefox */
* {
  scrollbar-width: thin;
  scrollbar-color: #7F89C5 #44475A;
}

/* Hide the default NavBar from App.vue when on home page */
.navbar {
  display: none !important;
}
</style>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  display: grid;
  grid-template-columns: 1fr 400px;
  height: 100vh;
  width: 100vw;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: #282A36;
  color: #e8eaed;
  line-height: 1.6;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
}

/* Header */
.header {
  grid-column: 1 / -1;
  background: #1E1F29;
  border-bottom: 1px solid #2d3748;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo {
  font-size: 46px;
  font-weight: 800;
  color: #00d4ff;
  text-decoration: none;
  display: flex;
  align-items: center;
}

.logo-brackets {
  color: #00d4ff;
  font-size: 50px;
  font-weight: 600;
  line-height: 1;
}

.logo-text {
  color: #ffffff;
  font-size: 46px;
  font-weight: 800;
  margin: 0 4px;
  border-bottom: 2px solid #00d4ff;
  padding-bottom: 0;
  line-height: 1;
  display: flex;
  align-items: center;
}

.header-nav {
  display: flex;
  gap: 32px;
  align-items: center;
}

.nav-item {
  color: #a0aec0;
  text-decoration: none;
  font-weight: 600;
  font-size: 40px;
  padding: 12px 20px;
  border-radius: 0;
  transition: all 0.3s;
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.5px;
  border-bottom: 2px solid transparent;
}

.nav-item:hover {
  color: #00d4ff;
  background: none;
  border-bottom: 2px solid #00d4ff;
}

.nav-item.active {
  color: #00d4ff;
  background: none;
  border-bottom: 2px solid #00d4ff;
}

.nav-icon {
  width: 46px;
  height: 46px;
  transition: all 0.2s;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
}

.settings-btn {
  background: #282A36;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-btn:hover {
  color: #BD93F9;
  background: rgba(189, 147, 249, 0.1);
  transform: translateY(-1px);
}

.settings-icon {
  width: 46px;
  height: 46px;
}

/* Main Content */
.main-content {
  padding: 24px;
  overflow-y: auto;
  height: calc(100vh - 76px);
  background: #282A36;
  margin-right: 16px;
}

.compose-section {
  background: #1E1F29;
  padding: 20px;
  margin-bottom: 24px;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.compose-section:focus-within {
  border-color: #00d4ff;
  box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.1);
}

.compose-textarea {
  width: 100%;
  background: transparent;
  border: none;
  color: #e8eaed;
  font-size: 16px;
  resize: none;
  height: 80px;
  margin-bottom: 16px;
  font-family: inherit;
}

.compose-textarea:focus {
  outline: none;
}

.compose-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.compose-tip {
  color: #718096;
  font-size: 14px;
}

.post-btn {
  background: #BD93F9;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 24px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.post-btn:hover:not(:disabled) {
  background: #A977E8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(189, 147, 249, 0.3);
}

.post-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-state, .error-state, .no-posts {
  background: #1E1F29;
  border: none;
  border-radius: 6px;
  padding: 24px;
  text-align: center;
  margin-bottom: 24px;
}

.retry-btn {
  background: #00d4ff;
  color: #0f1419;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 12px;
}

.feed {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.post {
  background: #1E1F29;
  border: none;
  border-radius: 6px;
  padding: 24px;
  transition: all 0.2s;
  cursor: pointer;
  word-wrap: break-word;
  overflow-wrap: break-word;
  max-width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.post:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 0;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.post-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-username-wrapper {
  display: inline;
}

.post-bracket {
  color: #00d4ff;
  font-size: 16px;
  font-weight: 500;
}

.post-username {
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
}

.post-timestamp {
  color: #718096;
  font-size: 14px;
  font-weight: 400;
}

.post-content-wrapper {
  margin-left: 52px;
  margin-bottom: 20px;
}

.post-comment-marker {
  color: #BD93F9;
  font-size: 16px;
  font-weight: 600;
  display: block;
  margin-bottom: 8px;
}

.post-comment-marker:last-child {
  margin-bottom: 0;
  margin-top: 8px;
  text-align: right;
  display: block;
}

.post-content {
  color: #cbd5e0;
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 0;
  margin-left: 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
  hyphens: auto;
  white-space: pre-wrap;
}

.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  margin-left: 52px;
  border-top: 1px solid #2d3748;
}

.post-stats {
  display: flex;
  align-items: center;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #718096;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.like-btn:hover {
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
}

.like-btn.liked {
  color: #00d4ff;
}

.like-icon {
  width: 36px;
  height: 36px;
  filter: brightness(0) saturate(100%) invert(100%) sepia(0%) saturate(0%) hue-rotate(0deg) brightness(100%) contrast(100%);
  transition: all 0.2s;
}

.like-btn.liked .like-icon {
  filter: brightness(0) saturate(100%) invert(62%) sepia(62%) saturate(7480%) hue-rotate(187deg) brightness(103%) contrast(106%);
}

.like-count {
  font-weight: 600;
}

.post-replies-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.replies-link {
  background: none;
  border: none;
  color: #00d4ff;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 14px;
  text-decoration: underline;
  transition: all 0.2s;
}

.replies-link:hover {
  background: rgba(0, 212, 255, 0.1);
}

.post-reply-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reply-btn {
  background: #BD93F9;
  color: #ffffff;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.reply-btn:hover {
  background: #A977E8;
  transform: translateY(-1px);
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 1;
  transition: opacity 0.2s;
}

.action-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.action-icon {
  width: 32px;
  height: 32px;
  filter: brightness(0) saturate(100%) invert(1);
  transition: filter 0.2s;
}

.edit-btn:hover .action-icon {
  filter: brightness(0) saturate(100%) invert(62%) sepia(62%) saturate(7480%) hue-rotate(187deg) brightness(103%) contrast(106%);
}

.delete-btn .action-icon {
  width: 36px;
  height: 36px;
}

.delete-btn:hover .action-icon {
  filter: brightness(0) saturate(100%) invert(23%) sepia(89%) saturate(7427%) hue-rotate(349deg) brightness(102%) contrast(107%);
}

/* Right Sidebar */
.right-sidebar {
  background: #1E1F29;
  border-left: 1px solid #2d3748;
  padding: 24px 24px 0 24px;
  overflow-y: auto;
  height: calc(100vh - 76px);
  position: relative;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #e8eaed;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-section {
  margin-bottom: 32px;
}

.search-input {  width: 100%;  background: #2d3748;  border: 1px solid #4a5568;  border-radius: 6px;  padding: 12px 16px;  color: #e8eaed;  font-size: 14px;  transition: all 0.2s;}

.search-input:focus {
  outline: none;
  border-color: #00d4ff;
  box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.1);
}

.recent-searches {
  margin-bottom: 32px;
}

.search-item {
  display: block;
  color: #a0aec0;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.2s;
  font-size: 14px;
  cursor: pointer;
}

.search-item:hover {
  background: #2d3748;
  color: #00d4ff;
}

.challenges-section {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.1) 0%, rgba(102, 126, 234, 0.1) 100%);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 24px;
}

.challenge-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 4px;
  margin-bottom: 8px;
  transition: all 0.2s;
  cursor: pointer;
}

.challenge-item:hover {
  background: rgba(0, 212, 255, 0.1);
  transform: translateY(-1px);
}

.challenge-icon {
  width: 24px;
  height: 24px;
  background: #00d4ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #0f1419;
  font-weight: 600;
}

.challenge-time {
  font-size: 12px;
  color: #718096;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .container {
    grid-template-columns: 1fr 350px;
  }
}

@media (max-width: 992px) {
  .container {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr;
  }
  
  .right-sidebar {
    display: none;
  }
  
  .header-nav {
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 8px 16px;
  }
  
  .header-nav {
    gap: 8px;
  }
  
  .nav-item {
    padding: 6px 12px;
    font-size: 14px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .logo {
    font-size: 20px;
  }
}

.share-btn {
  background: none;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.share-btn:hover {
  color: #BD93F9;
  background: rgba(189, 147, 249, 0.1);
  transform: translateY(-1px);
}

.share-icon {
  width: 36px;
  height: 36px;
}

/* User Profile Section */
.user-profile-section {
  position: fixed;
  bottom: 0;
  right: 0;
  left: unset;
  width: 400px;
  z-index: 10;
  margin-left: calc(100vw - 400px);
  padding: 12px 20px;
  background: #282A36;
  border-radius: 0;
  transition: all 0.2s;
  box-shadow: 0 -2px 16px rgba(0,0,0,0.25);
  border-top: 1px solid #2d3748;
  display: flex;
  align-items: center;
}

.user-profile-content {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  justify-content: space-between;
}

.user-profile-left {
  display: flex;
  align-items: center;
  gap: 16px;
  height: 100%;
}

.user-profile-avatar {
  width: 66px;
  height: 66px;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-initial {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  text-align: center;
}

.user-profile-info {
  flex: 1;
}

.user-profile-name {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
}

.logout-btn {
  background: none;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 0;
  border-radius: 8px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 66px;
  height: 66px;
  min-width: 66px;
  min-height: 66px;
}

.logout-icon {
  width: 66px;
  height: 66px;
  color: #ff5f56;
  fill: #ff5f56;
  transition: color 0.2s, fill 0.2s;
}
</style> 