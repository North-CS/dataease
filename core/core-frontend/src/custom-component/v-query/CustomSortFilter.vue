<template>
  <el-dialog
    destroy-on-close
    append-to-body
    :title="$t('v_query.custom_sort')"
    v-model="dialogShow"
    class="custom-sort_filter"
    width="300px"
  >
    <div>
      <draggable :list="sortList" animation="300" class="drag-list">
        <template #item="{ element }">
          <span :key="element.name" class="item-dimension" :title="element">
            <el-icon size="20px">
              <Icon name="drag"><drag class="svg-icon" /></Icon>
            </el-icon>
            <span class="item-span">
              {{ element }}
            </span>
          </span>
        </template>
      </draggable>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="save">{{ $t('chart.confirm') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import drag from '@/assets/svg/drag.svg'
import draggable from 'vuedraggable'
import { ref, unref } from 'vue'
import { cloneDeep } from 'lodash-es'

const sortList = ref([])
const dialogShow = ref(false)
const sortInit = list => {
  init(list)
  dialogShow.value = true
}
const init = list => {
  sortList.value = cloneDeep(list)
}

const emits = defineEmits(['save'])
const closeDialog = () => {
  dialogShow.value = false
}
const save = () => {
  emits('save', unref(sortList))
  closeDialog()
}

defineExpose({
  sortInit
})
</script>

<style lang="less">
.custom-sort_filter {
  .drag-list {
    overflow: auto;
    max-height: 800px;
    .item-dimension {
      padding: 2px;
      margin: 2px;
      border: solid 1px #eee;
      text-align: left;
      color: #606266;
      background-color: white;
      display: flex;
      align-items: center;
    }

    .item-icon {
      cursor: move;
      margin: 0 2px;
    }

    .item-span {
      display: inline-block;
      width: 100%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .item-dimension + .item-dimension {
      margin-top: 6px;
    }

    .item-dimension:hover {
      color: #1890ff;
      background: #e8f4ff;
      border-color: #a3d3ff;
      cursor: pointer;
    }
  }
}
</style>
