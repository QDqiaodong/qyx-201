<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { changeLogApi, transferApi } from '@/api'
import type { ChangeLog, ScanVerification } from '@/types'
import KitTransferDialog from '@/components/KitTransferDialog.vue'

const changeLogs = ref<ChangeLog[]>([])
const verifications = ref<ScanVerification[]>([])
const showTransferDialog = ref(false)
const activeTab = ref('logs')

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    changeLogs.value = await changeLogApi.getAll()
    verifications.value = await transferApi.getVerifications()
  } catch (e) {
    console.error('Failed to load data:', e)
  }
}

async function onTransferSuccess() {
  await loadData()
}

function resultLabel(result: string): string {
  switch (result) {
    case 'SUCCESS': return '核对通过'
    case 'FAIL_CODE_UNKNOWN': return '码无法识别'
    case 'FAIL_CODE_MISMATCH': return '码与教具不符'
    case 'FAIL_OWNER_CHANGED': return '已不在原责任人名下'
    default: return result
  }
}

function resultTagType(result: string): 'success' | 'danger' {
  return result === 'SUCCESS' ? 'success' : 'danger'
}

function usageLabel(v: ScanVerification): string {
  if (v.result !== 'SUCCESS') return '—'
  if (v.consumed) return '已用于更换'
  if (v.expireTime && new Date(v.expireTime).getTime() < Date.now()) return '已超时失效'
  return '待使用（限时）'
}

function usageTagType(v: ScanVerification): 'info' | 'success' | 'warning' {
  if (v.result !== 'SUCCESS') return 'info'
  if (v.consumed) return 'success'
  if (v.expireTime && new Date(v.expireTime).getTime() < Date.now()) return 'info'
  return 'warning'
}
</script>

<template>
  <div class="change-logs">
    <div class="page-header">
      <h2>责任人变更日志</h2>
      <el-button type="primary" @click="showTransferDialog = true">更换责任人</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="变更日志" name="logs">
        <el-table :data="changeLogs" stripe border :max-height="600">
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
      </el-tab-pane>

      <el-tab-pane label="扫码核对记录" name="verifications">
        <el-table :data="verifications" stripe border :max-height="600">
          <el-table-column prop="verifyTime" label="核对时间" width="165" />
          <el-table-column prop="kitCode" label="教具编号" width="110" />
          <el-table-column prop="kitName" label="教具名称" width="130" />
          <el-table-column prop="scannedCode" label="扫入码值" width="140" />
          <el-table-column prop="operator" label="核对人" width="90" />
          <el-table-column prop="expectedOldStaffName" label="核对时责任人" width="110">
            <template #default="scope">
              {{ scope.row.expectedOldStaffName || '无' }}
            </template>
          </el-table-column>
          <el-table-column label="核对结果" width="140">
            <template #default="scope">
              <el-tag :type="resultTagType(scope.row.result)">{{ resultLabel(scope.row.result) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="failReason" label="失败原因">
            <template #default="scope">
              {{ scope.row.failReason || '—' }}
            </template>
          </el-table-column>
          <el-table-column label="使用情况" width="120">
            <template #default="scope">
              <el-tag :type="usageTagType(scope.row)">{{ usageLabel(scope.row) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <KitTransferDialog v-model="showTransferDialog" @success="onTransferSuccess" />
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
