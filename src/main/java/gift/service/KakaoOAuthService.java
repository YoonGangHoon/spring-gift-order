package gift.service;

import gift.dto.KakaoTokenResponseDto;
import gift.dto.KakaoUserInfoResponseDto;
import gift.entity.KakaoMember;
import gift.jwt.JwtProvider;
import gift.repository.KakaoMemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOAuthService {

    private final RestTemplate restTemplate;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final JwtProvider jwtProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public KakaoOAuthService(
            RestTemplateBuilder builder,
            KakaoMemberRepository kakaoMemberRepository,
            JwtProvider jwtProvider
    ) {
        this.restTemplate = builder.build();
        this.kakaoMemberRepository = kakaoMemberRepository;
        this.jwtProvider = jwtProvider;
    }

    public String loginOrRegister(String code) {
        KakaoTokenResponseDto token = getAccessToken(code);
        KakaoUserInfoResponseDto user = getUserInfo(token.accessToken());
        KakaoMember member = kakaoMemberRepository.findByKakaoId(user.id())
                .orElseGet(() -> kakaoMemberRepository.save(
                        new KakaoMember(
                                user.id(),
                                token.accessToken(),
                                token.refreshToken(),
                                token.refreshTokenExpiresIn()
                        )
                )
        );

        return jwtProvider.generateToken(member);
    }

    private KakaoTokenResponseDto getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(tokenUrl, request, KakaoTokenResponseDto.class);
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, KakaoUserInfoResponseDto.class).getBody();
    }
}
