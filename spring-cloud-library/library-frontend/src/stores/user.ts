import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, setUser, getUser, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const userInfo = ref<any>(getUser())

  function setLogin(tokenValue: string, user: any) {
    token.value = tokenValue
    userInfo.value = user
    setToken(tokenValue)
    setUser(user)
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
  }

  function isAdmin(): boolean {
    return userInfo.value?.role === 'ADMIN'
  }

  function getUserId(): number | null {
    return userInfo.value?.id || null
  }

  return { token, userInfo, setLogin, logout, isAdmin, getUserId }
})
