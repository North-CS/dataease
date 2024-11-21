<template>
  <svg class="grid" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <pattern
        id="smallGrid"
        :width="smallGridW"
        :height="smallGridH"
        patternUnits="userSpaceOnUse"
      >
        <path
          :d="smallGridPathD"
          fill="none"
          stroke="rgba(207, 207, 207, 0.3)"
          stroke-width="0.8"
        />
      </pattern>
      <pattern
        id="middleGrid"
        :width="middleGridW"
        :height="middleGridH"
        patternUnits="userSpaceOnUse"
      >
        <rect :width="middleGridW" :height="middleGridH" fill="url(#smallGrid)" />
        <path :d="middleGridPathD" fill="none" stroke="rgba(207, 207, 207, 0.3)" stroke-width="1" />
      </pattern>
      <pattern id="grid" :width="gridW" :height="gridH" patternUnits="userSpaceOnUse">
        <rect :width="gridW" :height="gridH" fill="url(#middleGrid)" />
        <path :d="pathD" fill="none" stroke="rgba(207, 207, 207, 0.3)" stroke-width="1.2" />
      </pattern>
    </defs>
    <rect width="100%" height="100%" fill="url(#grid)" />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'
const matrixBase = 2

const props = defineProps({
  matrixStyle: {
    type: Object
  },
  themes: {
    type: String,
    default: 'dark'
  }
})

const pathD = computed(() => {
  return 'M ' + gridW.value + ' 0 L 0 0 0 ' + gridH.value
})

const middleGridPathD = computed(() => {
  return 'M ' + middleGridW.value + ' 0 L 0 0 0 ' + middleGridH.value
})

const smallGridPathD = computed(() => {
  return 'M ' + smallGridW.value + ' 0 L 0 0 0 ' + smallGridH.value
})

const gridW = computed(() => {
  return props.matrixStyle.width * 2 * matrixBase
})

const gridH = computed(() => {
  return props.matrixStyle.height * 2 * matrixBase
})

const middleGridW = computed(() => {
  return props.matrixStyle.width * matrixBase
})

const middleGridH = computed(() => {
  return props.matrixStyle.height * matrixBase
})

const smallGridW = computed(() => {
  return props.matrixStyle.width
})

const smallGridH = computed(() => {
  return props.matrixStyle.height
})
</script>

<style lang="less" scoped>
.grid {
  position: absolute;
  top: 0;
  left: 0;
}
</style>
