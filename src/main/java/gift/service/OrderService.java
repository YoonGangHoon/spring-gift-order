package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.OptionNotExistException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final KakaoMessageService kakaoMessageService;
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    public OrderService(KakaoMessageService kakaoMessageService,OrderRepository orderRepository,  OptionRepository optionRepository) {
        this.kakaoMessageService = kakaoMessageService;
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(String kakaoAccessToken ,OrderRequestDto requestDto) {
        Option option = optionRepository.findById(requestDto.optionId())
                .orElseThrow(() -> new OptionNotExistException(requestDto.optionId()));

        option.decreaseQuantity(requestDto.quantity());

        Order order = new Order(option, requestDto.quantity(), LocalDateTime.now(), requestDto.message());
        Order saved = orderRepository.save(order);
        kakaoMessageService.sendOrderMessage(kakaoAccessToken, saved);
        return new OrderResponseDto(saved.getId(), saved.getOption().getId(), saved.getQuantity(), saved.getOrderDateTime(), saved.getMessage());
    }

    public List<OrderResponseDto> getAllOrders(Pageable pageable) {

        return orderRepository.findAll(pageable)
                .stream()
                .map(o -> new OrderResponseDto(o.getId(), o.getOption().getId(), o.getQuantity(), o.getOrderDateTime(), o.getMessage()))
                .toList();
    }
}
