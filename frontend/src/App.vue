<template>
  <el-container class="app">
    <el-header class="app-header">
      <div class="logo">AI 小说转剧本</div>
      <el-menu
        mode="horizontal"
        :default-active="$route.path"
        router
        class="nav"
      >
        <el-menu-item index="/">上传转换</el-menu-item>
        <el-menu-item index="/preview">剧本预览</el-menu-item>
        <el-menu-item index="/history">历史记录</el-menu-item>
      </el-menu>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
</script>

<style>
html, body, #app { height: 100%; margin: 0; }
.app { height: 100vh; }
.app-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
}
.logo { font-weight: 600; font-size: 18px; margin-right: 32px; }
.nav { flex: 1; border-bottom: none; }
</style>
