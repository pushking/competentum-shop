package ru.avnakidkin.competentum.shop.web;

import com.vaadin.ui.VerticalLayout;
import ru.avnakidkin.competentum.shop.domain.Till;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Vertical layout with customers - queue
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
class QueueLayout extends VerticalLayout {

    private final Till till;
    private final Deque<CustomerLayout> customerLayouts;

    public QueueLayout(Till till) {
        this.till = till;
        customerLayouts = new ArrayDeque<>();

        setSpacing(true);
    }

    /**
     * Compare method till and layout till
     *
     * @param till checked till
     * @return true if the same, else false
     */
    public boolean isYour(Till till) {
        return this.till == till;
    }


    /**
     * Add and select new customer and deselect old
     *
     * @param hasNewCustomer true if last customer is new
     */
    public void updateCustomers(boolean hasNewCustomer) {
        if (!customerLayouts.isEmpty()) {
            // deselect last
            customerLayouts.getLast().removeStyleName("newCustomer");
        }
        if (hasNewCustomer) {
            // add new
            CustomerLayout customerLayout = new CustomerLayout(till.getQueue().getLast());
            addComponent(customerLayout);
            customerLayouts.addLast(customerLayout);

            // select last
            customerLayout.addStyleName("newCustomer");
        }
    }

    /**
     * Service first customer
     *
     * @return layout of released customer or null
     */
    public CustomerLayout serviceCustomer() {
        if (customerLayouts.isEmpty()) {
            // relax
            return null;
        }
        CustomerLayout firstLayout = customerLayouts.getFirst();
        if (till.getQueue().isEmpty() || !firstLayout.isYour(till.getQueue().getFirst())) {
            // release
            removeComponent(firstLayout);
            customerLayouts.removeFirst();
            return firstLayout;
        } else {
            // just update
            firstLayout.update();
            return null;
        }
    }
}