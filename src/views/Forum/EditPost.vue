<template>
  <div class="edit-post">
    <h2>编辑帖子</h2>
    <el-form :model="postForm" :rules="rules" ref="postFormRef" label-width="100px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="postForm.title" placeholder="请输入标题"></el-input>
      </el-form-item>
      
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="postForm.categoryId" placeholder="请选择分类">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="内容" prop="content">
        <el-input
          v-model="postForm.content"
          type="textarea"
          :rows="10"
          placeholder="请输入内容"
        ></el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const route = useRoute()
const router = useRouter()
const postFormRef = ref(null)
const loading = ref(false)
const categories = ref([])

const postForm = ref({
  title: '',
  content: '',
  categoryId: null
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, message: '内容至少 10 个字符', trigger: 'blur' }
  ]
}

const fetchPost = async () => {
  try {
    const { data } = await axios.get(`/posts/${route.params.id}`)
    postForm.value = {
      title: data.title,
      content: data.content,
      categoryId: data.categoryId
    }
  } catch (error) {
    console.error('获取帖子失败:', error)
    ElMessage.error('获取帖子失败')
    router.push('/forum/my')
  }
}

const fetchCategories = async () => {
  try {
    const { data } = await axios.get('/categories')
    categories.value = data
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类失败')
  }
}

const submitForm = async () => {
  if (!postFormRef.value) return
  
  try {
    await postFormRef.value.validate()
    loading.value = true
    
    await axios.put(`/posts/${route.params.id}`, postForm.value)
    ElMessage.success('更新成功')
    router.push('/forum/my')
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPost()
  fetchCategories()
})
</script>

<style scoped>
.edit-post {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: var(--primary-color);
}
</style> 