package ru.avnakidkin.competentum.shop.domain;

import java.io.Serializable;

/**
 * Customer
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
public class Customer implements Serializable {

    private final CustomerType customerType;
    private int goodsQuantity;

    public Customer(CustomerType customerType) {
        this.customerType = customerType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public int getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(int goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }
}
