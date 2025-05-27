<template>
  <div class="forgot-password-container">
    <el-card class="forgot-password-card">
      <template #header>
        <div class="card-header">
          <h2>重置密码</h2>
        </div>
      </template>
      
      <el-form
        ref="resetForm"
        :model="resetForm"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="resetForm.email"
            type="email"
            placeholder="请输入注册时使用的邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-button
          type="primary"
          native-type="submit"
          class="submit-btn"
          :loading="loading"
          >发送重置链接</el-button
        >
        
        <div class="back-to-login">
          <el-link type="primary" @click="$router.push('/login')">
            返回登录
          </el-link>
        </div>
      </el-form>
      
      <div v-if="submitted" class="success-message">
        <el-alert
          title="重置链接已发送"
          type="success"
          description="请检查您的邮箱，点击邮件中的链接重置密码。如果没有收到邮件，请检查垃圾邮件文件夹。"
          show-icon
          :closable="false"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const resetForm = reactive({
  email: ''
})

const loading = ref(false)
const submitted = ref(false)

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    loading.value = true
    await userStore.resetPassword(resetForm.email)
    submitted.value = true
    ElMessage.success('重置链接已发送到您的邮箱')
  } catch (error) {
    ElMessage.error(error.message || '发送失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.forgot-password-card {
  width: 100%;
  max-width: 400px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.submit-btn {
  width: 100%;
  margin-bottom: 20px;
}

.back-to-login {
  text-align: center;
  margin-bottom: 20px;
}

.success-message {
  margin-top: 20px;
}
</style> 