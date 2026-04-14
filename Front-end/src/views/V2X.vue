<template>
  <div class="v2x-view">
    <div class="header">
      <div class="title-wrap">
        <h2>📡 V2X 통신 실시간 관리</h2>
        <div class="selector-group">
          <label>관리 교차로:</label>
          <select v-model="selectedId" @change="updateData" class="intersection-select">
            <option v-for="item in intersectionList" :key="item.id" :value="item.id">
              {{ item.name }} ({{ item.id }})
            </option>
          </select>
        </div>
      </div>
    </div>

    <div class="v2x-stats-row">
      <div class="v2x-card stat-box">
        <span class="label">평균 통신 지연 (avg_latency_ms)</span>
        <strong class="val blue">{{ commLog.avg_latency_ms }}<small>ms</small></strong>
        <p class="sub-desc" :class="commLog.avg_latency_ms > 50 ? 'text-danger' : ''">
          {{ commLog.avg_latency_ms > 50 ? '⚠️ 지연 시간 증가' : '✅ 통신 상태 양호' }}
        </p>
      </div>

      <div class="v2x-card stat-box">
        <span class="label">SPaT 메시지 전송 성공률</span>
        <strong class="val green">{{ calculateSuccessRate }}<small>%</small></strong>
        <p class="sub-desc">
          실패 건수: {{ commLog.spat_fail_count }} / {{ commLog.spat_send_count }}
        </p>
      </div>

      <div class="v2x-card stat-box">
        <span class="label">접속 차량 (connected_vehicle_count)</span>
        <strong class="val orange">{{ commLog.connected_vehicle_count }}<small>대</small></strong>
        <p class="sub-desc">장비명: {{ commLog.v2x_device_id }}</p>
      </div>
    </div>

    <div class="v2x-card chart-section">
      <h3>📈 최근 24시간 통신 지연(Latency) 변동 추이</h3>
      <div class="chart-inner-bg">
        <div class="chart-container">
          <svg viewBox="0 0 700 120" class="v2x-svg">
            <line
              v-for="i in 4"
              :key="'grid-' + i"
              x1="0"
              :y1="i * 30"
              x2="700"
              :y2="i * 30"
              stroke="rgba(200, 200, 200, 0.1)"
              stroke-width="1"
            />
            <path :d="chartPath" fill="none" stroke="#3498db" stroke-width="4" class="path-anim" />
            <g v-for="(p, i) in historyPoints" :key="i">
              <circle :cx="p.x" :cy="p.y" r="5" :fill="p.val > 60 ? '#e74c3c' : '#3498db'" />
            </g>
          </svg>
          <div class="chart-labels">
            <span>20시</span><span>00시</span><span>04시</span><span>08시</span><span>12시</span
            ><span>16시</span><span>현재</span>
          </div>
        </div>
      </div>
    </div>

    <div class="v2x-card table-card">
      <div class="table-header-group">
        <h3>📋 실시간 통신 로그 (v2x_communication_log)</h3>
        <div class="pagination-controls">
          <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">이전</button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">
            다음
          </button>
        </div>
      </div>
      <div class="table-wrapper">
        <table class="log-table">
          <thead>
            <tr>
              <th>측정 시각</th>
              <th>SPaT 전송</th>
              <th>SPaT 실패</th>
              <th>평균 지연</th>
              <th>통신 실패</th>
              <th>접속 차량</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(log, idx) in paginatedLogs" :key="idx">
              <td>{{ log.v2x_log_time }}</td>
              <td>{{ log.spat_send_count }}건</td>
              <td :class="{ 'text-danger': log.spat_fail_count > 0 }">
                {{ log.spat_fail_count }}건
              </td>
              <td>{{ log.avg_latency_ms }}ms</td>
              <td :class="{ 'text-danger': log.comm_fail_count > 0 }">
                {{ log.comm_fail_count }}건
              </td>
              <td>{{ log.connected_vehicle_count }}대</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/api'

const currentTime = ref(new Date().toLocaleString())
let timer = null

const intersectionList = [
  { id: 'ICN-01', name: '인천시청입구 삼거리' },
  { id: 'ICN-02', name: '예술회관역 사거리' },
  { id: 'ICN-03', name: '중앙공원 사거리' },
]

const selectedId = ref('ICN-03')
const currentPage = ref(1)
const itemsPerPage = 5

const commLog = ref({
  v2x_device_id: '',
  spat_send_count: 1,
  spat_fail_count: 0,
  avg_latency_ms: 0,
  comm_fail_count: 0,
  connected_vehicle_count: 0,
})

const historyData = ref([])
const logHistory = ref([])

const totalPages = computed(() => Math.ceil(logHistory.value.length / itemsPerPage) || 1)
const paginatedLogs = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return logHistory.value.slice(start, end)
})

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--
}
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++
}

const historyPoints = computed(() => {
  return historyData.value.map((val, idx) => ({
    x: idx * 100 + 50,
    y: 100 - val * 0.8,
    val: val,
  }))
})

const chartPath = computed(() =>
  historyPoints.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' '),
)

const calculateSuccessRate = computed(() => {
  const total = commLog.value.spat_send_count
  const fail = commLog.value.spat_fail_count
  return (((total - fail) / total) * 100).toFixed(2)
})

const updateData = async () => {
  try {
    const id = selectedId.value
    const [sumRes, histRes, logsRes] = await Promise.all([
      api.get(`/api/v2x/${id}`),
      api.get(`/api/v2x/${id}/history`),
      api.get(`/api/v2x/${id}/logs`),
    ])
    const s = sumRes.data
    commLog.value = {
      v2x_device_id: s.deviceId ?? '',
      spat_send_count: Math.max(1, s.spatSendCount ?? 1),
      spat_fail_count: s.spatFailCount ?? 0,
      avg_latency_ms: s.avgLatencyMs ?? 0,
      comm_fail_count: 0,
      connected_vehicle_count: s.connectedVehicleCount ?? 0,
    }
    const rows = logsRes.data ?? []
    if (rows.length) {
      commLog.value.comm_fail_count = rows[0].commFailCount ?? 0
    }
    historyData.value = histRes.data.map((h) => h.latency)
    logHistory.value = rows.map((log) => ({
      v2x_log_time: log.v2xLogTime,
      spat_send_count: log.spatSendCount,
      spat_fail_count: log.spatFailCount,
      avg_latency_ms: log.avgLatencyMs,
      comm_fail_count: log.commFailCount,
      connected_vehicle_count: log.connectedVehicleCount,
    }))
    currentPage.value = 1
  } catch (error) {
    console.error('V2X API 오류:', error)
  }
}

onMounted(() => {
  updateData()

  timer = setInterval(() => {
    currentTime.value = new Date().toLocaleString()
  }, 1000)
})
onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
/* [라이트모드 및 공통 레이아웃 - 기존 유지] */
.v2x-view {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 10px 24px;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  background: #f8f9fb;
}
.title-wrap h2 {
  font-size: 24px;
}

.header {
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  border-bottom: 2px solid #eee;
  padding-bottom: 15px;
}

.v2x-stats-row {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.v2x-card {
  background: white;
  padding: 18px;
  border-radius: 12px;
}

.val {
  font-size: 30px;
  font-weight: 800;
  display: block;
  margin: 4px 0;
}
.val.blue {
  color: #3498db;
}
.val.green {
  color: #2ecc71;
}
.val.orange {
  color: #e67e22;
}
.val small {
  font-size: 14px;
  margin-left: 4px;
  color: #888;
}

.chart-section {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}
.v2x-svg {
  width: 100%;
  height: 110px;
  overflow: visible;
}
.chart-labels {
  display: flex;
  justify-content: space-between;
  padding: 0 45px;
  font-size: 11px;
  color: #bbb;
  margin-top: 5px;
}

.table-card {
  flex: 0 1 auto;
  min-height: 260px;
  max-height: min(420px, 50vh);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.table-header-group {
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.table-wrapper {
  overflow-x: auto;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}
.log-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.log-table th {
  background: #fbfbfc;
  padding: 12px;
  text-align: left;
  border-bottom: 2px solid #eee;
  color: #555;
}
.log-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #eee;
}

.intersection-select,
.page-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
}

/* ==========================================================
   🌑 다크모드 (이미지와 동일하게 수정)
   ========================================================= */

.dark-theme .v2x-view {
  background: #535151 !important;
}

.dark-theme .v2x-card {
  background: #1e1e1e !important;
  color: #eee;
  border: 1px solid #2c2c2c;
  box-shadow: 0 10px 12px rgba(0, 0, 0, 0.4);
}

/* 💡 상하 라인 일치 수정: 헤더 하단과 테이블 카드 상단에 동일한 디자인 적용 */
.dark-theme .header {
  border-bottom: 2px solid #7e7e7e !important;
}

.dark-theme .table-card {
  border-top: 1px solid #333 !important;
}

/* 다크모드 글자색 일치 */
.dark-theme .header h2,
.dark-theme .header p,
.dark-theme .header strong,
.dark-theme .selector-group label,
.dark-theme h3 {
  color: #ffffff !important;
}

/* 테이블 내부 */
.dark-theme .log-table th {
  background: #252525;
  color: #bbb;
  border-bottom: 1px solid #333;
}
.dark-theme .log-table td {
  border-bottom: 1px solid #2a2a2a;
  color: #ddd;
}
.dark-theme .log-table {
  border-bottom: 1px solid #333;
}

/* 입력 요소 */
.dark-theme .intersection-select,
.dark-theme .page-btn {
  background: #252525;
  border: 1px solid #444;
  color: #eee;
}
.dark-theme .page-btn:disabled {
  background: #1e1e1e;
  color: #555;
}
</style>
