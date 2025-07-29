package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoTokenResponseDto;
import gift.exception.oauth.KakaoErrorHandler;
import gift.exception.oauth.OAuthException;
import gift.jwt.JwtProvider;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
@Import(KakaoOAuthServiceTest.TestConfig.class)
class KakaoOAuthServiceTest {

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public KakaoErrorHandler kakaoErrorHandler(ObjectMapper objectMapper) {
            return new KakaoErrorHandler(objectMapper);
        }

        @Bean
        public RestTemplateCustomizer errorHandlerCustomizer(KakaoErrorHandler errorHandler) {
            return restTemplate -> restTemplate.setErrorHandler(errorHandler);
        }
    }

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

        assertTrue(exception.getMessage().contains("-402"));
    }
}