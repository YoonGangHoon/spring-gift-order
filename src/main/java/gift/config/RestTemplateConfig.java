package gift.config;

import gift.exception.oauth.KakaoErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestTemplateConfig {

    private final KakaoErrorHandler errorHandler;

    public RestTemplateConfig(KakaoErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory)
                .errorHandler(errorHandler);
    }
}
