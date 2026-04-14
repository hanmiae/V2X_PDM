<template>
  <div class="analysis-view" :class="{ 'dark-theme': isDarkMode }">
    <div class="header">
      <div class="title-wrap">
        <h2>🛠️ 장비 예지보전 정밀 진단</h2>
        <div class="selector-group">
          <label>분석 대상 교차로:</label>
          <select v-model="selectedId" @change="updateData" class="intersection-select">
            <option v-for="item in intersectionList" :key="item.id" :value="item.id">
              {{ item.name }} ({{ item.id }})
            </option>
          </select>
        </div>
      </div>
    </div>

    <div class="top-prediction-row">
      <div class="risk-mini-card" :class="riskData.risk_level">
        <span class="label">종합 위험 점수</span>
        <strong class="score">{{ riskData.total_risk_score }}<small>/100</small></strong>
        <div class="badge">{{ riskData.risk_level }}</div>
      </div>

      <div class="rul-card">
        <div class="rul-info">
          <h3>⏳ 잔여 수명 예측 (RUL)</h3>
          <p class="rul-desc">
            현재 추세라면 메인보드가 약
            <strong class="text-danger">{{ riskData.remain_days }}일 이내</strong>에 완전히 고장 날
            확률이 <strong class="text-danger">{{ riskData.fail_prob }}%</strong>입니다.
          </p>
          <div class="rul-progress-container">
            <div class="rul-bar">
              <div
                class="rul-fill"
                :style="{
                  width: riskData.remain_days + '%',
                  backgroundColor: riskData.remain_days < 20 ? '#e74c3c' : '#2ecc71',
                }"
              ></div>
            </div>
            <div class="rul-labels">
              <span>교체 필요 (0일)</span>
              <span>권장 수명 (100일)</span>
            </div>
          </div>
        </div>
      </div>

      <div class="breakdown-mini-column">
        <div class="mini-sub-card">
          <span>제어기 위험 지수</span>
          <strong>{{ riskData.controller_risk_score }}</strong>
        </div>
        <div class="mini-sub-card">
          <span>V2X 통신 위험 지수</span>
          <strong>{{ riskData.v2x_risk_score }}</strong>
        </div>
      </div>
    </div>

    <div class="history-chart-card">
      <h3>📈 최근 24시간 위험 점수 변동 추이 (시간 단위)</h3>
      <div class="chart-container">
        <svg viewBox="0 0 700 180" class="history-svg">
          <line
            v-for="i in 6"
            :key="i"
            x1="0"
            :y1="i * 30"
            x2="700"
            :y2="i * 30"
            stroke="#eee"
            stroke-width="1"
            class="grid-line"
          />
          <path :d="chartPath" fill="none" stroke="#4a90e2" stroke-width="4" class="path-anim" />

          <g v-for="(point, idx) in historyData" :key="'pt-' + idx + '-' + point.x" class="chart-point-group">
            <circle
              :cx="point.x"
              :cy="point.y"
              :r="point.isEdge ? 5 : 6"
              :fill="point.val > 60 ? '#e74c3c' : '#4a90e2'"
              class="point-dot"
            />
            <circle :cx="point.x" :cy="point.y" r="20" fill="transparent" class="hover-trigger" />
            <g class="tooltip-box">
              <rect
                :x="point.x - 25"
                :y="point.y - 40"
                width="50"
                height="28"
                rx="5"
                fill="rgba(0,0,0,0.85)"
              />
              <text
                :x="point.x"
                :y="point.y - 22"
                text-anchor="middle"
                fill="white"
                font-size="14"
                font-weight="bold"
              >
                {{ point.val }}
              </text>
            </g>
          </g>
        </svg>
        <div class="chart-labels">
          <span v-for="label in chartLabels" :key="label" :class="{ today: label === '현재' }">{{
            label
          }}</span>
        </div>
      </div>
    </div>

    <div class="detail-analysis-grid">
      <div class="detail-card">
        <h3>🚥 제어기 상태 ({{ controllerLog.device_id }})</h3>
        <div class="metrics-grid">
          <div class="metric-item">
            <span class="m-label">CPU 온도 </span>
            <strong :class="{ 'text-danger': controllerLog.cpu_temp > 60 }"
              >{{ controllerLog.cpu_temp }}°C</strong
            >
          </div>
          <div class="metric-item">
            <span class="m-label">응답 속도 </span>
            <strong>{{ controllerLog.response_time_ms }}ms</strong>
          </div>
          <div class="metric-item">
            <span class="m-label">가동 시간 </span>
            <strong>{{ Math.floor(controllerLog.uptime_min / 60) }}시간</strong>
          </div>
          <div class="metric-item">
            <span class="m-label">오류/재부팅 </span>
            <strong class="text-warning"
              >{{ controllerLog.reboot_count }} / {{ controllerLog.error_count }}</strong
            >
          </div>
        </div>
      </div>

      <div class="detail-card">
        <h3>📡 V2X 품질 ({{ commLog.device_id }})</h3>
        <div class="metrics-grid">
          <div class="metric-item">
            <span class="m-label">SPaT 성공률 </span>
            <strong class="text-blue"
              >{{
                ((1 - commLog.spat_fail_count / commLog.spat_send_count) * 100).toFixed(2)
              }}%</strong
            >
          </div>
          <div class="metric-item">
            <span class="m-label">평균 지연 </span>
            <strong>{{ commLog.avg_latency_ms }}ms</strong>
          </div>
          <div class="metric-item">
            <span class="m-label">접속 차량 </span>
            <strong>{{ commLog.connected_vehicle_count }}대</strong>
          </div>
          <div class="metric-item">
            <span class="m-label">통신 실패 </span>
            <strong class="text-danger">{{ commLog.comm_fail_count }}건</strong>
          </div>
        </div>
      </div>
    </div>

    <div class="analysis-report-box">
      <div class="comment-content">
        <div class="icon">💡</div>
        <p><strong>AI 분석 요약:</strong> {{ riskData.analysis_comment }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, inject } from 'vue'
import api from '@/api'

const isDarkMode = inject('isDarkMode', ref(false))
const analysisTime = ref(new Date().toLocaleString())
let timer = null

const intersectionList = [
  { id: 'ICN-01', name: '인천시청입구 삼거리' },
  { id: 'ICN-02', name: '예술회관역 사거리' },
  { id: 'ICN-03', name: '중앙공원 사거리' },
]
const selectedId = ref('ICN-03')
const chartLabels = ['20시', '00시', '04시', '08시', '12시', '16시', '현재']

const riskData = ref({
  total_risk_score: 0,
  controller_risk_score: 0,
  v2x_risk_score: 0,
  risk_level: '',
  remain_days: 0,
  fail_prob: 0,
  analysis_comment: '',
})

const controllerLog = ref({
  device_id: '',
  cpu_temp: 0,
  response_time_ms: 0,
  uptime_min: 0,
  reboot_count: 0,
  error_count: 0,
})

const commLog = ref({
  device_id: '',
  spat_send_count: 1,
  spat_fail_count: 0,
  avg_latency_ms: 0,
  comm_fail_count: 0,
  connected_vehicle_count: 0,
})

const currentHistory = ref([])

const historyData = computed(() => {
  return currentHistory.value.map((val, idx) => ({
    x: 50 + idx * 100,
    y: 160 - val * 1.5,
    val: val,
  }))
})

const chartPath = computed(() => {
  return historyData.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
})

const round2 = (n) => (typeof n === 'number' ? Math.round(n * 100) / 100 : n)

const updateData = async () => {
  const id = selectedId.value
  try {
    const [paRes, histRes, ctrlRes, vxRes] = await Promise.all([
      api.get(`/api/predictive/${id}`),
      api.get(`/api/predictive/${id}/history`),
      api.get(`/api/predictive/${id}/controller`),
      api.get(`/api/predictive/${id}/v2x`),
    ])
    const pa = paRes.data
    riskData.value = {
      total_risk_score: round2(pa.totalRiskScore),
      controller_risk_score: round2(pa.controllerRiskScore),
      v2x_risk_score: round2(pa.v2xRiskScore),
      risk_level: pa.riskLevel,
      remain_days: round2(pa.remain_days ?? pa.remainDays),
      fail_prob: round2(pa.fail_prob ?? pa.failProb),
      analysis_comment: pa.analysisComment ?? '',
    }
    currentHistory.value = histRes.data.map((h) => h.score)
    const c = ctrlRes.data
    controllerLog.value = {
      device_id: c.deviceId,
      cpu_temp: c.cpuTemp,
      response_time_ms: c.responseTimeMs,
      uptime_min: c.uptimeMin,
      reboot_count: c.rebootCount,
      error_count: c.errorCount,
    }
    const v = vxRes.data
    commLog.value = {
      device_id: v.deviceId,
      spat_send_count: Math.max(1, v.spatSendCount ?? 1),
      spat_fail_count: v.spatFailCount ?? 0,
      avg_latency_ms: v.avgLatencyMs ?? 0,
      comm_fail_count: v.commFailCount ?? 0,
      connected_vehicle_count: v.connectedVehicleCount ?? 0,
    }
  } catch (error) {
    console.error('예지보전 API 오류:', error)
  }
}

onMounted(() => {
  updateData()

  timer = setInterval(() => {
    analysisTime.value = new Date().toLocaleString()
  }, 1000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.analysis-view {
  display: flex;
  flex-direction: column;
  gap: 13px;
  padding: 8px 8px 24px;
  background: #f8f9fb;
  max-width: 100%;
  box-sizing: border-box;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-bottom: 15px;
  border-bottom: 2px solid #eee;
}
.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.intersection-select {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
}

/* 💡 정렬 수정: 카드 사이의 가로 간격을 10px로 좁혀서 라인을 맞춤 */
.top-prediction-row {
  display: grid;
  grid-template-columns: 2fr 5.15fr 2fr;
  gap: 10px;
}
.risk-mini-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  text-align: center;
  border-top: 5px solid #2ecc71;
}
.risk-mini-card.위험 {
  border-top-color: #e74c3c;
}
.risk-mini-card.주의 {
  border-top-color: #f1c40f;
}
.risk-mini-card.경고 {
  border-top-color: #e67e22;
}
.risk-mini-card .score {
  font-size: 32px;
  font-weight: 800;
  display: block;
}
.risk-mini-card .badge {
  display: inline-block;
  padding: 2px 12px;
  border-radius: 20px;
  background: #e74c3c;
  color: white;
  font-size: 12px;
  margin-top: 5px;
}
.risk-mini-card.정상 .badge {
  background: #2ecc71;
}
.risk-mini-card.주의 .badge {
  background: #f1c40f;
}

.rul-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
.rul-desc {
  font-size: 14px;
  margin-bottom: 15px;
  color: #444;
}
.rul-bar {
  height: 12px;
  background: #eee;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 5px;
}
.rul-fill {
  height: 100%;
  transition: width 0.8s ease;
}
.rul-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
}

.mini-sub-card {
  background: white;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.mini-sub-card:last-child {
  margin-bottom: 0;
}
.mini-sub-card span {
  font-size: 11px;
  color: #888;
  display: block;
}
.mini-sub-card strong {
  font-size: 18px;
}

.history-chart-card {
  background: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
}
.history-chart-card h3 {
  font-size: 14px;
  margin-bottom: 10px;
  color: #444;
}
.history-svg {
  width: 100%;
  height: 180px;
  overflow: visible;
}
.tooltip-box {
  visibility: hidden;
  opacity: 0;
  transition: 0.2s;
  pointer-events: none;
  z-index: 10;
}
.chart-point-group:hover .tooltip-box {
  visibility: visible;
  opacity: 1;
}
.hover-trigger {
  cursor: pointer;
}
.chart-labels {
  display: flex;
  justify-content: space-between;
  padding: 0 45px;
  margin-top: 5px;
  font-size: 11px;
  color: #bbb;
}
.chart-labels .today {
  color: #4a90e2;
  font-weight: bold;
}

.detail-analysis-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.detail-card {
  background: white;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
.detail-card h3 {
  font-size: 14px;
  margin-bottom: 10px;
  border-left: 4px solid #4a90e2;
  padding-left: 8px;
  color: #444;
}
.metrics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.metric-item {
  background: #f9fbff;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
}
.metric-item strong {
  font-size: 18px;
  color: #333;
}

.analysis-report-box {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #eee;
}
.comment-content {
  display: flex;
  gap: 15px;
  align-items: center;
  background: #f8fbff;
  padding: 15px;
  border-radius: 8px;
  font-size: 13px;
  color: #333;
}

/* 🌑 다크모드 대응 */
.dark-theme .analysis-view {
  background: #535151;
}
.dark-theme .risk-mini-card,
.dark-theme .rul-card,
.dark-theme .mini-sub-card,
.dark-theme .history-chart-card,
.dark-theme .detail-card,
.dark-theme .analysis-report-box {
  background: #1e1e1e !important;
  color: #eee !important;
  border: 1px solid #333 !important;
}
.dark-theme .header {
  border-bottom: 2px solid #7e7e7e;
}
.dark-theme .rul-card h3,
.dark-theme .rul-card .rul-desc,
.dark-theme .rul-card .rul-labels span,
.dark-theme .header h2,
.dark-theme .header p,
.dark-theme .header strong,
.dark-theme .mini-sub-card span,
.dark-theme h3 {
  color: #ffffff;
}
.dark-theme .label {
  color: #ffffff;
}
.dark-theme .score,
.dark-theme .metric-item strong,
.dark-theme h2 {
  color: white !important;
}
.dark-theme .metric-item {
  background: #252525;
}
.dark-theme .m-label {
  color: #ececec;
}
.dark-theme .comment-content {
  background: #252525;
  color: #eee;
}
.dark-theme .intersection-select {
  background: #252525;
  border: 1px solid #444;
  color: #eee;
}
.dark-theme .grid-line {
  stroke: #333;
}
.dark-theme .rul-bar {
  background: #333;
}
.dark-theme .selector-group label {
  color: #ffffff;
}
/* ===== 다크모드 카드 그림자 부드럽게 ===== */

.dark-theme .risk-mini-card,
.dark-theme .rul-card,
.dark-theme .mini-sub-card,
.dark-theme .history-chart-card,
.dark-theme .detail-card,
.dark-theme .analysis-report-box {
  background-color: #1e1e1e !important;
  color: #eeeeee !important;
  border: 1px solid #333333 !important;

  box-shadow: 0 10px 12px rgba(0, 0, 0, 0.4);
}
</style>
