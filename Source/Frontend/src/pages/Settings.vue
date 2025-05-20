<template>
  <div class="settings-container">
    <div class="settings-sidebar">
      <h2>Settings</h2>
      <nav>
        <router-link 
          :to="{ name: 'Settings', query: { tab: 'account' } }" 
          :class="{ active: activeTab === 'account' }"
          class="nav-item"
        >
          Account Information
        </router-link>
        <router-link 
          :to="{ name: 'Settings', query: { tab: 'security' } }" 
          :class="{ active: activeTab === 'security' }"
          class="nav-item"
        >
          Security & Privacy
        </router-link>
        <router-link 
          :to="{ name: 'Settings', query: { tab: 'preferences' } }" 
          :class="{ active: activeTab === 'preferences' }"
          class="nav-item"
        >
          Preferences
        </router-link>
      </nav>
    </div>
    
    <div class="settings-content">
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else>
        <!-- Account Information Tab -->
        <account-settings v-if="activeTab === 'account'" :user="user" @update="handleUpdate" />
        
        <!-- Security & Privacy Tab -->
        <security-settings v-else-if="activeTab === 'security'" :user="user" @update="handleUpdate" />
        
        <!-- Preferences Tab -->
        <preferences-settings v-else-if="activeTab === 'preferences'" :user="user" @update="handleUpdate" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import AccountSettings from '../components/settings/AccountSettings.vue'
import SecuritySettings from '../components/settings/SecuritySettings.vue'
import PreferencesSettings from '../components/settings/PreferencesSettings.vue'
import { getUserByUsername, type UserProfile } from '../api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// State
const user = ref<UserProfile | null>(null)
const loading = ref(true)
const error = ref('')

// Get the active tab from URL query or default to 'account'
const activeTab = computed(() => {
  const tab = route.query.tab as string
  return ['account', 'security', 'preferences'].includes(tab) ? tab : 'account'
})

// When tab changes in URL, update the view
watch(() => route.query.tab, async () => {
  if (!['account', 'security', 'preferences'].includes(route.query.tab as string)) {
    router.replace({ query: { tab: 'account' } })
  }
})

// Load user data
async function loadUserData() {
  if (!userStore.user) {
    error.value = 'Not logged in'
    loading.value = false
    return
  }
  
  try {
    loading.value = true
    user.value = await getUserByUsername(userStore.user.username)
    loading.value = false
  } catch (err) {
    console.error('Error loading user data:', err)
    // Fallback to store data if API fails
    user.value = {
      id: userStore.user.id,
      username: userStore.user.username,
      email: userStore.user.email,
      bio: null,
      createdDate: new Date().toISOString()
    }
    loading.value = false
  }
}

// Handle user updates
function handleUpdate(field: string, value: string) {
  if (user.value) {
    // Update local state first for immediate feedback
    if (field === 'username') {
      user.value.username = value
      // Also update the userStore to ensure consistent state
      if (userStore.user) {
        userStore.user.username = value
      }
    } else if (field === 'email') {
      user.value.email = value
      if (userStore.user) {
        userStore.user.email = value
      }
    } else if (field === 'bio') {
      user.value.bio = value
    } else if (field === 'private') {
      user.value.isPrivate = value === 'true'
    }
    
    // Wait a short period before reloading to ensure the backend has processed the update
    setTimeout(loadUserData, 300)
  }
}

onMounted(async () => {
  // Initialize with 'account' tab if no tab is specified
  if (!route.query.tab) {
    router.replace({ query: { tab: 'account' } })
  }
  
  await loadUserData()
})
</script>

<style scoped>
.settings-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  gap: 2rem;
  min-height: calc(100vh - 60px);
}

.settings-sidebar {
  flex: 0 0 250px;
  background-color: var(--card-color, #fff);
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  height: fit-content;
}

.settings-sidebar h2 {
  margin-bottom: 1.5rem;
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--heading-color, #2d3748);
}

.settings-sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-item {
  display: block;
  padding: 0.75rem 1rem;
  color: var(--text-color, #4a5568);
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s;
}

.nav-item:hover {
  background-color: var(--hover-color, #f7fafc);
}

.nav-item.active {
  background-color: var(--primary-light, #ebf4ff);
  color: var(--primary-color, #3182ce);
  font-weight: 600;
}

.settings-content {
  flex: 1;
  background-color: var(--card-color, #fff);
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading, .error {
  text-align: center;
  padding: 2rem;
}

.error {
  color: var(--error-color, #e53e3e);
}

@media (max-width: 768px) {
  .settings-container {
    flex-direction: column;
    padding: 1rem;
  }
  
  .settings-sidebar {
    flex: none;
    width: 100%;
  }
}
</style> 