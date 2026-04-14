export async function parseJsonSafe(res) {
  const text = await res.text()
  if (!text) return {}
  try {
    return JSON.parse(text)
  } catch {
    return {}
  }
}

export async function apiGet(path) {
  const res = await fetch(path)
  const data = await parseJsonSafe(res)
  if (!res.ok) {
    const msg = data.message || data.error || res.statusText
    throw new Error(typeof msg === 'string' ? msg : '요청에 실패했습니다.')
  }
  return data
}
