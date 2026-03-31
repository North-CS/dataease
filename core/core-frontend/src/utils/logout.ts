import { useUserStoreWithOut } from '@/store/modules/user'
import router from '@/router'
import { usePermissionStoreWithOut } from '@/store/modules/permission'
import { interactiveStoreWithOut } from '@/store/modules/interactive'
import { useCache } from '@/hooks/web/useCache'
import request from '@/config/axios'

import { useAppearanceStoreWithOut } from '@/store/modules/appearance'

const { wsCache } = useCache()
const appearanceStore = useAppearanceStoreWithOut()
const permissionStore = usePermissionStoreWithOut()
const userStore = useUserStoreWithOut()
const interactiveStore = interactiveStoreWithOut()

export const logoutHandler = (justClean?: boolean, save_platform_status = false) => {
  // alert("退出入口！")
  const ticket = getSsoTicket()
  request.post({ url: '/sso/logout', data: { ticket } })
  document.cookie = `sso.jd.com=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`
  userStore.clear()
  userStore.$reset()
  permissionStore.clear()
  permissionStore.$reset()
  interactiveStore.clear()
  interactiveStore.$reset()
  removeCache()
  let queryRedirectPath =
    appearanceStore.homeEnable === 'true' ? '/home/index' : '/workbranch/index'
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
  router.push(justClean ? queryRedirectPath : `/login?redirect=${queryRedirectPath}`)
}

const getSsoTicket = (): string | null => {
  // 修改点：箭头函数参数去掉了括号，且合并为一行，符合 Prettier 规范
  const cookieValue = document.cookie
    .split('; ')
    .find(row => row.startsWith(`sso.jd.com=`))
    ?.split('=')[1]
  return cookieValue ? decodeURIComponent(cookieValue) : null
}

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
