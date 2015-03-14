package ru.avnakidkin.competentum.shop.service.impl;

import ru.avnakidkin.competentum.shop.domain.Customer;
import ru.avnakidkin.competentum.shop.domain.CustomerType;
import ru.avnakidkin.competentum.shop.domain.Till;
import ru.avnakidkin.competentum.shop.service.ShopService;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ShopService implementation
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
public class ShopServiceImpl implements ShopService {

    private static final int MAX_BASKET_SIZE = 10; // real between 1 and MAX_BASKET_SIZE
    private static final int MAX_TILL_PERFORMANCE = 5; // real between 1 and MAX_TILL_PERFORMANCE

    // customer probabilities
    private final int childProbability;
    private final int womanProbability;
    private final int manProbability;

    /**
     * Constructor
     *
     * @param childProbability probability of creating a child
     * @param womanProbability probability of creating a woman
     * @param manProbability   probability of creating a man
     * @see #createAndPutCustomer(java.util.List) createAndPutCustomer
     */
    public ShopServiceImpl(int childProbability, int womanProbability, int manProbability) {
        this.childProbability = childProbability;
        this.womanProbability = womanProbability;
        this.manProbability = manProbability;
    }

    @Override
    public Till createTill() {
        return new Till(ThreadLocalRandom.current().nextInt(MAX_TILL_PERFORMANCE) + 1);
    }

    @Override
    public Till createAndPutCustomer(List<Till> tills) {
        Customer newCustomer = createCustomer();
        Till till = tillChoice(newCustomer, tills);
        till.getQueue().addLast(newCustomer);
        return till;
    }

    @Override
    public void serviceCustomers(Collection<Till> tills) {
        for (Till till : tills) {
            serviceCustomer(till);
        }
    }

    /**
     * Create new customer
     *
     * @return new customer
     */
    private Customer createCustomer() {
        CustomerType type;
        int i = ThreadLocalRandom.current().nextInt(childProbability + womanProbability + manProbability);
        if (i < childProbability) {
            type = CustomerType.CHILD;
        } else if (i < childProbability + womanProbability) {
            type = CustomerType.WOMAN;
        } else {
            type = CustomerType.MAN;
        }
        Customer customer = new Customer(type);
        customer.setGoodsQuantity(ThreadLocalRandom.current().nextInt(MAX_BASKET_SIZE) + 1);
        return customer;
    }

    /**
     * Choice till for customer
     *
     * @param customer new customer
     * @param tills    available tills
     * @return chosen till
     */
    private Till tillChoice(Customer customer, List<Till> tills) {
        Till result = null;
        switch (customer.getCustomerType()) {
            case CHILD:
                result = tills.get(ThreadLocalRandom.current().nextInt(tills.size()));
                break;
            case WOMAN:
                for (Till till : tills) {
                    if (result == null || result.getQueue().size() > till.getQueue().size()) {
                        result = till;
                    }
                }
                break;
            case MAN:
                int steps, performance, minSteps = Integer.MAX_VALUE;
                for (Till till : tills) {
                    steps = 0;
                    performance = till.getPerformance();
                    // get steps number for queue
                    for (Customer waiter : till.getQueue()) {
                        steps += waiter.getGoodsQuantity() / performance;
                        if (waiter.getGoodsQuantity() % performance > 0) {
                            steps++;
                        }
                    }
                    // add steps number for the customer
                    steps += customer.getGoodsQuantity() / performance;
                    if (customer.getGoodsQuantity() % performance > 0) {
                        steps++;
                    }
                    // compare
                    if (steps < minSteps) {
                        minSteps = steps;
                        result = till;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + customer.getCustomerType());
        }
        return result;
    }

    /**
     * Service first customer
     *
     * @param till till with queue
     */
    private void serviceCustomer(Till till) {
        if (till.getQueue().isEmpty()) {
            return;
        }
        // service
        Customer customer = till.getQueue().getFirst();
        customer.setGoodsQuantity(customer.getGoodsQuantity() - till.getPerformance());
        // release
        if (customer.getGoodsQuantity() < 1) {
            till.getQueue().removeFirst();
        }
    }
}
