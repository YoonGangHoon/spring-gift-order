package gift.service;

import gift.dto.KakaoTokenResponseDto;
import gift.exception.oauth.KakaoErrorHandler;
import gift.exception.oauth.OAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOAuthService {

    private final RestTemplate restTemplate;
    public KakaoOAuthService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public KakaoTokenResponseDto getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    KakaoTokenResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            KakaoErrorHandler.handle(ex);
            throw new OAuthException("카카오 인증 실패: " + ex.getMessage());
        }
    }
}
