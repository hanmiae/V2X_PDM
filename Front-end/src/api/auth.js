import api from '@/api'

function messageFromAxios(e) {
  const d = e?.response?.data
  if (d == null) return e?.message || '요청에 실패했습니다.'
  if (typeof d === 'string') return d
  if (typeof d.message === 'string') return d.message
  if (typeof d.error === 'string') return d.error
  if (typeof d.detail === 'string') return d.detail
  return e?.message || '요청에 실패했습니다.'
}

export async function signup(payload) {
  try {
    const { data } = await api.post('/api/admins/signup', payload)
    return data
  } catch (e) {
    throw new Error(messageFromAxios(e))
  }
}

export async function login(payload) {
  try {
    const { data } = await api.post('/api/admins/login', payload)
    return data
  } catch (e) {
    throw new Error(messageFromAxios(e))
  }
}
