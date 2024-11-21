<template>
  <el-tooltip
    v-if="props.weight >= 7 && props.inGrid"
    effect="dark"
    :content="t('visualization.share')"
    placement="top"
  >
    <el-icon class="hover-icon hover-icon-in-table share-button-icon" @click.stop="share">
      <Icon name="icon_share-label_outlined"><icon_shareLabel_outlined class="svg-icon" /></Icon>
    </el-icon>
  </el-tooltip>
  <el-button v-if="props.weight >= 7 && props.isButton" @click.stop="share" icon="Share">{{
    t('visualization.share')
  }}</el-button>

  <el-dialog
    v-if="dialogVisible && props.weight >= 7"
    class="copy-link_dialog"
    :class="{
      'hidden-footer': !shareEnable || showTicket,
      'is-ticket-dialog': shareEnable && showTicket
    }"
    v-model="dialogVisible"
    :close-on-click-modal="true"
    :append-to-body="true"
    :before-close="beforeClose"
    :title="t('work_branch.public_link_share')"
    width="480px"
    :show-close="false"
  >
    <div class="share-dialog-container" :class="{ 'hidden-link-container': showTicket }">
      <div class="copy-link">
        <div class="open-share flex-align-center">
          <el-switch size="small" v-model="shareEnable" @change="enableSwitcher" />
          {{ shareTips }}
        </div>
        <div v-if="shareEnable" class="custom-link-line">
          <el-input
            ref="linkUuidRef"
            placeholder=""
            v-model="state.detailInfo.uuid"
            :disabled="!linkCustom"
            @blur="finishEditUuid"
          >
            <template v-if="!linkCustom" #prefix>
              {{ formatLinkBase() }}
            </template>
          </el-input>
          <el-button v-if="linkCustom" text @click="finishEditUuid">{{
            t('components.complete')
          }}</el-button>
          <el-button v-else @click="editUuid" size="default" plain>
            <template #icon>
              <icon name="icon_admin_outlined"><icon_admin_outlined class="svg-icon" /></icon>
            </template>
          </el-button>
        </div>

        <div v-if="shareEnable" class="exp-container">
          <el-checkbox
            ref="expCheckbox"
            :disabled="!shareEnable"
            v-model="overTimeEnable"
            @change="expEnableSwitcher"
          >
            <div class="checkbox-span">
              <span>{{ t('visualization.over_time') }}</span>
              <span class="pe-require" :class="{ 'pe-tips-hidden': !sharePeRequire }">
                <span>*</span>
              </span>
            </div>
          </el-checkbox>
          <div class="inline-share-item-picker">
            <el-date-picker
              :clearable="false"
              size="small"
              v-if="state.detailInfo.exp"
              class="share-exp-picker"
              v-model="state.detailInfo.exp"
              type="datetime"
              placeholder=""
              :shortcuts="shortcuts"
              @change="expChangeHandler"
              :disabled-date="disabledDate"
              value-format="x"
            />
            <span v-if="expError" class="exp-error">{{ t('work_branch.share_time_limit') }}</span>
          </div>
        </div>
        <div v-if="shareEnable" class="pwd-container">
          <el-checkbox
            ref="pwdCheckbox"
            :disabled="!shareEnable"
            v-model="passwdEnable"
            @change="pwdEnableSwitcher"
          >
            <div class="checkbox-span">
              <span>{{ t('visualization.passwd_protect') }}</span>
              <span class="pe-require" :class="{ 'pe-tips-hidden': !sharePeRequire }">
                <span>*</span>
              </span>
            </div>
          </el-checkbox>
          <div class="auto-pwd-container" v-if="passwdEnable">
            <el-checkbox
              :disabled="!shareEnable"
              v-model="state.detailInfo.autoPwd"
              @change="autoEnableSwitcher"
              :label="t('visualization.auto_pwd')"
            />
          </div>
          <div class="inline-share-item" v-if="passwdEnable">
            <el-input
              ref="pwdRef"
              v-model="state.detailInfo.pwd"
              :readonly="state.detailInfo.autoPwd"
              size="small"
              @blur="validatePwdFormat"
            >
              <template #append>
                <div class="share-pwd-opt">
                  <div
                    v-if="state.detailInfo.autoPwd"
                    @click.stop="resetPwd"
                    class="share-reset-container"
                  >
                    <span>{{ t('commons.reset') }}</span>
                  </div>
                  <div @click.stop="copyPwd" class="share-reset-container">
                    <span>{{ t('commons.copy') }}</span>
                  </div>
                </div>
              </template>
            </el-input>
          </div>
        </div>
      </div>
    </div>
    <div v-if="shareEnable && showTicket" class="share-ticket-container">
      <share-ticket
        :uuid="state.detailInfo.uuid"
        :resource-id="props.resourceId"
        :ticket-require="state.detailInfo.ticketRequire"
        @require-change="updateRequireTicket"
        @close="closeTicket"
      />
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button secondary @click="openTicket">{{ t('work_branch.ticket_setting') }}</el-button>
        <el-button :disabled="!shareEnable || expError" type="primary" @click.stop="copyInfo">
          {{ t('visualization.copy_link') }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import dvShare from '@/assets/svg/dv-share.svg'
import icon_shareLabel_outlined from '@/assets/svg/icon_share-label_outlined.svg'
import icon_admin_outlined from '@/assets/svg/icon_admin_outlined.svg'
import { useI18n } from '@/hooks/web/useI18n'
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import request from '@/config/axios'
import { propTypes } from '@/utils/propTypes'
import { ShareInfo, SHARE_BASE, shortcuts } from './option'
import { ElMessage, ElLoading } from 'element-plus-secondary'
import useClipboard from 'vue-clipboard3'
import ShareTicket from './ShareTicket.vue'
import { useEmbedded } from '@/store/modules/embedded'
import { useShareStoreWithOut } from '@/store/modules/share'
const shareStore = useShareStoreWithOut()
const embeddedStore = useEmbedded()
const { toClipboard } = useClipboard()
const { t } = useI18n()
const props = defineProps({
  inGrid: propTypes.bool.def(false),
  resourceId: propTypes.string.def(''),
  resourceType: propTypes.string.def(''),
  weight: propTypes.number.def(0),
  isButton: propTypes.bool.def(false)
})
const pwdRef = ref(null)
const expCheckbox = ref()
const pwdCheckbox = ref()
const loadingInstance = ref<any>(null)
const dialogVisible = ref(false)
const overTimeEnable = ref(false)
const passwdEnable = ref(false)
const shareEnable = ref(false)
const linkAddr = ref('')
const expError = ref(false)
const linkCustom = ref(false)
const linkUuidRef = ref(null)
const showTicket = ref(false)
const state = reactive({
  detailInfo: {
    id: '',
    uuid: '',
    pwd: '',
    exp: 0,
    autoPwd: true,
    ticketRequire: false
  } as ShareInfo
})
const emits = defineEmits(['loaded'])
const shareTips = computed(
  () =>
    `${t('work_branch.open_link_hint')}${
      props.resourceType === 'dashboard'
        ? t('work_branch.dashboard')
        : t('work_branch.big_data_screen')
    }`
)
const sharePeRequire = computed(() => shareStore.getSharePeRequire)
const editUuid = () => {
  linkCustom.value = true
  nextTick(() => {
    if (linkUuidRef?.value) {
      linkUuidRef.value.input.focus()
    }
  })
}
const validateUuid = async () => {
  const val = state.detailInfo.uuid
  const className = 'link-uuid-error-msg'
  if (!val) {
    showPageError(t('commons.cannot_be_null'), linkUuidRef, className)
    return false
  }
  const regex = /^[a-zA-Z0-9]{8,16}$/
  const result = regex.test(val)
  if (!result) {
    showPageError(t('work_branch.uuid_checker'), linkUuidRef, className)
  } else {
    const msg = await uuidValidateApi(val)
    showPageError(msg, linkUuidRef, className)
    return !msg
  }
  return result
}

const uuidValidateApi = async val => {
  const url = '/share/editUuid'
  const data = { resourceId: props.resourceId, uuid: val }
  const res = await request.post({ url, data })
  return res.data
}
const finishEditUuid = async () => {
  const uuidValid = await validateUuid()
  linkCustom.value = !uuidValid
}
const copyPwd = async () => {
  if (shareEnable.value && passwdEnable.value) {
    if (!state.detailInfo.autoPwd && existErrorMsg('link-pwd-error-msg')) {
      ElMessage.warning(t('work_branch.error_password_hint'))
      return
    }
    try {
      await toClipboard(state.detailInfo.pwd)
      ElMessage.success(t('common.copy_success'))
    } catch (e) {
      ElMessage.warning(t('common.copy_unsupported'))
    }
  } else {
    ElMessage.warning(t('common.copy_unsupported'))
  }
}
const copyInfo = async () => {
  if (shareEnable.value) {
    try {
      if (existErrorMsg('link-uuid-error-msg')) {
        ElMessage.warning(t('work_branch.error_link_hint'))
        return
      }
      if (passwdEnable.value && !state.detailInfo.autoPwd && existErrorMsg('link-pwd-error-msg')) {
        ElMessage.warning(t('work_branch.error_password_hint'))
        return
      }
      if (sharePeRequire.value) {
        const peRequireValid = validatePeRequire()
        if (!peRequireValid) {
          return
        }
      }
      formatLinkAddr()
      await toClipboard(linkAddr.value)
      ElMessage.success(t('common.copy_success'))
    } catch (e) {
      ElMessage.warning(t('common.copy_unsupported'))
    }
  } else {
    ElMessage.warning(t('common.copy_unsupported'))
  }
  dialogVisible.value = false
}

const disabledDate = date => {
  return date.getTime() < new Date().getTime()
}

const showLoading = () => {
  loadingInstance.value = ElLoading.service({ target: '.share-dialog-container' })
}
const closeLoading = () => {
  loadingInstance.value?.close()
}

const share = () => {
  dialogVisible.value = true
  loadShareInfo(validatePeRequire)
}

const loadShareInfo = cb => {
  showLoading()
  const resourceId = props.resourceId
  const url = `/share/detail/${resourceId}`
  request
    .get({ url })
    .then(res => {
      state.detailInfo = { ...res.data }
      setPageInfo()
    })
    .finally(() => {
      closeLoading()
      cb && cb()
    })
}

const setPageInfo = () => {
  if (state.detailInfo.id && state.detailInfo.uuid) {
    shareEnable.value = true
    formatLinkAddr()
  }
  passwdEnable.value = !!state.detailInfo.pwd
  overTimeEnable.value = !!state.detailInfo.exp
}

const enableSwitcher = () => {
  const resourceId = props.resourceId
  const url = `/share/switcher/${resourceId}`
  request.post({ url }).then(() => {
    loadShareInfo(null)
  })
}

const formatLinkAddr = () => {
  linkAddr.value = formatLinkBase() + state.detailInfo.uuid
}
const formatLinkBase = () => {
  let prefix = '/'
  if (embeddedStore.baseUrl) {
    prefix = embeddedStore.baseUrl + '#'
  } else {
    const href = window.location.href
    prefix = href.substring(0, href.indexOf('#') + 1)
  }
  if (prefix.includes('oidcbi/') || prefix.includes('casbi/')) {
    prefix = prefix.replace('oidcbi/', '')
    prefix = prefix.replace('casbi/', '')
  }
  return prefix + SHARE_BASE
}

const expEnableSwitcher = val => {
  let exp = 0
  if (val) {
    const now = new Date()
    now.setTime(now.getTime() + 3600 * 1000)
    exp = now.getTime()
    state.detailInfo.exp = exp
  }
  validateExpRequire()
  expChangeHandler(exp)
}

const expChangeHandler = exp => {
  if (overTimeEnable.value && exp < new Date().getTime()) {
    expError.value = true
    return
  }
  expError.value = false
  const resourceId = props.resourceId
  const url = '/share/editExp'
  const data = { resourceId, exp }
  request.post({ url, data }).then(() => {
    loadShareInfo(null)
  })
}
const beforeClose = async done => {
  if (!shareEnable.value) {
    showTicket.value = false
    done()
    return
  }
  if (sharePeRequire.value) {
    const peRequireValid = validatePeRequire()
    if (!peRequireValid) {
      return
    }
  }
  const pwdValid = validatePwdFormat()
  const uuidValid = await validateUuid()
  if (pwdValid && uuidValid) {
    showTicket.value = false
    done()
  }
}
const validatePeRequire = () => {
  if (shareEnable.value && sharePeRequire.value) {
    const expRequireValid = validateExpRequire()
    const pwdRequireValid = validatePwdRequire()
    return expRequireValid && pwdRequireValid
  }
  return true
}

const validateExpRequire = () => {
  if (!sharePeRequire.value || overTimeEnable.value) {
    showCheckboxError(null, expCheckbox)
    return true
  }
  showCheckboxError(t('common.required'), expCheckbox)
  return false
}

const validatePwdRequire = () => {
  if (!sharePeRequire.value || passwdEnable.value) {
    showCheckboxError(null, pwdCheckbox)
    return true
  }
  showCheckboxError(t('common.required'), pwdCheckbox)
  return false
}
const validatePwdFormat = () => {
  if (!shareEnable.value || state.detailInfo.autoPwd) {
    showPageError(null, pwdRef)
    return true
  }
  const val = state.detailInfo.pwd
  if (!val) {
    showPageError(t('work_branch.password_null_hint'), pwdRef)
    return false
  }
  const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{4,10}$/
  const regep = new RegExp(regex)
  if (!regep.test(val)) {
    showPageError(t('work_branch.password_hint'), pwdRef)
    return false
  }
  showPageError(null, pwdRef)
  resetPwdHandler(val, false)
  return true
}
const showCheckboxError = (msg, target, className?: string) => {
  if (!target.value) {
    return
  }
  className = className || 'checkbox-span-require'
  const fullClassName = `.${className}`
  const e = target.value.$el
  if (!msg) {
    e.style = null
    e.children[0].children[1].style.borderColor = null
    const child = e.children[1].children[0].querySelector(fullClassName)
    if (child) {
      e.children[1].children[0].removeChild(child)
    }
  } else {
    e.style.color = 'red'
    e.children[0].children[1].style.borderColor = 'red'
    const child = e.children[1].children[0].querySelector(fullClassName)
    if (!child) {
      const errorDom = document.createElement('span')
      errorDom.className = className
      errorDom.innerText = msg
      e.children[1].children[0].appendChild(errorDom)
    } else {
      child.innerText = msg
    }
  }
}
const showPageError = (msg, target, className?: string) => {
  className = className || 'link-pwd-error-msg'
  const fullClassName = `.${className}`
  const domRef = target || pwdRef
  if (!domRef.value) {
    return
  }
  const e = domRef.value.input
  if (!msg) {
    e.style = null
    e.style.borderColor = null
    const child = e.parentElement.querySelector(fullClassName)
    if (child) {
      e.parentElement['style'] = null
      e.parentElement.removeChild(child)
    }
  } else {
    e.style.color = 'red'
    e.style.borderColor = 'red'
    e.parentElement['style']['box-shadow'] = '0 0 0 1px red inset'
    const child = e.parentElement.querySelector(fullClassName)
    if (!child) {
      const errorDom = document.createElement('div')
      errorDom.className = className
      errorDom.innerText = msg
      e.parentElement.appendChild(errorDom)
    } else {
      child.innerText = msg
    }
  }
}
const existErrorMsg = (className: string) => {
  return document.getElementsByClassName(className)?.length
}
const autoEnableSwitcher = val => {
  if (val) {
    showPageError(null, pwdRef)
    resetPwd()
  } else {
    state.detailInfo.pwd = ''
    nextTick(() => {
      pwdRef.value.input.focus()
    })
  }
}
const pwdEnableSwitcher = val => {
  let pwd = ''
  if (val) {
    pwd = getUuid()
  }
  validatePwdRequire()
  resetPwdHandler(pwd, true)
}
const resetPwd = () => {
  const pwd = getUuid()
  resetPwdHandler(pwd, true)
}
const resetPwdHandler = (pwd?: string, autoPwd?: boolean) => {
  const resourceId = props.resourceId
  const url = '/share/editPwd'
  const data = { resourceId, pwd, autoPwd }
  request.post({ url, data }).then(() => {
    loadShareInfo(null)
  })
}

const getUuid = () => {
  const length = 10
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+'
  let result = ''
  const specialChars = '!@#$%^&*()_+'
  let hasSpecialChar = false

  for (let i = 0; i < length; i++) {
    if (i === 0) {
      result += characters.charAt(Math.floor(Math.random() * characters.length))
    } else {
      if (!hasSpecialChar && i < length - 2) {
        result += specialChars.charAt(Math.floor(Math.random() * specialChars.length))
        hasSpecialChar = true
      } else {
        result += characters.charAt(Math.floor(Math.random() * characters.length))
      }
    }
  }
  result = result
    .split('')
    .sort(() => 0.5 - Math.random())
    .join('')
  return result
}

const openTicket = () => {
  showTicket.value = true
}
const closeTicket = () => {
  showTicket.value = false
}
const updateRequireTicket = val => {
  state.detailInfo.ticketRequire = val
}

const execute = () => {
  share()
}
defineExpose({
  execute
})

onMounted(() => {
  if (!props.inGrid && props.weight >= 7) {
    const commandInfo = {
      label: t('visualization.share'),
      command: 'share',
      svgName: dvShare
    }
    emits('loaded', commandInfo)
  }
})
</script>
<style lang="less">
.is-ticket-dialog {
  .ed-dialog__header {
    display: none;
  }
}
.copy-link_dialog {
  .ed-dialog__header {
    padding: 16px 16px 10px !important;
    .ed-dialog__title {
      font-size: 14px !important;
    }
  }
  .ed-dialog__body {
    padding: 16px !important;
  }
  .ed-dialog__footer {
    border-top: 1px solid #1f232926;
    padding: 12px 16px 16px;
  }
}
.hidden-footer {
  .ed-dialog__footer {
    display: none !important;
  }
}
</style>
<style lang="less" scoped>
.share-button-icon {
  margin-left: 4px;
}
.copy-link_dialog {
  .exp-container {
    .ed-checkbox {
      margin-right: 10px;
    }
    .inline-share-item-picker {
      display: flex;
      align-items: center;
      :deep(.share-exp-picker) {
        margin-left: 25px !important;
        .ed-input__wrapper {
          width: 200px !important;
        }
      }
      .exp-error {
        color: var(--ed-color-danger);
        font-size: 12px;
      }
    }
  }
  .pwd-container {
    .auto-pwd-container {
      padding: 0 25px 6px;
    }
    .ed-checkbox {
      margin-right: 10px;
    }
    .inline-share-item {
      margin-left: 25px;
      width: 220px;

      :deep(.ed-input-group__append) {
        width: initial !important;
        background: none;
        color: #1f2329;
        padding: 0px 0px !important;
        .share-pwd-opt {
          display: flex;
          padding: 1px;
          .share-reset-container {
            &:not(:first-child) {
              border-left: 1px solid var(--ed-input-border-color) !important;
            }
            width: 45px;
            display: flex;
            justify-content: center;
            &:hover {
              cursor: pointer;
              background-color: #f5f6f7;
            }
            &:active {
              cursor: pointer;
              background-color: #eff0f1;
            }
          }
        }
      }
      :deep(.link-pwd-error-msg) {
        color: red;
        position: absolute;
        z-index: 9;
        font-size: 10px;
        height: 10px;
        top: 21px;
        width: 350px;
        left: 0px;
      }
    }
  }

  .copy-link {
    font-weight: 400;

    .open-share {
      margin: -18px 0 8px 0;
      color: #646a73;
      font-size: 12px;
      font-style: normal;
      line-height: 20px;
      .ed-switch {
        margin-right: 8px;
      }
    }
    .custom-link-line {
      display: flex;
      margin-bottom: 16px;
      align-items: center;
      button {
        width: 40px;
        min-width: 40px;
        margin-left: 8px;
        height: 100%;
      }
      :deep(.link-uuid-error-msg) {
        color: red;
        position: absolute;
        z-index: 9;
        font-size: 10px;
        height: 10px;
        top: 25px;
        width: 350px;
        left: 0px;
      }
    }
  }
  .hidden-link-container {
    display: none;
  }
}
:deep(.checkbox-span) {
  display: flex;
  align-items: center;
  .pe-require {
    color: red;
    font-size: 10px;
    line-height: 32px;
    margin: 0 4px;
  }
  .checkbox-span-require {
    font-size: 10px;
  }
  .pe-tips-hidden {
    display: none;
  }
}
</style>
