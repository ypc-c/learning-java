import request from './request'

export function getNoticeList(params: { page?: number; size?: number; type?: string } = {}) {
  return request({
    url: '/notice/list',
    method: 'get',
    params
  })
}

export function getMyNotices(params: { page?: number; size?: number } = {}) {
  return request({
    url: '/notice/my',
    method: 'get',
    params
  })
}

export function getNoticeById(id: number) {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

export function createNotice(data: { title: string; content: string; type: string }) {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

export function deleteNotice(id: number) {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}
