package gift;

import gift.dto.KakaoTokenResponseDto;
import gift.exception.oauth.OAuthException;
import gift.service.KakaoOAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoOAuthService.class)
class KakaoOAuthServiceTest {

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void 파싱_성공() {
        // given
        String json = """
        {
            "access_token": "test-access-token",
            "token_type": "bearer",
            "expires_in": 21599,
            "refresh_token": "test-refresh-token",
            "refresh_token_expires_in": 5183999,
            "scope": "account_email profile"
        }
        """;

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        // when
        KakaoTokenResponseDto result = kakaoOAuthService.getAccessToken("test-code");

        // then
        assertThat(result.accessToken()).isEqualTo("test-access-token");
    }

    @Test
    void 카카오_에러_응답시_예외_발생() {
        // given
        String code = "invalid-code";

        String errorResponse = """
            {
              "msg": "insufficient scopes.",
              "code": -402,
              "api_type": "TALK_MEMO_DEFAULT_SEND",
              "required_scopes": [
                "talk_message"
              ],
              "allowed_scopes": [
                "profile",
                "account_email"
              ]
            }
        """;

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // when & then
        OAuthException exception = assertThrows(OAuthException.class, () -> {
            kakaoOAuthService.getAccessToken(code);
        });

        assertTrue(exception.getMessage().contains("동의하지 않은 항목입니다. 추가 동의가 필요합니다."));
    }
}
