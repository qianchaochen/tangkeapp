<template>
  <view class="page">
    <!-- Header -->
    <view class="header">
      <text class="title">消费记录</text>
      <button class="filter-btn" @click="toggleFilter">筛选</button>
    </view>

    <!-- Filter Panel -->
    <view class="filter-panel" v-if="showFilter">
      <view class="filter-group">
        <text class="filter-label">日期范围</text>
        <picker mode="date" :value="filter.startDate" @change="onStartDateChange">
          <view class="picker">{{ filter.startDate || '开始日期' }}</view>
        </picker>
        <text class="date-separator">至</text>
        <picker mode="date" :value="filter.endDate" @change="onEndDateChange">
          <view class="picker">{{ filter.endDate || '结束日期' }}</view>
        </picker>
      </view>

      <view class="filter-group">
        <text class="filter-label">交易类型</text>
        <picker :range="transactionTypes" range-key="label" @change="onTypeChange">
          <view class="picker">{{ selectedTypeLabel || '全部类型' }}</view>
        </picker>
      </view>

      <view class="filter-group">
        <text class="filter-label">项目类型</text>
        <picker :range="projectTypes" range-key="label" @change="onProjectTypeChange">
          <view class="picker">{{ selectedProjectTypeLabel || '全部项目' }}</view>
        </picker>
      </view>

      <view class="filter-actions">
        <button class="reset-btn" @click="resetFilter">重置</button>
        <button class="apply-btn" @click="applyFilter">应用</button>
      </view>
    </view>

    <!-- Summary Stats -->
    <view class="summary-stats" v-if="!showFilter && summaryStats">
      <view class="stat-item">
        <text class="stat-value">¥{{ summaryStats.totalRevenue }}</text>
        <text class="stat-label">总收入</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">¥{{ summaryStats.totalSpend }}</text>
        <text class="stat-label">总支出</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ summaryStats.totalTransactions }}</text>
        <text class="stat-label">交易数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ summaryStats.uniqueCustomers }}</text>
        <text class="stat-label">客户数</text>
      </view>
    </view>

    <!-- Loading -->
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>

    <!-- Empty State -->
    <view class="empty-state" v-if="!loading && consumptionRecords.length === 0">
      <text class="empty-icon">📊</text>
      <text class="empty-text">暂无消费记录</text>
    </view>

    <!-- Records List -->
    <view class="records-list" v-if="!loading && consumptionRecords.length > 0">
      <view class="record-item" v-for="record in consumptionRecords" :key="record.id">
        <view class="record-header">
          <view class="customer-info">
            <text class="customer-name">{{ record.customerName }}</text>
            <text class="customer-phone">{{ record.customerPhone || '未知电话' }}</text>
          </view>
          <view class="amount" :class="record.type">
            {{ record.formattedAmount }}
          </view>
        </view>
        
        <view class="record-details">
          <view class="detail-item">
            <text class="detail-label">类型:</text>
            <text class="detail-value">{{ getTransactionTypeLabel(record.type) }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">项目:</text>
            <text class="detail-value">{{ record.projectTypeDisplay }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">来源:</text>
            <text class="detail-value">{{ record.source }}</text>
          </view>
          <view class="detail-item">
            <text class="detail-label">时间:</text>
            <text class="detail-value">{{ record.formattedDate }}</text>
          </view>
        </view>

        <view class="record-notes" v-if="record.metadata">
          <text class="notes-label">备注:</text>
          <text class="notes-content">{{ record.metadata }}</text>
        </view>
      </view>
    </view>

    <!-- Pagination -->
    <view class="pagination" v-if="!loading && totalPages > 1">
      <button class="page-btn" :disabled="currentPage === 0" @click="prevPage">上一页</button>
      <text class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</text>
      <button class="page-btn" :disabled="currentPage >= totalPages - 1" @click="nextPage">下一页</button>
    </view>

    <!-- Floating Action Button -->
    <view class="fab" @click="showAddRecordModal">
      <text class="fab-icon">+</text>
    </view>

    <!-- Add Record Modal -->
    <view class="modal-overlay" v-if="showAddModal" @click="hideAddRecordModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">新增消费记录</text>
          <button class="close-btn" @click="hideAddRecordModal">×</button>
        </view>
        
        <view class="form-group">
          <text class="form-label">客户ID *</text>
          <input class="form-input" type="number" v-model="newRecord.customerId" placeholder="输入客户ID" />
        </view>

        <view class="form-group">
          <text class="form-label">金额 *</text>
          <input class="form-input" type="digit" v-model="newRecord.amount" placeholder="输入金额" />
        </view>

        <view class="form-group">
          <text class="form-label">项目类型 *</text>
          <picker :range="projectTypes" range-key="label" @change="onNewRecordProjectTypeChange">
            <view class="picker">{{ newRecord.projectType || '选择项目类型' }}</view>
          </picker>
        </view>

        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea class="form-textarea" v-model="newRecord.notes" placeholder="输入备注信息"></textarea>
        </view>

        <view class="modal-actions">
          <button class="cancel-btn" @click="hideAddRecordModal">取消</button>
          <button class="submit-btn" @click="submitNewRecord">创建</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getConsumptionRecords, createConsumptionRecord, getQuickStats } from '@/services/consumptionService.js'

export default {
  data() {
    return {
      // Filter state
      showFilter: false,
      filter: {
        startDate: '',
        endDate: '',
        type: '',
        projectType: '',
        page: 0,
        size: 20,
        sortBy: 'createdAt',
        sortDir: 'desc'
      },
      
      // Data
      consumptionRecords: [],
      summaryStats: null,
      loading: false,
      currentPage: 0,
      totalPages: 0,
      
      // Options
      transactionTypes: [
        { value: '', label: '全部类型' },
        { value: 'SPEND', label: '消费' },
        { value: 'RECHARGE', label: '充值' },
        { value: 'REFUND', label: '退款' },
        { value: 'ADJUSTMENT', label: '调整' }
      ],
      
      projectTypes: [
        { value: '', label: '全部项目' },
        { value: 'GENERAL', label: '普通消费' },
        { value: 'POND_ARTICLES', label: '池塘文章' },
        { value: 'DISCOUNT_CAMPAIGN', label: '折扣活动' },
        { value: 'PROMOTION', label: '促销活动' },
        { value: 'SUBSCRIPTION', label: '订阅服务' },
        { value: 'OTHER', label: '其他' }
      ],
      
      selectedTypeLabel: '',
      selectedProjectTypeLabel: '',
      
      // Add record modal
      showAddModal: false,
      newRecord: {
        customerId: '',
        amount: '',
        projectType: '',
        notes: ''
      }
    }
  },

  onLoad() {
    this.loadConsumptionRecords()
    this.loadSummaryStats()
  },

  methods: {
    // Load consumption records
    async loadConsumptionRecords() {
      this.loading = true
      try {
        const response = await getConsumptionRecords(this.filter)
        this.consumptionRecords = response.content || []
        this.currentPage = response.number || 0
        this.totalPages = response.totalPages || 0
      } catch (error) {
        console.error('Failed to load consumption records:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'error'
        })
      } finally {
        this.loading = false
      }
    },

    // Load summary stats
    async loadSummaryStats() {
      try {
        const stats = await getQuickStats()
        this.summaryStats = {
          totalRevenue: this.formatCurrency(stats.totalRevenue),
          totalSpend: this.formatCurrency(stats.totalSpend),
          totalTransactions: stats.totalTransactions,
          uniqueCustomers: stats.uniqueCustomers
        }
      } catch (error) {
        console.error('Failed to load summary stats:', error)
      }
    },

    // Filter methods
    toggleFilter() {
      this.showFilter = !this.showFilter
    },

    onStartDateChange(e) {
      this.filter.startDate = e.detail.value
    },

    onEndDateChange(e) {
      this.filter.endDate = e.detail.value
    },

    onTypeChange(e) {
      const selectedType = this.transactionTypes[e.detail.value]
      this.filter.type = selectedType.value
      this.selectedTypeLabel = selectedType.label
    },

    onProjectTypeChange(e) {
      const selectedProjectType = this.projectTypes[e.detail.value]
      this.filter.projectType = selectedProjectType.value
      this.selectedProjectTypeLabel = selectedProjectType.label
    },

    resetFilter() {
      this.filter = {
        startDate: '',
        endDate: '',
        type: '',
        projectType: '',
        page: 0,
        size: 20,
        sortBy: 'createdAt',
        sortDir: 'desc'
      }
      this.selectedTypeLabel = ''
      this.selectedProjectTypeLabel = ''
      this.loadConsumptionRecords()
    },

    applyFilter() {
      this.filter.page = 0
      this.loadConsumptionRecords()
      this.showFilter = false
    },

    // Pagination
    prevPage() {
      if (this.currentPage > 0) {
        this.filter.page = this.currentPage - 1
        this.loadConsumptionRecords()
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.filter.page = this.currentPage + 1
        this.loadConsumptionRecords()
      }
    },

    // Add record modal
    showAddRecordModal() {
      this.showAddModal = true
    },

    hideAddRecordModal() {
      this.showAddModal = false
      this.resetNewRecordForm()
    },

    onNewRecordProjectTypeChange(e) {
      const selectedProjectType = this.projectTypes[e.detail.value]
      this.newRecord.projectType = selectedProjectType.value
    },

    resetNewRecordForm() {
      this.newRecord = {
        customerId: '',
        amount: '',
        projectType: '',
        notes: ''
      }
    },

    async submitNewRecord() {
      if (!this.newRecord.customerId || !this.newRecord.amount || !this.newRecord.projectType) {
        uni.showToast({
          title: '请填写必填字段',
          icon: 'error'
        })
        return
      }

      try {
        await createConsumptionRecord({
          customerId: parseInt(this.newRecord.customerId),
          amount: parseFloat(this.newRecord.amount),
          projectType: this.newRecord.projectType,
          notes: this.newRecord.notes
        })

        uni.showToast({
          title: '创建成功',
          icon: 'success'
        })

        this.hideAddRecordModal()
        this.loadConsumptionRecords()
        this.loadSummaryStats()
      } catch (error) {
        console.error('Failed to create record:', error)
        uni.showToast({
          title: error.message || '创建失败',
          icon: 'error'
        })
      }
    },

    // Helper methods
    getTransactionTypeLabel(type) {
      const typeMap = {
        'SPEND': '消费',
        'RECHARGE': '充值',
        'REFUND': '退款',
        'ADJUSTMENT': '调整'
      }
      return typeMap[type] || type
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
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.filter-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.filter-panel {
  background-color: white;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.filter-group {
  margin-bottom: 15px;
}

.filter-label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
  font-weight: bold;
}

.picker {
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 5px;
  background-color: white;
  color: #333;
}

.date-separator {
  margin: 0 10px;
  color: #666;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.reset-btn, .apply-btn {
  flex: 1;
  padding: 12px;
  border-radius: 5px;
  border: none;
  font-size: 14px;
}

.reset-btn {
  background-color: #f0f0f0;
  color: #333;
}

.apply-btn {
  background-color: #667eea;
  color: white;
}

.summary-stats {
  display: flex;
  background-color: white;
  margin: 10px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.stat-item {
  flex: 1;
  padding: 15px;
  text-align: center;
  border-right: 1px solid #eee;
}

.stat-item:last-child {
  border-right: none;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.loading, .empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
}

.records-list {
  padding: 10px;
}

.record-item {
  background-color: white;
  margin-bottom: 10px;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
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

.customer-phone {
  font-size: 12px;
  color: #666;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 5px;
}

.amount.SPEND {
  color: #e74c3c;
  background-color: #ffeaea;
}

.amount.RECHARGE {
  color: #27ae60;
  background-color: #eafaf1;
}

.record-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-bottom: 10px;
}

.detail-item {
  display: flex;
}

.detail-label {
  font-size: 12px;
  color: #666;
  margin-right: 5px;
  min-width: 30px;
}

.detail-value {
  font-size: 12px;
  color: #333;
}

.record-notes {
  border-top: 1px solid #eee;
  padding-top: 8px;
  margin-top: 8px;
}

.notes-label {
  font-size: 12px;
  color: #666;
  margin-right: 5px;
}

.notes-content {
  font-size: 12px;
  color: #333;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  gap: 15px;
}

.page-btn {
  background-color: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  font-size: 14px;
}

.page-btn:disabled {
  background-color: #ccc;
}

.page-info {
  color: #666;
  font-size: 14px;
}

.fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.fab-icon {
  color: white;
  font-size: 24px;
  font-weight: bold;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #666;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-group {
  margin-bottom: 15px;
}

.form-label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
}

.form-input, .form-textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 10px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-textarea {
  height: 80px;
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  flex: 1;
  padding: 12px;
  border-radius: 5px;
  border: none;
  font-size: 14px;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.submit-btn {
  background-color: #667eea;
  color: white;
}
</style>