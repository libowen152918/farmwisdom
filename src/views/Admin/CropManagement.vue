<template>
  <div class="crop-management">
    <el-card>
      <template #header>
        <div class="header">
          <h2>作物管理</h2>
          <el-button type="primary" @click="handleAddCrop">
            <el-icon><Plus /></el-icon>
            添加作物
          </el-button>
        </div>
      </template>

      <el-table :data="crops" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="作物名称" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="plantingSeason" label="种植季节" />
        <el-table-column prop="growthCycle" label="生长周期" />
        <el-table-column prop="yield" label="预计产量" />
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

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑作物对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加作物' : '编辑作物'"
      width="500px"
    >
      <el-form :model="cropForm" :rules="rules" ref="cropFormRef" label-width="100px">
        <el-form-item label="作物名称" prop="name">
          <el-input v-model="cropForm.name" placeholder="请输入作物名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="cropForm.category" placeholder="请选择分类">
            <el-option label="粮食作物" value="粮食作物" />
            <el-option label="经济作物" value="经济作物" />
            <el-option label="蔬菜" value="蔬菜" />
            <el-option label="水果" value="水果" />
          </el-select>
        </el-form-item>
        <el-form-item label="种植季节" prop="plantingSeason">
          <el-input v-model="cropForm.plantingSeason" placeholder="请输入种植季节" />
        </el-form-item>
        <el-form-item label="生长周期" prop="growthCycle">
          <el-input v-model="cropForm.growthCycle" placeholder="请输入生长周期" />
        </el-form-item>
        <el-form-item label="预计产量" prop="yield">
          <el-input v-model="cropForm.yield" placeholder="请输入预计产量" />
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
import { getCropList, addCrop, updateCrop, deleteCrop } from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const cropFormRef = ref(null)
const crops = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 获取作物列表
const fetchCropList = async () => {
  loading.value = true
  try {
    const response = await getCropList(currentPage.value, pageSize.value)
    console.log('Fetched crop records:', response.data.records);
    if (response && response.data && Array.isArray(response.data.records)) {
      crops.value = response.data.records
      total.value = response.data.total
    } else {
      console.warn('获取的帖子列表数据格式不正确或为空', response);
      crops.value = []
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
    console.error('Error fetching posts:', error)
    crops.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCropList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCropList()
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchCropList()
})

const cropForm = reactive({
  id: null,
  name: '',
  category: '',
  plantingSeason: '',
  growthCycle: '',
  yield: ''
})

const rules = {
  name: [
    { required: true, message: '请输入作物名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  plantingSeason: [
    { required: true, message: '请输入种植季节', trigger: 'blur' }
  ],
  growthCycle: [
    { required: true, message: '请输入生长周期', trigger: 'blur' }
  ],
  yield: [
    { required: true, message: '请输入预计产量', trigger: 'blur' }
  ]
}

const handleAddCrop = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.keys(cropForm).forEach(key => {
    cropForm[key] = ''
  })
}

const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.keys(cropForm).forEach(key => {
    cropForm[key] = row[key]
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除作物 "${row.name}" 吗？`, '警告', {
      type: 'warning'
    })
    loading.value = true
    await deleteCrop(row.id)
    ElMessage.success('删除成功')
    await fetchCropList() // 重新加载列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除作物失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!cropFormRef.value) return
  
  try {
    await cropFormRef.value.validate()
    submitting.value = true
    
    const data = {
      name: cropForm.name,
      category: cropForm.category,
      plantingSeason: cropForm.plantingSeason,
      growthCycle: cropForm.growthCycle,
      yield: cropForm.yield
    }
    
    if (dialogType.value === 'add') {
      await addCrop(data)
      ElMessage.success('添加成功')
    } else {
      if (cropForm.id) {
        await updateCrop(cropForm.id, data)
        ElMessage.success('更新成功')
      } else {
        console.error('更新作物时缺少ID')
        ElMessage.error('更新失败：缺少作物ID')
      }
    }
    
    dialogVisible.value = false
    await fetchCropList() // 重新加载列表
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.crop-management {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 