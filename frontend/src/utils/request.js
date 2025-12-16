import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const request = axios.create({
  baseURL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('Response error:', error)
    
    if (error.response) {
      const message = error.response.data?.message || 'Request Failed'
      switch (error.response.status) {
        case 401:
          alert('Unauthorized')
          localStorage.removeItem('token')
          break
        case 403:
          alert('Access Denied')
          break
        case 404:
          alert('Resource Not Found')
          break
        case 500:
          alert('Server Error')
          break
        default:
          alert(message)
      }
    } else {
      alert('Network Error')
    }
    
    return Promise.reject(error)
  }
)

export default request
