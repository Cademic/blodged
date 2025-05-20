<template>
  <div class="preferences-settings">
    <h2>Preferences</h2>
    <p class="settings-description">Customize your experience with appearance and notification settings.</p>
    
    <div class="settings-section">
      <h3 class="section-title">Appearance</h3>
      
      <div class="option-card">
        <h4 class="option-title">Theme Mode</h4>
        <div class="theme-options">
          <div 
            class="theme-option" 
            :class="{ active: selectedTheme === 'light' }"
            @click="updateTheme('light')"
          >
            <div class="theme-preview light-theme"></div>
            <div class="theme-label">Light</div>
          </div>
          
          <div 
            class="theme-option" 
            :class="{ active: selectedTheme === 'dark' }"
            @click="updateTheme('dark')"
          >
            <div class="theme-preview dark-theme"></div>
            <div class="theme-label">Dark</div>
          </div>
          
          <div 
            class="theme-option" 
            :class="{ active: selectedTheme === 'system' }"
            @click="updateTheme('system')"
          >
            <div class="theme-preview system-theme"></div>
            <div class="theme-label">System</div>
          </div>
        </div>
      </div>
      
      <div class="option-card">
        <h4 class="option-title">Code Theme</h4>
        <div class="code-theme-options">
          <select v-model="selectedCodeTheme" class="code-theme-select" @change="updateCodeTheme">
            <option value="default">Default</option>
            <option value="monokai">Monokai</option>
            <option value="github">GitHub</option>
            <option value="dracula">Dracula</option>
            <option value="solarized">Solarized</option>
          </select>
          
          <div class="code-preview">
            <pre><code>// Example code preview
function greetUser(name) {
  console.log(`Hello, ${name}!`);
  return true;
}</code></pre>
          </div>
        </div>
      </div>
    </div>
    
    <div class="settings-section">
      <h3 class="section-title">Notifications</h3>
      
      <div class="notification-option">
        <div class="notification-content">
          <div class="notification-title">New Post Comments</div>
          <div class="notification-description">
            Get notified when someone comments on your posts.
          </div>
        </div>
        <div class="notification-controls">
          <label class="toggle-switch">
            <input type="checkbox" v-model="notificationSettings.comments" @change="updateNotificationSettings">
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>
      
      <div class="notification-option">
        <div class="notification-content">
          <div class="notification-title">New Likes</div>
          <div class="notification-description">
            Get notified when someone likes your posts.
          </div>
        </div>
        <div class="notification-controls">
          <label class="toggle-switch">
            <input type="checkbox" v-model="notificationSettings.likes" @change="updateNotificationSettings">
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>
      
      <div class="notification-option">
        <div class="notification-content">
          <div class="notification-title">New Followers</div>
          <div class="notification-description">
            Get notified when someone follows you.
          </div>
        </div>
        <div class="notification-controls">
          <label class="toggle-switch">
            <input type="checkbox" v-model="notificationSettings.followers" @change="updateNotificationSettings">
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>
      
      <div class="notification-option">
        <div class="notification-content">
          <div class="notification-title">Direct Messages</div>
          <div class="notification-description">
            Get notified when you receive a new message.
          </div>
        </div>
        <div class="notification-controls">
          <label class="toggle-switch">
            <input type="checkbox" v-model="notificationSettings.messages" @change="updateNotificationSettings">
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>
    </div>
    
    <div class="settings-section">
      <h3 class="section-title">Content Preferences</h3>
      
      <div class="option-card">
        <h4 class="option-title">Interests</h4>
        <p class="option-description">
          Select topics you're interested in to personalize your feed.
        </p>
        
        <div class="tags-container">
          <div 
            v-for="tag in availableTags" 
            :key="tag.id"
            class="interest-tag"
            :class="{ active: selectedTags.includes(tag.id) }"
            @click="toggleTag(tag.id)"
          >
            {{ tag.name }}
          </div>
        </div>
        
        <div class="tags-actions">
          <button 
            type="button" 
            class="tags-action-btn" 
            @click="saveInterests"
            :disabled="updatingInterests || !selectedTagsChanged"
          >
            {{ updatingInterests ? 'Saving...' : 'Save Interests' }}
          </button>
        </div>
      </div>
    </div>
    
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { UserProfile } from '../../api'

// Define props
const props = defineProps<{
  user: UserProfile | null
}>()

// Define emits
const emit = defineEmits<{
  (e: 'update', field: string, value: string): void
}>()

// Theme state
const selectedTheme = ref('system') // light, dark, system
const selectedCodeTheme = ref('default')

// Tags/interests state
const availableTags = ref([
  { id: 1, name: 'JavaScript' },
  { id: 2, name: 'Python' },
  { id: 3, name: 'React' },
  { id: 4, name: 'Vue' },
  { id: 5, name: 'Angular' },
  { id: 6, name: 'Node.js' },
  { id: 7, name: 'PHP' },
  { id: 8, name: 'Java' },
  { id: 9, name: 'C#' },
  { id: 10, name: 'Ruby' },
  { id: 11, name: 'Go' },
  { id: 12, name: 'Rust' },
  { id: 13, name: 'TypeScript' },
  { id: 14, name: 'Swift' },
  { id: 15, name: 'Kotlin' }
])

// Initial selected tags - would be loaded from user preferences
const initialSelectedTags = ref([1, 4, 6, 13])
const selectedTags = ref([...initialSelectedTags.value])

// Computed to check if tags changed
const selectedTagsChanged = computed(() => {
  if (selectedTags.value.length !== initialSelectedTags.value.length) return true
  
  const sortedInitial = [...initialSelectedTags.value].sort()
  const sortedCurrent = [...selectedTags.value].sort()
  
  for (let i = 0; i < sortedInitial.length; i++) {
    if (sortedInitial[i] !== sortedCurrent[i]) return true
  }
  
  return false
})

// Notification settings
const notificationSettings = ref({
  comments: true,
  likes: true,
  followers: true,
  messages: true
})

// UI state
const updatingTheme = ref(false)
const updatingCodeTheme = ref(false)
const updatingNotifications = ref(false)
const updatingInterests = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// Update theme setting
async function updateTheme(theme: 'light' | 'dark' | 'system') {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (selectedTheme.value === theme) return
  
  updatingTheme.value = true
  selectedTheme.value = theme
  
  try {
    // In a real app, you would call an API endpoint here
    // For simulation, we'll use a timeout
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // Update actual theme (would be implemented with CSS variables or a theme system)
    const htmlEl = document.documentElement
    if (theme === 'light') {
      htmlEl.classList.remove('dark-theme')
      htmlEl.classList.add('light-theme')
    } else if (theme === 'dark') {
      htmlEl.classList.remove('light-theme')
      htmlEl.classList.add('dark-theme')
    } else {
      // System - would check OS preference here
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      htmlEl.classList.remove(prefersDark ? 'light-theme' : 'dark-theme')
      htmlEl.classList.add(prefersDark ? 'dark-theme' : 'light-theme')
    }
    
    // Show success message
    successMessage.value = 'Theme updated successfully.'
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('Error updating theme:', error)
    errorMessage.value = 'Failed to update theme. Please try again.'
    selectedTheme.value = 'system' // Reset to default on error
  } finally {
    updatingTheme.value = false
  }
}

// Update code editor theme
async function updateCodeTheme() {
  errorMessage.value = ''
  successMessage.value = ''
  
  updatingCodeTheme.value = true
  
  try {
    // In a real app, you would call an API endpoint here
    // For simulation, we'll use a timeout
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // Update code preview - in a real app, this would be done via CSS
    const codePreview = document.querySelector('.code-preview')
    if (codePreview) {
      codePreview.className = 'code-preview ' + selectedCodeTheme.value
    }
    
    // Show success message
    successMessage.value = 'Code theme updated successfully.'
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('Error updating code theme:', error)
    errorMessage.value = 'Failed to update code theme. Please try again.'
  } finally {
    updatingCodeTheme.value = false
  }
}

// Update notification settings
async function updateNotificationSettings() {
  errorMessage.value = ''
  successMessage.value = ''
  
  updatingNotifications.value = true
  
  try {
    // In a real app, you would call an API endpoint here
    // For simulation, we'll use a timeout
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // Show success message
    successMessage.value = 'Notification preferences updated successfully.'
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('Error updating notification settings:', error)
    errorMessage.value = 'Failed to update notification settings. Please try again.'
  } finally {
    updatingNotifications.value = false
  }
}

// Toggle tag selection
function toggleTag(tagId: number) {
  const index = selectedTags.value.indexOf(tagId)
  if (index === -1) {
    selectedTags.value.push(tagId)
  } else {
    selectedTags.value.splice(index, 1)
  }
}

// Save interests/tags
async function saveInterests() {
  errorMessage.value = ''
  successMessage.value = ''
  
  updatingInterests.value = true
  
  try {
    // In a real app, you would call an API endpoint here
    // For simulation, we'll use a timeout
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // Update initial selection to match current
    initialSelectedTags.value = [...selectedTags.value]
    
    // Show success message
    successMessage.value = 'Your interests have been updated successfully.'
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('Error updating interests:', error)
    errorMessage.value = 'Failed to update interests. Please try again.'
  } finally {
    updatingInterests.value = false
  }
}
</script>

<style scoped>
.preferences-settings {
  max-width: 800px;
}

h2 {
  margin-bottom: 0.5rem;
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--heading-color, #2d3748);
}

.settings-description {
  margin-bottom: 2rem;
  color: var(--text-light, #718096);
}

.settings-section {
  margin-bottom: 2.5rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid var(--border-color, #e2e8f0);
}

.settings-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1.25rem;
  color: var(--heading-color, #2d3748);
}

.option-card {
  background-color: var(--card-bg, #f9fafb);
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.option-card:last-child {
  margin-bottom: 0;
}

.option-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--heading-color, #2d3748);
}

.option-description {
  font-size: 0.875rem;
  color: var(--text-light, #718096);
  margin-bottom: 1rem;
}

/* Theme options */
.theme-options {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.theme-option {
  cursor: pointer;
  width: 100px;
  text-align: center;
  transition: transform 0.2s;
}

.theme-option:hover {
  transform: translateY(-2px);
}

.theme-option.active .theme-preview {
  border: 2px solid var(--primary-color, #3182ce);
}

.theme-preview {
  height: 60px;
  border-radius: 0.375rem;
  margin-bottom: 0.5rem;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.light-theme {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
}

.dark-theme {
  background-color: #1a202c;
  border: 1px solid #4a5568;
}

.system-theme {
  background: linear-gradient(to right, #ffffff 50%, #1a202c 50%);
  border: 1px solid #e2e8f0;
}

.theme-label {
  font-size: 0.875rem;
  color: var(--text-color, #4a5568);
}

/* Code theme options */
.code-theme-options {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.code-theme-select {
  padding: 0.5rem;
  border: 1px solid var(--border-color, #e2e8f0);
  border-radius: 0.375rem;
  font-size: 0.875rem;
  width: 100%;
  max-width: 200px;
}

.code-preview {
  background-color: #1a202c;
  color: #f7fafc;
  border-radius: 0.375rem;
  padding: 1rem;
  font-family: monospace;
  font-size: 0.875rem;
  overflow-x: auto;
}

.code-preview pre {
  margin: 0;
}

/* Code theme variations - would be more extensive in a real app */
.code-preview.monokai {
  background-color: #272822;
  color: #f8f8f2;
}

.code-preview.github {
  background-color: #ffffff;
  color: #24292e;
  border: 1px solid #e1e4e8;
}

.code-preview.dracula {
  background-color: #282a36;
  color: #f8f8f2;
}

.code-preview.solarized {
  background-color: #fdf6e3;
  color: #657b83;
}

/* Notification options */
.notification-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid var(--border-color, #e2e8f0);
}

.notification-option:last-child {
  border-bottom: none;
}

.notification-content {
  max-width: 80%;
}

.notification-title {
  font-weight: 500;
  font-size: 0.9375rem;
  margin-bottom: 0.25rem;
  color: var(--text-color, #4a5568);
}

.notification-description {
  font-size: 0.8125rem;
  color: var(--text-light, #718096);
}

/* Toggle switch styling */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #cbd5e0;
  transition: .4s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: var(--primary-color, #3182ce);
}

input:focus + .toggle-slider {
  box-shadow: 0 0 1px var(--primary-color, #3182ce);
}

input:checked + .toggle-slider:before {
  transform: translateX(24px);
}

/* Interests/tags section */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.interest-tag {
  background-color: #edf2f7;
  color: #4a5568;
  padding: 0.375rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.interest-tag:hover {
  background-color: #e2e8f0;
}

.interest-tag.active {
  background-color: var(--primary-color, #3182ce);
  color: white;
}

.tags-actions {
  display: flex;
  justify-content: flex-start;
  margin-top: 1rem;
}

.tags-action-btn {
  padding: 0.5rem 1rem;
  background-color: var(--primary-color, #3182ce);
  color: white;
  border: none;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tags-action-btn:hover:not(:disabled) {
  background-color: var(--primary-dark, #2c5282);
}

.tags-action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1.5rem;
  padding: 0.75rem;
  background-color: #FEF2F2;
  color: #DC2626;
  border-radius: 0.375rem;
  font-size: 0.875rem;
}

.success-message {
  margin-top: 1.5rem;
  padding: 0.75rem;
  background-color: #F0FDF4;
  color: #16A34A;
  border-radius: 0.375rem;
  font-size: 0.875rem;
}

@media (max-width: 640px) {
  .theme-options {
    gap: 0.5rem;
  }
  
  .theme-option {
    width: 80px;
  }
  
  .notification-option {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .notification-controls {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 