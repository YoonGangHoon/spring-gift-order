package gift.service;

import gift.dto.WishCreateResponseDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.MemberNotFoundException;
import gift.exception.ProductNotExistException;
import gift.exception.WishAlreadyExistException;
import gift.exception.WishNotExistException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {

    @InjectMocks
    private WishService wishService;

    @Mock
    private WishRepository wishRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Pageable pageable;

    @Test
    void 위시리스트_조회_성공() {
        Long memberId = 1L;
        Product product1 = new Product("아이스 아메리카노", 4500, "ice1.jpg");
        Product product2 = new Product("아이스 카페라떼", 5000, "ice2.jpg");
        ReflectionTestUtils.setField(product1, "id", 100L);
        ReflectionTestUtils.setField(product2, "id", 200L);

        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);
        Wish wish1 = new Wish(member, product1);
        Wish wish2 = new Wish(member, product2);
        ReflectionTestUtils.setField(wish1, "id", 10L);
        ReflectionTestUtils.setField(wish2, "id", 20L);

        when(wishRepository.findAllByMemberId(memberId, pageable)).thenReturn(List.of(wish1, wish2));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(200L)).thenReturn(Optional.of(product2));

        List<WishResponseDto> wishlist = wishService.getWishlist(memberId, pageable);

        assertThat(wishlist).hasSize(2);
        assertThat(wishlist.get(0).product().name()).isEqualTo("아이스 아메리카노");
        assertThat(wishlist.get(1).product().price()).isEqualTo(5000);
    }

    @Test
    void 위시리스트_추가_성공() {
        Long memberId = 1L;
        Long productId = 100L;
        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);
        Product product = new Product("아이스 아메리카노", 4500, "img.jpg");
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(product, "id", productId);

        when(wishRepository.existsByMemberIdAndProductId(memberId, productId)).thenReturn(false);
        when(productRepository.existsById(productId)).thenReturn(true);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Wish savedWish = new Wish(member, product);
        ReflectionTestUtils.setField(savedWish, "id", 10L);
        when(wishRepository.save(any(Wish.class))).thenReturn(savedWish);

        WishCreateResponseDto response = wishService.add(memberId, productId);

        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.productId()).isEqualTo(productId);
        verify(wishRepository).save(any(Wish.class));
    }

    @Test
    void 위시리스트_추가_실패_이미존재() {
        when(wishRepository.existsByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(WishAlreadyExistException.class, () -> wishService.add(1L, 1L));
    }

    @Test
    void 위시리스트_추가_실패_상품없음() {
        when(wishRepository.existsByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(false);
        when(productRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ProductNotExistException.class, () -> wishService.add(1L, 1L));
    }

    @Test
    void 위시리스트_추가_실패_회원없음() {
        when(wishRepository.existsByMemberIdAndProductId(anyLong(), anyLong())).thenReturn(false);
        when(productRepository.existsById(anyLong())).thenReturn(true);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> wishService.add(1L, 1L));
    }

    @Test
    void 위시리스트_삭제_성공() {
        Long wishId = 10L;
        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);
        Product product = new Product("name", 1000, "img");
        Wish wish = new Wish(member, product);
        ReflectionTestUtils.setField(wish, "id", wishId);

        when(wishRepository.findById(wishId)).thenReturn(Optional.of(wish));

        wishService.remove(wishId);

        verify(wishRepository).delete(wish);
    }

    @Test
    void 위시리스트_삭제_실패_없음() {
        when(wishRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(WishNotExistException.class, () -> wishService.remove(1L));
    }
}