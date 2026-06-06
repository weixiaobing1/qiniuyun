import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 180_000
})

http.interceptors.response.use(
  resp => {
    const body = resp.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) return body.data
      const err = new Error(body.message || '业务错误')
      err.code = body.code
      return Promise.reject(err)
    }
    return body
  },
  err => {
    const msg = err.response?.data?.message || err.message || '请求失败'
    return Promise.reject(new Error(msg))
  }
)

export default http
