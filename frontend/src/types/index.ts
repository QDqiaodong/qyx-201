export interface Kit {
  id: number
  kitCode: string
  qrCode: string | null
  name: string
  category: string
  adaptedClasses: string
  specification: string
  location: string
  status: string
  imageUrl: string
  responsibleStaffId: number | null
  responsibleStaffName: string | null
}

export interface Staff {
  id: number
  staffCode: string
  name: string
  department: string
  position: string
  phone: string
  email: string
  status: string
}

export interface ChangeLog {
  id: number
  kitId: number
  kitCode: string
  kitName: string
  oldStaffId: number | null
  oldStaffName: string | null
  newStaffId: number
  newStaffName: string
  changeTime: string
  operator: string
  reason: string | null
  verificationId: number | null
  scannedCode: string | null
  verifyOperator: string | null
  verifyTime: string | null
}

export interface ScanVerifyRequest {
  kitId: number
  qrCode: string
  operator: string
  expectedOldStaffId: number | null
}

export interface ScanVerification {
  id: number
  kitId: number
  kitCode: string
  kitName: string
  scannedCode: string
  operator: string
  expectedOldStaffId: number | null
  expectedOldStaffName: string | null
  result: string
  failReason: string | null
  verifyTime: string
  expireTime: string | null
  consumed: boolean
}

export interface TransferConfirmRequest {
  verificationId: number
  newStaffId: number
  reason: string
}

export interface MonthlySummary {
  year: number
  month: number
  totalChanges: number
  changeLogs: ChangeLog[]
}
