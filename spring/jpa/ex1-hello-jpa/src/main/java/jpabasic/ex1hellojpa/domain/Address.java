package jpabasic.ex1hellojpa.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@EqualsAndHashCode
public class Address {

    //공통으로 관리할 수 있음
    @Column(length = 15)
    private String city;
    @Column(length = 20)

    private String street;
    @Column(length = 10)
    private String zipcode;

    //공통으로 사용할 수 있는 의미있는 비즈니스 코드 작성 가능
    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
