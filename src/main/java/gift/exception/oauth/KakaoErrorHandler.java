package gift.exception.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoErrorResponseDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class KakaoErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public KakaoErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            KakaoErrorResponseDto error = objectMapper
                    .readerFor(KakaoErrorResponseDto.class)
                    .readValue(responseBody);

            throw new OAuthException("Kakao API 에러 - code: " + error.code() + ", msg: " + error.msg());
        } catch (Exception e) {
            throw new OAuthException("Kakao API 응답 파싱 실패: " + responseBody, e);
        }
    }
}