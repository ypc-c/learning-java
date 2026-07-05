import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const service = axios.create({
  baseURL: '',
  timeout: 15000
})

// Request interceptor: attach JWT token
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor: handle errors
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || 'Request failed')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        removeToken()
        ElMessage.error('Session expired, please login again')
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error('Access denied')
      } else if (status === 429) {
        ElMessage.error('Too many requests, please try later')
      } else {
        ElMessage.error(error.response.data?.message || `Server error (${status})`)
      }
    } else if (error.message.includes('timeout')) {
      ElMessage.error('Request timeout')
    } else {
      ElMessage.error('Network error')
    }
    return Promise.reject(error)
  }
)

export default service
