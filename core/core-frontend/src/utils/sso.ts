/**
 * SSO工具函数
 * 用于处理SSO相关的操作，如Cookie操作、URL参数提取、重定向等
 *
 * 重要修改：
 * 1. 实现了从Cookie中读取SSO票据的功能
 * 2. 实现了设置SSO票据到Cookie的功能
 * 3. 实现了移除SSO票据的功能
 * 4. 实现了从URL中提取参数的功能
 * 5. 实现了重定向到SSO登录页的功能
 * 6. 实现了检查是否已登录的功能
 */
import { useCache } from '@/hooks/web/useCache'
import { ssoConfig } from '@/config/sso'
import {ssoLogoutApi} from "@/api/sso";

const { wsCache } = useCache()

/**
 * SSO票据名称
 * 注意：这里使用的是临时名称，实际应该使用ssoConfig中的配置
 */
const TICKET_NAME = 'sso.jd.com'

/**
 * 从Cookie中读取SSO票据
 * @returns SSO票据值，如果不存在则返回null
 */
export const getSsoTicket = (): string | null => {
  const cookieValue = document.cookie
    .split('; ')
    .find(row => row.startsWith(`${TICKET_NAME}=`))
    ?.split('=')[1]
  return cookieValue ? decodeURIComponent(cookieValue) : null
}

/**
 * 设置SSO票据到Cookie
 * @param ticket SSO票据值
 */
export const setSsoTicket = (ticket: string): void => {
  const expirationDate = new Date()
  expirationDate.setDate(expirationDate.getDate() + 7) // 7天过期
  document.cookie = `${TICKET_NAME}=${encodeURIComponent(ticket)}; expires=${expirationDate.toUTCString()}; path=/`
}

/**
 * 移除SSO票据
 */
export const removeSsoTicket = (): void => {
  const ticket = getSsoTicket();
  alert("移除sso票据"+ ticket)
  ssoLogoutApi(ticket);
  document.cookie = `${TICKET_NAME}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`
}

/**
 * 从URL中提取参数
 * @param paramName 参数名
 * @returns 参数值，如果不存在则返回null
 */
export const getUrlParam = (paramName: string): string | null => {
  const urlParams = new URLSearchParams(window.location.search)
  return urlParams.get(paramName)
}

/**
 * 重定向到SSO登录页
 */
export const redirectToSsoLogin = (): void => {
  const redirectUrl = encodeURIComponent(window.location.href)
  window.location.href = `${ssoConfig.loginUrl}?${ssoConfig.redirectParam}=${redirectUrl}`
}

/**
 * 检查是否已登录（基于SSO票据）
 * @returns 是否已登录
 */
export const checkSsoLogin = (): boolean => {
  return !!getSsoTicket()
}
