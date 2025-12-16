<template>
  <div class="splash-container">
    <div class="logo-container">
      <div class="logo-text">UniApp</div>
      <div class="subtitle">Full Stack Application</div>
    </div>
    
    <div class="status-container">
      <div v-if="loading" class="loading">
        <div class="loading-text">Connecting to server...</div>
        <div class="spinner"></div>
      </div>
      
      <div v-else-if="error" class="error">
        <div class="error-text">{{ errorMessage }}</div>
        <button @click="checkServerHealth" class="retry-btn">Retry</button>
      </div>
      
      <div v-else class="success">
        <div class="success-text">✓ Connected</div>
      </div>
    </div>
    
    <div class="version-info">
      <div class="version-text">v0.0.1</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { checkHealth } from '../../src/api/health'
import { useAppStore } from '../../src/store/app'

const router = useRouter()
const appStore = useAppStore()
const loading = ref(true)
const error = ref(false)
const errorMessage = ref('')

const checkServerHealth = async () => {
  loading.value = true
  error.value = false
  errorMessage.value = ''
  
  try {
    const response = await checkHealth()
    console.log('Health check response:', response)
    
    appStore.setServerStatus(response)
    appStore.setReady(true)
    
    setTimeout(() => {
      router.push('/index')
    }, 1000)
  } catch (err) {
    console.error('Health check failed:', err)
    error.value = true
    errorMessage.value = 'Failed to connect to server'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  setTimeout(() => {
    checkServerHealth()
  }, 500)
})
</script>

<style scoped>
.splash-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  position: relative;
}

.logo-container {
  text-align: center;
  margin-bottom: 60px;
}

.logo-text {
  font-size: 48px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
}

.status-container {
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading, .error, .success {
  text-align: center;
}

.loading-text, .error-text, .success-text {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.retry-btn {
  background-color: #ffffff;
  color: #667eea;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 10px;
  cursor: pointer;
}

.retry-btn:hover {
  opacity: 0.9;
}

.version-info {
  position: absolute;
  bottom: 30px;
}

.version-text {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}
</style>
