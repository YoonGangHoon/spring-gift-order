package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAuthErrorResponseDto(
        @JsonProperty("error") String error,
        @JsonProperty("error_description") String errorDescription) {
}
