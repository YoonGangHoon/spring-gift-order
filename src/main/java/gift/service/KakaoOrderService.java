package gift.service;

import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class KakaoOrderService {

    private final OrderService orderService;
    private final KakaoMessageService kakaoMessageService;

    public KakaoOrderService(OrderService orderService, KakaoMessageService kakaoMessageService) {
        this.orderService = orderService;
        this.kakaoMessageService = kakaoMessageService;
    }

    public OrderResponseDto createOrder(Long memberId, OrderRequestDto requestDto) {
        Order order = orderService.createOrder(memberId, requestDto);
        kakaoMessageService.sendOrderMessage(order.getMember().getAccessToken(), order);

        return new OrderResponseDto(
                order.getId(),
                order.getOption().getId(),
                order.getQuantity(),
                order.getOrderDateTime(),
                order.getMessage()
        );
    }
}
