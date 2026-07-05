import request from './request'

export function login(username: string, password: string) {
  return request({
    url: '/user/login',
    method: 'post',
    data: { username, password }
  })
}

export function register(data: {
  username: string
  password: string
  realName?: string
  role?: string
  phone?: string
  email?: string
}) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function getUserList() {
  return request({
    url: '/user/list',
    method: 'get'
  })
}
