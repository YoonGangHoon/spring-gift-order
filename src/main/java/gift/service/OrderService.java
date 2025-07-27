package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.OptionNotExistException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    public OrderService(OrderRepository orderRepository,  OptionRepository optionRepository) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
    }

    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Option option = optionRepository.findById(requestDto.optionId())
                .orElseThrow(() -> new OptionNotExistException(requestDto.optionId()));

        option.decreaseQuantity(requestDto.quantity());

        Order order = new Order(option, requestDto.quantity(), LocalDateTime.now(), requestDto.message());
        Order saved = orderRepository.save(order);
        return new OrderResponseDto(saved.getId(), saved.getOption().getId(), saved.getQuantity(), saved.getOrderDateTime(), saved.getMessage());
    }
}
