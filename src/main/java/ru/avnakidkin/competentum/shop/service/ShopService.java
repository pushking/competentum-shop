package ru.avnakidkin.competentum.shop.service;

import ru.avnakidkin.competentum.shop.domain.Till;

import java.util.Collection;
import java.util.List;

/**
 * Service
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
public interface ShopService {

    /**
     * Create a till
     *
     * @return new till
     */
    Till createTill();

    /**
     * Create a customer and put him in a queue
     *
     * @param tills available tills
     * @return till with new customer
     */
    Till createAndPutCustomer(List<Till> tills);

    /**
     * Service first customers
     *
     * @param tills available tills
     */
    void serviceCustomers(Collection<Till> tills);
}
