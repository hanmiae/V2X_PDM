<template>
  <router-view v-slot="{ Component }">
    <component :is="Component" class="route-root" />
  </router-view>
</template>

<style>
/* 전역: 뷰포트 기준 단일 열, 메인 레이아웃에서만 세로 스크롤 */
html {
  height: 100%;
  overflow-x: hidden;
}
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  min-height: 100%;
  min-height: 100dvh;
  overflow-x: hidden;
  -webkit-font-smoothing: antialiased;
}
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
}
/* 가로 사이드바 + 메인(로그인 분할 화면) 모두 기본 row — column이면 레이아웃 붕괴 */
.route-root {
  flex: 1 1 auto;
  min-height: 0;
  min-width: 0;
  display: flex;
  flex-direction: row;
  align-items: stretch;
}
</style>
