package gift.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        Long optionId,
        Integer quantity,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime orderDateTime,
        String message) {
}
