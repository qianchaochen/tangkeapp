import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isReady = ref(false)
  const serverStatus = ref(null)
  const userInfo = ref(null)
  
  const setReady = (status) => {
    isReady.value = status
  }
  
  const setServerStatus = (status) => {
    serverStatus.value = status
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
  }
  
  const clearUserInfo = () => {
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  return {
    isReady,
    serverStatus,
    userInfo,
    setReady,
    setServerStatus,
    setUserInfo,
    clearUserInfo
  }
})
