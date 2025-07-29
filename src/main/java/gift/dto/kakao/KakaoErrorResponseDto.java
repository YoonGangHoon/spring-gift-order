package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoErrorResponseDto(
        @JsonProperty("code") Integer code,
        @JsonProperty("msg") String msg) {
}