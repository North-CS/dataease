<template>
  <div class="home-container">
    <iframe
      v-if="homeUrl"
      :src="homeUrl"
      frameborder="0"
      width="100%"
      height="100%"
      allow="autoplay; encrypted-media"
      allowfullscreen
    ></iframe>
    <div v-else class="content-box">
      <p>请前往系统设置 -> 外观配置 进行首页 URL 配置。</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useAppearanceStoreWithOut } from '@/store/modules/appearance'

const appearanceStore = useAppearanceStoreWithOut()
const homeUrl = computed(() => appearanceStore.getHomeUrl || 'https://www.fit2cloud.com/')
</script>

<style scoped lang="less">
.home-container {
  width: 100%;
  height: calc(100vh - 56px);
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;

  iframe {
    border: none;
    display: block;
  }
}

.content-box {
  text-align: center;
  font-size: 18px;
  color: #606266;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
