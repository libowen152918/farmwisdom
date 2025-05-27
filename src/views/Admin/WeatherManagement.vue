<template>
  <div class="weather-management">
    <el-card>
      <template #header>
        <div class="header">
          <h2>天气管理</h2>
          <el-button type="primary" @click="handleAddWeather">
            <el-icon><Plus /></el-icon>
            添加天气信息
          </el-button>
        </div>
      </template>

      <el-table :data="weatherData" v-loading="loading" style="width: 100%">
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="temperature" label="温度">
          <template #default="{ row }">
            {{ row.temperature }}°C
          </template>
        </el-table-column>
        <el-table-column prop="humidity" label="湿度">
          <template #default="{ row }">
            {{ row.humidity }}%
          </template>
        </el-table-column>
        <el-table-column prop="weatherCondition" label="天气状况" />
        <el-table-column prop="recordTime" label="记录时间">
          <template #default="{ row }">
            {{ formatDate(row.recordTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑天气信息对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加天气信息' : '编辑天气信息'"
      width="500px"
    >
      <el-form :model="weatherForm" :rules="rules" ref="weatherFormRef" label-width="100px">
        <el-form-item label="地点" prop="location">
          <el-input v-model="weatherForm.location" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="温度" prop="temperature">
          <el-input v-model="weatherForm.temperature" placeholder="请输入温度">
            <template #append>°C</template>
          </el-input>
        </el-form-item>
        <el-form-item label="湿度" prop="humidity">
          <el-input v-model="weatherForm.humidity" placeholder="请输入湿度">
            <template #append>%</template>
          </el-input>
        </el-form-item>
        <el-form-item label="天气状况" prop="weatherCondition">
          <el-select v-model="weatherForm.weatherCondition" placeholder="请选择天气状况">
            <el-option label="晴天" value="晴天" />
            <el-option label="多云" value="多云" />
            <el-option label="阴天" value="阴天" />
            <el-option label="小雨" value="小雨" />
            <el-option label="中雨" value="中雨" />
            <el-option label="大雨" value="大雨" />
            <el-option label="暴雨" value="暴雨" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="weatherForm.description" type="textarea" placeholder="请输入天气描述" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getWeatherList, addWeather, updateWeather, deleteWeather } from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const weatherFormRef = ref(null)
const weatherData = ref([])

// 获取天气列表
const fetchWeatherList = async () => {
  loading.value = true
  try {
    const response = await getWeatherList()
    console.log('Weather API Response:', response)
    
    // 直接使用响应数据，因为它已经是分页对象
    if (response && response.records) {
      weatherData.value = response.records
    } else {
      weatherData.value = []
      console.warn('Unexpected response format:', response)
    }
  } catch (error) {
    console.error('获取天气列表失败:', error)
    ElMessage.error('获取天气列表失败')
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchWeatherList()
})

const weatherForm = reactive({
  id: null,
  location: '',
  temperature: '',
  humidity: '',
  weatherCondition: '',
  description: ''
})

const rules = {
  location: [
    { required: true, message: '请输入地点', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '请输入温度', trigger: 'blur' },
    { pattern: /^-?\d+(\.\d+)?$/, message: '请输入有效的温度值', trigger: 'blur' }
  ],
  humidity: [
    { required: true, message: '请输入湿度', trigger: 'blur' },
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的湿度值', trigger: 'blur' }
  ],
  weatherCondition: [
    { required: true, message: '请选择天气状况', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入天气描述', trigger: 'blur' }
  ]
}

const handleAddWeather = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.keys(weatherForm).forEach(key => {
    weatherForm[key] = ''
  })
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.keys(weatherForm).forEach(key => {
    weatherForm[key] = row[key]
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除 ${row.location} 的天气信息吗？`, '警告', {
      type: 'warning'
    })
    loading.value = true
    await deleteWeather(row.id)
    ElMessage.success('删除成功')
    await fetchWeatherList() // 重新加载列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除天气信息失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleSubmit = async () => {
  if (!weatherFormRef.value) return
  
  try {
    await weatherFormRef.value.validate()
    submitting.value = true
    
    const data = {
      location: weatherForm.location,
      temperature: parseFloat(weatherForm.temperature),
      humidity: parseFloat(weatherForm.humidity),
      weatherCondition: weatherForm.weatherCondition,
      description: weatherForm.description,
      recordTime: new Date().toISOString()
    }
    
    if (dialogType.value === 'add') {
      await addWeather(data)
      ElMessage.success('添加成功')
    } else {
      if (!weatherForm.id) {
        throw new Error('更新失败：缺少天气信息ID')
      }
      await updateWeather(weatherForm.id, data)
      ElMessage.success('更新成功')
    }
    
    dialogVisible.value = false
    await fetchWeatherList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.weather-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 