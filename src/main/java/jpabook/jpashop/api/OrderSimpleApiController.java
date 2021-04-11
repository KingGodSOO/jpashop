package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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
    public List<SimpleOrderDto> ordersV2() { // 원래는 dto를 바로 반환하면 안 되고, Result 래퍼객체로 감싸야한다. 실습예제 편의상 바로 반환.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o)) // 엔티티를 Dto의 파라미터로 받는 건 문제가 되지 않는다.
                .collect(Collectors.toList());

        return result;
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - fetch join으로 쿼리 1번 호출
     * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     *
     * 연관 관계의 엔티티를 join하여 같이 조회하기 때문에, 엔티티가 영속성인 상태가 됨. 그래서 lazy loading이 발생하지 않는 것.
     * */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * V4. JPA에서 DTO로 바로 조회
     *-쿼리1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     *
     * 성능 최적화는 더 되었지만, 화면 최적화 되었기 때문에 재사용성이 없다는 단점이 있다.
     * repository에서 웹 계층이 침범한 경우이기 때문에, repository의 순수성도 깨진다. -> 이는 패키지를 분리하면서 어느정도 해결
     * trade off를 잘 고려하여, 위 ordersV3를 디폴트로 두고 특수한 경우에 사용하자.
     *
     * select 컬럼이 정말로 진짜진짜 많고, 트래픽이 어마어마하게 많다면 그런 경우에 고려해보자.
     * 솔직히 그렇게 좋아보이진 않는다~ 물론 querydsl로 들어가면 얘기가 달라질 수는 있겠지만.
     *
     * 쿼리 방식 선택 권장 순서
     * 1. 우선 엔티티를 DTO로 변환하는 방법을 선택한다.
     * 2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
     * 3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
     * 4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용한다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
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
