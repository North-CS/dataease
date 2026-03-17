/**
 * SSO配置文件
 * 用于配置SSO相关的参数
 *
 * 重要修改：
 * 1. 配置了SSO登录地址、验证地址和登出地址
 * 2. 配置了票据参数名、重定向参数名
 * 3. 配置了系统回调地址
 */
export const ssoConfig = {
  // SSO登录地址 - 跳转到SSO统一登录页面
  loginUrl: 'http://localhost:8081',
  // SSO验证地址 - 验证SSO票据的有效性
  verifyUrl: 'http://localhost:8081/api/verify',
  // SSO登出地址 - 销毁SSO登录态
  logoutUrl: 'http://localhost:8081/api/logout',
  // 票据参数名 - SSO重定向回系统时携带的临时票据参数名
  ticketParam: 'sso_service_ticket',
  // 重定向参数名 - 跳转到SSO登录页面时携带的重定向地址参数名
  redirectParam: 'redirect',
  // 系统回调地址 - SSO登录成功后重定向回的地址
  callbackUrl: window.origin + window.location.pathname
}
