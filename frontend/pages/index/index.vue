<template>
  <div class="container">
    <div class="header">
      <div class="title">Welcome to UniApp</div>
      <div class="description">Full Stack Application</div>
    </div>
    
    <div class="content">
      <div class="card">
        <div class="card-title">Server Status</div>
        <div v-if="serverStatus" class="status-info">
          <div class="status-item">
            <span class="label">Status:</span>
            <span class="value status-up">{{ serverStatus.status }}</span>
          </div>
          <div class="status-item">
            <span class="label">Message:</span>
            <span class="value">{{ serverStatus.message }}</span>
          </div>
          <div class="status-item">
            <span class="label">Version:</span>
            <span class="value">{{ serverStatus.version }}</span>
          </div>
          <div class="status-item">
            <span class="label">Timestamp:</span>
            <span class="value timestamp">{{ formatTime(serverStatus.timestamp) }}</span>
          </div>
        </div>
        <div v-else class="no-status">
          <div class="no-status-text">No server status available</div>
        </div>
      </div>
      
      <div class="actions">
        <button @click="refreshStatus" class="btn btn-primary">
          Refresh Status
        </button>
      </div>
      
      <div class="info-section">
        <div class="info-title">Getting Started</div>
        <div class="info-items">
          <div class="info-item">
            <span class="bullet">•</span>
            <span class="info-text">Backend running on port 8080</span>
          </div>
          <div class="info-item">
            <span class="bullet">•</span>
            <span class="info-text">Frontend with UniApp & Vue 3</span>
          </div>
          <div class="info-item">
            <span class="bullet">•</span>
            <span class="info-text">State management with Pinia</span>
          </div>
          <div class="info-item">
            <span class="bullet">•</span>
            <span class="info-text">HTTP client with Axios</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '../../src/store/app'
import { checkHealth } from '../../src/api/health'

const appStore = useAppStore()
const serverStatus = computed(() => appStore.serverStatus)
const loading = ref(false)

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString()
}

const refreshStatus = async () => {
  loading.value = true
  
  try {
    const response = await checkHealth()
    appStore.setServerStatus(response)
    alert('Status Updated')
  } catch (error) {
    console.error('Failed to refresh status:', error)
    alert('Update Failed')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!serverStatus.value) {
    refreshStatus()
  }
})
</script>

<style scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  text-align: center;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 8px;
}

.description {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.content {
  padding: 20px;
}

.card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 15px;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: flex-start;
}

.label {
  font-size: 14px;
  color: #666666;
  min-width: 90px;
  font-weight: 500;
}

.value {
  font-size: 14px;
  color: #333333;
  flex: 1;
}

.status-up {
  color: #52c41a;
  font-weight: 600;
}

.timestamp {
  font-size: 12px;
  color: #999999;
}

.no-status {
  padding: 20px 0;
  text-align: center;
}

.no-status-text {
  color: #999999;
  font-size: 14px;
}

.actions {
  margin-bottom: 20px;
}

.btn {
  width: 100%;
  padding: 14px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background-color: #667eea;
  color: #ffffff;
}

.btn-primary:hover {
  background-color: #5568d3;
}

.info-section {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 15px;
}

.info-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.bullet {
  color: #667eea;
  font-size: 20px;
  line-height: 20px;
}

.info-text {
  font-size: 14px;
  color: #666666;
  line-height: 20px;
}
</style>
