<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="header">
          <h2>用户管理</h2>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ROLE_ADMIN' ? 'danger' : (row.role === 'ROLE_EXPERT' ? 'warning' : 'success')">
              {{ formatRole(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.isEnabled"
              :active-value="true"
              :inactive-value="false"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(row)">
              重置密码
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户"
      width="500px"
    >
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option
              v-for="roleOption in roleOptions"
              :key="roleOption.value"
              :label="roleOption.label"
              :value="roleOption.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, updateUser, deleteUser, resetUserPassword, toggleUserStatus, getRoleOptions } from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const userFormRef = ref(null)
const searchQuery = ref('')
const users = ref([])
const roleOptions = ref([]) // State to store role options

// 过滤用户列表
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user => 
    user.username.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  )
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await getUserList()
    console.log('User API Response:', response)
    
    // 直接使用响应数据，因为它已经是分页对象
    if (response && response.records) {
      users.value = response.records
    } else {
      users.value = []
      console.warn('Unexpected response format:', response)
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取角色选项
const fetchRoleOptions = async () => {
  try {
    console.log('Attempting to fetch role options...');
    const response = await getRoleOptions();
    console.log('Role options fetched successfully:', response);
    roleOptions.value = response;
  } catch (error) {
    console.error('获取角色选项失败:', error);
    ElMessage.error('获取角色选项失败');
  }
};

// 在组件挂载时获取数据
onMounted(() => {
  fetchUserList()
  fetchRoleOptions() // Fetch role options on mount
})

const userForm = reactive({
  id: '',
  username: '',
  email: '',
  role: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
}

const handleEdit = (row) => {
  dialogVisible.value = true
  Object.assign(userForm, row)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '警告', {
      type: 'warning'
    })
    loading.value = true
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    await fetchUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '警告', {
      type: 'warning'
    })
    loading.value = true
    await resetUserPassword(row.id)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置失败')
    }
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row, enabled) => {
  try {
    await toggleUserStatus(row.id, enabled)
    ElMessage.success(enabled ? '用户已启用' : '用户已禁用')
  } catch (error) {
    console.error('更新用户状态失败:', error)
    ElMessage.error('操作失败')
    // 恢复原状态
    row.isEnabled = !enabled
  }
}

const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    submitting.value = true
    
    await updateUser(userForm.id, {
      email: userForm.email,
      role: userForm.role
    })
    
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await fetchUserList()
  } catch (error) {
    console.error('更新用户失败:', error)
    ElMessage.error('更新失败')
  } finally {
    submitting.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 格式化角色显示
const formatRole = (role) => {
  // Attempt to find the role label from fetched options
  const roleOption = roleOptions.value.find(option => option.value === role);
  if (roleOption) {
    return roleOption.label;
  }

  // Fallback for standard roles if options are not found or loaded
  switch (role) {
    case 'ROLE_ADMIN':
      return '管理员';
    case 'ROLE_USER':
      return '普通用户';
    case 'ROLE_EXPERT':
      return '专家用户';
    default:
      return role; // Return original value if not matched
  }
};

</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 