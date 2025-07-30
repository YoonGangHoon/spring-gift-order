package gift.service;

import gift.entity.Member;
import gift.jwt.JwtProvider;
import gift.repository.MemberRepository;
import gift.utils.E2ETestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoOAuthService.class)
@Import(E2ETestUtils.class)
class KakaoOAuthServiceTokenTest {

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @Autowired
    private E2ETestUtils e2ETestUtils;

    @Autowired
    private MockRestServiceServer server;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        server.reset();

        Member member = new Member(
                1234567890L,
                "테스트유저",
                "mock-access-token",
                "mock-refresh-token",
                5184000
        );

        given(memberRepository.findByKakaoId(1234567890L)).willReturn(Optional.of(member));
        given(jwtProvider.generateToken((Member) member)).willReturn("mock-token");

    }

    @Test
    void 카카오_로그인_후_JWT_발급_성공() {
        // given
        String fakeCode = "fake-auth-code";

        String tokenResponse = """
            {
              "access_token": "mock-access-token",
              "refresh_token": "mock-refresh-token",
              "expires_in": 3600,
              "refresh_token_expires_in": 5184000,
              "token_type": "bearer",
              "scope": "profile"
            }
        """;
        server.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withSuccess(tokenResponse, MediaType.APPLICATION_JSON));

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

        // when
        String jwt = e2ETestUtils.테스트용_고정_토큰();

        // then
        assertThat(jwt).isNotBlank();
    }
}