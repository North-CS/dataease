import request from '@/config/axios'

export interface IGetTaskInfoReq {
  id?: string
  name?: string
}

export interface ITaskInfoInsertReq {
  [key: string]: any
}

export interface ISchedulerOption {
  interval: number
  unit: string
}

export interface ISource {
  type: string
  query: string
  tables: string
  datasourceId: string
  tableExtract: string
  dsTableList?: IDsTable[]
  dsList?: []
  fieldList?: ITableField[]
  targetFieldTypeList?: string[]
  incrementCheckbox?: string
  incrementField?: string
  esQuery?: string
}

export interface ITableField {
  id: string
  fieldSource: string
  fieldName: string
  fieldType: string
  remarks: string
  fieldSize: number
  fieldPrecision: number
  fieldPk: boolean
  fieldIndex: boolean
}

export interface ITargetProperty {
  /**
   * 启用分区on
   */
  partitionEnable: string
  /**
   * 分区类型
   * DateRange 日期
   * NumberRange 数值
   * List 列
   */
  partitionType: string
  /**
   * 启用动态分区on
   */
  dynamicPartitionEnable: string
  /**
   * 动态分区结束偏移量
   */
  dynamicPartitionEnd: number
  /**
   * 动态分区间隔单位
   * HOUR WEEK DAY MONTH YEAR
   */
  dynamicPartitionTimeUnit: string
  /**
   * 手动分区列值
   * 多个以','隔开
   */
  manualPartitionColumnValue: string
  /**
   * 手动分区数值区间开始值
   */
  manualPartitionStart: number
  /**
   * 手动分区数值区间结束值
   */
  manualPartitionEnd: number
  /**
   * 手动分区数值区间间隔
   */
  manualPartitionInterval: number
  /**
   * 手动分区日期区间
   * 2023-09-08 - 2023-09-15
   */
  manualPartitionTimeRange: string
  /**
   * 手动分区日期区间间隔单位
   */
  manualPartitionTimeUnit: string
  /**
   * 分区字段
   */
  partitionColumn: string
}

export interface ITarget {
  createTable?: string
  type: string
  fieldList: ITableField[]
  tableName: string
  datasourceId: string
  targetProperty: string
  dsList?: []
  multipleSelection?: ITableField[]
  property: ITargetProperty
  incrementSync: string
  incrementField: string
  incrementFieldType: string
  remarks: string
  faultToleranceRate: number
  incrementOffset: number
  incrementOffsetUnit: string
}

export class ITaskInfoRes {
  id: string

  name: string

  schedulerType: string

  schedulerConf: string

  schedulerOption: ISchedulerOption

  taskKey: string

  desc: string

  executorTimeout: number

  executorFailRetryCount: number

  source: ISource

  target: ITarget

  status: string
  startTime: string
  stopTime: string

  constructor(
    id: string,
    name: string,
    schedulerType: string,
    schedulerConf: string,
    taskKey: string,
    desc: string,
    executorTimeout: number,
    executorFailRetryCount: number,
    source: ISource,
    target: ITarget,
    status: string,
    startTime: string,
    stopTime: string,
    schedulerOption: ISchedulerOption
  ) {
    this.id = id
    this.name = name
    this.schedulerType = schedulerType
    this.schedulerConf = schedulerConf
    this.taskKey = taskKey
    this.desc = desc
    this.executorTimeout = executorTimeout
    this.executorFailRetryCount = executorFailRetryCount
    this.source = source
    this.target = target
    this.status = status
    this.startTime = startTime
    this.stopTime = stopTime
    this.schedulerOption = schedulerOption
  }
}

export interface ITaskInfoUpdateReq {
  [key: string]: any
}

export interface IDsTable {
  datasourceId: string
  name: string
  remark: string
  enableCheck: string
  datasetPath: string
}

export const getDatasourceListByTypeApi = (type: string, dsRole?: string) => {
  return request.get({
    url: `/sync/datasource/list/${type}`,
    params: dsRole ? { dsRole } : undefined
  })
}
export const getTaskInfoListApi = (current: number, size: number, data) => {
  return request.post({ url: `/sync/task/pager/${current}/${size}`, data: data })
}

export const executeOneApi = (id: string) => {
  return request.get({ url: `/sync/task/execute/${id}` })
}

export const startTaskApi = (id: string) => {
  return request.get({ url: `/sync/task/start/${id}` })
}

export const stopTaskApi = (id: string) => {
  return request.get({ url: `/sync/task/stop/${id}` })
}

export const addApi = (data: ITaskInfoInsertReq) => {
  return request.post({ url: `/sync/task/add`, data: data })
}

export const removeApi = (taskId: string) => {
  return request.post({ url: `/sync/task/remove/${taskId}` })
}

export const batchRemoveApi = (taskIds: string[]) => {
  return request.post({ url: `/sync/task/batch/del`, data: taskIds })
}

/**
 * 批量复制任务为「待编辑任务」：直接落库、不执行，编辑保存后转正
 */
export const batchCopyTaskApi = (taskIds: string[], targetNodeId: string) => {
  return request.post({ url: `/sync/task/batch/copy`, data: { taskIds, targetNodeId } })
}

export const groupCopyTaskApi = (groupId: string, targetNodeId: string) => {
  return request.post({ url: `/sync/task/group/copy`, data: { groupId, targetNodeId } })
}

export const modifyApi = (data: ITaskInfoUpdateReq) => {
  return request.post({ url: `/sync/task/update`, data: data })
}

export const findTaskInfoByIdApi = (taskId: string) => {
  return request.get({ url: `/sync/task/get/${taskId}` })
}

export const getDatasourceTableListApi = (dsId: string) => {
  return request.get({ url: `/sync/datasource/table/list/${dsId}` })
}

/**
 * 导出字段映射 Excel（以源表字段为基准生成模板，已映射字段带出当前配置）
 */
export const exportFieldMappingApi = data => {
  return request.post({
    url: '/sync/task/field/export',
    data,
    responseType: 'blob'
  })
}

/**
 * 解析字段映射 Excel，返回预览结果（不落库）
 */
export const importFieldMappingApi = data => {
  return request.post({
    url: '/sync/task/field/import',
    headersType: 'multipart/form-data',
    data
  })
}

/**
 * 同步任务树（目录/任务组）
 */
export const getSyncTreeApi = () => {
  return request.get({ url: '/sync/tree/tree' })
}

export const saveSyncTreeNodeApi = data => {
  return request.post({ url: '/sync/tree/save', data })
}

export const dragSyncTreeNodeApi = data => {
  return request.post({ url: '/sync/tree/drag', data })
}

export const removeSyncTreeNodeApi = id => {
  return request.post({ url: `/sync/tree/remove/${id}` })
}

export const moveSyncTaskApi = data => {
  return request.post({ url: '/sync/tree/move-task', data })
}

/**
 * 任务组：触发组执行（组内任务串行），返回组日志ID
 */
export const executeGroupApi = (id: string) => {
  return request.post({ url: `/sync/tree/executeGroup/${id}` })
}

/**
 * 任务组：组执行日志分页
 */
export const groupLogListApi = (groupId: string, goPage: number, pageSize: number) => {
  return request.get({ url: `/sync/tree/groupLog/${groupId}/${goPage}/${pageSize}` })
}

/**
 * 任务组：组内任务上移/下移
 */
export const orderGroupTaskApi = (taskId: string, direction: 'up' | 'down') => {
  return request.post({ url: '/sync/tree/groupTask/order', data: { taskId, direction } })
}

/**
 * 任务组：保存调度配置
 */
export const saveGroupScheduleApi = (data: {
  id: string
  schedulerType: string
  schedulerConf?: string
  failPolicy?: string
  overlapPolicy?: string
}) => {
  return request.post({ url: '/sync/tree/groupSchedule', data })
}

/**
 * 任务组：跨组执行日志分页
 */
export const groupLogGridApi = (
  goPage: number,
  pageSize: number,
  data: { groupId?: string; status?: string[]; startTime?: number; endTime?: number; keyword?: string }
) => {
  return request.post({ url: `/sync/tree/groupLog/pager/${goPage}/${pageSize}`, data })
}

/**
 * 任务组：查本次组执行里某成员任务对应的任务日志ID
 */
export const groupTaskLogIdApi = (groupLogId: string, taskId: string) => {
  return request.get({ url: `/sync/tree/groupLog/taskLog/${groupLogId}/${taskId}` })
}

/**
 * 任务组：终止组执行
 * scope: CURRENT_ONE只终止当前任务 / ABORT_ALL终止当前任务并中止整组
 */
export const terminateGroupApi = (groupLogId: string, scope: 'CURRENT_ONE' | 'ABORT_ALL') => {
  return request.post({ url: '/sync/tree/groupLog/terminate', data: { groupLogId, scope } })
}

/**
 * 任务组：设置组内任务「忽略执行」
 */
export const setGroupTaskSkipApi = (taskId: string, skip: boolean) => {
  return request.post({ url: '/sync/tree/groupTask/skip', data: { taskId, skip } })
}
