package gift.dto.product;

public record ProductResponseDto(
        Long id, String name, Integer price, String imageUrl) {
}
