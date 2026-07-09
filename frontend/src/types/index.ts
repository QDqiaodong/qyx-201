export interface Kit {
  id: number
  kitCode: string
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
}

export interface ChangeRequest {
  kitId: number
  newStaffId: number
  operator: string
  reason: string
}

export interface MonthlySummary {
  year: number
  month: number
  totalChanges: number
  changeLogs: ChangeLog[]
}
