<template>
  <div class="reply" :id="`reply-${reply.id}`">
    <div class="reply-header">
      <img 
        :src="`https://gravatar.com/avatar/${hashEmail(reply.replyAuthorUsername)}?d=mp&s=30`" 
        alt="Avatar" 
        class="reply-avatar"
      >
      <div class="reply-author">{{ reply.replyAuthorUsername }}</div>
    </div>
    
    <div v-if="isEditing" class="reply-edit-form">
      <textarea 
        v-model="editedContent" 
        class="reply-edit-input" 
        rows="3"
      ></textarea>
      <div class="reply-edit-actions">
        <button @click="saveEdit" class="btn btn-primary">Save</button>
        <button @click="cancelEdit" class="btn btn-secondary">Cancel</button>
      </div>
    </div>
    
    <div v-else class="reply-content" :id="`reply-content-${reply.id}`">
      {{ reply.replyContent }}
    </div>
    
    <div v-if="canModify && !isEditing" class="reply-actions">
      <button @click="startEdit" class="reply-action-btn edit-btn" title="Edit Reply">
        <img src="../assets/images/edit.png" alt="Edit" class="action-img" />
      </button>
      <button @click="deleteReplyHandler" class="reply-action-btn delete-btn" title="Delete Reply">
        <img src="../assets/images/delete.png" alt="Delete" class="action-img" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '../store/user'
import { editReply, deleteReply } from '../api'
import type { ReplyMetadata } from '../api'
import md5 from 'md5'

const props = defineProps<{
  reply: ReplyMetadata
}>()

const emit = defineEmits<{
  (e: 'update', id: number, content: string): void
  (e: 'delete', id: number): void
}>()

const userStore = useUserStore()
const isEditing = ref(false)
const editedContent = ref(props.reply.replyContent)

// Check if the current user can modify this reply
const canModify = computed(() => {
  return userStore.isLoggedIn && userStore.user?.username === props.reply.replyAuthorUsername
})

function hashEmail(email: string): string {
  return md5(email.trim().toLowerCase())
}

function startEdit() {
  isEditing.value = true
  editedContent.value = props.reply.replyContent
}

function cancelEdit() {
  isEditing.value = false
}

async function saveEdit() {
  try {
    await editReply(props.reply.id, editedContent.value)
    isEditing.value = false
    emit('update', props.reply.id, editedContent.value)
  } catch (error) {
    console.error('Error saving reply edit:', error)
  }
}

async function deleteReplyHandler() {
  if (confirm('Are you sure you want to delete this reply?')) {
    try {
      await deleteReply(props.reply.id)
      emit('delete', props.reply.id)
    } catch (error) {
      console.error('Error deleting reply:', error)
    }
  }
}
</script>

<style scoped>
.reply {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  border-left: 3px solid #e0e0e0;
}

.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
  gap: 0.5rem;
}

.reply-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-author {
  font-weight: 500;
  font-size: 0.95rem;
  color: #42b983;
}

.reply-content {
  color: #333;
  font-size: 0.95rem;
  margin-bottom: 0.5rem;
  white-space: pre-line;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.reply-action-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.reply-action-btn:hover {
  background-color: #f0f0f0;
}

.action-img {
  width: 18px;
  height: 18px;
}

.delete-btn:hover {
  background-color: #fee2e2;
}

.reply-edit-form {
  margin-bottom: 0.75rem;
}

.reply-edit-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  resize: vertical;
  font-family: inherit;
}

.reply-edit-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.btn {
  padding: 0.35rem 0.75rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn-primary {
  background-color: #42b983;
  color: white;
}

.btn-secondary {
  background-color: #e0e0e0;
  color: #333;
}
</style> 