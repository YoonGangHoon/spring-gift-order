package gift.e2e;

import gift.dto.option.OptionRequestDto;
import gift.dto.product.ProductRequestDto;
import gift.dto.product.ProductResponseDto;
import gift.entity.Member;
import gift.jwt.JwtProvider;
import gift.repository.MemberRepository;
import gift.service.KakaoOAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private String token;
    private ProductResponseDto product;

    @MockitoBean
    private KakaoOAuthService kakaoOAuthService;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        // 1. 임의의 토큰과 사용자 정의
        token = "mock-jwt-token";
        Long kakaoId = 1234567890L;
        Member mockMember = new Member(
                kakaoId,
                "테스트유저",
                "mock-access-token",
                "mock-refresh-token",
                5184000
        );

        // 2. OAuthService는 token 발급 대신 mock JWT 반환
        given(jwtProvider.generateToken(any(Member.class))).willReturn("mock-jwt-token");

        // 3. DB 및 토큰 발급 Mock 설정
        given(memberRepository.findByKakaoId(kakaoId)).willReturn(Optional.of(mockMember));
        given(jwtProvider.generateToken(mockMember)).willReturn(token);

        // 4. 상품 등록 (테스트 준비용)
        List<OptionRequestDto> options = List.of(new OptionRequestDto("테스트 옵션", 100));
        ProductRequestDto request = new ProductRequestDto("테스트 상품", 5000, "test.jpg", options);

        restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        ProductResponseDto[] products = restClient.get()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ProductResponseDto[].class);

        Assertions.assertNotNull(products);
        product = products[products.length - 1];
    }

    @Test
    void 상품을_등록하고_조회() {
        List<OptionRequestDto> options = List.of(new OptionRequestDto("테스트 옵션", 100));
        ProductRequestDto request = new ProductRequestDto("녹차", 3500, "green_tea.jpg", options);

        restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        ProductResponseDto[] response = restClient.get()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ProductResponseDto[].class);

        assertThat(response).isNotNull();
        ProductResponseDto product = response[response.length - 1];
        assertThat(product.name()).isEqualTo("녹차");
        assertThat(product.price()).isEqualTo(3500);
        assertThat(product.imageUrl()).isEqualTo("green_tea.jpg");
    }

    @Test
    void 상품을_수정하고_조회() {
        List<OptionRequestDto> options = List.of(new OptionRequestDto("테스트 옵션", 100));
        ProductRequestDto request = new ProductRequestDto("아이스 카페라떼", 7000, "ice_cafe_latte.jpg", options);

        restClient.put()
                .uri("/api/products/" + product.id())
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        ProductResponseDto response = restClient.get()
                .uri("/api/products/" + product.id())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ProductResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("아이스 카페라떼");
        assertThat(response.price()).isEqualTo(7000);
    }

    @Test
    void 상품을_삭제한다() {
        restClient.delete()
                .uri("/api/products/" + product.id())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.get()
                    .uri("/api/products/" + product.id())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void 상품_등록_유효성_검사_실패() {
        List<OptionRequestDto> options = List.of(new OptionRequestDto("테스트 옵션", 100));
        ProductRequestDto invalidRequest = new ProductRequestDto("@카카오@", 10, "kakao.jpg", options);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/products")
                    .header("Authorization", "Bearer " + token)
                    .body(invalidRequest)
                    .retrieve()
                    .toBodilessEntity();
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        String responseBody = exception.getResponseBodyAsString();
        assertThat(responseBody).contains("특수문자");
        assertThat(responseBody).contains("카카오");
        assertThat(responseBody).contains("100원");
    }
}