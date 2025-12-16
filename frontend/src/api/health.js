import request from '../utils/request'

export function checkHealth() {
  return request({
    url: '/api/health',
    method: 'get'
  })
}
