package gift.dto.wish;

import gift.dto.product.ProductResponseDto;

public record WishResponseDto(
        Long id, ProductResponseDto product) {
}
