<template>
  <div class="split-screen">
    <div class="left-panel">
      <div class="logo">V2XCONTROL<span class="dot">.</span></div>
    </div>

    <div class="right-panel">
      <div class="login-card-box">
        <h2>Sign up</h2>
        <div class="divider"></div>
        <form @submit.prevent="handleSignup">
          <div class="form-group">
            <label>Employee No (사원번호)</label>
            <input type="text" v-model="employeeNo" placeholder="사원번호를 입력하세요" />
          </div>

          <div class="form-group">
            <label>ID (아이디)</label>
            <input type="text" v-model="loginId" placeholder="사용할 아이디를 입력하세요" />
          </div>

          <div class="form-group">
            <label>Password (비밀번호)</label>
            <input type="password" v-model="password" placeholder="비밀번호를 입력하세요" />
          </div>

          <div class="form-group">
            <label>Name (이름)</label>
            <input type="text" v-model="adminName" placeholder="관리자 이름을 입력하세요" />
          </div>

          <div class="form-group">
            <label>Phone (휴대폰번호)</label>
            <input type="text" v-model="phoneNumber" placeholder="010-1234-5678" />
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '처리 중…' : 'Sign up' }}
          </button>
        </form>

        <p class="register-footer">
          Already have an account? <router-link to="/">Login Here</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { signup as signupApi } from '@/api/auth'

const router = useRouter()
const employeeNo = ref('')
const loginId = ref('')
const password = ref('')
const adminName = ref('')
const phoneNumber = ref('')
const loading = ref(false)

const handleSignup = async () => {
  if (!employeeNo.value.trim()) {
    alert('사원번호를 입력해주세요.')
    return
  }
  if (!loginId.value.trim()) {
    alert('아이디를 입력해주세요.')
    return
  }
  if (!password.value.trim()) {
    alert('비밀번호를 입력해주세요.')
    return
  }
  if (!adminName.value.trim()) {
    alert('이름을 입력해주세요.')
    return
  }
  if (!phoneNumber.value.trim()) {
    alert('휴대폰번호를 입력해주세요.')
    return
  }

  loading.value = true
  try {
    await signupApi({
      employeeNo: employeeNo.value.trim(),
      loginId: loginId.value.trim(),
      password: password.value,
      adminName: adminName.value.trim(),
      phoneNumber: phoneNumber.value.trim(),
    })
    alert('회원가입이 완료되었습니다. 로그인해 주세요.')
    await router.push('/')
  } catch (e) {
    alert(e instanceof Error ? e.message : '회원가입에 실패했습니다.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.split-screen {
  display: flex;
  width: 100%;
  max-width: 100%;
  flex: 1;
  min-height: 100dvh;
  overflow: hidden;
}

/* 왼쪽 30% 비율 */
.left-panel {
  flex: 3;
  background-color: #4a90e2;
  display: flex;
  justify-content: center;
  align-items: center;
}
.logo {
  font-size: 67px;
  font-weight: 900;
  color: white;
  letter-spacing: -2px;
  white-space: nowrap;
}
.dot {
  color: #f1c40f;
}

/* 오른쪽 70% 비율 */
.right-panel {
  flex: 7;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff; /* 어두운 배경 유지 */
}

/* 흰색 상자 디자인 */
.login-card-box {
  width: 100%;
  max-width: 420px;
  background-color: #ffffff;
  padding: 50px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
}

.divider {
  text-align: center;
  border-bottom: 1px solid #eee;
  line-height: 0.1em;
  margin: 25px 0;
}

h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 5px;
}
.subtitle {
  color: #666;
  font-size: 14px;
  margin-bottom: 25px;
}

.form-group {
  margin-bottom: 18px; /* 간격을 살짝 좁혀 전체 높이 조절 */
}
label {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 6px;
}
input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}
input:focus {
  outline: none;
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}
.submit-btn {
  width: 100%;
  padding: 14px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
}
.submit-btn:hover {
  background: #357abd;
}

.register-footer {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 14px;
}
.register-footer a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
}
</style>
