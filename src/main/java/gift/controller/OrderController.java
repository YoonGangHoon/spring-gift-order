package gift.controller;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        List<OrderResponseDto> responseDtoList = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(responseDtoList);
    }
}
