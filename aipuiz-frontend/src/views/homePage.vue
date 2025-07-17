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
                    已拒绝
                    <button @click="handleSelectCourse(course.id)">重新申请</button>
                  </template>
                  <template v-else>
                    {{ statusText(course.status) }}
                  </template>
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
          <div style="margin-bottom: 20px;">
            <h3>上传课程内容</h3>
            <select v-model="selectedCourseId" required>
              <option value="" disabled>请选择课程</option>
              <option v-for="course in teacherCourseList" :key="course.id" :value="course.id">
                {{ course.name }}
              </option>
            </select>
            <input type="file" @change="onFileChange" accept=".pdf,.ppt,.pptx" />
            <button :disabled="uploadLoading" @click="handleUpload">
              {{ uploadLoading ? '上传中...' : '上传' }}
            </button>
            <div v-if="uploadLoading">
              上传进度：{{ uploadProgress }}%
            </div>
          </div>
          <div>
            <h3>我的课程列表</h3>
            <table class="course-table" v-if="teacherCourseList.length">
              <thead>
                <tr>
                  <th>课程ID</th>
                  <th>课程名称</th>
                  <th>课程描述</th>
                  <th>创建时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="course in teacherCourseList" :key="course.id">
                  <td>{{ course.id }}</td>
                  <td>{{ course.name }}</td>
                  <td>{{ course.description }}</td>
                  <td>{{ course.createTime }}</td>
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
            <form @submit.prevent="handleEditCourse">
              <div>
                <label>课程名称：</label>
                <input v-model="editForm.name" required />
              </div>
              <div>
                <label>课程描述：</label>
                <input v-model="editForm.description" required />
              </div>
              <div>
                <label>教师：</label>
                <select v-model="editForm.teacherId" required>
                  <option v-for="teacher in teacherList" :key="teacher.id" :value="teacher.id">
                    {{ teacher.userName }}
                  </option>
                </select>
              </div>
              <button class="edit" type="submit">保存</button>
              <button class="delete" type="button" @click="editVisible = false">取消</button>
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
                      <button class="approve" @click="handleApproveJoin(req.id)">同意</button>
                      <button class="reject" @click="handleRejectJoin(req.id)">不同意</button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { addCourse } from '@/api/course'
import { getTeacherList } from '@/api/user'
import { getCourseList } from '@/api/course'
import { updateCourse } from '@/api/course'
import { deleteCourse } from '@/api/course'
import { getStudentCourseList } from '@/api/course'
import { joinCourse } from '@/api/course'
import { getJoinCourseRequestList } from '@/api/course'
import { acceptJoinRequest } from '@/api/course'
import { uploadCourseContent, getTeacherCourseList, getCourseById } from '@/api/course'

const user = ref()

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

// 上传相关
const uploadLoading = ref(false)
const uploadProgress = ref(0)
const selectedFile = ref<File | null>(null)
const selectedCourseId = ref<string | number>('')

const onFileChange = (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (files && files.length > 0) {
    selectedFile.value = files[0]
  } else {
    selectedFile.value = null
  }
}

// 获取教师课程列表
const fetchTeacherCourseList = async () => {
  if (!user.value) return
  try {
    const res = await getTeacherCourseList(user.value.id)
    if (res.data.code === 0) {
      teacherCourseList.value = res.data.data
    }
  } catch (e) {
    alert('获取课程列表失败')
  }
}

// 上传课程内容
const handleUpload = async () => {
  if (!selectedFile.value || !selectedCourseId.value) {
    alert('请选择课程和文件')
    return
  }
  uploadLoading.value = true
  uploadProgress.value = 0
  try {
    await uploadCourseContent(selectedFile.value, selectedCourseId.value, {
      onUploadProgress: (progressEvent: ProgressEvent) => {
        if (progressEvent.lengthComputable) {
          uploadProgress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        }
      }
    })
    alert('上传成功')
    selectedFile.value = null
    selectedCourseId.value = ''
  } catch (e) {
    alert('上传失败')
  } finally {
    uploadLoading.value = false
    uploadProgress.value = 0
  }
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
  padding: 24px;
  z-index: 1000;
  border-radius: 10px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.12);
}

input[placeholder], select {
  font-size: 1em;
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
</style>
