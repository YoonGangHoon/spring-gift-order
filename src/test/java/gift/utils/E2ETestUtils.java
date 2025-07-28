package gift.utils;

import gift.service.KakaoOAuthService;
import org.springframework.stereotype.Component;

@Component
public class E2ETestUtils {

    private final KakaoOAuthService kakaoOAuthService;

    public E2ETestUtils(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    public String 카카오_테스트_계정으로_토큰_발급(String fakeCode) {
        return kakaoOAuthService.loginOrRegister(fakeCode);
    }
}