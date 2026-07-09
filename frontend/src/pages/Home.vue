<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { kitApi, staffApi, changeLogApi } from '@/api'

const kitCount = ref(0)
const staffCount = ref(0)
const changeCount = ref(0)
const currentMonthChanges = ref(0)

onMounted(async () => {
  try {
    const kits = await kitApi.getAll()
    kitCount.value = kits.length
    
    const staff = await staffApi.getAll()
    staffCount.value = staff.length
    
    const logs = await changeLogApi.getAll()
    changeCount.value = logs.length
    
    const summary = await changeLogApi.getCurrentMonthSummary()
    currentMonthChanges.value = summary.totalChanges
  } catch (e) {
    console.error('Failed to load statistics:', e)
  }
})
</script>

<template>
  <div class="home-page">
    <div class="welcome-section">
      <h1>欢迎使用研学基地科普教具管理系统</h1>
      <p>实时管理科普教具、绑定责任人、追踪变更记录</p>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="教具总数" :value="kitCount" suffix="件" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="教职工总数" :value="staffCount" suffix="人" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="变更记录" :value="changeCount" suffix="条" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card highlight">
          <el-statistic title="本月变更" :value="currentMonthChanges" suffix="条" />
        </el-card>
      </el-col>
    </el-row>
    
    <div class="feature-cards">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card" body-style="{ padding: '20px' }">
            <div class="feature-icon">📦</div>
            <h3>教具建档</h3>
            <p>管理科普教具基础信息，包括编号、品类、适配班级等</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" body-style="{ padding: '20px' }">
            <div class="feature-icon">👤</div>
            <h3>责任人绑定</h3>
            <p>为每件教具分配专属负责教职工，支持灵活更换</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" body-style="{ padding: '20px' }">
            <div class="feature-icon">📊</div>
            <h3>月度汇总</h3>
            <p>自动汇总当月全部责任人变更台账，生成变更总览</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  padding: 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-section h1 {
  color: #2d3748;
  margin-bottom: 10px;
}

.welcome-section p {
  color: #718096;
  font-size: 16px;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.stat-card.highlight :deep(.el-statistic__value) {
  color: #667eea;
}

.feature-cards {
  margin-top: 20px;
}

.feature-card {
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.feature-card h3 {
  color: #2d3748;
  margin-bottom: 10px;
}

.feature-card p {
  color: #718096;
  font-size: 14px;
}
</style>
