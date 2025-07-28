package gift.controller;

import gift.dto.KakaoMemberLoginResponseDto;
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
    public ResponseEntity<KakaoMemberLoginResponseDto> callback(@RequestParam("code") String code) {
        String jwt = kakaoOAuthService.loginOrRegister(code);
        return ResponseEntity.ok(new KakaoMemberLoginResponseDto(jwt));
    }
}