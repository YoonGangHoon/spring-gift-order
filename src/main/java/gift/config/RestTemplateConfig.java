package gift.config;

import gift.exception.oauth.KakaoApiErrorHandler;
import gift.exception.oauth.KakaoAuthErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate kakaoAuthRestTemplate(KakaoAuthErrorHandler errorHandler) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(errorHandler);
        return restTemplate;
    }

    @Bean
    public RestTemplate kakaoApiRestTemplate(KakaoApiErrorHandler errorHandler) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(errorHandler);
        return restTemplate;
    }
}
