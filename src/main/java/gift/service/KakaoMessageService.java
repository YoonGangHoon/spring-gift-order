package gift.service;

import gift.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api-url}")
    private String kakaoApiUrl;

    @Value("${kakao.redirect-message-web-url}")
    private String webUrl;

    @Value("${kakao.redirect-message-mobile-url}")
    private String mobileUrl;

    public KakaoMessageService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void sendOrderMessage(String kakaoAccessToken, Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String messageJson = buildMessage(order);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", messageJson);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(kakaoApiUrl+"/v2/api/talk/memo/default/send", request, String.class);
    }

    private String buildMessage(Order order) {
        return """
        {
            "object_type": "text",
            "text": "주문이 완료되었습니다❗️\\n상품명: %s\\n옵션명: %s\\n주문 수량: %d\\n요청사항: %s",
            "link": {
                "web_url": "%s",
                "mobile_web_url": "%s"
            },
            "button_title": "주문 내역 확인하기"
        }
        """.formatted(
                order.getOption().getProduct().getName(),
                order.getOption().getName(),
                order.getQuantity(),
                order.getMessage(),
                webUrl,
                mobileUrl
        );
    }
}
