package v2x_predictive_maintenance.v2x.service.Dashboard;

/**
 * 통합 모니터링 KPI에서 사용하는 위험 등급 문자열 규칙.
 * <ul>
 *   <li><b>정상</b>: DB·시뮬레이터가 저장하는 {@code 정상}만 정상으로 간주 (앞뒤 공백·NBSP 정규화 후 비교).</li>
 *   <li><b>이상</b>: {@code 주의}, {@code 경고}, {@code 위험} 및 그 외 비어 있지 않은 값(오타 등)은 이상으로 집계해 누락을 줄임.</li>
 *   <li>분석 이력이 없는 교차로는 <b>정상 작동</b>으로 본다(등록만 되어 수집 전인 경우).</li>
 * </ul>
 */
public final class MonitoringRiskLevel {

    public static final String LABEL_NORMAL = "정상";

    private MonitoringRiskLevel() {}

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().replace('\u00A0', ' ');
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    /** 최신 분석이 있고, 그 등급이 정상일 때만 true */
    public static boolean isNormalLatest(String normalizedLevel) {
        return LABEL_NORMAL.equals(normalizedLevel);
    }
}
