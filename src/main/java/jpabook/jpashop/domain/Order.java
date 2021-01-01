package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 주로 Order에서 Delivery를 조회하기 때문에, 연관관계의 주인(FK와 가까운 쪽)을 Order로 둔다.
    private Delivery delivery;
    
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING) // 타입은 반드시 String으로!
    private OrderStatus status; // 주문 상태 [ORDER, CANCLE)

    //==연관관계 (편의)메서드==///
    // 주체적으로 관계를 컨트롤하는 클래스에 넣으면 좋다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}