package gift.e2e;

import gift.dto.option.OptionRequestDto;
import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.dto.wish.WishCreateResponseDto;
import gift.dto.wish.WishRequestDto;
import gift.dto.wish.WishResponseDto;
import gift.service.KakaoOAuthService;
import gift.utils.E2ETestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private String token;
    private String fakeCode;

    @Autowired
    private KakaoOAuthService kakaoOAuthService;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        fakeCode = "fake-code";
        token = new E2ETestUtils(kakaoOAuthService).카카오_테스트_계정으로_토큰_발급(fakeCode);
    }

    @Test
    void 위시리스트_추가_및_조회_삭제() {

        // 상품 등록
        List<OptionRequestDto> options = of(
                new OptionRequestDto("테스트 옵션", 100)
        );
        ProductRequestDto productRequest = new ProductRequestDto("아이스 아메리카노", 4500, "ice_americano.jpg", options);

        ProductResponseDto productResponse = restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .retrieve()
                .body(ProductResponseDto.class);

        assertThat(productResponse).isNotNull();

        // 위시 리스트에 상품 추가
        WishRequestDto wishRequest = new WishRequestDto(productResponse.id());

        WishCreateResponseDto wishResponse = restClient.post()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .body(wishRequest)
                .retrieve()
                .body(WishCreateResponseDto.class);

        assertThat(wishResponse.productId()).isEqualTo(productResponse.id());

        // 위시 리스트 조회
        List<WishResponseDto> wishlist = restClient.get()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<WishResponseDto>>() {});

        assertThat(wishlist.get(0).product().name()).isEqualTo("아이스 아메리카노");

        // 위시 리스트에서 제거
        restClient.delete()
                .uri("/api/wishes/" + wishResponse.id())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        // 삭제 후 위시 리스트가 비었는지 확인
        List<WishResponseDto> afterDelete = restClient.get()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<WishResponseDto>>() {});

        assertThat(afterDelete).isEmpty();
    }

    @Test
    void 존재하지_않는_상품으로_위시_추가_시_실패() {
        // given
        WishRequestDto request = new WishRequestDto(999999L);

        // when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/wishes")
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(WishCreateResponseDto.class);
        });

        // then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void 중복된_상품_위시_추가_시_실패() {
        // given
        List<OptionRequestDto> options = of(
                new OptionRequestDto("테스트 옵션", 100)
        );
        ProductRequestDto productRequest = new ProductRequestDto("카페라떼", 4800, "latte.jpg", options);

        ProductResponseDto product = restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .retrieve()
                .body(ProductResponseDto.class);

        WishRequestDto wishRequest = new WishRequestDto(product.id());

        restClient.post()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .body(wishRequest)
                .retrieve()
                .toBodilessEntity();

        // when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/wishes")
                    .header("Authorization", "Bearer " + token)
                    .body(wishRequest)
                    .retrieve()
                    .body(WishCreateResponseDto.class);
        });

        // then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}