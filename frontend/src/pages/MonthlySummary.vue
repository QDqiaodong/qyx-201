<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { changeLogApi } from '@/api'
import type { MonthlySummary } from '@/types'

const summaries = ref<MonthlySummary[]>([])
const currentSummary = ref<MonthlySummary | null>(null)
const selectedYear = ref(new Date().getFullYear())
const selectedMonth = ref(new Date().getMonth() + 1)
const yearOptions = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i)
const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1)

onMounted(async () => {
  await loadAllSummaries()
  await loadCurrentMonthSummary()
})

async function loadAllSummaries() {
  try {
    summaries.value = await changeLogApi.getAllMonthlySummaries()
  } catch (e) {
    console.error('Failed to load summaries:', e)
  }
}

async function loadCurrentMonthSummary() {
  try {
    currentSummary.value = await changeLogApi.getMonthlySummary(selectedYear.value, selectedMonth.value)
  } catch (e) {
    console.error('Failed to load summary:', e)
  }
}

async function querySummary() {
  try {
    currentSummary.value = await changeLogApi.getMonthlySummary(selectedYear.value, selectedMonth.value)
    alert('查询成功')
  } catch (e) {
    alert('查询失败，请重试')
  }
}
</script>

<template>
  <div class="monthly-summary">
    <div class="page-header">
      <h2>月度变更日志汇总</h2>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-bar">
        <span>选择月份：</span>
        <el-select v-model="selectedYear" style="width: 120px">
          <el-option v-for="year in yearOptions" :key="year" :label="year + '年'" :value="year" />
        </el-select>
        <el-select v-model="selectedMonth" style="width: 100px">
          <el-option v-for="month in monthOptions" :key="month" :label="month + '月'" :value="month" />
        </el-select>
        <el-button type="primary" @click="querySummary">查询</el-button>
      </div>
    </el-card>
    
    <el-card class="summary-card" v-if="currentSummary">
      <div class="summary-header">
        <h3>{{ currentSummary.year }}年{{ currentSummary.month }}月变更台账</h3>
        <div class="summary-stat">
          <span class="stat-label">本月变更总数：</span>
          <span class="stat-value">{{ currentSummary.totalChanges }}</span>
          <span class="stat-unit">条</span>
        </div>
      </div>
      
      <el-table :data="currentSummary.changeLogs" stripe border :max-height="500">
        <el-table-column prop="kitCode" label="教具编号" width="110" />
        <el-table-column prop="kitName" label="教具名称" width="130" />
        <el-table-column prop="oldStaffName" label="原责任人" width="90">
          <template #default="scope">
            {{ scope.row.oldStaffName || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="newStaffName" label="新责任人" width="90" />
        <el-table-column prop="changeTime" label="变更时间" width="165" />
        <el-table-column prop="operator" label="操作人" width="90" />
        <el-table-column prop="scannedCode" label="扫码码值" width="140">
          <template #default="scope">
            {{ scope.row.scannedCode || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="verifyOperator" label="核对人" width="90">
          <template #default="scope">
            {{ scope.row.verifyOperator || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="verifyTime" label="核对时间" width="165">
          <template #default="scope">
            {{ scope.row.verifyTime || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="变更原因">
          <template #default="scope">
            {{ scope.row.reason || '无' }}
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="currentSummary.changeLogs.length === 0" class="empty-message">
        本月暂无责任人变更记录
      </div>
    </el-card>
    
    <div class="history-section">
      <h3>历史月度汇总</h3>
      <el-row :gutter="20">
        <el-col v-for="summary in summaries" :key="`${summary.year}-${summary.month}`" :span="6">
          <el-card class="summary-item" @click="selectedYear = summary.year; selectedMonth = summary.month; querySummary()">
            <div class="summary-title">{{ summary.year }}年{{ summary.month }}月</div>
            <div class="summary-count">变更 {{ summary.totalChanges }} 条</div>
          </el-card>
        </el-col>
      </el-row>
      
      <div v-if="summaries.length === 0" class="empty-message">
        暂无历史变更记录
      </div>
    </div>
  </div>
</template>

<style scoped>
.monthly-summary {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-card {
  margin-bottom: 30px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.summary-stat {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
}

.stat-unit {
  font-size: 16px;
  color: #718096;
}

.empty-message {
  text-align: center;
  padding: 40px;
  color: #718096;
}

.history-section {
  margin-top: 30px;
}

.history-section h3 {
  margin-bottom: 20px;
}

.summary-item {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.summary-item:hover {
  transform: translateY(-5px);
}

.summary-title {
  font-size: 18px;
  font-weight: bold;
  color: #2d3748;
  margin-bottom: 10px;
}

.summary-count {
  font-size: 14px;
  color: #667eea;
}
</style>
