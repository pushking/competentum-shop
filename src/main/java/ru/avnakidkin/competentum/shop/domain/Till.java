package ru.avnakidkin.competentum.shop.domain;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Till with a queue
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
public class Till implements Serializable {

    private final int performance;
    private final Deque<Customer> queue;

    public Till(int performance) {
        this.performance = performance;
        queue = new ArrayDeque<>();
    }

    public int getPerformance() {
        return performance;
    }

    public Deque<Customer> getQueue() {
        return queue;
    }
}
