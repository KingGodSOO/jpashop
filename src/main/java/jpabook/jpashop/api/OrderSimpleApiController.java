package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne(ManyToOne, OneToOne) (컬렉션 연관이 아닌 것) 조회
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    // v1. 정말 다양한 문제가 있으므로 하면 안 된다. -> 강의 문서를 참고하자.
    // API response에는 꼭 필요한 데이터가 들어가는 것이 좋다.
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        // lazy 강제 초기화
        for (Order order :
                all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

}
