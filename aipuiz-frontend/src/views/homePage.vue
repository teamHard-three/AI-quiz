<template>
  <div>
    <div v-if="!user">您还没登录</div>
    <div v-else>
      <template v-if="user.userRole === 'student'">
        <!-- 学生主页内容 -->
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
      <template v-else-if="user.userRole === 'teacher'">
        <!-- 老师主页内容 -->
        <h2>欢迎老师：{{ user.userAccount }}</h2>
        <p>这里是老师专属主页内容。</p>
      </template>
      <template v-else-if="user.userRole === 'admin'">
        <!-- 管理员主页内容 -->
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
          <button type="submit">创建课程</button>
        </form>
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
            <button type="submit">保存</button>
            <button type="button" @click="editVisible = false">取消</button>
          </form>
        </div>
        <div style="display: flex; justify-content: flex-end; align-items: center; margin-bottom: 10px;">
          <input v-model="searchName" placeholder="按课程名称查询" style="padding: 4px 8px; margin-right: 8px;" />
          <button @click="handleSearch">查询</button>
        </div>
        <table class="course-table" v-if="courseList.length">
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
            <tr v-for="course in filteredCourseList" :key="course.id">
              <td>{{ course.id }}</td>
              <td>{{ course.name }}</td>
              <td>{{ course.description }}</td>
              <td>
                {{ getTeacherName(course.teacherId) }}
              </td>
              <td>
                <button @click="openEdit(course)">修改</button>
                <button @click="confirmDelete(course.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div style="margin-top: 20px;">
          <h4>学生选课申请列表</h4>
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
                  <button @click="handleApproveJoin(req.id)">同意</button>
                  <button @click="handleRejectJoin(req.id)">不同意</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>暂无学生申请</div>
        </div>
      </template>
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

const getTeacherName = (teacherId: string) => {
  const teacher = teacherList.value.find(t => t.id === teacherId)
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
</script>

<style scoped>
.add-course-form {
  margin-top: 20px;
}
.add-course-form div {
  margin-bottom: 10px;
}
.add-course-form label {
  display: inline-block;
  width: 80px;
}
.add-course-form input {
  padding: 4px 8px;
}
.course-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 30px;
}
.course-table th, .course-table td {
  border: 1px solid #ccc;
  padding: 8px 12px;
  text-align: left;
}
.course-table th {
  background: #f5f5f5;
}
.edit-dialog {
  position: fixed;
  left: 50%; top: 50%;
  transform: translate(-50%, -50%);
  background: #fff;
  border: 1px solid #ccc;
  padding: 24px;
  z-index: 1000;
}
</style>
