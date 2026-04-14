import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.DEV ? 'http://localhost:1004' : '',
  headers: {
    'Content-Type': 'application/json',
  },
})

export default api
