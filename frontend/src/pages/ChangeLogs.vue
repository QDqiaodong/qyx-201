<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { changeLogApi, kitApi, staffApi } from '@/api'
import type { ChangeLog, Kit, Staff, ChangeRequest } from '@/types'

const changeLogs = ref<ChangeLog[]>([])
const kits = ref<Kit[]>([])
const staffList = ref<Staff[]>([])
const showDialog = ref(false)
const form = ref({
  kitId: 0,
  newStaffId: 0,
  operator: '',
  reason: ''
})

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    changeLogs.value = await changeLogApi.getAll()
    kits.value = await kitApi.getAll()
    staffList.value = await staffApi.getAll()
  } catch (e) {
    console.error('Failed to load data:', e)
  }
}

function openDialog() {
  form.value = {
    kitId: 0,
    newStaffId: 0,
    operator: '',
    reason: ''
  }
  showDialog.value = true
}

async function changeResponsibleStaff() {
  try {
    const request: ChangeRequest = {
      kitId: form.value.kitId,
      newStaffId: form.value.newStaffId,
      operator: form.value.operator,
      reason: form.value.reason
    }
    await changeLogApi.changeResponsibleStaff(request)
    alert('责任人变更成功')
    showDialog.value = false
    await loadData()
  } catch (e) {
    alert('操作失败，请重试')
  }
}
</script>

<template>
  <div class="change-logs">
    <div class="page-header">
      <h2>责任人变更日志</h2>
      <el-button type="primary" @click="openDialog()">更换责任人</el-button>
    </div>
    
    <el-table :data="changeLogs" stripe border :max-height="600">
      <el-table-column prop="kitCode" label="教具编号" />
      <el-table-column prop="kitName" label="教具名称" />
      <el-table-column prop="oldStaffName" label="原责任人">
        <template #default="scope">
          {{ scope.row.oldStaffName || '无' }}
        </template>
      </el-table-column>
      <el-table-column prop="newStaffName" label="新责任人" />
      <el-table-column prop="changeTime" label="变更时间" />
      <el-table-column prop="operator" label="操作人" />
      <el-table-column prop="reason" label="变更原因">
        <template #default="scope">
          {{ scope.row.reason || '无' }}
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDialog" title="更换责任人" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择教具" required>
          <el-select v-model="form.kitId" placeholder="请选择教具">
            <el-option v-for="kit in kits" :key="kit.id" :label="`${kit.kitCode} - ${kit.name}`" :value="kit.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新责任人" required>
          <el-select v-model="form.newStaffId" placeholder="请选择新责任人">
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人" required>
          <el-input v-model="form.operator" placeholder="请输入操作人姓名" />
        </el-form-item>
        <el-form-item label="变更原因">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入变更原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="changeResponsibleStaff">确定变更</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.change-logs {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
