/**
 * 退出登录逻辑
 * 用于处理用户退出登录的操作
 *
 * 重要修改：
 * 1. 添加了清除SSO票据的功能
 * 2. 添加了SSO登出逻辑，当配置了SSO时，重定向到SSO登录页
 */
import { useUserStoreWithOut } from '@/store/modules/user'
import router from '@/router'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { interactiveStoreWithOut } from '@/store/modules/interactive'
import { useCache } from '@/hooks/web/useCache'
// SSO相关导入
import { removeSsoTicket } from '@/utils/sso'
import { ssoConfig } from '@/config/sso'

const { wsCache } = useCache()
const permissionStore = usePermissionStoreWithOut()
const userStore = useUserStoreWithOut()
const interactiveStore = interactiveStoreWithOut()

/**
 * 退出登录处理函数
 * @param justClean 是否只清理数据不重定向
 * @param save_platform_status 是否保存平台状态
 */
export const logoutHandler = (justClean?: boolean, save_platform_status = false) => {
  // 清理用户数据
  userStore.clear()
  userStore.$reset()
  permissionStore.clear()
  permissionStore.$reset()
  interactiveStore.clear()
  interactiveStore.$reset()
  removeCache()
  // 清除SSO票据
  removeSsoTicket()

  let queryRedirectPath = '/workbranch/index'
  // 如果redirect参数中有值
  if (router.currentRoute.value.fullPath) {
    queryRedirectPath = router.currentRoute.value.fullPath as string
  }

  let pathname = window.location.pathname
  if (pathname) {
    if (pathname.includes('oidcbi/')) {
      if (save_platform_status) {
        return
      }
      pathname = pathname.replace('oidcbi/', '')
      if (pathname.includes('mobile.html')) {
        pathname = pathname.replace('mobile.html', '')
      }
      pathname = pathname.substring(0, pathname.length - 1)
      window.location.href = pathname + '/oidcbi/oidc/logout'
      return
    } else if (pathname.includes('casbi/')) {
      if (save_platform_status) {
        return
      }
      pathname = pathname.replace('casbi/', '')
      if (pathname.includes('mobile.html')) {
        pathname = pathname.replace('mobile.html', '')
      }
      pathname = pathname.substring(0, pathname.length - 1)
      const uri = window.location.href
      window.location.href = pathname + '/casbi/cas/logout?service=' + uri
      return
    }
    pathname = pathname.substring(0, pathname.length - 1)
  }

  if (wsCache.get('custom_auth_logout_url')) {
    window.location.href = wsCache.get('custom_auth_logout_url')
  }

  // 如果配置了SSO，重定向到SSO登录页
  if (ssoConfig.loginUrl) {
    window.location.href = window.location.origin + window.location.pathname
  } else {
    router.push(justClean ? queryRedirectPath : `/login?redirect=${queryRedirectPath}`)
  }
}

/**
 * 清除缓存
 */
const removeCache = () => {
  const keys = Object.keys(wsCache['storage'])
  keys.forEach(key => {
    if (
      key.startsWith('de-plugin-') ||
      key === 'de-platform-client' ||
      key === 'pwd-validity-period' ||
      key === 'xpack-model-distributed'
    ) {
      wsCache.delete(key)
    }
  })
}
