package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakao.KakaoUserInfoResponseDto;
import gift.entity.Member;
import gift.exception.kakao.KakaoApiErrorHandler;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(KakaoOAuthService.class)
@Import(KakaoOAuthApiTest.TestConfig.class)
class KakaoOAuthApiTest {

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @Autowired
    private MockRestServiceServer server;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    void userInfo_정상_응답() {
        given(memberRepository.findByKakaoId(1234567890L))
                .willReturn(Optional.of(new Member(1234567890L, "닉네임", "a", "b", 3600)));

        String userResponse = """
            {
              "id": 1234567890,
              "kakao_account": {
                "profile": {
                  "nickname": "테스트유저"
                }
              }
            }
        """;

        server.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andRespond(withSuccess(userResponse, MediaType.APPLICATION_JSON));

        KakaoUserInfoResponseDto dto = kakaoOAuthService.getUserInfo("mock-access-token");

        assertThat(dto.id()).isEqualTo(1234567890L);
        assertThat(dto.kakao_account().profile().nickname()).isEqualTo("테스트유저");
    }

    @Test
    void userInfo_에러_응답() {
        String errorJson = """
            {
              "code": -402,
              "msg": "insufficient scopes."
            }
        """;

        server.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        var ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            kakaoOAuthService.getUserInfo("invalid-token");
        });

        assertThat(ex.getMessage()).contains("-402");
    }

    static class TestConfig {
        @Bean
        public KakaoApiErrorHandler kakaoApiErrorHandler(ObjectMapper objectMapper) {
            return new KakaoApiErrorHandler(objectMapper);
        }

        @Bean
        public RestTemplate kakaoApiRestTemplate(KakaoApiErrorHandler errorHandler) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(errorHandler);
            return restTemplate;
        }
    }
}
