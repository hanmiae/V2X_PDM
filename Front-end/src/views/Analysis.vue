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
                  width: (100 - riskData.total_risk_score) + '%',
                  backgroundColor: riskData.total_risk_score > 60 ? '#e74c3c' : '#2ecc71',
                }"
              ></div>
            </div>
            <div class="rul-labels">
              <span>교체 필요</span>
              <span>정상 운영</span>
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
      <h3>📈 최근 1시간 위험 점수 추이 (실시간 10분 단위)</h3>
      <div class="chart-container">
        <svg viewBox="0 0 700 180" class="history-svg" preserveAspectRatio="none">
          <line v-for="i in 6" :key="i" x1="0" :y1="i * 30" x2="700" :y2="i * 30" stroke="#eee" stroke-width="1" class="grid-line" />
          
          <path :d="chartPath" fill="none" stroke="#4a90e2" stroke-width="4" stroke-linejoin="round" stroke-linecap="round" class="path-anim" />

          <g v-for="(point, idx) in historyData" :key="'pt-' + idx" class="chart-point-group">
            <circle
              :cx="point.x" :cy="point.y"
              :r="point.isEdge ? 5 : 6"
              :fill="point.val > 60 ? '#e74c3c' : '#4a90e2'"
              class="point-dot"
            />
            <g class="tooltip-box">
              <rect :x="point.x - 25" :y="point.y - 40" width="50" height="28" rx="5" fill="rgba(0,0,0,0.85)" />
              <text :x="point.x" :y="point.y - 22" text-anchor="middle" fill="white" font-size="12" font-weight="bold">
                {{ point.val }}
              </text>
            </g>
          </g>
        </svg>
        <div class="chart-labels">
          <span v-for="label in chartLabels" :key="label" :class="{ today: label === '현재' }">
            {{ label }}
          </span>
        </div>
      </div>
    </div>

    <div class="detail-analysis-grid">
      <div class="detail-card">
        <h3>🚥 제어기 상태 ({{ controllerLog.deviceId }})</h3>
        <div class="metrics-grid">
          <div class="metric-item"><span>CPU 온도</span><strong>{{ controllerLog.cpuTemp }}°C</strong></div>
          <div class="metric-item"><span>응답 속도</span><strong>{{ controllerLog.responseTimeMs }}ms</strong></div>
          <div class="metric-item"><span>가동 시간</span><strong>{{ controllerUptimeHours }}h</strong></div>
          <div class="metric-item"><span>에러 건수</span><strong class="text-warning">{{ controllerLog.errorCount }}</strong></div>
        </div>
      </div>
      <div class="detail-card">
        <h3>📡 V2X 품질 ({{ commLog.deviceId }})</h3>
        <div class="metrics-grid">
          <div class="metric-item"><span>평균 지연</span><strong>{{ commLog.avgLatencyMs }}ms</strong></div>
          <div class="metric-item"><span>차량 접속</span><strong>{{ commLog.connectedVehicleCount }}</strong></div>
          <div class="metric-item"><span>통신 실패</span><strong class="text-danger">{{ commLog.commFailCount }}</strong></div>
          <div class="metric-item"><span>성공률</span><strong>{{ v2xSpatSuccessPercent }}</strong></div>
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
let dataTimer = null

const intersectionList = [
  { id: 'ICN-01', name: '인천시청입구 삼거리' },
  { id: 'ICN-02', name: '예술회관역 사거리' },
  { id: 'ICN-03', name: '중앙공원 사거리' },
]
const selectedId = ref('ICN-03')
const chartLabels = ref([])

const riskData = ref({ total_risk_score: 0, controller_risk_score: 0, v2x_risk_score: 0, risk_level: '정상', remain_days: 0, fail_prob: 0, analysis_comment: '' })
const controllerLog = ref({ deviceId: '-', cpuTemp: 0, responseTimeMs: 0, uptimeMin: 0, errorCount: 0 })
const commLog = ref({ deviceId: '-', spatSendCount: 0, spatFailCount: 0, avgLatencyMs: 0, commFailCount: 0, connectedVehicleCount: 0 })
const currentHistory = ref([0, 0, 0, 0, 0, 0, 0])

/** 백엔드 DTO는 camelCase; snake_case 응답도 허용 */
function normalizeControllerStatus(d) {
  if (!d || typeof d !== 'object') {
    return { deviceId: '-', cpuTemp: 0, responseTimeMs: 0, uptimeMin: 0, errorCount: 0 }
  }
  return {
    deviceId: d.deviceId ?? d.device_id ?? '-',
    cpuTemp: Number(d.cpuTemp ?? d.cpu_temp ?? 0),
    responseTimeMs: Number(d.responseTimeMs ?? d.response_time_ms ?? 0),
    uptimeMin: Number(d.uptimeMin ?? d.uptime_min ?? 0),
    errorCount: Number(d.errorCount ?? d.error_count ?? 0),
  }
}

function normalizeV2xStatus(d) {
  if (!d || typeof d !== 'object') {
    return { deviceId: '-', spatSendCount: 0, spatFailCount: 0, avgLatencyMs: 0, commFailCount: 0, connectedVehicleCount: 0 }
  }
  return {
    deviceId: d.deviceId ?? d.device_id ?? '-',
    spatSendCount: Number(d.spatSendCount ?? d.spat_send_count ?? 0),
    spatFailCount: Number(d.spatFailCount ?? d.spat_fail_count ?? 0),
    avgLatencyMs: Number(d.avgLatencyMs ?? d.avg_latency_ms ?? 0),
    commFailCount: Number(d.commFailCount ?? d.comm_fail_count ?? 0),
    connectedVehicleCount: Number(d.connectedVehicleCount ?? d.connected_vehicle_count ?? 0),
  }
}

const controllerUptimeHours = computed(() => {
  const min = controllerLog.value.uptimeMin
  if (min == null || Number.isNaN(min)) return 0
  return Math.floor(min / 60)
})

const v2xSpatSuccessPercent = computed(() => {
  const send = commLog.value.spatSendCount
  const fail = commLog.value.spatFailCount
  if (!send || send <= 0) return '—'
  const pct = ((send - fail) / send) * 100
  if (Number.isNaN(pct)) return '—'
  return `${pct.toFixed(1)}%`
})

const historyData = computed(() => {
  const totalWidth = 700;
  const sidePadding = 30; 
  const usableWidth = totalWidth - (sidePadding * 2);
  const dataLen = currentHistory.value.length;
  
  return currentHistory.value.map((val, idx) => ({
    x: sidePadding + (usableWidth / (dataLen - 1)) * idx, 
    y: 160 - (val * 1.4), 
    val: val,
    isEdge: idx === 0 || idx === dataLen - 1
  }))
})

const chartPath = computed(() => {
  if (historyData.value.length === 0) return '';
  return historyData.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
})

const updateChartLabels = () => {
  const labels = [];
  const now = new Date();
  for (let i = 6; i >= 1; i--) {
    const time = new Date(now.getTime() - i * 10 * 60 * 1000);
    labels.push(`${time.getHours()}:${time.getMinutes().toString().padStart(2, '0')}`);
  }
  labels.push('현재');
  chartLabels.value = labels;
}

const updateData = async () => {
  updateChartLabels();
  const id = selectedId.value;
  const results = await Promise.allSettled([
    api.get(`/api/predictive/${id}`),
    api.get(`/api/predictive/${id}/history`),
    api.get(`/api/predictive/${id}/controller`),
    api.get(`/api/predictive/${id}/v2x`),
  ]);

  const [paRes, histRes, ctrlRes, vxRes] = results;

  if (paRes.status === 'fulfilled') {
    const pa = paRes.value.data;
    riskData.value = {
      total_risk_score: Math.round(pa.total_risk_score || pa.totalRiskScore || 0),
      controller_risk_score: Math.round(pa.controller_risk_score || pa.controllerRiskScore || 0),
      v2x_risk_score: Math.round(pa.v2x_risk_score || pa.v2xRiskScore || 0),
      risk_level: pa.risk_level || pa.riskLevel || '정상',
      remain_days: pa.remain_days ?? pa.remainDays ?? 15,
      fail_prob: pa.fail_prob ?? pa.failProb ?? 5,
      analysis_comment: pa.analysis_comment || pa.analysisComment || '',
    };
  } else {
    console.warn('예지보전 분석 API 실패:', paRes.reason);
  }

  if (histRes.status === 'fulfilled' && histRes.value.data?.length > 0) {
    const scores = histRes.value.data.map((h) => h.total_risk_score ?? h.score ?? 0);
    currentHistory.value =
      scores.length >= 7 ? [...scores.slice(-7)] : [...Array(7 - scores.length).fill(0), ...scores];
  } else if (histRes.status === 'rejected') {
    console.warn('위험 히스토리 API 실패:', histRes.reason);
  }

  if (ctrlRes.status === 'fulfilled') {
    controllerLog.value = normalizeControllerStatus(ctrlRes.value.data);
  } else {
    console.warn('제어기 상태 API 실패:', ctrlRes.reason);
    controllerLog.value = normalizeControllerStatus(null);
  }

  if (vxRes.status === 'fulfilled') {
    commLog.value = normalizeV2xStatus(vxRes.value.data);
  } else {
    console.warn('V2X 상태 API 실패:', vxRes.reason);
    commLog.value = normalizeV2xStatus(null);
  }
}

onMounted(() => {
  updateData();
  // 실시간성을 위해 3초마다 갱신 (서버 부하가 걱정되면 5000으로 원복)
  dataTimer = setInterval(updateData, 3000); 
})

onUnmounted(() => {
  if (dataTimer) clearInterval(dataTimer);
})
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
.analysis-view { display: flex; flex-direction: column; gap: 13px; padding: 8px 8px 24px; background: #f8f9fb; max-width: 100%; box-sizing: border-box; }
.header { display: flex; justify-content: space-between; align-items: flex-end; padding-bottom: 15px; border-bottom: 2px solid #eee; }
.title-wrap { display: flex; flex-direction: column; gap: 5px; }
.intersection-select { padding: 6px 12px; border-radius: 6px; border: 1px solid #ddd; background: white; cursor: pointer; }
.top-prediction-row { display: grid; grid-template-columns: 2fr 5.15fr 2fr; gap: 10px; }
.risk-mini-card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); text-align: center; border-top: 5px solid #2ecc71; }
.risk-mini-card.위험 { border-top-color: #e74c3c; }
.risk-mini-card.주의 { border-top-color: #f1c40f; }
.risk-mini-card.경고 { border-top-color: #e67e22; }
.risk-mini-card .score { font-size: 32px; font-weight: 800; display: block; }
.risk-mini-card .badge { display: inline-block; padding: 2px 12px; border-radius: 20px; background: #e74c3c; color: white; font-size: 12px; margin-top: 5px; }
.risk-mini-card.정상 .badge { background: #2ecc71; }
.risk-mini-card.주의 .badge { background: #f1c40f; }
.rul-card { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); }
.rul-desc { font-size: 14px; margin-bottom: 15px; color: #444; }
.rul-bar { height: 12px; background: #eee; border-radius: 6px; overflow: hidden; margin-bottom: 5px; }
.rul-fill { height: 100%; transition: width 0.8s ease; }
.rul-labels { display: flex; justify-content: space-between; font-size: 11px; color: #999; }
.mini-sub-card { background: white; padding: 15px; border-radius: 10px; margin-bottom: 10px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); }
.mini-sub-card:last-child { margin-bottom: 0; }
.mini-sub-card span { font-size: 11px; color: #888; display: block; }
.mini-sub-card strong { font-size: 18px; }
.history-chart-card { background: white; padding: 15px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); position: relative; }
.history-chart-card h3 { font-size: 15px; margin-bottom: 10px; color: #444; }
.history-svg { width: 100%; height: 180px; overflow: visible; box-sizing: border-box; }
.tooltip-box { visibility: hidden; opacity: 0; transition: 0.2s; pointer-events: none; z-index: 10; }
.chart-point-group:hover .tooltip-box { visibility: visible; opacity: 1; }
.chart-labels { display: flex; justify-content: space-between; padding: 0 30px; margin-top: 10px; font-size: 11px; color: #bbb; }
.chart-labels .today { color: #4a90e2; font-weight: bold; }
.detail-analysis-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.detail-card { background: white; padding: 15px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); }
.detail-card h3 { font-size: 15px; margin-bottom: 10px; border-left: 4px solid #4a90e2; padding-left: 8px; color: #444; }
.metrics-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.metric-item { background: #f9fbff; padding: 12px; border-radius: 10px; text-align: center; }
.metric-item strong { font-size: 18px; color: #333; }
.analysis-report-box { background: #fff; padding: 20px; border-radius: 12px; border: 1px solid #eee; }
.comment-content { display: flex; gap: 15px; align-items: center; background: #f8fbff; padding: 15px; border-radius: 8px; font-size: 13px; color: #333; }
.dark-theme .analysis-view { background: #535151; }
.dark-theme .risk-mini-card, .dark-theme .rul-card, .dark-theme .mini-sub-card, .dark-theme .history-chart-card, .dark-theme .detail-card, .dark-theme .analysis-report-box { background: #1e1e1e !important; color: #eee !important; border: 1px solid #333 !important; box-shadow: 0 10px 12px rgba(0, 0, 0, 0.4); }
.dark-theme .header { border-bottom: 2px solid #7e7e7e; }
.dark-theme h2, .dark-theme h3, .dark-theme strong, .dark-theme span { color: #ffffff !important; }
</style>