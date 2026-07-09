<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { staffApi } from '@/api'
import type { Staff } from '@/types'

const staffList = ref<Staff[]>([])
const showDialog = ref(false)
const isEdit = ref(false)
const form = ref({
  id: 0,
  staffCode: '',
  name: '',
  department: '',
  position: '',
  phone: '',
  email: '',
  status: 'ACTIVE'
})

const statusOptions = [
  { label: '在职', value: 'ACTIVE' },
  { label: '离职', value: 'INACTIVE' }
]

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    staffList.value = await staffApi.getAll()
  } catch (e) {
    console.error('Failed to load data:', e)
  }
}

function openDialog(edit = false) {
  isEdit.value = edit
  if (!edit) {
    form.value = {
      id: 0,
      staffCode: '',
      name: '',
      department: '',
      position: '',
      phone: '',
      email: '',
      status: 'ACTIVE'
    }
  }
  showDialog.value = true
}

function editStaff(row: Staff) {
  form.value = {
    id: row.id,
    staffCode: row.staffCode,
    name: row.name,
    department: row.department,
    position: row.position,
    phone: row.phone,
    email: row.email,
    status: row.status
  }
  openDialog(true)
}

async function saveStaff() {
  try {
    if (isEdit.value) {
      await staffApi.update(form.value.id, form.value)
      alert('教职工信息更新成功')
    } else {
      await staffApi.create(form.value)
      alert('教职工创建成功')
    }
    showDialog.value = false
    await loadData()
  } catch (e) {
    alert('操作失败，请重试')
  }
}

async function deleteStaff(id: number) {
  if (!confirm('确定要删除该教职工吗？')) return
  try {
    await staffApi.delete(id)
    alert('教职工删除成功')
    await loadData()
  } catch (e) {
    alert('删除失败，请重试')
  }
}
</script>

<template>
  <div class="staff-management">
    <div class="page-header">
      <h2>教职工管理</h2>
      <el-button type="primary" @click="openDialog()">新增教职工</el-button>
    </div>
    
    <el-table :data="staffList" stripe border :max-height="600">
      <el-table-column prop="staffCode" label="工号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="department" label="部门" />
      <el-table-column prop="position" label="职位" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          {{ scope.row.status === 'ACTIVE' ? '在职' : '离职' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" @click="editStaff(scope.row as Staff)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteStaff(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑教职工' : '新增教职工'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="工号" required>
          <el-input v-model="form.staffCode" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="form.department" placeholder="请输入部门" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="form.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveStaff">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.staff-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}
</style>
