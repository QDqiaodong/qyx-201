<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { kitApi, staffApi, transferApi, errorMessage } from '@/api'
import type { Kit, Staff, ScanVerification } from '@/types'

const props = defineProps<{
  modelValue: boolean
  /** 从教具列表进入时预选教具 */
  kitId?: number | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const kits = ref<Kit[]>([])
const staffList = ref<Staff[]>([])
const step = ref<1 | 2>(1)
const verifying = ref(false)
const confirming = ref(false)
const verifyError = ref('')
const confirmError = ref('')

const scanForm = ref({
  kitId: null as number | null,
  qrCode: '',
  operator: ''
})
const verification = ref<ScanVerification | null>(null)
const confirmForm = ref({
  newStaffId: null as number | null,
  reason: ''
})

const selectedKit = computed(() => kits.value.find(k => k.id === scanForm.value.kitId) || null)

watch(() => props.modelValue, async (visible) => {
  if (visible) {
    resetState()
    await loadData()
    if (props.kitId) {
      scanForm.value.kitId = props.kitId
    }
  }
})

function resetState() {
  step.value = 1
  verification.value = null
  verifyError.value = ''
  confirmError.value = ''
  scanForm.value = { kitId: null, qrCode: '', operator: '' }
  confirmForm.value = { newStaffId: null, reason: '' }
}

async function loadData() {
  try {
    kits.value = await kitApi.getAll()
    staffList.value = await staffApi.getAll()
  } catch (e) {
    ElMessage.error(errorMessage(e, '基础数据加载失败'))
  }
}

async function doVerify() {
  verifyError.value = ''
  if (!scanForm.value.kitId) {
    verifyError.value = '请先选择要更换责任人的教具'
    return
  }
  if (!scanForm.value.qrCode.trim()) {
    verifyError.value = '请先扫码：把这件教具标签上的码扫进来'
    return
  }
  if (!scanForm.value.operator.trim()) {
    verifyError.value = '请填写核对人'
    return
  }
  verifying.value = true
  try {
    verification.value = await transferApi.verify({
      kitId: scanForm.value.kitId,
      qrCode: scanForm.value.qrCode.trim(),
      operator: scanForm.value.operator.trim(),
      expectedOldStaffId: selectedKit.value?.responsibleStaffId ?? null
    })
    step.value = 2
  } catch (e) {
    verifyError.value = errorMessage(e, '扫码核对失败，请重试')
  } finally {
    verifying.value = false
  }
}

async function doConfirm() {
  confirmError.value = ''
  if (!verification.value) {
    confirmError.value = '请先扫码核对，核对通过后才能登记更换'
    return
  }
  if (!confirmForm.value.newStaffId) {
    confirmError.value = '请选择新责任人'
    return
  }
  confirming.value = true
  try {
    await transferApi.confirm({
      verificationId: verification.value.id,
      newStaffId: confirmForm.value.newStaffId,
      reason: confirmForm.value.reason
    })
    ElMessage.success('责任人更换成功')
    close()
    emit('success')
  } catch (e) {
    confirmError.value = errorMessage(e, '更换失败，请重试')
  } finally {
    confirming.value = false
  }
}

function backToScan() {
  step.value = 1
  verification.value = null
  scanForm.value.qrCode = ''
  confirmError.value = ''
}

function close() {
  emit('update:modelValue', false)
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    title="更换责任人"
    width="560px"
    @update:model-value="close"
    @closed="resetState"
  >
    <el-steps :active="step - 1" align-center finish-status="success" style="margin-bottom: 24px">
      <el-step title="扫码核对" />
      <el-step title="登记更换" />
    </el-steps>

    <!-- 第一步：扫码核对 -->
    <div v-if="step === 1">
      <el-alert
        type="info"
        :closable="false"
        title="先扫码后换人：请把这件教具标签上的二维码扫进来，系统按码值核对档案，核对通过才能登记更换。"
        style="margin-bottom: 16px"
      />
      <el-form label-width="100px">
        <el-form-item label="选择教具" required>
          <el-select
            v-model="scanForm.kitId"
            placeholder="请选择教具"
            :disabled="!!props.kitId"
            style="width: 100%"
          >
            <el-option v-for="kit in kits" :key="kit.id" :label="`${kit.kitCode} - ${kit.name}`" :value="kit.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedKit" label="当前责任人">
          <span>{{ selectedKit.responsibleStaffName || '未分配' }}</span>
        </el-form-item>
        <el-form-item label="扫码" required>
          <el-input
            v-model="scanForm.qrCode"
            placeholder="扫描或输入教具标签上的二维码值"
            autofocus
            clearable
            @keyup.enter="doVerify"
          />
        </el-form-item>
        <el-form-item label="核对人" required>
          <el-input v-model="scanForm.operator" placeholder="请输入核对人姓名" @keyup.enter="doVerify" />
        </el-form-item>
      </el-form>
      <el-alert v-if="verifyError" type="error" :title="verifyError" :closable="false" show-icon />
    </div>

    <!-- 第二步：登记更换 -->
    <div v-else>
      <el-alert
        type="success"
        :closable="false"
        title="扫码核对通过"
        style="margin-bottom: 16px"
      />
      <el-descriptions :column="1" border size="small" style="margin-bottom: 16px">
        <el-descriptions-item label="教具">
          {{ verification?.kitCode }} - {{ verification?.kitName }}
        </el-descriptions-item>
        <el-descriptions-item label="扫入码值">{{ verification?.scannedCode }}</el-descriptions-item>
        <el-descriptions-item label="核对人">{{ verification?.operator }}</el-descriptions-item>
        <el-descriptions-item label="核对时间">{{ verification?.verifyTime }}</el-descriptions-item>
        <el-descriptions-item label="当前责任人">{{ verification?.expectedOldStaffName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="凭证有效期至">{{ verification?.expireTime }}</el-descriptions-item>
      </el-descriptions>
      <el-form label-width="100px">
        <el-form-item label="新责任人" required>
          <el-select v-model="confirmForm.newStaffId" placeholder="请选择新责任人" style="width: 100%">
            <el-option v-for="staff in staffList" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="变更原因">
          <el-input v-model="confirmForm.reason" type="textarea" :rows="3" placeholder="请输入变更原因" />
        </el-form-item>
      </el-form>
      <el-alert v-if="confirmError" type="error" :title="confirmError" :closable="false" show-icon />
    </div>

    <template #footer>
      <template v-if="step === 1">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="verifying" @click="doVerify">扫码核对</el-button>
      </template>
      <template v-else>
        <el-button @click="backToScan">重新扫码</el-button>
        <el-button type="primary" :loading="confirming" @click="doConfirm">确认更换</el-button>
      </template>
    </template>
  </el-dialog>
</template>
