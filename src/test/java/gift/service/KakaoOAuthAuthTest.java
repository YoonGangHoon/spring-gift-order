package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakao.KakaoTokenResponseDto;
import gift.exception.oauth.KakaoAuthErrorHandler;
import gift.jwt.JwtProvider;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoOAuthService.class)
@Import(KakaoOAuthAuthTest.TestConfig.class)
class KakaoOAuthAuthTest {

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @Autowired
    private MockRestServiceServer server;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void accessToken_정상_응답() {
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
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        KakaoTokenResponseDto result = kakaoOAuthService.getAccessToken("code");

        assertThat(result.accessToken()).isEqualTo("test-access-token");
    }

    @Test
    void accessToken_에러_응답() {
        String errorJson = """
            {
              "error": "invalid_grant",
              "error_description": "Invalid authorization code"
            }
        """;

        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        var ex = assertThrows(RuntimeException.class, () -> {
            kakaoOAuthService.getAccessToken("invalid-code");
        });

        assertThat(ex.getMessage()).contains("invalid_grant");
    }

    static class TestConfig {
        @Bean
        public KakaoAuthErrorHandler kakaoAuthErrorHandler(ObjectMapper objectMapper) {
            return new KakaoAuthErrorHandler(objectMapper);
        }

        @Bean
        public RestTemplate kakaoAuthRestTemplate(KakaoAuthErrorHandler errorHandler) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(errorHandler);
            return restTemplate;
        }
    }
}
