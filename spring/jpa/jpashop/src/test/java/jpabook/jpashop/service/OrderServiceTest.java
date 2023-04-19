package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        String bookName = "시골 JPA";
        int bookPrice = 10000;
        int stockQuantity = 10;
        Book book = createBook(bookName, bookPrice, stockQuantity);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", bookPrice * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", stockQuantity - orderCount, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();

        String bookName = "시골 JPA";
        int bookPrice = 10000;
        int stockQuantity = 10;
        Book book = createBook(bookName, bookPrice, stockQuantity);

        int orderCount = 11;
        
        //when
        orderService.order(member.getId(), book.getId(), orderCount);
        
        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
        
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();

        String bookName = "시골 JPA";
        int bookPrice = 10000;
        int stockQuantity = 10;
        Book book = createBook(bookName, bookPrice, stockQuantity);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", stockQuantity, book.getStockQuantity());

    }
    
    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("안양시", "안양천서로", "177"));
        em.persist(member);
        return member;
    }

    private Book createBook(String bookName, int  bookPrice, int stockQuantity) {
        Book book = new Book();
        book.setName(bookName);
        book.setStockQuantity(stockQuantity);
        book.setPrice(bookPrice);
        em.persist(book);
        return book;
    }
}