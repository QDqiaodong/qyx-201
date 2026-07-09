import axios from 'axios'
import type { Kit, Staff, ChangeLog, ChangeRequest, MonthlySummary } from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    throw error
  }
)

export const kitApi = {
  getAll: async (): Promise<Kit[]> => api.get('/kits'),
  getById: async (id: number): Promise<Kit> => api.get(`/kits/${id}`),
  create: async (data: Omit<Kit, 'id' | 'responsibleStaffName'>): Promise<Kit> => api.post('/kits', data),
  update: async (id: number, data: Omit<Kit, 'id' | 'responsibleStaffName'>): Promise<Kit> => api.put(`/kits/${id}`, data),
  delete: async (id: number): Promise<void> => api.delete(`/kits/${id}`),
  getByCategory: async (category: string): Promise<Kit[]> => api.get(`/kits/category/${category}`),
  getByStaff: async (staffId: number): Promise<Kit[]> => api.get(`/kits/staff/${staffId}`),
  getCategories: async (): Promise<string[]> => api.get('/kits/categories')
}

export const staffApi = {
  getAll: async (): Promise<Staff[]> => api.get('/staff'),
  getById: async (id: number): Promise<Staff> => api.get(`/staff/${id}`),
  create: async (data: Omit<Staff, 'id'>): Promise<Staff> => api.post('/staff', data),
  update: async (id: number, data: Omit<Staff, 'id'>): Promise<Staff> => api.put(`/staff/${id}`, data),
  delete: async (id: number): Promise<void> => api.delete(`/staff/${id}`),
  searchByName: async (name: string): Promise<Staff[]> => api.get(`/staff/search?name=${name}`),
  getByDepartment: async (department: string): Promise<Staff[]> => api.get(`/staff/department/${department}`)
}

export const changeLogApi = {
  changeResponsibleStaff: async (data: ChangeRequest): Promise<ChangeLog> => api.post('/change-logs/change', data),
  getAll: async (): Promise<ChangeLog[]> => api.get('/change-logs'),
  getByKit: async (kitId: number): Promise<ChangeLog[]> => api.get(`/change-logs/kit/${kitId}`),
  getByStaff: async (staffId: number): Promise<ChangeLog[]> => api.get(`/change-logs/staff/${staffId}`),
  getMonthlySummary: async (year: number, month: number): Promise<MonthlySummary> => api.get(`/change-logs/monthly?year=${year}&month=${month}`),
  getCurrentMonthSummary: async (): Promise<MonthlySummary> => api.get('/change-logs/monthly/current'),
  getAllMonthlySummaries: async (): Promise<MonthlySummary[]> => api.get('/change-logs/monthly/all')
}
