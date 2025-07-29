package gift.dto.order;

public record OrderRequestDto(
        Long optionId,
        Integer quantity,
        String message) {
}
