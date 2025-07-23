<template>
  <div class="home-root">
    <div class="card">
      <div v-if="!user">您还没登录</div>
      <template v-else>
        <!-- 学生主页 -->
        <template v-if="user.userRole === 'student'">
          <h2>欢迎学生：{{ user.userAccount }}</h2>
          <p>这里是学生专属主页内容。</p>
          <table class="course-table" v-if="studentCourseList.length">
            <thead>
              <tr>
                <th>课程ID</th>
                <th>课程名称</th>
                <th>课程描述</th>
                <th>选课状态</th>
                <th>操作</th>
                <th>反馈</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="course in studentCourseList" :key="course.id">
                <td>{{ course.id }}</td>
                <td>{{ course.name }}</td>
                <td>{{ course.description }}</td>
                <td>
                  <template v-if="course.status === null">
                    未选
                    <button @click="handleSelectCourse(course.id)">选择</button>
                  </template>
                  <template v-else-if="course.status === 'rejected' || course.status === 'REJECTED'">
                    <span>已拒绝</span>
                    <button @click="handleSelectCourse(course.id)">重新申请</button>
                  </template>
                  <template v-else>
                    {{ statusText(course.status) }}
                  </template>
                </td>
                <td>
                  <button
                    v-if="quizStatuses[course.id] && quizStatuses[course.id].status === 'answered'"
                    class="answered-btn"
                    disabled
                  >
                    已回答，得分：{{ quizStatuses[course.id].score }}
                  </button>
                  <button class="view-quiz-btn"
                    v-else-if="course.status === 'approved' || course.status === 'APPROVED'"
                    @click="goToAnswerPage(course.id, course.name)"
                  >
                    查看课程题目
                  </button>
                </td>
                <td>
                  <button @click="openFeedbackModal(course.id)">反馈</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>暂无课程</div>
        </template>
        <!-- 教师主页 -->
        <template v-else-if="user.userRole === 'teacher'">
          <h2>欢迎老师：{{ user.userAccount }}</h2>
          <p>这里是老师专属主页内容。</p>
          <div>
            <h3>我的课程列表</h3>
            <table class="course-table" v-if="teacherCourseList.length">
              <thead>
                <tr>
                  <th>课程ID</th>
                  <th>课程名称</th>
                  <th>课程描述</th>
                  <th>操作</th>
                  <th>反馈</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="course in teacherCourseList" :key="course.id">
                  <td>{{ course.id }}</td>
                  <td>{{ course.name }}</td>
                  <td>{{ course.description }}</td>
                  <td>
                    <input type="file" :id="'file-'+course.id" style="display:none" @change="onFileChange($event, course.id)" accept=".pdf,.ppt,.pptx" />

                    <!-- Case 1: Questions already generated -->
                    <button
                      v-if="uploadStatusMap[course.id] === 'generated'"
                      class="generated-btn"
                      disabled
                    >
                      已生成题目
                    </button>

                    <!-- Case 2: Uploaded, ready to create questions -->
                    <button
                      v-else-if="uploadStatusMap[course.id] === 'uploaded'"
                      class="create-question-btn"
                      @click="handleCreateQuestion(course.id)"
                    >
                      创建题目
                    </button>

                    <!-- Case 3: Default, show upload button -->
                    <button
                      v-else
                      class="upload-btn"
                      @click="triggerFileInput(course.id)"
                      :disabled="uploadLoading && uploadingCourseId === course.id"
                    >
                      上传
                    </button>

                    <span v-if="uploadLoading && uploadingCourseId === course.id">
                      上传中... {{ uploadProgress }}%
                    </span>
                  </td>
                  <td>
                    <button class="submit-feedback-btn"@click="openFeedbackModal(course.id)">问题反馈</button>
                    <button class="view-feedback-btn"@click="openFeedbackList(course.id, course.name)">查看反馈</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-else>暂无课程</div>
          </div>
        </template>
        <!-- 管理员主页 -->
        <template v-else-if="user.userRole === 'admin'">
          <h2>欢迎管理员：{{ user.userAccount }}</h2>
          <p>这里是管理员专属主页内容。</p>
          <form @submit.prevent="handleAddCourse" class="add-course-form">
            <div>
              <label>课程名称：</label>
              <input v-model="courseForm.name" required placeholder="请输入课程名称" />
            </div>
            <div>
              <label>课程描述：</label>
              <input v-model="courseForm.description" required placeholder="请输入课程描述" />
            </div>
            <div>
              <label>教师：</label>
              <select v-model="courseForm.teacherId" required>
                <option value="" disabled>请选择教师</option>
                <option v-for="teacher in teacherList" :key="teacher.id" :value="teacher.id">
                  {{ teacher.userName }}
                </option>
              </select>
            </div>
            <button class="submit-btn" type="submit">创建课程</button>
          </form>
          <div style="display: flex; justify-content: flex-end; align-items: center; margin-bottom: 10px;">
            <input v-model="searchName" placeholder="按课程名称查询" style="padding: 4px 8px; margin-right: 8px;" />
            <button @click="handleSearch">查询</button>
          </div>
          <table class="course-table" v-if="filteredCourseList.length">
            <thead>
              <tr>
                <th>课程ID</th>
                <th>课程名称</th>
                <th>课程描述</th>
                <th>教师</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="course in filteredCourseList" :key="course.id" @click="showCourseDetail(course.id)" style="cursor:pointer;">
                <td>{{ course.id }}</td>
                <td>{{ course.name }}</td>
                <td>{{ course.description }}</td>
                <td>{{ getTeacherName(course.teacherId) }}</td>
                <td>
                  <button class="edit" @click.stop="openEdit(course)">修改</button>
                  <button class="delete" @click.stop="confirmDelete(course.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>暂无课程</div>
          <div v-if="editVisible" class="edit-dialog">
            <form @submit.prevent="handleEditCourse" class="edit-form">
              <div class="form-row">
                <label>课程名称：</label>
                <input v-model="editForm.name" required />
              </div>
              <div class="form-row">
                <label>课程描述：</label>
                <input v-model="editForm.description" required />
              </div>
              <div class="form-row">
                <label>教师：</label>
                <select v-model="editForm.teacherId" required>
                  <option v-for="teacher in teacherList" :key="teacher.id" :value="teacher.id">
                    {{ teacher.userName }}
                  </option>
                </select>
              </div>
              <div class="form-actions">
                <button class="edit" type="submit">保存</button>
                <button class="delete" type="button" @click="editVisible = false">取消</button>
              </div>
            </form>
          </div>
          <div style="margin-top: 28px;">
            <h4 style="margin-bottom: 12px;">学生选课申请列表</h4>
            <table v-if="allJoinRequests.length" class="course-table">
              <thead>
                <tr>
                  <th>申请ID</th>
                  <th>课程名称</th>
                  <th>学生ID</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="req in allJoinRequests" :key="req.id">
                  <td>{{ req.id }}</td>
                  <td>{{ req.courseName }}</td>
                  <td>{{ req.studentId }}</td>
                  <td>{{ statusText(req.status) }}</td>
                  <td>
                    <template v-if="req.status === 'PENDING' || req.status === 'pending'">
                      <button class="approve-btn" @click="handleApproveJoin(req.id)">同意</button>
                      <button class="reject-btn" @click="handleRejectJoin(req.id)">不同意</button>
                    </template>
                    <template v-else-if="req.status === 'APPROVED' || req.status === 'approved'">
                      <span style="color:green;">已同意</span>
                    </template>
                    <template v-else-if="req.status === 'REJECTED' || req.status === 'rejected'">
                      <span style="color:red;">已拒绝</span>
                    </template>
                    <template v-else>
                      已操作
                    </template>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-else>暂无学生申请</div>
          </div>
        </template>
      </template>
    </div>
    <div v-if="courseDetailVisible" class="course-detail-dialog">
      <div class="course-detail-card">
        <h3>课程详情</h3>
        <p><strong>课程ID：</strong>{{ courseDetail.id }}</p>
        <p><strong>课程名称：</strong>{{ courseDetail.name }}</p>
        <p><strong>课程描述：</strong>{{ courseDetail.description }}</p>
        <p><strong>教师：</strong>{{ getTeacherName(courseDetail.teacherId) }}</p>
        <button @click="courseDetailVisible = false">关闭</button>
      </div>
    </div>
    <!-- Feedback Modal -->
    <div v-if="isFeedbackModalVisible" class="feedback-modal-overlay">
      <div class="feedback-modal">
        <h3>课程反馈</h3>
        <textarea
          v-model="feedbackContent"
          placeholder="请输入您的反馈..."
          rows="5"
        ></textarea>
        <div class="modal-actions">
          <button @click="handleFeedbackSubmit" :disabled="isSubmittingFeedback">
            {{ isSubmittingFeedback ? '提交中...' : '提交' }}
          </button>
          <button @click="isFeedbackModalVisible = false" class="cancel-btn">取消</button>
        </div>
      </div>
    </div>

    <!-- Answer Modal -->
    <div v-if="isAnswerModalVisible" class="answer-modal-overlay">
      <div class="answer-card">
        <h2 class="page-title">课程：{{ currentCourseName }} - 在线答题</h2>
        <button @click="isAnswerModalVisible = false" class="close-btn">&times;</button>
        <div v-if="questions.length > 0">
          <div v-for="(question, index) in questions" :key="index" class="question-block">
            <p class="question-title">{{ index + 1 }}. {{ question.title }}</p>
            <div class="options-list">
              <label v-for="option in question.options" :key="option.key" class="option-label">
                <input type="radio" :name="'question-' + index" :value="option.key" v-model="answers[index]">
                <span class="option-key">{{ option.key }}:</span>
                <span class="option-value">{{ option.value }}</span>
              </label>
            </div>
          </div>
          <button @click="submitAnswers" class="submit-btn" :disabled="isSubmittingAnswers">
            {{ isSubmittingAnswers ? '提交中...' : '提交答案' }}
          </button>
        </div>
        <div v-else class="loading-text">
          <p v-if="isLoadingQuestions">题目加载中...</p>
          <p v-else>该课程暂无题目。</p>
        </div>
      </div>
    </div>
    <!-- Feedback List Modal -->
    <div v-if="isFeedbackListVisible" class="feedback-modal-overlay">
      <div class="feedback-modal">
        <h3>学生反馈列表 - {{ feedbackListCourseName }}</h3>
        <div v-if="feedbackLoading">加载中...</div>
        <div v-else-if="feedbackList.length === 0">暂无学生反馈</div>
        <ul v-else style="max-height:300px;overflow:auto;padding-left:0;">
          <li v-for="item in feedbackList" :key="item.id" style="margin-bottom:12px;list-style:none;">
            <div><b>学生ID：</b>{{ item.studentId }}</div>
            <div><b>内容：</b>{{ item.content }}</div>
            <div style="color:#888;font-size:0.95em;">{{ item.createTime }}</div>
            <hr v-if="feedbackList.length > 1" style="margin:8px 0;" />
          </li>
        </ul>
        <div class="modal-actions">
          <button @click="isFeedbackListVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { addCourse } from '@/api/course'
import { getTeacherList } from '@/api/user'
import { getCourseList, getCourseById, updateCourse, deleteCourse, getStudentCourseList, joinCourse, getJoinCourseRequestList, acceptJoinRequest, uploadCourseContent, getTeacherCourseList } from '@/api/course'
import { createQuestion, checkCourseContent, showQuestion, answerQuestion , getQuestionList } from '@/api/question'
import { getCourseFeedback, submitStudentFeedback } from '@/api/feedback'

const router = useRouter()
const user = ref()

// State for quiz statuses - RE-ADD THIS
const quizStatuses = ref<Record<string, { status: string; score: string }>>({})

const updateQuizStatuses = () => {
  studentCourseList.value.forEach(course => {
    const status = localStorage.getItem(`quizStatus_${course.id}`)
    if (status) {
      quizStatuses.value[course.id] = JSON.parse(status)
    }
  })
}

function updateUser() {
  const u = localStorage.getItem('user')
  user.value = u ? JSON.parse(u) : null
}

onMounted(() => {
  updateUser()
})

window.addEventListener('storage', updateUser)

// 管理员创建课程表单
const courseForm = ref({
  name: '',
  description: '',
  teacherId: null
})

// 教师列表
const teacherList = ref<{ id: string; userName: string }[]>([])

// 课程列表
const courseList = ref<any[]>([])

const editVisible = ref(false)
const editForm = ref({
  id: '',
  name: '',
  description: '',
  teacherId: ''
})

const openEdit = (course) => {
  editForm.value = { ...course }
  editVisible.value = true
}

const handleEditCourse = async () => {
  try {
    const { id, name, description, teacherId } = editForm.value
    const res = await updateCourse(id, { name, description, teacherId })
    if (res.data.code === 0) {
      alert('修改成功')
      editVisible.value = false
      // 重新获取课程列表
      await fetchCourseList()
    } else {
      alert(res.data.message || '修改失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

const getTeacherName = (teacherId: string | number) => {
  const teacher = teacherList.value.find(t => String(t.id) === String(teacherId))
  return teacher ? teacher.userName : teacherId
}

const searchName = ref('')
const filteredCourseList = computed(() => {
  if (!searchName.value) return courseList.value
  return courseList.value.filter(c => c.name.includes(searchName.value))
})

const handleSearch = async () => {
  // 这里直接用 computed 过滤，无需额外请求
}

const confirmDelete = (id) => {
  if (window.confirm('确定要删除该课程吗？')) {
    handleDelete(id)
  }
}

const handleDelete = async (id) => {
  try {
    const res = await deleteCourse([id])
    if (res.data.code === 0) {
      alert('删除成功')
      await fetchCourseList()
    } else {
      alert(res.data.message || '删除失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

// 课程列表刷新方法
const fetchCourseList = async () => {
  try {
    const courseRes = await getCourseList()
    if (courseRes.data.code === 0) {
      courseList.value = courseRes.data.data
    }
  } catch (e) {
    alert('获取课程列表失败')
  }
}

const studentCourseList = ref<any[]>([])

const fetchStudentCourseList = async () => {
  if (!user.value) return
  try {
    const res = await getStudentCourseList(user.value.id)
    if (res.data.code === 0) {
      studentCourseList.value = res.data.data
      console.log('studentCourseList:', studentCourseList.value) // 打印内容
    }
  } catch (e) {
    alert('获取学生课程列表失败')
  }
}

onActivated(async () => {
  if (user.value && user.value.userRole === 'student') {
    await fetchStudentCourseList()
  }
})

watch(studentCourseList, updateQuizStatuses)

const allJoinRequests = ref<any[]>([])

const fetchAllJoinRequests = async () => {
  const requests: any[] = []
  for (const course of courseList.value) {
    const res = await getJoinCourseRequestList(course.id)
    if (res.data.code === 0 && Array.isArray(res.data.data)) {
      // 给每条申请加上课程名
      res.data.data.forEach(req => {
        requests.push({
          ...req,
          courseName: course.name
        })
      })
    }
  }
  allJoinRequests.value = requests
}

onMounted(async () => {
  updateUser()
  if (user.value && user.value.userRole === 'admin') {
    try {
      const res = await getTeacherList()
      if (res.data.code === 0) {
        teacherList.value = res.data.data
      }
      await fetchCourseList()
      await fetchAllJoinRequests() // 新增
    } catch (e) {
      alert('获取教师列表或课程列表失败')
    }
  }
  if (user.value && user.value.userRole === 'student') {
    await fetchStudentCourseList()
  }
  if (user.value && user.value.userRole === 'teacher') {
    await fetchTeacherCourseList()
  }
})

watch(courseList, async (newVal) => {
  if (user.value && user.value.userRole === 'admin') {
    await fetchAllJoinRequests()
  }
})

const handleAddCourse = async () => {
  try {
    const res = await addCourse(courseForm.value)
    if (res.data.code === 0) {
      alert('课程创建成功')
      courseForm.value = { name: '', description: '', teacherId: null }
      await fetchCourseList() // 添加成功后刷新
    } else {
      alert(res.data.message || '创建失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

const statusText = (status: string | null) => {
  if (status === null) return '未选'
  if (status === 'pending' || status === 'PENDING') return '待审核'
  if (status === 'approved' || status === 'APPROVED') return '已通过'
  if (status === 'rejected' || status === 'REJECTED') return '已拒绝'
  return status
}

const handleSelectCourse = async (courseId: string | number) => {
  if (!window.confirm('是否选择该课程？')) {
    return
  }
  try {
    const res = await joinCourse(courseId)
    if (res.data.code === 0) {
      alert('选课申请已提交，等待审核')
      await fetchStudentCourseList()
    } else {
      alert(res.data.message || '选课失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

const handleApproveJoin = async (requestId: string | number) => {
  if (!window.confirm('确定要同意该学生加入课程吗？')) return
  try {
    const res = await acceptJoinRequest(requestId, 'APPROVED')
    if (res.data.code === 0) {
      alert('同意成功')
      await fetchAllJoinRequests() // 刷新申请列表
      await fetchCourseList()      // 刷新课程列表，确保数据最新
    } else {
      alert(res.data.message || '同意失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

const handleRejectJoin = async (requestId: string | number) => {
  if (!window.confirm('确定要拒绝该学生加入课程吗？')) return
  try {
    const res = await acceptJoinRequest(requestId, 'REJECTED')
    if (res.data.code === 0) {
      alert('拒绝成功')
      await fetchAllJoinRequests()
      await fetchCourseList()
    } else {
      alert(res.data.message || '拒绝失败')
    }
  } catch (e) {
    alert('请求失败')
  }
}

// 教师课程列表
const teacherCourseList = ref<any[]>([])

const checkUploadedFiles = () => {
  teacherCourseList.value.forEach(course => {
    const file = localStorage.getItem('uploadedFile_' + course.id)
    if (file && uploadStatusMap.value[course.id] !== 'generated') {
      uploadStatusMap.value[course.id] = 'uploaded'
  }
  })
}

//获取教师课程列表
const fetchTeacherCourseList = async () => {
  if (!user.value) return
  try {
    const res = await getTeacherCourseList(user.value.id)
    if (res.data.code === 0) {
      teacherCourseList.value = res.data.data
      // 1. 先全部设为 idle
      teacherCourseList.value.forEach(course => {
        uploadStatusMap.value[course.id] = 'idle'
      })
      // 2. 先判断题目是否已生成
      for (const course of teacherCourseList.value) {
        try {
          const questionRes = await checkCourseContent(course.id);
          if (
            questionRes.data.code === 0 &&
            Array.isArray(questionRes.data.data) &&
            questionRes.data.data.length > 0
          ) {
            uploadStatusMap.value[course.id] = 'generated';
          }
        } catch (e) {
          console.error(`Failed to check questions for course ${course.id}`, e);
        }
      }
      // 3. 再检查本地上传记录，但**不能覆盖 generated**
      teacherCourseList.value.forEach(course => {
        const file = localStorage.getItem('uploadedFile_' + course.id)
        if (
          file &&
          uploadStatusMap.value[course.id] !== 'generated'
        ) {
          uploadStatusMap.value[course.id] = 'uploaded'
        }
      })
    }
  } catch (e) {
    alert('获取课程列表失败')
  }
}

// 上传相关
const uploadLoading = ref(false)
const uploadProgress = ref(0)
const selectedFile = ref<File | null>(null)
const uploadingCourseId = ref<string | number>('')
const fakeProgressTimer = ref<number | null>(null)
const fakeProgressValue = ref(0)

// 每门课程上传状态
const uploadStatusMap = ref<Record<string, 'idle' | 'uploading' | 'uploaded' | 'generated'>>({})

const triggerFileInput = (courseId: string | number) => {
  const input = document.getElementById('file-' + courseId) as HTMLInputElement
  if (input) input.click()
}

const onFileChange = (e: Event, courseId: string | number) => {
  const files = (e.target as HTMLInputElement).files
  if (files && files.length > 0) {
    selectedFile.value = files[0]
    uploadingCourseId.value = courseId
    handleUpload(courseId)
  }
}

const handleUpload = async (courseId: string | number) => {
  if (!selectedFile.value || !courseId) {
    alert('请选择文件')
    return
  }
  uploadLoading.value = true
  uploadProgress.value = 0
  fakeProgressValue.value = 0
  uploadStatusMap.value[courseId] = 'uploading'

  // 启动假进度
  if (fakeProgressTimer.value) clearInterval(fakeProgressTimer.value)
  fakeProgressTimer.value = window.setInterval(() => {
    if (fakeProgressValue.value < 98) {
      fakeProgressValue.value += 100 / 60
      uploadProgress.value = Math.floor(fakeProgressValue.value)
    }
  }, 1000)

  try {
    await uploadCourseContent(selectedFile.value, courseId, {
      onUploadProgress: (progressEvent: ProgressEvent) => {
        if (progressEvent.lengthComputable) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          uploadProgress.value = percent
          fakeProgressValue.value = percent
        }
      }
    })
    uploadProgress.value = 100
    fakeProgressValue.value = 100
    alert('上传成功')
    localStorage.setItem('uploadedFile_' + courseId, selectedFile.value.name) // 存储文件名
    selectedFile.value = null
    uploadingCourseId.value = ''
    uploadStatusMap.value[courseId] = 'uploaded'
  } catch (e) {
    alert('上传失败')
    uploadStatusMap.value[courseId] = 'idle'
  } finally {
    uploadLoading.value = false
    if (fakeProgressTimer.value) {
      clearInterval(fakeProgressTimer.value)
      fakeProgressTimer.value = null
    }
    uploadProgress.value = 0
    fakeProgressValue.value = 0
  }
}

const handleCreateQuestion = async (courseId: string | number) => {
  try {
    const res = await createQuestion(1, courseId)
    if (res.data.code === 0) {
      alert('题目创建成功！')
      uploadStatusMap.value[courseId] = 'generated'
    } else {
      alert(res.data.message || '题目创建失败')
    }
  } catch (e) {
    alert('题目创建失败')
  }
}

// --- Answer Modal State ---
const isAnswerModalVisible = ref(false)
const isLoadingQuestions = ref(false)
const currentCourseId = ref<string | null>(null)
const currentCourseName = ref('')
const questionData = ref<any>(null)
const questions = ref<any[]>([]);
const answers = ref<string[]>([]);
const isSubmittingAnswers = ref(false);

const openAnswerModal = async (courseId: string, courseName: string) => {
  isAnswerModalVisible.value = true
  isLoadingQuestions.value = true
  currentCourseId.value = courseId
  currentCourseName.value = courseName
  questions.value = []
  answers.value = []

  try {
    const res = await showQuestion(courseId);
    if (res.data.code === 0 && res.data.data) {
      questionData.value = res.data.data;
      if (typeof res.data.data.question === 'string') {
        questions.value = JSON.parse(res.data.data.question);
        answers.value = new Array(questions.value.length).fill(null);
      }
    } else {
      alert(res.data.message || '获取题目失败');
    }
  } catch (e) {
    alert('请求题目失败');
  } finally {
    isLoadingQuestions.value = false
  }
}

const submitAnswers = async () => {
  const unfilledQuestions = answers.value.map((answer, index) => answer === null ? index + 1 : -1).filter(index => index !== -1);
  if (unfilledQuestions.length > 0) {
    alert(`第 ${unfilledQuestions.join(', ')} 题尚未回答！`);
    return;
  }

  isSubmittingAnswers.value = true;
  try {
    const payload = {
      courserId: currentCourseId.value!,
      studentId: user.value.id,
      questionId: questionData.value.id,
      choices: answers.value,
    };
    const res = await answerQuestion(currentCourseId.value!, payload);
    if (res.data.code === 0) {
      const resultMessage = res.data.data || '提交成功！';
      alert(resultMessage);
      
      const scoreMatch = resultMessage.match(/你的得分是(\d+)分/);
      const score = scoreMatch ? scoreMatch[1] : 'N/A';
      const totalScoreMatch = resultMessage.match(/总分(\d+)分/);
      const totalScore = totalScoreMatch ? totalScoreMatch[1] : 'N/A';

      const submissionStatus = { status: 'answered', score: `${score}/${totalScore}` };
      localStorage.setItem(`quizStatus_${currentCourseId.value!}`, JSON.stringify(submissionStatus));
      
      // Immediately update UI
      quizStatuses.value[currentCourseId.value!] = submissionStatus;

      isAnswerModalVisible.value = false;
    } else {
      alert(res.data.message || '提交失败');
    }
  } catch (e) {
    console.error("提交答案请求失败:", e);
    alert('提交答案请求失败，详情请查看控制台。');
  } finally {
    isSubmittingAnswers.value = false;
  }
};

// Replace goToAnswerPage with this
const goToAnswerPage = (courseId: string, courseName: string) => {
  openAnswerModal(courseId, courseName)
}

const courseDetailVisible = ref(false)
const courseDetail = ref<any>(null)

const showCourseDetail = async (courseId: string | number) => {
  try {
    const res = await getCourseById(courseId)
    if (res.data.code === 0) {
      courseDetail.value = res.data.data
      courseDetailVisible.value = true
    } else {
      alert(res.data.message || '获取课程详情失败')
    }
  } catch (e) {
    alert('获取课程详情失败')
  }
}

const isFeedbackModalVisible = ref(false)
const feedbackContent = ref('')
const currentFeedbackCourseId = ref<string | number | null>(null)
const isSubmittingFeedback = ref(false)

const openFeedbackModal = (courseId: string | number) => {
  currentFeedbackCourseId.value = courseId
  feedbackContent.value = ''
  isFeedbackModalVisible.value = true
}

const handleFeedbackSubmit = async () => {
  if (!feedbackContent.value.trim()) {
    alert('反馈内容不能为空！')
    return
  }
  isSubmittingFeedback.value = true
  try {
    if (user.value.userRole === 'student') {
      const res = await submitStudentFeedback({
        courseId: currentFeedbackCourseId.value!,
        studentId: user.value.id,
        content: feedbackContent.value
      })
      if (res.data.code === 0) {
        alert('反馈提交成功！')
        isFeedbackModalVisible.value = false
      } else {
        alert(res.data.message || '提交失败')
      }
    } else if (user.value.userRole === 'teacher') {
      const res = await getQuestionList(currentFeedbackCourseId.value!, feedbackContent.value)
      if (res.data.code === 0) {
        alert('反馈已提交，正在重新生成题目！')
        isFeedbackModalVisible.value = false
      } else {
        alert(res.data.message || '提交失败')
      }
    }
  } catch (error) {
    console.error("提交反馈请求失败:", error);
    alert('提交反馈请求失败')
  } finally {
    isSubmittingFeedback.value = false
  }
}

const isFeedbackListVisible = ref(false)
const feedbackList = ref<any[]>([])
const feedbackLoading = ref(false)
const feedbackListCourseName = ref('')

const openFeedbackList = async (courseId: string | number, courseName: string) => {
  isFeedbackListVisible.value = true
  feedbackList.value = []
  feedbackLoading.value = true
  feedbackListCourseName.value = courseName
  try {
    const res = await getCourseFeedback(courseId)
    if (res.data.code === 0 && Array.isArray(res.data.data)) {
      feedbackList.value = res.data.data
    } else {
      feedbackList.value = []
    }
  } catch (e) {
    feedbackList.value = []
  } finally {
    feedbackLoading.value = false
  }
}

</script>

<style scoped>
body, .home-root {
  min-height: 100vh;
  background: linear-gradient(135deg, #ededf4 0%, #8ea4ca 100%);
  background-size: cover;
}

.home-root {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
}

.card {
  background: rgba(255,255,255,0.85);
  border-radius: 18px;
  box-shadow: 0 8px 32px 0 rgba(31,38,135,0.18);
  padding: 36px 40px;
  margin: 40px auto;
  max-width: 1000px;
  width: 100%;
  backdrop-filter: blur(6px);
  border: 1.5px solid rgba(255,255,255,0.18);
}

.header-title {
  font-size: 2em;
  font-weight: bold;
  color: #222;
  margin-bottom: 18px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(31,38,135,0.08);
}

.search-bar {
  float: right;
  margin-bottom: 18px;
}
.search-bar input {
  border-radius: 18px;
  border: 1px solid #dcdfe6;
  padding: 7px 18px;
  outline: none;
  font-size: 1em;
  background: rgba(255,255,255,0.7);
  transition: border 0.2s;
}
.search-bar input:focus {
  border-color: #409eff;
}
.search-bar button {
  background:rgb(143, 182, 233);
  color: #222;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-left: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(195, 199, 252, 0.12);
}
.search-bar button:hover {
  background:rgb(120, 167, 253);
}

h2 {
  font-size: 1.3em;
  margin-bottom: 10px;
  color: #222;
  font-weight: 700;
}

p {
  color: #666;
  margin-bottom: 18px;
}

.add-course-form {
  margin-top: 20px;
  margin-bottom: 32px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
  align-items: flex-end;
}
.add-course-form div {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
}
.add-course-form label {
  font-size: 0.98em;
  color: #555;
  margin-bottom: 4px;
}
.add-course-form input,
.add-course-form select {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 7px 12px;
  outline: none;
  transition: border 0.2s;
  font-size: 1em;
  background: #fafbfc;
}
.add-course-form input:focus,
.add-course-form select:focus {
  border-color: #81d8d7;
}

button {
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  color: #fff;
  cursor: pointer;
  margin-right: 8px;
  transition: box-shadow 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(64,158,255,0.08);
}
button.approve {
  background: linear-gradient(90deg, #543f63 0%, #b4a2e8 100%);
}
button.reject {
  background: linear-gradient(90deg, #bdb5d7 0%, #7356b1 100%);
  color: #222;
}
button.edit {
  background: linear-gradient(90deg, #a0cdcc 0%, #20bba1 100%);
}
button.delete {
  background: linear-gradient(90deg, #afddf2 0%, #003c75 100%);
}
button:hover {
  filter: brightness(1.08);
  box-shadow: 0 4px 16px rgba(64,158,255,0.18);
}

.submit-btn {
  background: linear-gradient(90deg, #e0c3fc 0%, #8ec5fc 100%);
  color: #222;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-left: 0;
  margin-right: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(224,195,252,0.12);
}
.submit-btn:hover {
  background: linear-gradient(90deg, #c1a0e8 0%, #6ea3e7 100%);
}
.course-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: rgba(255,255,255,0.7);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(31,38,135,0.08);
  margin-top: 24px;
  table-layout: fixed;
}
.course-table th, .course-table td {
  border-bottom: 1px solid #e0e0e0;
  padding: 16px 12px;
  text-align: center;
  font-size: 1.08em;
  word-break: break-all;
}
.course-table th {
  background: rgba(240,247,255,0.7);
  font-weight: 700;
  color: #222;
}
.course-table tr:last-child td {
  border-bottom: none;
}
.course-table td {
  color: #444;
}
.course-table button {
  margin-right: 8px;
  padding: 5px 14px;
  font-size: 0.98em;
  display: inline-block;
}
.course-table td:last-child {
  white-space: nowrap;
}

/* 让两个表格的列宽对齐 */
.course-table th,
.course-table td {
  min-width: 90px;
}
.course-table th:nth-child(1),
.course-table td:nth-child(1) {
  width: 120px;
}
.course-table th:nth-child(2),
.course-table td:nth-child(2) {
  width: 120px;
}
.course-table th:nth-child(3),
.course-table td:nth-child(3) {
  width: 180px;
}
.course-table th:nth-child(4),
.course-table td:nth-child(4) {
  width: 120px;
}
.course-table th:nth-child(5),
.course-table td:nth-child(5) {
  width: 160px;
}

.edit-dialog {
  position: fixed;
  left: 50%; top: 50%;
  transform: translate(-50%, -50%);
  background: #fff;
  border: 1px solid #ccc;
  padding: 32px 36px;
  z-index: 1000;
  border-radius: 10px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.12);
  min-width: 340px;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-row label {
  min-width: 80px; /* 控制冒号对齐 */
  text-align: right;
  font-weight: 500;
  color: #333;
}

.form-row input,
.form-row select {
  flex: 1;
  padding: 7px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 1em;
  background: #fafbfc;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 18px;
  margin-top: 10px;
}

.edit-form button.edit {
  background: linear-gradient(90deg, #a0cdcc 0%, #20bba1 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}

.edit-form button.delete {
  background: linear-gradient(90deg, #afddf2 0%, #003c75 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}

input[placeholder], select {
  font-size: 1em;
}

.upload-btn {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-right: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(64,158,255,0.18);
}
.upload-btn:hover {
  background: linear-gradient(90deg, #357ae8 0%, #409eff 100%);
}
.create-question-btn {
  background: linear-gradient(90deg, #67c23a 0%, #b3e19d 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-right: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(103,194,58,0.18);
}
.create-question-btn:hover {
  background: linear-gradient(90deg, #529b2e 0%, #67c23a 100%);
}

.generated-btn {
  background: #909399; /* A gray color for disabled state */
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: not-allowed;
}

.answered-btn {
  background: #909399;
  color: #fff;
  cursor: not-allowed;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
}

/* 选课状态列的按钮（如“选择”、“重新申请”），蓝色渐变 */
.course-table td:nth-child(4) button {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
}


/* 反馈列的按钮 */
.course-table td:nth-child(6) button {
  background: linear-gradient(90deg,rgb(54, 236, 48) 0%,rgb(26, 253, 18) 100%);
  color: #222;
}

/* 禁用按钮 */
button[disabled] {
  background: #909399 !important;
  color: #fff !important;
  cursor: not-allowed;
}

/* 让同一单元格内多个按钮有间距 */
.course-table td button {
  margin-left: 8px;
  /* 你也可以加上 margin-right: 8px; 让两边都有间距 */
}

@media (max-width: 900px) {
  .card {
    padding: 16px 8px;
  }
  .add-course-form {
    flex-direction: column;
    gap: 12px 0;
  }
  .course-table th,
  .course-table td {
    min-width: 60px;
    padding: 8px 6px;
  }
}
.course-detail-dialog {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.course-detail-card {
  background: #fff;
  padding: 32px 40px;
  border-radius: 8px;
  min-width: 320px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.2);
}

.feedback-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.feedback-modal {
  background: white;
  padding: 25px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.3);
}

.feedback-modal h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.feedback-modal textarea {
  width: 100%;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #ccc;
  margin-bottom: 20px;
  font-size: 1em;
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-actions button {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.modal-actions .cancel-btn {
  background-color: #f0f0f0;
}

/* --- Answer Modal Styles (copied and adapted from AnswerPage.vue) --- */
.answer-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.answer-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 30px 40px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 2.5em;
  color: #aaa;
  cursor: pointer;
  line-height: 1;
}

.page-title {
  font-size: 1.8em;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.loading-text {
  font-size: 1.2em;
  color: #555;
  text-align: center;
  padding: 40px 0;
}

.question-block {
  margin-bottom: 25px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.question-title {
  font-size: 1.2em;
  color: #444;
  margin-bottom: 15px;
  line-height: 1.5;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.2s;
}
.option-label:hover {
  background-color: #f7f7f7;
}
.option-label input[type="radio"] {
  margin-right: 12px;
  transform: scale(1.2);
}
.option-key {
  font-weight: bold;
  margin-right: 8px;
}

.submit-btn {
  display: block;
  width: 100%;
  padding: 12px;
  font-size: 1.2em;
  color: #fff;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 30px;
  transition: all 0.3s;
}
.submit-btn:hover {
  filter: brightness(1.1);
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
}
.submit-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
  box-shadow: none;
}

/* 反馈弹窗提交按钮 */
.feedback-modal .modal-actions button:first-child {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  margin-right: 8px;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(64,158,255,0.18);
}
/* 反馈弹窗提交按钮悬停效果 */
.feedback-modal .modal-actions button:first-child:hover {
  background: linear-gradient(90deg, #357ae8 0%, #409eff 100%);
}

/* 反馈弹窗取消按钮 */
.feedback-modal .modal-actions .cancel-btn {
  background: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 20px;
  padding: 7px 22px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
}
/* 反馈弹窗取消按钮悬停效果 */
.feedback-modal .modal-actions .cancel-btn:hover {
  background: #e0e0e0;
}

/* 管理员课程查询按钮（右上角） */
div[style*='justify-content: flex-end'] button {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}
div[style*='justify-content: flex-end'] button:hover {
  background: linear-gradient(90deg, #357ae8 0%, #409eff 100%);
}

/* 课程详情弹窗关闭按钮 */
.course-detail-dialog .course-detail-card button {
  background: #e0e0e0;
  color: #333;
  border-radius: 20px;
  padding: 7px 22px;
}
.course-detail-dialog .course-detail-card button:hover {
  background: #bdbdbd;
}

/* 选课申请同意按钮 */
.course-table .approve {
  background: linear-gradient(90deg, #67c23a 0%, #b3e19d 100%);
  color: #fff;
}
.course-table .approve:hover {
  background: linear-gradient(90deg, #529b2e 0%, #67c23a 100%);
}

/* 选课申请不同意按钮 */
.course-table .reject {
  background: linear-gradient(90deg, #f56c6c 0%, #fbb1b1 100%);
  color: #fff;
}
.course-table .reject:hover {
  background: linear-gradient(90deg, #d9534f 0%, #f56c6c 100%);
}

/* 管理员端课程修改按钮 */
.course-table .edit {
  background: linear-gradient(90deg, #20bba1 0%, #a0cdcc 100%);
  color: #fff;
}
.course-table .edit:hover {
  background: linear-gradient(90deg, #1a9c8a 0%, #20bba1 100%);
}

/* 管理员端课程删除按钮 */
.course-table .delete {
  background: linear-gradient(90deg, #dc3545 0%, #f56c6c 100%);
  color: #fff;
}
.course-table .delete:hover {
  background: linear-gradient(90deg, #b52a37 0%, #dc3545 100%);
}
/* 教师问题反馈按钮 */
.submit-feedback-btn {
  background: linear-gradient(90deg, #67c23a 0%, #b3e19d 100%);
  color: #fff;
}
/* 教师查看反馈按钮 */
.view-feedback-btn {
  background: linear-gradient(90deg, #ffb347 0%, #ffcc33 100%);
  color: #222;
}
/* 学生查看课程题目按钮 */
.view-quiz-btn {
  background: linear-gradient(90deg, #a18cd1 0%, #fbc2eb 100%);
  color: #fff;
}

</style>
