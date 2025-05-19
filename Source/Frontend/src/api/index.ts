import { useUserStore } from '../store/user'

// Interfaces for API responses
export interface UserProfile {
  id: number;
  username: string;
  email?: string;
  bio: string | null;
  createdDate: string;
  followedByCount?: number;
  followingCount?: number;
  likesGiven?: number;
}

export interface PostMetadata {
  id: number;
  postContent: string;
  authorUsername: string;
  likeCount: number;
}

/**
 * Base API request function with authentication
 */
async function apiRequest<T>(
  url: string, 
  options: RequestInit = {}
): Promise<T> {
  const userStore = useUserStore()
  
  // Default headers
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> || {})
  }
  
  // Add auth token if available
  if (userStore.token) {
    headers['Authorization'] = `Bearer ${userStore.token}`
  }
  
  const response = await fetch(url, {
    ...options,
    headers
  })
  
  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || `API error: ${response.status}`)
  }
  
  // Empty response (for 204 No Content, etc.)
  if (response.status === 204) {
    return {} as T
  }
  
  // Parse JSON response
  const data = await response.json()
  return data as T
}

/**
 * Create a new post
 */
export async function createPost(content: string): Promise<PostMetadata> {
  return apiRequest<PostMetadata>('/api/posts', {
    method: 'POST',
    body: JSON.stringify({ content })
  })
}

/**
 * Get user by username
 */
export async function getUserByUsername(username: string): Promise<UserProfile> {
  return apiRequest<UserProfile>(`/api/accounts/user/${username}`)
}

/**
 * Get posts by username
 */
export async function getPostsByUsername(username: string): Promise<PostMetadata[]> {
  return apiRequest<PostMetadata[]>(`/api/posts/user/${username}`)
}

/**
 * Get all users
 */
export async function getAllUsers(): Promise<UserProfile[]> {
  return apiRequest<UserProfile[]>('/api/accounts/users')
}

/**
 * Follow a user
 */
export async function followUser(userId: number): Promise<void> {
  return apiRequest<void>('/api/accounts/follow', {
    method: 'POST',
    body: JSON.stringify({ userToFollow: userId })
  })
}

/**
 * Unfollow a user
 */
export async function unfollowUser(userId: number): Promise<void> {
  return apiRequest<void>('/api/accounts/unfollow', {
    method: 'POST',
    body: JSON.stringify({ userToFollow: userId })
  })
}

/**
 * Like a post
 */
export async function likePost(postId: number): Promise<void> {
  return apiRequest<void>(`/api/posts/like/${postId}`, {
    method: 'POST'
  })
}

/**
 * Get users who liked a post
 */
export async function getPostLikes(postId: number): Promise<string[]> {
  return apiRequest<string[]>(`/api/posts/likedBy/${postId}`)
}

/**
 * Get users followed by a username
 */
export async function getFollowedByUsername(username: string): Promise<string[]> {
  return apiRequest<string[]>(`/api/accounts/followed-by/${username}`)
}

/**
 * Get users following a username
 */
export async function getFollowingByUsername(username: string): Promise<string[]> {
  return apiRequest<string[]>(`/api/accounts/follows/${username}`)
}

export async function loginApi(email: string, password: string) {
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Login failed:', errorText);
      throw new Error(errorText || 'Invalid credentials');
    }
    
    const data = await res.json();
    console.log('Login successful:', data);
    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

export async function registerApi(username: string, email: string, password: string) {
  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password }),
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Registration failed:', errorText);
      throw new Error(errorText || 'Registration failed');
    }
    
    const data = await res.json();
    console.log('Registration successful:', data);
    return data;
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
}

export async function getPostsApi() {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch('/api/posts/all', {
      method: 'GET',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Fetching posts failed:', errorText);
      throw new Error(errorText || 'Failed to fetch posts');
    }
    
    const data = await res.json();
    console.log('Posts fetched successfully:', data);
    return data;
  } catch (error) {
    console.error('Fetching posts error:', error);
    throw error;
  }
} 