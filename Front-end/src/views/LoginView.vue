<template>
  <div class="split-screen">
    <div class="left-panel">
      <div class="logo">V2XCONTROL<span class="dot">.</span></div>
    </div>

    <div class="right-panel">
      <div class="login-card-box">
        <h2>Log in</h2>

        <div class="divider"><span>Or</span></div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label>ID</label>
            <input type="text" v-model="userId" placeholder="아이디를 입력하세요" />
          </div>

          <div class="form-group">
            <div class="label-row">
              <label>Password</label>
            </div>
            <input type="password" v-model="password" placeholder="패스워드를 입력하세요" />
          </div>

          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '로그인 중…' : 'Log in' }}
          </button>
        </form>

        <p class="register-footer">
          Don't have an account? <router-link to="/signup">Register Here</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi } from '@/api/auth'
import { setAuthenticated } from '@/auth/session'

const router = useRouter()
const userId = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!userId.value.trim()) {
    alert('아이디를 입력해주세요.')
    return
  }
  if (!password.value.trim()) {
    alert('비밀번호를 입력해주세요.')
    return
  }

  loading.value = true
  try {
    await loginApi({
      loginId: userId.value.trim(),
      password: password.value,
    })
    setAuthenticated(true)
    await router.push('/dashboard')
  } catch (e) {
    alert(e instanceof Error ? e.message : '로그인에 실패했습니다.')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 기존 스타일 그대로 유지하고 하단 링크 스타일만 확인하세요 */
.split-screen {
  display: flex;
  width: 100%;
  max-width: 100%;
  flex: 1;
  min-height: 100dvh;
  overflow: hidden;
}

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

.right-panel {
  flex: 7;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card-box {
  width: 100%;
  max-width: 420px;
  background-color: #ffffff;
  padding: 50px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
}

h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 10px;
}
.divider {
  text-align: center;
  border-bottom: 1px solid #eee;
  line-height: 0.1em;
  margin: 25px 0;
}
.divider span {
  background: #fff;
  padding: 0 10px;
  color: #aaa;
  font-size: 14px;
}
.form-group {
  margin-bottom: 22px;
}
label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
}
input {
  width: 100%;
  padding: 13px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  box-sizing: border-box;
}
input:focus {
  outline: none;
  border-color: #4a90e2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}
.submit-btn {
  width: 100%;
  padding: 15px;
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

/* 회원가입 링크 스타일 */
.register-footer {
  text-align: center;
  margin-top: 35px;
  color: #666;
  font-size: 14px;
}
.register-footer a {
  color: #4a90e2;
  text-decoration: none;
  font-weight: 600;
}
</style>
