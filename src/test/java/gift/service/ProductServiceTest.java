package gift.service;

import gift.dto.option.OptionRequestDto;
import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.entity.Product;
import gift.exception.ProductNotExistException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionRepository optionRepository;

    @Test
    void 상품_생성_성공() {
        // given
        ProductRequestDto request = new ProductRequestDto(
                "아이스 아메리카노", 4500, "ice.jpg",
                List.of(new OptionRequestDto("Large", 10))
        );
        Product savedProduct = new Product("아이스 아메리카노", 4500, "ice.jpg");
        ReflectionTestUtils.setField(savedProduct, "id", 1L);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        ProductResponseDto response = productService.create(request);

        // then
        assertThat(response.name()).isEqualTo("아이스 아메리카노");
        verify(productRepository).save(any(Product.class));
        verify(optionRepository).saveAll(anyList());
    }

    @Test
    void 상품_조회_실패_예외발생() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // expect
        assertThrows(ProductNotExistException.class, () -> productService.find(1L));
    }

    @Test
    void 상품_삭제_성공_옵션_동시_삭제() {
        // given
        Product product = new Product("콜드브루", 5000, "coldbrew.jpg");
        ReflectionTestUtils.setField(product, "id", 1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        productService.delete(1L);

        // then
        verify(optionRepository).deleteAllByProduct(product);
        verify(productRepository).delete(product);
    }
}
