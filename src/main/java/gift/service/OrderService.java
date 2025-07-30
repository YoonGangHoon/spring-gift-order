package gift.service;

import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Wish;
import gift.exception.MemberNotFoundException;
import gift.exception.OptionNotExistException;
import gift.repository.MemberRepository;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;

    public OrderService(
            OrderRepository orderRepository,
            OptionRepository optionRepository,
            WishRepository wishRepository,
            MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Order createOrder(Long memberId, OrderRequestDto requestDto) {
        Option option = optionRepository.findById(requestDto.optionId())
                .orElseThrow(() -> new OptionNotExistException(requestDto.optionId()));
        option.decreaseQuantity(requestDto.quantity());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("memberId", memberId.toString()));

        Order order = new Order(member, option, requestDto.quantity(), LocalDateTime.now(), requestDto.message());
        Order saved = orderRepository.save(order);

        Wish wish = wishRepository.findByMemberIdAndProductId(memberId, option.getProduct().getId());
        wishRepository.delete(wish);

        return saved;
    }

    public List<OrderResponseDto> getAllOrders(Long memberId, Pageable pageable) {

        return orderRepository.findByMemberId(memberId, pageable)
                .stream()
                .map(o -> new OrderResponseDto(o.getId(), o.getOption().getId(), o.getQuantity(), o.getOrderDateTime(), o.getMessage()))
                .toList();
    }
}
