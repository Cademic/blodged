<template>
  <div class="replies-section">
    <h3 class="replies-title">
      {{ replies.length > 0 ? `Replies (${replies.length})` : 'No replies yet' }}
    </h3>
    
    <div v-if="isLoggedIn" class="reply-form">
      <textarea 
        v-model="newReplyContent" 
        placeholder="Write a reply..." 
        class="reply-input"
        :disabled="isSubmitting"
        rows="3"
      ></textarea>
      <div class="reply-form-actions">
        <button 
          @click="submitReply" 
          class="reply-submit-btn" 
          :disabled="isSubmitting || !newReplyContent.trim()"
        >
          {{ isSubmitting ? 'Submitting...' : 'Submit Reply' }}
        </button>
      </div>
    </div>
    <div v-else class="login-to-reply">
      <router-link to="/login">Log in to reply</router-link>
    </div>
    
    <div v-if="loading" class="loading-replies">
      Loading replies...
    </div>
    
    <div v-else-if="error" class="error-message">
      {{ error }}
      <button @click="fetchReplies" class="retry-btn">Retry</button>
    </div>
    
    <div v-else class="replies-list">
      <Reply 
        v-for="reply in replies" 
        :key="reply.id" 
        :reply="reply"
        @update="handleReplyUpdate"
        @delete="handleReplyDelete"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { getRepliesForPost, createReply } from '../api'
import type { ReplyMetadata } from '../api'
import Reply from './Reply.vue'

const props = defineProps<{
  postId: number
}>()

const userStore = useUserStore()
const isLoggedIn = userStore.isLoggedIn
const replies = ref<ReplyMetadata[]>([])
const loading = ref(true)
const error = ref('')
const newReplyContent = ref('')
const isSubmitting = ref(false)

onMounted(() => {
  fetchReplies()
})

async function fetchReplies() {
  loading.value = true
  error.value = ''
  
  try {
    replies.value = await getRepliesForPost(props.postId)
  } catch (e: any) {
    error.value = e.message || 'Failed to load replies'
  } finally {
    loading.value = false
  }
}

async function submitReply() {
  if (!newReplyContent.value.trim() || isSubmitting.value) return
  
  isSubmitting.value = true
  
  try {
    await createReply(props.postId, newReplyContent.value.trim())
    
    // Clear the input immediately
    newReplyContent.value = ''
    
    // Wait a moment before refreshing replies to let backend catch up
    setTimeout(async () => {
      try {
        // Refresh replies to include the new one
        await fetchReplies()
      } catch (refreshError) {
        console.error('Error refreshing replies:', refreshError)
        // Add the new reply locally if we can't refresh from server
        const username = userStore.user?.username || 'You'
        replies.value.unshift({
          id: Date.now(), // Temporary ID
          replyContent: newReplyContent.value,
          replyAuthorUsername: username,
          postId: props.postId
        })
      }
      isSubmitting.value = false
    }, 500)
  } catch (e: any) {
    // Check if error contains specific database exception
    let errorMsg = 'Failed to submit reply'
    let shouldRefreshAfterDelay = false
    
    // Check if it's the known database error about incorrect result size
    if (e.message && e.message.includes('IncorrectResultSizeDataAccessException')) {
      errorMsg = 'The reply was created, but there was a database issue loading the updated post. Try refreshing the page.'
      shouldRefreshAfterDelay = true
    } 
    // Check if it's a Thymeleaf template parsing error
    else if (e.message && e.message.includes('TemplateInputException') || e.message.includes('template parsing')) {
      errorMsg = 'Your reply was created, but there was a display issue. Click retry to see your reply.'
      shouldRefreshAfterDelay = true
    } else {
      errorMsg = e.message || 'Failed to submit reply'
    }
    
    // Clear the input as the reply was likely created
    if (shouldRefreshAfterDelay) {
      newReplyContent.value = ''
      
      // Try to fetch replies again after a delay
      setTimeout(() => {
        fetchReplies()
      }, 1000)
    }
    
    error.value = errorMsg
    isSubmitting.value = false
  }
}

function handleReplyUpdate(id: number, content: string) {
  // Update the reply in the local array
  const replyIndex = replies.value.findIndex(reply => reply.id === id)
  if (replyIndex !== -1) {
    replies.value[replyIndex].replyContent = content
  }
}

function handleReplyDelete(id: number) {
  // Remove the reply from the local array
  replies.value = replies.value.filter(reply => reply.id !== id)
}
</script>

<style scoped>
.replies-section {
  margin-top: 2rem;
}

.replies-title {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: #333;
  font-weight: 500;
}

.reply-form {
  margin-bottom: 1.5rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.reply-input {
  width: 100%;
  padding: 1rem;
  border: none;
  resize: vertical;
  font-family: inherit;
  font-size: 0.95rem;
  min-height: 60px;
}

.reply-input:focus {
  outline: none;
}

.reply-form-actions {
  display: flex;
  justify-content: flex-end;
  padding: 0.75rem;
  background-color: #f5f5f5;
}

.reply-submit-btn {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background-color 0.2s;
}

.reply-submit-btn:hover:not(:disabled) {
  background-color: #3aa876;
}

.reply-submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.login-to-reply {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
}

.login-to-reply a {
  color: #6366F1;
  text-decoration: none;
}

.login-to-reply a:hover {
  text-decoration: underline;
}

.loading-replies,
.error-message {
  padding: 1rem;
  text-align: center;
  color: #666;
}

.error-message {
  color: #e53935;
}

.retry-btn {
  background-color: #6366F1;
  color: white;
  border: none;
  padding: 0.35rem 0.75rem;
  border-radius: 4px;
  margin-left: 0.5rem;
  cursor: pointer;
  font-size: 0.85rem;
}

.replies-list {
  margin-top: 1rem;
}
</style> 