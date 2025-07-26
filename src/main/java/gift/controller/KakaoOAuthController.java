package gift.controller;

import gift.dto.KakaoTokenResponseDto;
import gift.service.KakaoOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;
    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService) {
        this.kakaoOAuthService = kakaoOAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<KakaoTokenResponseDto> callback(@RequestParam("code") String code) {
        KakaoTokenResponseDto accessToken = kakaoOAuthService.getAccessToken(code);
        return ResponseEntity.ok(accessToken);
    }
}