import request from './request'

export function borrowBook(userId: number, bookId: number, dueDays: number = 30) {
  return request({
    url: '/borrow',
    method: 'post',
    params: { userId, bookId, dueDays }
  })
}

export function returnBook(recordId: number) {
  return request({
    url: `/borrow/return/${recordId}`,
    method: 'post'
  })
}

export function getMyBorrows() {
  return request({
    url: '/borrow/my',
    method: 'get'
  })
}

export function getAllBorrows() {
  return request({
    url: '/borrow/list',
    method: 'get'
  })
}
