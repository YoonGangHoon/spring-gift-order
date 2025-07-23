package gift;

import gift.dto.KakaoTokenResponseDto;
import gift.service.KakaoOAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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
}
