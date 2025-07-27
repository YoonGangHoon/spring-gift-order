package gift.controller;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.service.OptionService;
import gift.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OptionService optionService;
    private final OrderService orderService;
    public OrderController(OptionService optionService,  OrderService orderService) {
        this.optionService = optionService;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> order(@RequestBody OrderRequestDto requestDto) {
        optionService.reduceOptionQuantity(requestDto.optionId(), requestDto.quantity());

        return ResponseEntity.ok(orderService.createOrder(requestDto));
    }
}
