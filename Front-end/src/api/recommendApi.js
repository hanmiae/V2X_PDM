import { apiGet, parseJsonSafe } from './client'

export function getRecommendSummary() {
  return apiGet('/api/recommend/summary')
}

export function getRecommendList(page = 0, size = 5) {
  const q = new URLSearchParams({ page: String(page), size: String(size) })
  return apiGet(`/api/recommend/list?${q}`)
}

export async function downloadRecommendReport(riskResultId) {
  const res = await fetch(`/api/recommend/report/${riskResultId}`)
  if (!res.ok) {
    const data = await parseJsonSafe(res)
    const msg = data.message || data.error || res.statusText
    throw new Error(typeof msg === 'string' ? msg : '보고서를 받지 못했습니다.')
  }
  const blob = await res.blob()
  const filename = `maint-report-${riskResultId}.txt`
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.rel = 'noopener'
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}
