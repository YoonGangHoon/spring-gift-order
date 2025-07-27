package gift.controller;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> order(
            @RequestHeader(value = "X-Kakao-Access-Token", required = false) String kakaoAccessToken,
            @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(orderService.createOrder(kakaoAccessToken, requestDto));
    }
}
