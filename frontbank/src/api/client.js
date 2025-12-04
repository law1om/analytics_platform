export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

async function request(path, options = {}) {
  const token = localStorage.getItem('authToken')
  const headers = new Headers(options.headers || {})
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }
  if (token) {
    headers.set('Authorization', token)
  }
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })
  if (!response.ok) {
    const text = await response.text()
    let payload
    try {
      payload = JSON.parse(text)
    } catch {
      payload = { message: text || 'Request failed' }
    }
    const error = new Error(payload.message || 'Request failed')
    error.status = response.status
    error.payload = payload
    throw error
  }
  const contentType = response.headers.get('content-type')
  if (contentType && contentType.includes('application/json')) {
    return response.json()
  }
  return response.text()
}

export function fetchDivisions() {
  return request('/divisions')
}

export function uploadExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request('/tasks/import', {
    method: 'POST',
    body: formData,
  })
}
