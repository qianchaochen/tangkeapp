<template>
  <view class="page">
    <!-- Header -->
    <view class="header">
      <text class="title">消费统计</text>
      <picker mode="date" :value="dateRange.startDate" @change="onStartDateChange">
        <view class="date-picker">{{ dateRange.startDate || '开始日期' }}</view>
      </picker>
      <text class="date-separator">至</text>
      <picker mode="date" :value="dateRange.endDate" @change="onEndDateChange">
        <view class="date-picker">{{ dateRange.endDate || '结束日期' }}</view>
      </picker>
    </view>

    <!-- Loading -->
    <view class="loading" v-if="loading">
      <text>加载统计数据...</text>
    </view>

    <!-- Stats Content -->
    <view class="stats-content" v-if="!loading && stats">
      <!-- Summary Cards -->
      <view class="summary-cards">
        <view class="summary-card">
          <text class="card-value">¥{{ formatCurrency(stats.totalRevenue) }}</text>
          <text class="card-label">总收入</text>
        </view>
        <view class="summary-card">
          <text class="card-value">¥{{ formatCurrency(stats.totalSpend) }}</text>
          <text class="card-label">总支出</text>
        </view>
        <view class="summary-card">
          <text class="card-value">{{ stats.totalTransactions }}</text>
          <text class="card-label">交易数</text>
        </view>
        <view class="summary-card">
          <text class="card-value">{{ stats.uniqueCustomers }}</text>
          <text class="card-label">客户数</text>
        </view>
      </view>

      <!-- Tab Navigation -->
      <view class="tabs">
        <view class="tab-item" :class="{ active: activeTab === 'overview' }" @click="switchTab('overview')">
          <text>概览</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'trends' }" @click="switchTab('trends')">
          <text>趋势</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'customers' }" @click="switchTab('customers')">
          <text>客户</text>
        </view>
        <view class="tab-item" :class="{ active: activeTab === 'projects' }" @click="switchTab('projects')">
          <text>项目</text>
        </view>
      </view>

      <!-- Overview Tab -->
      <view class="tab-content" v-if="activeTab === 'overview'">
        <!-- Source Distribution Chart -->
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">来源分布</text>
          </view>
          <view class="chart-content">
            <canvas canvas-id="sourceChart" class="chart-canvas"></canvas>
          </view>
        </view>

        <!-- Project Type Breakdown -->
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">项目类型分布</text>
          </view>
          <view class="chart-content">
            <view class="breakdown-list">
              <view class="breakdown-item" v-for="item in stats.projectTypeBreakdown" :key="item.projectType">
                <view class="breakdown-info">
                  <text class="breakdown-name">{{ item.projectTypeDisplay }}</text>
                  <text class="breakdown-stats">{{ item.count }}笔 · ¥{{ formatCurrency(item.totalAmount) }}</text>
                </view>
                <view class="breakdown-progress">
                  <view class="progress-bar">
                    <view class="progress-fill" :style="{ width: item.percentage + '%' }"></view>
                  </view>
                  <text class="progress-text">{{ item.percentage.toFixed(1) }}%</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- Visit Frequency Analysis -->
        <view class="chart-container" v-if="stats.visitFrequency">
          <view class="chart-header">
            <text class="chart-title">访客频率分析</text>
          </view>
          <view class="chart-content">
            <view class="frequency-stats">
              <view class="frequency-item">
                <text class="frequency-value">{{ stats.visitFrequency.oneTimeCustomers }}</text>
                <text class="frequency-label">一次性客户</text>
              </view>
              <view class="frequency-item">
                <text class="frequency-value">{{ stats.visitFrequency.regularCustomers }}</text>
                <text class="frequency-label">常客</text>
              </view>
              <view class="frequency-item">
                <text class="frequency-value">{{ stats.visitFrequency.frequentCustomers }}</text>
                <text class="frequency-label">高频客户</text>
              </view>
            </view>
            <view class="avg-visits">
              <text>平均访问次数: {{ stats.visitFrequency.averageVisitsPerCustomer.toFixed(1) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Trends Tab -->
      <view class="tab-content" v-if="activeTab === 'trends'">
        <!-- Daily Trends Chart -->
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">每日趋势</text>
          </view>
          <view class="chart-content">
            <canvas canvas-id="dailyChart" class="chart-canvas"></canvas>
          </view>
        </view>

        <!-- Weekly Trends -->
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">每周趋势</text>
          </view>
          <view class="chart-content">
            <canvas canvas-id="weeklyChart" class="chart-canvas"></canvas>
          </view>
        </view>

        <!-- Monthly Trends -->
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">每月趋势</text>
          </view>
          <view class="chart-content">
            <canvas canvas-id="monthlyChart" class="chart-canvas"></canvas>
          </view>
        </view>
      </view>

      <!-- Customers Tab -->
      <view class="tab-content" v-if="activeTab === 'customers'">
        <!-- Top Spenders -->
        <view class="customer-section">
          <view class="section-header">
            <text class="section-title">消费排行</text>
          </view>
          <view class="customer-list">
            <view class="customer-item" v-for="(customer, index) in stats.topSpenders" :key="customer.customerId">
              <view class="customer-rank">{{ index + 1 }}</view>
              <view class="customer-info">
                <text class="customer-name">{{ customer.customerName }}</text>
                <text class="customer-phone">{{ customer.customerPhone || '未知电话' }}</text>
                <text class="customer-source">{{ customer.source }}</text>
              </view>
              <view class="customer-stats">
                <text class="customer-amount">¥{{ formatCurrency(customer.totalAmount) }}</text>
                <text class="customer-transactions">{{ customer.transactionCount }}笔</text>
                <text class="customer-vip-score" v-if="customer.vipScore">VIP: {{ customer.vipScore.toFixed(1) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- Top Frequent Customers -->
        <view class="customer-section">
          <view class="section-header">
            <text class="section-title">活跃度排行</text>
          </view>
          <view class="customer-list">
            <view class="customer-item" v-for="(customer, index) in stats.topFrequentCustomers" :key="customer.customerId">
              <view class="customer-rank">{{ index + 1 }}</view>
              <view class="customer-info">
                <text class="customer-name">{{ customer.customerName }}</text>
                <text class="customer-phone">{{ customer.customerPhone || '未知电话' }}</text>
                <text class="customer-source">{{ customer.source }}</text>
              </view>
              <view class="customer-stats">
                <text class="customer-amount">¥{{ formatCurrency(customer.totalAmount) }}</text>
                <text class="customer-transactions">{{ customer.transactionCount }}笔</text>
                <text class="customer-vip-score" v-if="customer.vipScore">VIP: {{ customer.vipScore.toFixed(1) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- Projects Tab -->
      <view class="tab-content" v-if="activeTab === 'projects'">
        <view class="chart-container">
          <view class="chart-header">
            <text class="chart-title">项目收入对比</text>
          </view>
          <view class="chart-content">
            <canvas canvas-id="projectChart" class="chart-canvas"></canvas>
          </view>
        </view>

        <view class="project-details">
          <view class="project-item" v-for="item in stats.projectTypeBreakdown" :key="item.projectType">
            <view class="project-header">
              <text class="project-name">{{ item.projectTypeDisplay }}</text>
              <text class="project-amount">¥{{ formatCurrency(item.totalAmount) }}</text>
            </view>
            <view class="project-stats">
              <text class="project-count">{{ item.count }}笔交易</text>
              <text class="project-percentage">{{ item.percentage.toFixed(1) }}%占比</text>
            </view>
            <view class="project-progress">
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: item.percentage + '%' }"></view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- Error State -->
    <view class="error-state" v-if="!loading && !stats">
      <text class="error-icon">📊</text>
      <text class="error-text">暂无统计数据</text>
    </view>
  </view>
</template>

<script>
import { getConsumptionStatistics, getDailySummaries, getSourceDistribution, getTopSpenders, getTopFrequentCustomers } from '@/services/consumptionService.js'

export default {
  data() {
    return {
      loading: false,
      activeTab: 'overview',
      stats: null,
      dateRange: {
        startDate: '',
        endDate: ''
      },
      charts: {
        sourceChart: null,
        dailyChart: null,
        weeklyChart: null,
        monthlyChart: null,
        projectChart: null
      }
    }
  },

  onLoad() {
    this.initDateRange()
    this.loadStatsData()
  },

  onShow() {
    // Refresh data when returning to this page
    if (this.stats) {
      this.loadStatsData()
    }
  },

  methods: {
    // Initialize date range (last 30 days)
    initDateRange() {
      const endDate = new Date()
      const startDate = new Date()
      startDate.setDate(endDate.getDate() - 30)
      
      this.dateRange.startDate = this.formatDate(startDate)
      this.dateRange.endDate = this.formatDate(endDate)
    },

    // Load all statistics data
    async loadStatsData() {
      this.loading = true
      try {
        const params = {
          startDate: this.dateRange.startDate,
          endDate: this.dateRange.endDate
        }

        const [stats, dailySummaries, sourceDistribution, topSpenders, topFrequentCustomers] = await Promise.all([
          getConsumptionStatistics(params),
          getDailySummaries(params),
          getSourceDistribution(params),
          getTopSpenders(params),
          getTopFrequentCustomers(params)
        ])

        this.stats = stats
        this.stats.dailySummaries = dailySummaries
        this.stats.sourceDistribution = sourceDistribution
        this.stats.topSpenders = topSpenders
        this.stats.topFrequentCustomers = topFrequentCustomers

        // Initialize charts after data is loaded
        this.$nextTick(() => {
          this.initCharts()
        })

      } catch (error) {
        console.error('Failed to load statistics:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'error'
        })
      } finally {
        this.loading = false
      }
    },

    // Initialize charts
    initCharts() {
      // Note: This is a placeholder for chart initialization
      // In a real implementation, you would use uCharts or similar library
      console.log('Initializing charts...')
      
      // Example chart data structure for uCharts:
      // this.initSourceChart()
      // this.initDailyChart()
      // etc.
    },

    // Switch between tabs
    switchTab(tab) {
      this.activeTab = tab
    },

    // Date range handlers
    onStartDateChange(e) {
      this.dateRange.startDate = e.detail.value
      this.loadStatsData()
    },

    onEndDateChange(e) {
      this.dateRange.endDate = e.detail.value
      this.loadStatsData()
    },

    // Helper methods
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },

    formatCurrency(amount) {
      if (!amount) return '0.00'
      return parseFloat(amount).toFixed(2)
    }
  }
}
</script>

<style scoped>
.page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  color: white;
  font-size: 18px;
  font-weight: bold;
  flex-shrink: 0;
}

.date-picker {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  flex: 1;
  text-align: center;
}

.date-separator {
  color: white;
  font-size: 12px;
  flex-shrink: 0;
}

.loading, .error-state {
  text-align: center;
  padding: 50px 20px;
  color: #666;
}

.error-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
}

.stats-content {
  padding: 15px;
}

.summary-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 20px;
}

.summary-card {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.card-label {
  font-size: 14px;
  color: #666;
}

.tabs {
  display: flex;
  background-color: white;
  border-radius: 10px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.tab-item {
  flex: 1;
  padding: 15px;
  text-align: center;
  color: #666;
  font-size: 14px;
  border-right: 1px solid #eee;
}

.tab-item:last-child {
  border-right: none;
}

.tab-item.active {
  background-color: #667eea;
  color: white;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chart-container {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.chart-header {
  padding: 15px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.chart-content {
  padding: 15px;
}

.chart-canvas {
  width: 100%;
  height: 250px;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breakdown-info {
  flex: 1;
}

.breakdown-name {
  display: block;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.breakdown-stats {
  font-size: 12px;
  color: #666;
}

.breakdown-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  width: 80px;
  height: 6px;
  background-color: #eee;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 35px;
}

.frequency-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
}

.frequency-item {
  text-align: center;
}

.frequency-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.frequency-label {
  font-size: 12px;
  color: #666;
}

.avg-visits {
  text-align: center;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 5px;
  font-size: 14px;
  color: #333;
}

.customer-section {
  background-color: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.section-header {
  padding: 15px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.customer-list {
  display: flex;
  flex-direction: column;
}

.customer-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.customer-item:last-child {
  border-bottom: none;
}

.customer-rank {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 15px;
}

.customer-info {
  flex: 1;
}

.customer-name {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.customer-phone, .customer-source {
  font-size: 12px;
  color: #666;
  margin-right: 10px;
}

.customer-stats {
  text-align: right;
}

.customer-amount {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 2px;
}

.customer-transactions, .customer-vip-score {
  font-size: 12px;
  color: #666;
}

.project-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.project-item {
  background-color: white;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.project-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.project-amount {
  font-size: 16px;
  font-weight: bold;
  color: #667eea;
}

.project-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.project-count, .project-percentage {
  font-size: 12px;
  color: #666;
}

.project-progress {
  margin-top: 5px;
}
</style>