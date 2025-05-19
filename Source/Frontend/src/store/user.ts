import { defineStore } from 'pinia'

interface User {
  id: number
  username: string
  email: string
}

interface AuthState {
  user: User | null
  token: string | null
  initialized: boolean
}

export const useUserStore = defineStore('user', {
  state: (): AuthState => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token'),
    initialized: false
  }),
  getters: {
    isLoggedIn(): boolean {
      return !!this.token && !!this.user
    }
  },
  actions: {
    setUser(user: User, token: string) {
      this.user = user
      this.token = token
      this.initialized = true
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('token', token)
      
      // Set cookie expiration time
      const expiresDate = new Date()
      expiresDate.setDate(expiresDate.getDate() + 7) // 7 days from now
      document.cookie = `auth_initialized=true; expires=${expiresDate.toUTCString()}; path=/`
    },
    logout() {
      this.user = null
      this.token = null
      this.initialized = false
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      document.cookie = 'auth_initialized=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;'
    },
    checkAuth() {
      // If we have a token but not a user, try to restore from localStorage
      if (this.token && !this.user) {
        const storedUser = localStorage.getItem('user')
        if (storedUser) {
          try {
            this.user = JSON.parse(storedUser)
          } catch (e) {
            this.logout() // Invalid user data, log out
          }
        }
      }
      
      this.initialized = true
      return this.isLoggedIn
    }
  },
}) 