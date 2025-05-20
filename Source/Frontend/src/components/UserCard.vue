<template>
  <div class="user-card">
    <div class="card-body">
      <img :src="`https://gravatar.com/avatar/${hashEmail(user.email)}?d=mp&s=60`" alt="Profile Picture" class="user-avatar">
      <p class="user-name">
        <router-link :to="`/profile/${user.username}`">{{ user.username }}</router-link>
      </p>
      <button 
        v-if="showFollowButton && currentUserId !== user.id"
        class="follow-button" 
        :class="{ 'following': isFollowing }"
        @click="toggleFollow"
        @mouseenter="handleMouseEnter"
        @mouseleave="handleMouseLeave"
      >
        {{ buttonText }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { followUser, unfollowUser } from '../api';
import md5 from 'md5';
import { useUserStore } from '../store/user';

const props = defineProps<{
  user: {
    id: number;
    username: string;
    email?: string;
  };
  isFollowing: boolean;
  showFollowButton?: boolean;
}>();

const emit = defineEmits(['follow-changed']);

const userStore = useUserStore();
const currentUserId = computed(() => userStore.user?.id || 0);
const isFollowing = ref(props.isFollowing);
const buttonText = ref(props.isFollowing ? 'Following' : 'Follow');

function hashEmail(email: string | undefined) {
  if (!email) {
    return 'default-avatar-hash';
  }
  return md5(email.trim().toLowerCase());
}

function handleMouseEnter() {
  if (isFollowing.value) {
    buttonText.value = 'Unfollow';
  }
}

function handleMouseLeave() {
  if (isFollowing.value) {
    buttonText.value = 'Following';
  }
}

async function toggleFollow() {
  if (!userStore.isLoggedIn) {
    return;
  }
  
  try {
    if (isFollowing.value) {
      await unfollowUser(props.user.id);
    } else {
      await followUser(props.user.id);
    }
    
    isFollowing.value = !isFollowing.value;
    buttonText.value = isFollowing.value ? 'Following' : 'Follow';
    
    // Emit event to parent component
    emit('follow-changed', {
      userId: props.user.id,
      isFollowing: isFollowing.value
    });
  } catch (err) {
    console.error('Failed to toggle follow status:', err);
  }
}
</script>

<style scoped>
.user-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 1rem;
  padding: 1.5rem;
  text-align: center;
}

.card-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.user-name {
  margin: 0;
  font-weight: 500;
}

.user-name a {
  color: #42b983;
  text-decoration: none;
}

.user-name a:hover {
  text-decoration: underline;
}

.follow-button {
  padding: 0.5rem 1.25rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  border: none;
  transition: all 0.2s;
  background-color: #42b983;
  color: white;
}

.follow-button:hover {
  background-color: #3aa876;
}

.follow-button.following {
  background-color: #e0e0e0;
  color: #333;
}

.follow-button.following:hover {
  background-color: #f44336;
  color: white;
}
</style> 