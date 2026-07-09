<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { kitApi, staffApi } from '@/api'
import type { Kit, Staff } from '@/types'

const staffList = ref<Staff[]>([])
const kits = ref<Kit[]>([])
const selectedStaffId = ref<number | null>(null)
const searchKeyword = ref('')

onMounted(async () => {
  await loadStaff()
})

async function loadStaff() {
  try {
    staffList.value = await staffApi.getAll()
  } catch (e) {
    console.error('Failed to load staff:', e)
  }
}

async function queryKits() {
  if (!selectedStaffId.value) {
    alert('请选择教职工')
    return
  }
  try {
    kits.value = await kitApi.getByStaff(selectedStaffId.value)
    alert(`查询到 ${kits.value.length} 件教具`)
  } catch (e) {
    alert('查询失败，请重试')
  }
}

async function searchByName() {
  if (!searchKeyword.value.trim()) {
    alert('请输入教职工姓名')
    return
  }
  try {
    const staff = await staffApi.searchByName(searchKeyword.value)
    if (staff.length > 0) {
      selectedStaffId.value = staff[0].id
      await queryKits()
    } else {
      alert('未找到匹配的教职工')
      kits.value = []
    }
  } catch (e) {
    alert('查询失败，请重试')
  }
}

function getSelectedStaffName() {
  const staff = staffList.value.find(s => s.id === selectedStaffId.value)
  return staff ? staff.name : ''
}
</script>

<template>
  <div class="staff-kits">
    <div class="page-header">
      <h2>按教职工查询教具</h2>
    </div>
    
    <el-card class="search-card">
      <div class="search-section">
        <div class="search-row">
          <span class="search-label">选择教职工：</span>
          <el-select v-model="selectedStaffId" style="width: 200px" placeholder="请选择教职工">
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
          <el-button type="primary" @click="queryKits">查询</el-button>
        </div>
        
        <div class="search-row">
          <span class="search-label">按姓名搜索：</span>
          <el-input v-model="searchKeyword" placeholder="输入教职工姓名" style="width: 200px" @keyup.enter="searchByName" />
          <el-button @click="searchByName">搜索</el-button>
        </div>
      </div>
    </el-card>
    
    <el-card v-if="selectedStaffId" class="result-card">
      <div class="result-header">
        <h3>{{ getSelectedStaffName() }} 名下的教具</h3>
        <span class="result-count">共 {{ kits.length }} 件</span>
      </div>
      
      <el-table :data="kits" stripe border :max-height="500">
        <el-table-column prop="kitCode" label="教具编号" />
        <el-table-column prop="name" label="教具名称" />
        <el-table-column prop="category" label="科普品类" />
        <el-table-column prop="adaptedClasses" label="适配班级" />
        <el-table-column prop="location" label="存放位置" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            {{ scope.row.status === 'IN_USE' ? '使用中' : '已停用' }}
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="kits.length === 0" class="empty-message">
        该教职工暂无绑定的教具
      </div>
    </el-card>
    
    <div v-if="!selectedStaffId" class="empty-hint">
      <el-card class="hint-card">
        <p>请选择或搜索教职工，查询其名下负责的全部教具</p>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.staff-kits {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-label {
  font-weight: 500;
}

.result-card {
  margin-top: 20px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.result-count {
  color: #667eea;
  font-size: 16px;
}

.empty-message {
  text-align: center;
  padding: 40px;
  color: #718096;
}

.empty-hint {
  margin-top: 50px;
}

.hint-card {
  text-align: center;
  padding: 40px;
  color: #718096;
}
</style>
