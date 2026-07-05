import request from './request'

export function getBookList(params: {
  page?: number
  size?: number
  keyword?: string
  category?: string
}) {
  return request({
    url: '/book/list',
    method: 'get',
    params
  })
}

export function getBookById(id: number) {
  return request({
    url: `/book/${id}`,
    method: 'get'
  })
}

export function addBook(data: any) {
  return request({
    url: '/book',
    method: 'post',
    data
  })
}

export function updateBook(id: number, data: any) {
  return request({
    url: `/book/${id}`,
    method: 'put',
    data
  })
}

export function deleteBook(id: number) {
  return request({
    url: `/book/${id}`,
    method: 'delete'
  })
}
