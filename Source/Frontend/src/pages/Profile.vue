<template>
  <div class="profile-container">
    <div class="profile-content" v-if="user">
      <!-- User Profile Header -->
      <div class="profile-header">
        <div class="profile-avatar">
          <img :src="`https://gravatar.com/avatar/${hashEmail(user?.email)}?d=mp`" alt="Profile Picture">
        </div>
        <div class="profile-info">
          <h1 class="profile-username">{{ user.username }}</h1>
          <div class="profile-stats">
            <div class="stat">
              <span class="stat-count">{{ followersCount }}</span>
              <span class="stat-label">Followers</span>
            </div>
            <div class="stat">
              <span class="stat-count">{{ followingCount }}</span>
              <span class="stat-label">Following</span>
            </div>
          </div>
          <div class="profile-actions" v-if="user.id !== currentUserId">
            <button 
              class="btn" 
              :class="{ 'btn-primary': !isFollowing, 'btn-secondary': isFollowing }"
              @click="toggleFollow"
            >
              {{ isFollowing ? 'Following' : 'Follow' }}
            </button>
          </div>
          <div class="profile-actions" v-else>
            <router-link to="/settings" class="btn btn-primary">Edit Profile</router-link>
          </div>
        </div>
      </div>
      
      <!-- User Bio Card -->
      <div class="bio-card">
        <div class="bio-header">About Me</div>
        <div class="bio-content">
          <p v-if="user.bio">{{ user.bio }}</p>
          <p class="empty-bio" v-else>This user hasn't written a bio!</p>
        </div>
        <div class="bio-footer">
          Member since {{ formatDate(user.createdDate || new Date()) }}
        </div>
      </div>
      
      <!-- User Posts -->
      <div class="posts-container">
        <h2 class="posts-title">Posts</h2>
        <div v-if="loading" class="loading-posts">Loading posts...</div>
        <div v-else-if="posts.length === 0" class="no-posts">
          This user hasn't posted anything yet.
        </div>
        <div v-else class="posts-list">
          <div v-for="post in posts" :key="post.id" class="post-card">
            <div class="post-header">
              <img :src="`https://gravatar.com/avatar/${hashEmail(user?.email)}?d=mp&s=40`" alt="Avatar" class="post-avatar">
              <div class="post-author">{{ user.username }}</div>
            </div>
            <div class="post-content">{{ post.postContent }}</div>
            <div class="post-footer">
              <button 
                class="like-button" 
                :class="{ 'liked': post.liked }"
                @click="toggleLike(post)"
              >
                <img src="../assets/images/like.webp" alt="Like" class="like-img" />
                <span class="like-count">{{ post.likeCount }}</span>
              </button>
              <router-link :to="`/posts/view/${post.id}`" class="view-replies-link">
                View all replies
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="profile-loading">
      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-else>Loading profile...</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { 
  getUserByUsername, 
  getPostsByUsername, 
  getPostLikes, 
  getFollowedByUsername, 
  getFollowingByUsername,
  followUser,
  unfollowUser,
  likePost,
  unlikePost,
  checkPostLike
} from '../api'
import type { UserProfile, PostMetadata } from '../api'
import md5 from 'md5'

interface Post extends PostMetadata {
  liked: boolean;
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// State
const user = ref<UserProfile | null>(null)
const posts = ref<Post[]>([])
const loading = ref(true)
const error = ref('')
const isFollowing = ref(false)
const followersCount = ref(0)
const followingCount = ref(0)

// Computed
const currentUserId = computed(() => userStore.user?.id || 0)

// Fetch user data
async function fetchUserData() {
  const username = route.params.username as string
  
  loading.value = true
  error.value = ''
  
  if (!username) {
    // If no username is provided in the route, use the current user's username
    if (userStore.user) {
      router.replace(`/profile/${userStore.user.username}`)
      return
    } else {
      error.value = 'No username provided'
      loading.value = false
      return
    }
  }
  
  try {
    // Fetch user data by username
    try {
      user.value = await getUserByUsername(username)
    } catch (err: any) {
      console.error('Error fetching user data:', err)
      
      // If we're viewing the current user's profile, use the data from userStore
      if (userStore.isLoggedIn && username === userStore.user?.username) {
        user.value = {
          id: userStore.user.id,
          username: userStore.user.username,
          email: userStore.user.email,
          bio: null,
          createdDate: new Date().toISOString()
        }
      } else {
        throw err
      }
    }
    
    // Fetch user's posts
    try {
      const postsData = await getPostsByUsername(username)
      
      // Process posts data
      posts.value = postsData.map((post: PostMetadata) => ({
        ...post,
        liked: false,
        likeCount: post.likeCount || 0 // Ensure likeCount exists
      }))
    } catch (err) {
      console.error('Error fetching posts:', err)
      posts.value = []
    }
    
    // If logged in, fetch like status for posts
    if (userStore.isLoggedIn && userStore.user && posts.value.length > 0) {
      for (const post of posts.value) {
        try {
          post.liked = await checkPostLike(post.id)
        } catch (err) {
          console.error(`Error checking like status for post ${post.id}:`, err)
        }
      }
    }
    
    // Fetch follow counts
    try {
      const followers = await getFollowedByUsername(username)
      followersCount.value = followers.length
      
      // Check if current user is following this profile
      if (userStore.isLoggedIn && userStore.user && followers.includes(userStore.user.username)) {
        isFollowing.value = true
      }
      
      const following = await getFollowingByUsername(username)
      followingCount.value = following.length
    } catch (err) {
      console.error('Error fetching follow data:', err)
      followersCount.value = 0
      followingCount.value = 0
    }
    
  } catch (err: any) {
    console.error('Error loading profile:', err)
    error.value = err.message || 'Failed to load profile'
    user.value = null
  } finally {
    loading.value = false
  }
}

// Helper function to get user ID from username
async function getUserIdByUsername(username: string): Promise<number | null> {
  try {
    const response = await fetch(`/api/accounts/user/${username}`, {
      headers: { 'Authorization': userStore.token ? `Bearer ${userStore.token}` : '' }
    })
    
    if (!response.ok) {
      throw new Error('Failed to get user data')
    }
    
    const userData = await response.json()
    return userData.id
  } catch (err) {
    console.error('Error getting user ID:', err)
    return null
  }
}

// Follow/unfollow user
async function toggleFollow() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  const username = route.params.username as string
  const userId = user.value?.id
  
  if (!userId) {
    error.value = 'Failed to find user ID'
    return
  }
  
  try {
    if (isFollowing.value) {
      await unfollowUser(userId)
    } else {
      await followUser(userId)
    }
    
    isFollowing.value = !isFollowing.value
    followersCount.value = isFollowing.value 
      ? followersCount.value + 1 
      : followersCount.value - 1
  } catch (err: any) {
    console.error('Follow error:', err)
    error.value = err.message || 'Failed to update follow status'
  }
}

// Like/unlike a post
async function toggleLike(post: Post) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  try {
    // First, verify current like status
    const currentLikeStatus = await checkPostLike(post.id)
    
    // If the display state doesn't match the actual state, fix it
    if (post.liked !== currentLikeStatus) {
      console.log('Fixing inconsistent like state')
      post.liked = currentLikeStatus
      // Refresh user data to get accurate like count
      await fetchUserData()
      return
    }
    
    if (post.liked) {
      await unlikePost(post.id)
    } else {
      await likePost(post.id)
    }
    
    post.liked = !post.liked
    post.likeCount = post.liked 
      ? post.likeCount + 1 
      : post.likeCount - 1
      
    // Re-check like status from server to ensure consistency
    setTimeout(async () => {
      if (post && post.id) {
        post.liked = await checkPostLike(post.id)
      }
    }, 500)
  } catch (err: any) {
    console.error('Like error:', err)
    // Refresh user data on error to ensure UI is consistent
    await fetchUserData()
  }
}

// Helper functions
function hashEmail(email: string | undefined) {
  if (!email) {
    return 'default-avatar-hash'; // Return a default hash if email is undefined
  }
  return md5(email.trim().toLowerCase())
}

function formatDate(dateString: string | Date) {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Load data on component mount
onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.profile-avatar img {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.profile-info {
  flex: 1;
}

.profile-username {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #333;
}

.profile-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-count {
  font-weight: bold;
  font-size: 1.2rem;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.profile-actions {
  margin-top: 1rem;
}

.btn {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #42b983;
  color: white;
}

.btn-primary:hover {
  background-color: #3aa876;
}

.btn-secondary {
  background-color: #e0e0e0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #d0d0d0;
}

.bio-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.bio-header {
  background-color: #f5f5f5;
  padding: 1rem;
  font-weight: bold;
  border-bottom: 1px solid #e0e0e0;
}

.bio-content {
  padding: 1.5rem;
  min-height: 80px;
}

.empty-bio {
  color: #999;
  font-style: italic;
}

.bio-footer {
  padding: 1rem;
  color: #666;
  font-size: 0.9rem;
  border-top: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.posts-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.posts-title {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.post-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 1.5rem;
}

.post-header {
  display: flex;
  align-items: center;
  padding: 1rem;
  gap: 0.75rem;
  border-bottom: 1px solid #f0f0f0;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.post-author {
  font-weight: 500;
}

.post-content {
  padding: 1.5rem;
  white-space: pre-line;
}

.post-footer {
  padding: 0.75rem 1.5rem;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-start;
}

.like-button {
  background: none;
  border: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.like-button:hover {
  background-color: #f0f0f0;
}

.like-img {
  width: 20px;
  height: 25px;
  transition: all 0.2s;
}

.liked .like-img {
  filter: brightness(0) saturate(100%) invert(62%) sepia(62%) saturate(7480%) hue-rotate(187deg) brightness(103%) contrast(106%);
}

.liked {
  color: #e53935;
  font-weight: bold;
}

.like-count {
  font-size: 0.9rem;
  color: #666;
}

.view-replies-link {
  margin-left: auto;
  color: #6366F1;
  text-decoration: none;
  font-size: 0.9rem;
  padding: 0.5rem;
}

.view-replies-link:hover {
  text-decoration: underline;
}

.no-posts, .loading-posts {
  text-align: center;
  padding: 2rem;
  color: #666;
  background: white;
  border-radius: 8px;
  margin-top: 1rem;
}

.profile-loading, .error-message {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.error-message {
  color: #e53935;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .profile-stats {
    justify-content: center;
  }
}
</style> 