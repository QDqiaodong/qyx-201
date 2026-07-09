<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { kitApi, staffApi } from '@/api'
import type { Kit, Staff } from '@/types'

const kits = ref<Kit[]>([])
const staffList = ref<Staff[]>([])
const showDialog = ref(false)
const isEdit = ref(false)
const form = ref({
  id: 0,
  kitCode: '',
  name: '',
  category: '',
  adaptedClasses: '',
  specification: '',
  location: '',
  status: 'IN_USE',
  imageUrl: '',
  responsibleStaffId: null as number | null
})

const statusOptions = [
  { label: '使用中', value: 'IN_USE' },
  { label: '已停用', value: 'DISABLED' }
]

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    kits.value = await kitApi.getAll()
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
      kitCode: '',
      name: '',
      category: '',
      adaptedClasses: '',
      specification: '',
      location: '',
      status: 'IN_USE',
      imageUrl: '',
      responsibleStaffId: null
    }
  }
  showDialog.value = true
}

function editKit(row: Kit) {
  form.value = {
    id: row.id,
    kitCode: row.kitCode,
    name: row.name,
    category: row.category,
    adaptedClasses: row.adaptedClasses,
    specification: row.specification,
    location: row.location,
    status: row.status,
    imageUrl: row.imageUrl,
    responsibleStaffId: row.responsibleStaffId
  }
  openDialog(true)
}

async function saveKit() {
  try {
    if (isEdit.value) {
      await kitApi.update(form.value.id, form.value)
      alert('教具更新成功')
    } else {
      await kitApi.create(form.value)
      alert('教具创建成功')
    }
    showDialog.value = false
    await loadData()
  } catch (e) {
    alert('操作失败，请重试')
  }
}

async function deleteKit(id: number) {
  if (!confirm('确定要删除该教具吗？')) return
  try {
    await kitApi.delete(id)
    alert('教具删除成功')
    await loadData()
  } catch (e) {
    alert('删除失败，请重试')
  }
}
</script>

<template>
  <div class="kit-management">
    <div class="page-header">
      <h2>科普教具管理</h2>
      <el-button type="primary" @click="openDialog()">新增教具</el-button>
    </div>
    
    <el-table :data="kits" stripe border :max-height="600">
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
      <el-table-column prop="responsibleStaffName" label="责任人">
        <template #default="scope">
          {{ scope.row.responsibleStaffName || '未分配' }}
        </template>
      </el-table-column>
      <el-table-column label="图片" width="100">
        <template #default="scope">
          <el-image :src="scope.row.imageUrl || '/placeholder.png'" style="width: 60px; height: 60px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" @click="editKit(scope.row as Kit)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteKit(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑教具' : '新增教具'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="教具编号" required>
          <el-input v-model="form.kitCode" placeholder="请输入教具编号" />
        </el-form-item>
        <el-form-item label="教具名称" required>
          <el-input v-model="form.name" placeholder="请输入教具名称" />
        </el-form-item>
        <el-form-item label="科普品类" required>
          <el-input v-model="form.category" placeholder="如：物理实验、化学实验、生物标本" />
        </el-form-item>
        <el-form-item label="适配班级">
          <el-input v-model="form.adaptedClasses" placeholder="如：三年级、四年级" />
        </el-form-item>
        <el-form-item label="规格说明">
          <el-input v-model="form.specification" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="存放位置">
          <el-input v-model="form.location" placeholder="如：A区101室" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="责任人">
          <el-select v-model="form.responsibleStaffId" placeholder="请选择责任人">
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveKit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.kit-management {
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
