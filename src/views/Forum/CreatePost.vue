<template>
  <div class="create-post-container">
    <el-card>
      <template #header>
        <h2>发表新帖子</h2>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        class="create-post-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请输入帖子内容"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            发布
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { usePostStore } from '@/store/posts'

const router = useRouter()
const postStore = usePostStore()
const formRef = ref(null)
const submitting = ref(false)
const categories = ref([])

const formData = ref({
  title: '',
  categoryId: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' },
    { type: 'number', message: '分类ID必须是数字', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, message: '内容至少需要10个字符', trigger: 'blur' }
  ]
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await axios.get('/categories')
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    
    submitting.value = true
    const result = await postStore.createPost(formData.value)
    
    ElMessage.success('发布成功')
    
    // 更新帖子列表
    await postStore.fetchPosts(1, 10)  // 重新加载第一页的帖子
    
    // 跳转到论坛首页
    router.push('/forum')
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('发布失败')
    }
  } finally {
    submitting.value = false
  }
}

// 取消发布
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.create-post-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.create-post-form {
  margin-top: 20px;
}

:deep(.el-input__wrapper) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}
</style> 