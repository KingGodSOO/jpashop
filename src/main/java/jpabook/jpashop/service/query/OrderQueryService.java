package jpabook.jpashop.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
//    private final OrderRepository orderRepository;
//
//    // 화면에 fit한 dto로 변환하는 쿼리 발생 로직을 다른 서비스로 분리 (커멘드와 쿼리 분리)(즉 화면애 관한 로직과 핵심 비즈니스 로직을 분리함.)
//    public List<OrderDto> ordersV2() {
//
//        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
//        List<OrderDto> result = orders.stream()
//                .map(OrderDto::new)
//                .collect(toList());
//
//        return result;
//    }
}
