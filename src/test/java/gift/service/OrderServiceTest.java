package gift.service;

import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.*;
import gift.exception.MemberNotFoundException;
import gift.exception.OptionNotExistException;
import gift.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OptionRepository optionRepository;
    private WishRepository wishRepository;
    private MemberRepository memberRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        optionRepository = mock(OptionRepository.class);
        wishRepository = mock(WishRepository.class);
        memberRepository = mock(MemberRepository.class);

        orderService = new OrderService(
                orderRepository,
                optionRepository,
                wishRepository,
                memberRepository
        );
    }

    @Test
    void 주문_생성_성공() {
        // given
        Long memberId = 123L;
        Long optionId = 10L;
        int quantity = 2;
        String message = "생일 축하해!";
        OrderRequestDto requestDto = new OrderRequestDto(optionId, quantity, message);

        Member member = new Member(123L, "닉네임", "access-token", "refresh-token", 0);

        Product product = new Product("꽃다발", 10000, "url");
        Option option = new Option("빨강", 10, product);
        ReflectionTestUtils.setField(option, "id", optionId);

        Order savedOrder = new Order(member, option, quantity, LocalDateTime.now(), message);
        ReflectionTestUtils.setField(savedOrder, "id", 100L);

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(wishRepository.findByMemberIdAndProductId(memberId, product.getId())).thenReturn(new Wish(member, product));

        // when
        Order response = orderService.createOrder(memberId, requestDto);

        // then
        assertThat(response).isEqualTo(savedOrder);
    }

    @Test
    void 주문시_존재하지_않는_옵션이면_예외() {
        // given
        Long memberId = 123L;
        Long optionId = 999L;
        OrderRequestDto requestDto = new OrderRequestDto(optionId, 1, "메시지");

        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(memberId, requestDto))
                .isInstanceOf(OptionNotExistException.class);
    }

    @Test
    void 주문시_존재하지_않는_회원이면_예외() {
        // given
        Long memberId = 123L;
        Long optionId = 1L;
        OrderRequestDto requestDto = new OrderRequestDto(optionId, 1, "메시지");

        Product product = new Product("상품", 1000, "url");
        Option option = new Option("옵션", 10, product);
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(memberId, requestDto))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 회원의_주문_전체_조회() {
        // given
        Long memberId = 123L;
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product("선물", 1000, "url");
        Option option = new Option("옵션", 10, product);
        ReflectionTestUtils.setField(option, "id", 1L);

        Order order1 = new Order(null, option, 1, LocalDateTime.now(), "메시지1");
        ReflectionTestUtils.setField(order1, "id", 100L);

        Order order2 = new Order(null, option, 2, LocalDateTime.now(), "메시지2");
        ReflectionTestUtils.setField(order2, "id", 101L);

        when(orderRepository.findByMemberId(memberId, pageable))
                .thenReturn(List.of(order1, order2));

        // when
        List<OrderResponseDto> result = orderService.getAllOrders(memberId, pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).message()).isEqualTo("메시지1");
        assertThat(result.get(1).message()).isEqualTo("메시지2");
    }
}