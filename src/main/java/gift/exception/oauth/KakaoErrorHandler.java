package gift.exception.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

public class KakaoErrorHandler {

    public static void handle(HttpClientErrorException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(body);

            int errorCode = json.path("code").asInt();
            String errorMsg = json.path("msg").asText();

            switch (errorCode) {
                case -1 -> throw new OAuthException("카카오 서버 내부 오류. 잠시 후 다시 시도해주세요.");
                case -2 -> throw new OAuthException("요청 파라미터가 올바르지 않습니다.");
                case -3 -> throw new OAuthException("API 사용 설정이 비활성화되어 있습니다. 개발자 사이트에서 설정을 확인하세요.");
                case -4 -> throw new OAuthException("계정이 제재 중입니다.");
                case -5 -> throw new OAuthException("요청에 대한 권한이 없습니다. 권한 설정을 확인하세요.");
                case -6 -> throw new OAuthException("허용되지 않은 동작입니다. API 문서를 참고하세요.");
                case -7 -> throw new OAuthException("서비스 점검 중이거나 일시적인 내부 문제입니다.");
                case -8 -> throw new OAuthException("올바르지 않은 요청 헤더입니다.");
                case -9 -> throw new OAuthException("서비스가 종료된 API입니다.");
                case -10 -> throw new OAuthException("쿼터를 초과했습니다. 호출을 제한하거나 제휴 문의 바랍니다.");
                case -101 -> throw new OAuthException("카카오 계정 연결이 필요합니다.");
                case -102 -> throw new OAuthException("이미 연결된 사용자입니다.");
                case -103 -> throw new OAuthException("존재하지 않거나 휴면 상태인 계정입니다.");
                case -201 -> throw new OAuthException("앱에 등록되지 않은 사용자 프로퍼티를 요청했습니다.");
                case -402 -> throw new OAuthException("동의하지 않은 항목입니다. 추가 동의가 필요합니다.");
                case -406 -> throw new OAuthException("14세 미만 사용자는 이 앱을 이용할 수 없습니다.");
                case -401 -> throw new OAuthException("유효하지 않은 앱 키 또는 액세스 토큰입니다.");
                case -501 -> throw new OAuthException("카카오톡 미가입 또는 유예 사용자입니다.");
                case -602 -> throw new OAuthException("이미지 용량 초과입니다.");
                case -603 -> throw new OAuthException("카카오 내부 처리 중 타임아웃 발생. 다시 시도해주세요.");
                case -606 -> throw new OAuthException("업로드 가능한 이미지 개수를 초과했습니다.");
                case -903 -> throw new OAuthException("등록되지 않은 앱 키입니다.");
                case -911 -> throw new OAuthException("지원하지 않는 이미지 포맷입니다.");
                case -9798 -> throw new OAuthException("카카오 서비스 점검 중입니다.");
                default -> throw new OAuthException("알 수 없는 카카오 API 오류: " + errorMsg);
            }

        } catch (IOException e) {
            throw new OAuthException("카카오 오류 응답 파싱 실패", e);
        }
    }
}