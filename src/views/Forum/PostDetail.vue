<template>
  <div class="post-detail">
    <el-card v-loading="postStore.loading">
      <template #header>
        <div class="header">
          <h2>{{ postStore.currentPost?.title }}</h2>
          <div class="post-meta">
            <span>作者：{{ postStore.currentPost?.author }}</span>
            <span>发布时间：{{ postStore.currentPost?.createTime }}</span>
          </div>
        </div>
      </template>

      <div class="content" v-if="postStore.currentPost">
        {{ postStore.currentPost.content }}
      </div>

      <div class="comments" v-if="postStore.currentPost">
        <h3>评论区</h3>
        <div class="comment-list">
          <div v-for="comment in postStore.currentPost.comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <div class="comment-content">
              {{ comment.content }}
            </div>
          </div>
        </div>

        <div class="comment-form" v-if="userStore.isAuthenticated">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="请输入您的评论"
          />
          <el-button type="primary" @click="handleComment" :loading="commenting">
            发表评论
          </el-button>
        </div>
        <div v-else class="login-tip">
          请<router-link to="/login">登录</router-link>后发表评论
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '../../store/posts'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const userStore = useUserStore()
const commentContent = ref('')
const commenting = ref(false)

onMounted(async () => {
  const postId = route.params.id
  try {
    await postStore.fetchPostById(postId)
    if (!postStore.currentPost) {
      ElMessage.error('帖子不存在或已被删除')
      router.push('/forum')
    }
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    ElMessage.error('获取帖子详情失败')
    router.push('/forum')
  }
})

const handleComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  commenting.value = true
  try {
    // 这里需要实现评论功能
    ElMessage.success('评论成功')
    commentContent.value = ''
  } catch (error) {
    ElMessage.error('评论失败')
  } finally {
    commenting.value = false
  }
}
</script>

<style scoped>
.post-detail {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.post-meta {
  color: #666;
  font-size: 14px;
  margin-top: 10px;
}

.post-meta span {
  margin-right: 20px;
}

.content {
  line-height: 1.8;
  margin-bottom: 30px;
}

.comments {
  margin-top: 30px;
}

.comment-list {
  margin: 20px 0;
}

.comment-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: bold;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  line-height: 1.6;
}

.comment-form {
  margin-top: 20px;
}

.comment-form .el-button {
  margin-top: 10px;
}

.login-tip {
  text-align: center;
  color: #666;
  margin-top: 20px;
}

.login-tip a {
  color: #409EFF;
}
</style> 