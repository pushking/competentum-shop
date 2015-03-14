package ru.avnakidkin.competentum.shop.web;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import ru.avnakidkin.competentum.shop.domain.Customer;

/**
 * Layout for customer
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
class CustomerLayout extends ImageAndLabel {

    private final Customer customer;
    private final int basketSize;

    public CustomerLayout(Customer customer) {
        super(new ThemeResource("images/" + customer.getCustomerType().toString().toLowerCase() + ".png"),
                customer.getGoodsQuantity() + " / " + customer.getGoodsQuantity());

        this.customer = customer;
        basketSize = customer.getGoodsQuantity();

        setMargin(new MarginInfo(false, true, false, true));
    }

    /**
     * Compare method customer and layout customer
     *
     * @param customer checked customer
     * @return true if the same, else false
     */
    public boolean isYour(Customer customer) {
        return this.customer == customer;
    }


    /**
     * Update goods quantity
     */
    public void update() {
        setText(customer.getGoodsQuantity() + " / " + basketSize);
    }

    /**
     * Setup original quantity
     */
    public void reset() {
        setText("" + basketSize);
    }
}