package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity //JPA 추가
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA
    private Long id;

    @Column(name = "item_name", length = 10) //JPA
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
