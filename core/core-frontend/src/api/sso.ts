/**
 * SSO API接口
 * 用于与后端SSO相关接口进行交互
 *
 * 重要修改：
 * 1. 实现了SSO登录接口 - 兑换正式票据
 * 2. 实现了SSO验证票据接口 - 验证登录态
 * 3. 实现了SSO登出接口 - 销毁正式票据
 * 4. 修改了接口路径，与需求文档保持一致
 */
import request from '@/config/axios'

/**
 * SSO登录接口 - 兑换正式票据
 * @param ticket 临时票据
 * @returns 登录结果，包含token和过期时间
 */
export const ssoLoginApi = (ticket: string) => {
  return request.post({ url: '/sso/login', data: { ticket } })
}

/**
 * SSO验证票据接口 - 验证登录态
 * @param ticket SSO票据
 * @returns 验证结果
 */
export const ssoVerifyTicketApi = (ticket: string) => {
  return request.post({ url: '/sso/verify', data: { ticket } })
}

/**
 * SSO登出接口 - 销毁正式票据
 * @returns 登出结果
 */
export const ssoLogoutApi = (ticket: string) => {
  alert("进入等出逻辑")
  return request.post({ url: '/sso/logout',data:{ ticket } })
}
