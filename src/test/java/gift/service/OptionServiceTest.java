package gift.service;

import gift.dto.option.OptionRequestDto;
import gift.dto.option.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.DuplicateOptionNameException;
import gift.exception.LastOptionException;
import gift.exception.OptionNotExistException;
import gift.exception.ProductNotExistException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @InjectMocks
    private OptionService optionService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void 옵션_생성_성공() {
        Long productId = 1L;
        OptionRequestDto request = new OptionRequestDto("Large", 10);
        Product productProxy = mock(Product.class);

        when(optionRepository.existsByProductIdAndName(productId, request.name())).thenReturn(false);
        when(entityManager.getReference(Product.class, productId)).thenReturn(productProxy);

        Option savedOption = new Option(request.name(), request.quantity(), productProxy);
        ReflectionTestUtils.setField(savedOption, "id", 100L);

        when(optionRepository.save(any(Option.class))).thenReturn(savedOption);

        OptionResponseDto response = optionService.create(productId, request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Large");
        verify(optionRepository).save(any(Option.class));
    }

    @Test
    void 옵션_생성_실패_중복_예외() {
        when(optionRepository.existsByProductIdAndName(anyLong(), anyString())).thenReturn(true);

        assertThrows(DuplicateOptionNameException.class,
                () -> optionService.create(1L, new OptionRequestDto("Large", 10)));
    }

    @Test
    void 옵션_목록_조회_성공() {
        Long productId = 1L;
        Product product = new Product("Product", 1000, "img");
        when(productRepository.existsById(productId)).thenReturn(true);

        Option option1 = new Option("Small", 5, product);
        ReflectionTestUtils.setField(option1, "id", 1L);
        Option option2 = new Option("Large", 10, product);
        ReflectionTestUtils.setField(option2, "id", 2L);

        when(optionRepository.findAllByProductId(productId)).thenReturn(List.of(option1, option2));

        List<OptionResponseDto> options = optionService.find(productId);

        assertThat(options).hasSize(2);
        assertThat(options.get(0).name()).isEqualTo("Small");
        assertThat(options.get(1).quantity()).isEqualTo(10);
    }

    @Test
    void 옵션_목록_조회_실패_상품없음() {
        when(productRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ProductNotExistException.class, () -> optionService.find(1L));
    }

    @Test
    void 옵션_수정_성공() {
        Long productId = 1L;
        Long optionId = 10L;
        OptionRequestDto request = new OptionRequestDto("Medium", 15);

        Product product = new Product("Product", 1000, "img");
        Option option = new Option("Small", 10, product);
        ReflectionTestUtils.setField(product, "id", productId);
        ReflectionTestUtils.setField(option, "id", optionId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.existsByProductAndName(product, request.name())).thenReturn(false);

        OptionResponseDto response = optionService.update(productId, optionId, request);

        assertThat(response.name()).isEqualTo("Medium");
        assertThat(response.quantity()).isEqualTo(15);
        verify(optionRepository).save(any(Option.class));
    }

    @Test
    void 옵션_수정_실패_상품없음() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotExistException.class,
                () -> optionService.update(1L, 1L, new OptionRequestDto("Medium", 10)));
    }

    @Test
    void 옵션_수정_실패_옵션없음() {
        Product product = new Product("Product", 1000, "img");
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(optionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OptionNotExistException.class,
                () -> optionService.update(1L, 1L, new OptionRequestDto("Medium", 10)));
    }

    @Test
    void 옵션_수정_실패_중복_이름() {
        Long productId = 1L;
        Long optionId = 10L;

        Product product = new Product("Product", 1000, "img");
        Option option = new Option("Small", 10, product);
        ReflectionTestUtils.setField(product, "id", productId);
        ReflectionTestUtils.setField(option, "id", optionId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.existsByProductAndName(product, "Medium")).thenReturn(true);

        OptionRequestDto request = new OptionRequestDto("Medium", 15);

        assertThrows(DuplicateOptionNameException.class,
                () -> optionService.update(productId, optionId, request));
    }

    @Test
    void 옵션_삭제_성공() {
        Long productId = 1L;
        Long optionId = 10L;

        Product productProxy = mock(Product.class);
        Option option = new Option("OptionName", 10, productProxy);
        ReflectionTestUtils.setField(option, "id", optionId);

        when(optionRepository.findByIdAndProductId(optionId, productId)).thenReturn(Optional.of(option));
        when(optionRepository.countByProduct(productProxy)).thenReturn(2);
        when(entityManager.getReference(Product.class, productId)).thenReturn(productProxy);

        optionService.delete(productId, optionId);

        verify(optionRepository).delete(option);
    }

    @Test
    void 옵션_삭제_실패_마지막옵션() {
        Long productId = 1L;
        Long optionId = 10L;

        Product productProxy = mock(Product.class);
        Option option = new Option("OptionName", 10, productProxy);
        ReflectionTestUtils.setField(option, "id", optionId);

        when(optionRepository.findByIdAndProductId(optionId, productId)).thenReturn(Optional.of(option));
        when(optionRepository.countByProduct(productProxy)).thenReturn(1);
        when(entityManager.getReference(Product.class, productId)).thenReturn(productProxy);

        assertThrows(LastOptionException.class, () -> optionService.delete(productId, optionId));
    }

    @Test
    void 수량_감소_성공() {
        Long optionId = 10L;
        Product product = new Product("Product", 1000, "img");
        Option option = spy(new Option("OptionName", 20, product));
        ReflectionTestUtils.setField(option, "id", optionId);

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        optionService.reduceOptionQuantity(optionId, 5);

        verify(option).decreaseQuantity(5);
    }

    @Test
    void 수량_감소_실패_옵션없음() {
        when(optionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OptionNotExistException.class, () -> optionService.reduceOptionQuantity(1L, 5));
    }
}