package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리 * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
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

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X) * - 단점: 지연로딩으로 쿼리 N번 호출
     * N + 1 = 1 + 회원 N + 배송 N. 총 5번 쿼리 호출(최악의 경우. 이미 영속성인 경우는 쿼리가 줄어들 수도 있다.)
     * Eager로 변경해도 쿼리가 줄지 않는다. 필요하면 fetch join을 해야 한다.
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() { // 원래는 dto를 바로 반환하면 안 되고, Result 래퍼객체로 감싸야한다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o)) // 엔티티를 Dto의 파라미터로 받는 건 문제가 되지 않는다.
                .collect(Collectors.toList());

        return result;
    }

    // api 스펙 정의 -> entity가 변하더라도 스펙이 바뀌지 않는다.
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // dto가 엔티티를 parameter로 받는 것은 문제가 되지 않는다. 별로 중요하지 않은 곳에서 중요한 엔티티를 받는 것이기 때문에.
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
