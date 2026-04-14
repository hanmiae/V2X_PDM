<template>
  <div class="recommend-container" :class="{ 'dark-mode': isDarkMode }">
    <div class="header-section">
      <div class="title-group">
        <h2>🛠️ 장비 유지보수 및 교체 권고</h2>
      </div>
    </div>

    <div class="section-divider-top"></div>

    <p v-if="loadError" class="load-error">{{ loadError }}</p>

    <div class="summary-row">
      <div class="stat-card critical">
        <div class="stat-icon">🚨</div>
        <div class="stat-info">
          <span class="label">긴급 교체 권고</span>
          <span class="value">{{ summary.criticalCount }}건</span>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <span class="label">정밀 점검 대상</span>
          <span class="value">{{ summary.warningCount }}건</span>
        </div>
      </div>
      <div class="stat-card stable">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <span class="label">정상 가동 중</span>
          <span class="value">{{ summary.normalCount }}건</span>
        </div>
      </div>
    </div>

    <div class="table-container shadow">
      <div v-if="totalElements > 0" class="table-meta">전체 {{ totalElements }}건</div>
      <p v-if="!recommendList.length && !loading" class="empty-hint">표시할 분석 결과가 없습니다.</p>
      <table v-else class="recommend-table">
        <thead>
          <tr>
            <th>분석 대상 교차로</th>
            <th>장비 정보</th>
            <th>위험 등급</th>
            <th>핵심 이상 지표 (AI 분석)</th>
            <th>💡 현장 조치 권고 사항</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in recommendList"
            :key="item.id"
            :class="{ 'high-risk': item.riskLevel === '위험' }"
          >
            <td class="location-cell">
              <strong>{{ item.intersectionName }}</strong>
              <span class="id-tag">{{ item.intersectionId }}</span>
            </td>
            <td>
              <div class="device-info">
                <span class="device-id">{{ item.deviceId }}</span>
                <span class="device-type-badge">{{ item.deviceType }}</span>
              </div>
            </td>
            <td>
              <span :class="['status-badge', item.riskLevelClass]">
                {{ item.riskLevel }}
              </span>
            </td>
            <td class="comment-cell">
              <div class="analysis-msg">
                <span class="bullet">•</span> {{ item.analysisComment }}
              </div>
              <div class="remain-life" v-if="item.remainDays != null">
                예상 잔여 수명: <strong>{{ Math.round(item.remainDays) }}일</strong>
              </div>
            </td>
            <td class="action-cell">
              <div class="recommendation-box">
                <span class="action-title">권고:</span>
                <span class="action-text">{{ item.actionMessage }}</span>
              </div>
            </td>
            <td class="control-cell">
              <button class="btn-report" @click="openReport(item)">보고서</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="totalElements > 0" class="pagination-bar">
        <button
          type="button"
          class="page-btn"
          :disabled="currentPage <= 0 || loading"
          @click="goPage(currentPage - 1)"
        >
          이전
        </button>
        <span class="page-info">
          {{ currentPage + 1 }} / {{ totalPages || 1 }} 페이지
        </span>
        <button
          type="button"
          class="page-btn"
          :disabled="currentPage >= totalPages - 1 || loading"
          @click="goPage(currentPage + 1)"
        >
          다음
        </button>
      </div>
    </div>

    <div class="workflow-section shadow" style="margin-top: 25px">
      <div class="workflow-header">
        <h4>🔄 통합 지능형 유지보수 워크플로우</h4>
        <span class="step-badge">AI 자동화 프로세스</span>
      </div>
      <div class="workflow-steps">
        <div class="step-item">
          <div class="step-icon">📡</div>
          <div class="step-content">
            <span class="step-num">STEP 01</span>
            <h6>실시간 데이터 수집</h6>
            <p>LCT/V2X 장비 로그 수집 (온도, 지연시간, 패킷)</p>
          </div>
        </div>
        <div class="arrow">➡️</div>
        <div class="step-item active">
          <div class="step-icon">🧠</div>
          <div class="step-content">
            <span class="step-num">STEP 02</span>
            <h6>AI 위험 분석</h6>
            <p>위험 점수(Risk Score) 산출 및 RUL 잔여수명 예측</p>
          </div>
        </div>
        <div class="arrow">➡️</div>
        <div class="step-item">
          <div class="step-icon">💡</div>
          <div class="step-content">
            <span class="step-num">STEP 03</span>
            <h6>조치 권고 생성</h6>
            <p>현장 상황별 최적의 유지보수 가이드 자동 바인딩</p>
          </div>
        </div>
        <div class="arrow">➡️</div>
        <div class="step-item highlight">
          <div class="step-icon">🔧</div>
          <div class="step-content">
            <span class="step-num">STEP 04</span>
            <h6>현장 조치 및 피드백</h6>
            <p>유지보수 팀 출동 및 결과 시스템 업데이트</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue'
import * as recApi from '@/api/recommendApi'

const isDarkMode = inject('isDarkMode', ref(false))
const loadError = ref('')
const loading = ref(true)
const summary = ref({
  criticalCount: 0,
  warningCount: 0,
  normalCount: 0,
})
const PAGE_SIZE = 5

const recommendList = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

async function loadSummary() {
  summary.value = await recApi.getRecommendSummary()
}

async function loadPage() {
  const data = await recApi.getRecommendList(currentPage.value, PAGE_SIZE)
  recommendList.value = data.content ?? []
  totalPages.value = data.totalPages ?? 0
  totalElements.value = data.totalElements ?? 0
  if (typeof data.number === 'number') {
    currentPage.value = data.number
  }
  const empty = (recommendList.value?.length ?? 0) === 0
  const tp = totalPages.value
  if (empty && totalElements.value > 0 && tp > 0 && currentPage.value !== tp - 1) {
    currentPage.value = tp - 1
    await loadPage()
  }
}

async function loadAll() {
  loadError.value = ''
  loading.value = true
  try {
    await Promise.all([loadSummary(), loadPage()])
  } catch (e) {
    loadError.value = e instanceof Error ? e.message : '데이터를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

async function loadListOnly() {
  loadError.value = ''
  loading.value = true
  try {
    await loadPage()
  } catch (e) {
    loadError.value = e instanceof Error ? e.message : '목록을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

async function goPage(p) {
  if (p < 0 || (totalPages.value > 0 && p >= totalPages.value)) return
  currentPage.value = p
  await loadListOnly()
}

onMounted(loadAll)

async function openReport(item) {
  try {
    await recApi.downloadRecommendReport(item.id)
  } catch (e) {
    alert(e instanceof Error ? e.message : '보고서를 내려받지 못했습니다.')
  }
}
</script>

<style scoped>
.load-error {
  color: #c0392b;
  padding: 8px;
  background: #fdecea;
  border-radius: 8px;
  margin-bottom: 10px;
  font-size: 14px;
}
.empty-hint {
  padding: 24px;
  color: #888;
  text-align: center;
}
.recommend-container {
  padding: 10px 10px 24px;
  background: #f4f7fa;
  max-width: 100%;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}
.header-section h2 {
  font-size: 22px;
  color: #2c3e50;
  margin: 0;
}

.section-divider-top {
  border-top: 2px solid #eee;
  margin-bottom: 15px;
}

.summary-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 20px;
}
.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 12px;
}

.label {
  font-size: 14px;
  color: #7f8c8d;
}
.value {
  font-size: 26px;
  font-weight: 800;
  color: #2c3e50;
  display: block;
}

.workflow-section {
  background: white;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 25px;
}
.workflow-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f3f5;
}
.workflow-header h4 {
  margin: 0;
  color: #34495e;
  font-size: 16px;
}
.step-badge {
  font-size: 11px;
  background: #e7f5ff;
  color: #228be6;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
}
.workflow-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
}
.step-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}
.step-icon {
  font-size: 24px;
  width: 50px;
  height: 50px;
  background: #f8f9fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  border: 2px solid transparent;
}
.step-item.active .step-icon {
  background: #e7f5ff;
  border-color: #339af0;
}
.step-item.highlight .step-icon {
  background: #fff5f5;
  border-color: #fa5252;
}
.step-num {
  font-size: 10px;
  color: #adb5bd;
  font-weight: 800;
}
.step-content h6 {
  margin: 4px 0;
  font-size: 14px;
  color: #2c3e50;
}
.step-content p {
  font-size: 11px;
  color: #868e96;
  margin: 0;
  line-height: 1.4;
}
.arrow {
  color: #dee2e6;
  font-size: 20px;
  margin-bottom: 30px;
}

.table-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
}
.table-meta {
  padding: 12px 18px;
  border-bottom: 1px solid #f1f3f5;
  background: #fcfcfd;
  font-size: 13px;
  color: #868e96;
  text-align: right;
}
.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 16px;
  border-top: 1px solid #f1f3f5;
  background: #fcfcfd;
}
.page-btn {
  padding: 8px 18px;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}
.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
}
.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.page-info {
  font-size: 13px;
  color: #495057;
  min-width: 120px;
  text-align: center;
}
.recommend-table {
  width: 100%;
  border-collapse: collapse;
}
.recommend-table th {
  background: #fcfcfd;
  padding: 18px;
  font-size: 13px;
  font-weight: 600;
  color: #34495e;
  border-bottom: 2px solid #f1f3f5;
  text-align: left;
}
.recommend-table td {
  padding: 20px 18px;
  border-bottom: 1px solid #f1f3f5;
  vertical-align: middle;
}

.location-cell strong {
  font-size: 15px;
  display: block;
}
.id-tag {
  font-size: 11px;
  color: #adb5bd;
}
.device-type-badge {
  font-size: 11px;
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 4px;
  color: #495057;
  margin-left: 8px;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
}
.status-badge.danger {
  background: #fff5f5;
  color: #fa5252;
  border: 1px solid #ffc9c9;
}
.status-badge.warning {
  background: #fff9db;
  color: #f08c00;
  border: 1px solid #ffec99;
}

.analysis-msg {
  font-weight: 500;
  color: #34495e;
  margin-bottom: 4px;
}
.remain-life {
  font-size: 12px;
  color: #868e96;
}
.remain-life strong {
  color: #e74c3c;
}

.recommendation-box {
  background: #f8fbff;
  padding: 10px 14px;
  border-radius: 8px;
  border-left: 4px solid #339af0;
  font-size: 13px;
}
.action-title {
  font-weight: 800;
  color: #339af0;
  margin-right: 6px;
}
.action-text {
  color: #495057;
}

.btn-report {
  padding: 8px 16px;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}
.btn-report:hover {
  background: #f8f9fa;
  border-color: #ced4da;
}

.dark-mode {
  background: #535151 !important;
}

.dark-mode h2,
.dark-mode .value,
.dark-mode .workflow-header h4,
.dark-mode .step-content h6,
.dark-mode .recommend-table th,
.dark-mode .location-cell strong,
.dark-mode .analysis-msg {
  color: #ffffff !important;
}

.dark-mode .stat-card,
.dark-mode .table-container,
.dark-mode .workflow-section {
  background: #1e1e1e !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4) !important;
}

.dark-mode .label,
.dark-mode .step-content p,
.dark-mode .id-tag,
.dark-mode .remain-life,
.dark-mode .recommend-table td {
  color: #d1d1d1 !important;
}

.dark-mode .workflow-header {
  border-bottom-color: #333;
}

.dark-mode .step-icon {
  background: #2a2a2a;
}
.dark-mode .recommend-table th {
  background: #252525;
  border-bottom-color: #333;
}
.dark-mode .recommend-table td {
  border-bottom-color: #2a2a2a;
}
.dark-mode .recommendation-box {
  background: #1a222d;
  border-left-color: #1c7ed6;
}
.dark-mode .action-text {
  color: #f0f0f0 !important;
}
.dark-mode .device-type-badge {
  background: #333;
  color: #d1d1d1;
}
.dark-mode .btn-report {
  background: #2a2a2a;
  border-color: #444;
  color: #eee;
}
.dark-mode .table-meta,
.dark-mode .pagination-bar {
  background: #252525;
  border-color: #333;
}
.dark-mode .table-meta,
.dark-mode .page-info {
  color: #adb5bd !important;
}
.dark-mode .page-btn {
  background: #2a2a2a;
  border-color: #444;
  color: #eee;
}
</style>
