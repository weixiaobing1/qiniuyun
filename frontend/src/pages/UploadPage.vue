<template>
  <div class="upload-page">
    <el-card shadow="never" class="card">
      <template #header>
        <div class="card-header">
          <span class="title">第 1 步：上传或粘贴小说</span>
          <el-tag type="info" size="small">支持 TXT / MD / HTML，单次最多 50 万字</el-tag>
        </div>
      </template>

      <el-form :model="meta" label-width="80px" class="meta">
        <el-form-item label="标题">
          <el-input v-model="meta.title" placeholder="可选，留空则使用文件名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="meta.author" placeholder="可选" clearable />
        </el-form-item>
      </el-form>

      <el-tabs v-model="mode" class="tabs">
        <el-tab-pane label="文件上传" name="file">
          <el-upload
            class="dropzone"
            drag
            :auto-upload="false"
            :show-file-list="false"
            :on-change="onFileSelected"
            accept=".txt,.md,.html,.htm"
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">点击或拖拽文件到此处</div>
            <div class="upload-hint">支持 .txt / .md / .html / .htm</div>
          </el-upload>
        </el-tab-pane>

        <el-tab-pane label="粘贴文本" name="paste">
          <el-input
            v-model="pastedText"
            type="textarea"
            :rows="12"
            placeholder="粘贴小说全文，章节标题需独占一行（如 第一章 / Chapter 1 / 楔子）"
            resize="vertical"
          />
          <div class="paste-actions">
            <span class="counter">{{ pastedText.length }} 字</span>
            <el-button type="primary" :loading="store.loading" @click="onPasteSubmit">
              解析章节
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card v-if="store.hasSession" shadow="never" class="card">
      <template #header>
        <div class="card-header">
          <span class="title">第 2 步：选择要转换的章节</span>
          <div class="header-actions">
            <el-tag>{{ store.title }} · {{ store.author }}</el-tag>
            <el-tag type="success">总字数 {{ store.totalChars }}</el-tag>
            <el-tag :type="store.canConvert ? 'success' : 'warning'">
              已选 {{ store.selectedCount }} 章（≥3）
            </el-tag>
          </div>
        </div>
      </template>

      <div class="chapter-toolbar">
        <el-button size="small" @click="store.selectAll">全选</el-button>
        <el-button size="small" @click="store.clearSelection">清空</el-button>
        <el-button size="small" type="danger" plain @click="onReset">重新上传</el-button>
        <el-button
          class="convert-btn"
          type="primary"
          :disabled="!store.canConvert"
          @click="onGoConvert"
        >
          下一步：开始 AI 转换
        </el-button>
      </div>

      <el-table
        :data="store.chapters"
        @row-click="row => store.toggleChapter(row.id)"
        class="chapter-table"
        height="420"
      >
        <el-table-column width="52">
          <template #default="{ row }">
            <el-checkbox
              :model-value="store.selectedChapterIds.includes(row.id)"
              @click.stop
              @change="store.toggleChapter(row.id)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="index" label="#" width="60" />
        <el-table-column prop="title" label="章节标题" min-width="220" show-overflow-tooltip />
        <el-table-column label="预览" min-width="380" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="preview">{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="charCount" label="字数" width="100" sortable />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { useNovelStore } from '../stores/novel'

const router = useRouter()
const store = useNovelStore()

const mode = ref('file')
const pastedText = ref('')
const meta = reactive({ title: '', author: '' })

async function onFileSelected(uploadFile) {
  try {
    await store.uploadFile(uploadFile.raw, meta)
    ElMessage.success(`解析成功：共 ${store.chapters.length} 章`)
  } catch (err) {
    ElMessage.error(err.message || '上传失败')
  }
}

async function onPasteSubmit() {
  if (!pastedText.value.trim()) {
    ElMessage.warning('请先粘贴文本')
    return
  }
  try {
    await store.pasteText({
      text: pastedText.value,
      title: meta.title,
      author: meta.author
    })
    ElMessage.success(`解析成功：共 ${store.chapters.length} 章`)
  } catch (err) {
    ElMessage.error(err.message || '解析失败')
  }
}

function onReset() {
  store.reset()
  pastedText.value = ''
}

function onGoConvert() {
  if (!store.canConvert) {
    ElMessage.warning('请至少选择 3 个章节')
    return
  }
  router.push({ name: 'preview' })
}
</script>

<style scoped>
.upload-page {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title { font-weight: 600; }
.header-actions { display: flex; gap: 8px; }
.meta { max-width: 520px; margin-bottom: 8px; }
.dropzone :deep(.el-upload-dragger) {
  padding: 32px;
  border-radius: 8px;
}
.upload-icon { font-size: 48px; color: var(--el-color-primary); }
.upload-text { font-size: 16px; margin-top: 8px; }
.upload-hint { color: #909399; font-size: 12px; margin-top: 4px; }
.paste-actions {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.counter { color: #909399; font-size: 13px; }
.chapter-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}
.convert-btn { margin-left: auto; }
.preview { color: #606266; font-size: 13px; }
.chapter-table :deep(tr) { cursor: pointer; }
</style>
