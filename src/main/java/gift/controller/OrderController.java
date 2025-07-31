package gift.controller;

import gift.config.LoginMember;
import gift.dto.order.OrderRequestDto;
import gift.dto.order.OrderResponseDto;
import gift.entity.Member;
import gift.service.KakaoOrderService;
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
    private final KakaoOrderService kakaoOrderService;
    public OrderController(OrderService orderService, KakaoOrderService kakaoOrderService) {
        this.orderService = orderService;
        this.kakaoOrderService = kakaoOrderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> order(
            @LoginMember Member member,
            @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(kakaoOrderService.createOrder(member.getId(), requestDto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @LoginMember Member member,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        List<OrderResponseDto> responseDtoList = orderService.getAllOrders(member.getId(), pageable);
        return ResponseEntity.ok(responseDtoList);
    }
}
