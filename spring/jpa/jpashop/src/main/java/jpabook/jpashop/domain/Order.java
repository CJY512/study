package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    /*
    엔티티 클래스로 등록한 클래스지만, DB 테이블과는 별도로 기능이(추가 필드나 메소드) 필요한 경우가 있다.
    예를 들어 DB 테이블에는 존재하지 않지만, 엔티티 클래스에는 등록되어 같이 운용하는 경우가 있다.
    XML이나 JSON으로 클라이언트에 데이터를 전해주어야 할 경우, 모델을 이용하지 않으면 객체나 특정 콜렉션 타입을 리턴하게 된다.
    특정 클래스의 객체를 리턴할 경우, XML이나 JSON은 클래스 내부에 존재하는 내용으로 문서를 구성한다.
    하지만 클래스에는 존재하지 않지만, 서로 관련있는 내용끼리 묶어서 클라이언트에 전해두어야 할 필요가 있다.
    이럴 경우, DB 테이블에 간섭하지 않고, 엔티티 클래스 내부에서만 동작하게 하는 어노테이션을 사용한다.
    @Transient 어노테이션을 사용하는데, 이 어노테이션은 하이버네이트의 jpa 패키지에 위치하고 있다.
    @Transient 어노테이션을 사용한 필드나 메소드는 DB 테이블에 적용되지 않는다.

    @Transient
    private String notForColumn
    */

    //==연관관계 편의 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
