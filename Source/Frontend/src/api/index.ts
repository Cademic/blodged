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
  replyCount: number;
  isLiked?: boolean;
}

export interface ReplyMetadata {
  id: number;
  replyContent: string;
  replyAuthorUsername: string;
  postId: number;
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
  
  // Check if response is HTML (common error during initial load)
  const contentType = response.headers.get('content-type')
  if (contentType && contentType.includes('text/html')) {
    console.warn('Server returned HTML instead of JSON. Server might still be starting up.')
    throw new Error('Server is starting up. Please try again in a moment.')
  }
  
  // Parse JSON response
  try {
    const data = await response.json()
    return data as T
  } catch (jsonError) {
    console.error('Error parsing JSON response:', jsonError)
    throw new Error('Server returned invalid data. It might still be starting up. Please refresh the page.')
  }
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
 * Like a post (uses standard controller endpoint)
 */
export async function likePost(postId: number): Promise<void> {
  try {
    const userStore = useUserStore();
    
    // If not logged in, abort
    if (!userStore.isLoggedIn || !userStore.user) {
      throw new Error('You must be logged in to like posts');
    }

    // Check if user has already liked this post
    const alreadyLiked = await checkPostLike(postId);
    if (alreadyLiked) {
      console.log('User already liked this post, skipping like action');
      return;
    }
    
    // Using fetch directly instead of apiRequest to work with the non-REST endpoint
    const token = localStorage.getItem('token');
    const res = await fetch(`/like/${postId}`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Like post failed:', errorText);
      throw new Error(errorText || 'Failed to like post');
    }
    
    return;
  } catch (error) {
    console.error('Like post error:', error);
    throw error;
  }
}

/**
 * Unlike a post (uses standard controller endpoint)
 */
export async function unlikePost(postId: number): Promise<void> {
  try {
    // Using fetch directly instead of apiRequest to work with the non-REST endpoint
    const token = localStorage.getItem('token');
    const res = await fetch(`/unlike/${postId}`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Unlike post failed:', errorText);
      throw new Error(errorText || 'Failed to unlike post');
    }
    
    return;
  } catch (error) {
    console.error('Unlike post error:', error);
    throw error;
  }
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
    
    // Check if response is HTML (common error during initial load)
    const contentType = res.headers.get('content-type');
    if (contentType && contentType.includes('text/html')) {
      console.warn('Server returned HTML instead of JSON. Server might still be starting up.');
      throw new Error('Server is starting up. Please try again in a moment.');
    }
    
    let data;
    try {
      data = await res.json();
    } catch (jsonError) {
      console.error('Error parsing JSON response:', jsonError);
      throw new Error('Server returned invalid data. It might still be starting up. Please refresh the page.');
    }
    
    // If we have posts and likes data
    if (data.posts && data.likes) {
      // Map the posts with likes information
      return data.posts.map((post: PostMetadata) => ({
        ...post,
        isLiked: data.likes[post.id] || false
      }));
    }
    
    console.log('Posts fetched successfully:', data);
    return data;
  } catch (error) {
    console.error('Fetching posts error:', error);
    throw error;
  }
}

/**
 * Get replies for a specific post
 */
export async function getRepliesForPost(postId: number): Promise<ReplyMetadata[]> {
  return apiRequest<ReplyMetadata[]>(`/api/replies/post/${postId}`)
}

/**
 * Create a new reply to a post
 */
export async function createReply(postId: number, content: string): Promise<void> {
  try {
    const token = localStorage.getItem('token');
    
    // Create form data for the request
    const formData = new FormData();
    formData.append('replyContent', content);
    // Add the 'post' parameter required by the backend
    formData.append('post', 'post');
    
    const res = await fetch(`/replies/create/${postId}`, {
      method: 'POST',
      headers: { 
        'Authorization': token ? `Bearer ${token}` : '',
      },
      body: formData,
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Create reply failed:', errorText);
      throw new Error(errorText || 'Failed to create reply');
    }
    
    return;
  } catch (error) {
    console.error('Create reply error:', error);
    throw error;
  }
}

/**
 * Edit an existing reply
 */
export async function editReply(replyId: number, content: string): Promise<void> {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`/replies/edit/${replyId}`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      body: JSON.stringify({ content }),
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Edit reply failed:', errorText);
      throw new Error(errorText || 'Failed to edit reply');
    }
    
    return;
  } catch (error) {
    console.error('Edit reply error:', error);
    throw error;
  }
}

/**
 * Delete a reply
 */
export async function deleteReply(replyId: number): Promise<void> {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`/replies/delete/${replyId}`, {
      method: 'DELETE',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Delete reply failed:', errorText);
      throw new Error(errorText || 'Failed to delete reply');
    }
    
    return;
  } catch (error) {
    console.error('Delete reply error:', error);
    throw error;
  }
}

/**
 * Edit an existing post
 */
export async function editPost(postId: number, content: string): Promise<void> {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`/api/posts/${postId}`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      body: JSON.stringify({ content }),
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Edit post failed:', errorText);
      throw new Error(errorText || 'Failed to edit post');
    }
    
    return;
  } catch (error) {
    console.error('Edit post error:', error);
    throw error;
  }
}

/**
 * Delete a post
 */
export async function deletePost(postId: number): Promise<void> {
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`/api/posts/${postId}`, {
      method: 'DELETE',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
      },
      credentials: 'include',
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('Delete post failed:', errorText);
      throw new Error(errorText || 'Failed to delete post');
    }
    
    return;
  } catch (error) {
    console.error('Delete post error:', error);
    throw error;
  }
}

/**
 * Get count of replies for a post
 */
export async function getReplyCountForPost(postId: number): Promise<number> {
  try {
    const replies = await getRepliesForPost(postId);
    return replies.length;
  } catch (error) {
    console.error(`Error getting reply count for post ${postId}:`, error);
    return 0;
  }
}

/**
 * Check if current user has liked a post 
 */
export async function checkPostLike(postId: number): Promise<boolean> {
  try {
    const userStore = useUserStore();
    
    // If not logged in, user hasn't liked any posts
    if (!userStore.isLoggedIn || !userStore.user) {
      return false;
    }
    
    // Get users who liked this post
    const likers = await getPostLikes(postId);
    
    // Check if current user's username is in the likers list
    return likers.includes(userStore.user.username);
  } catch (error) {
    console.error('Error checking post like:', error);
    return false;
  }
} 