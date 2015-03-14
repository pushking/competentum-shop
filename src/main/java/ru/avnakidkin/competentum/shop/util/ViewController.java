package ru.avnakidkin.competentum.shop.util;

/**
 * Used for interaction between child and parent
 *
 * @author Alexey Nakidkin {@literal <avnakidkin@gmail.com>}
 */
public interface ViewController {

    /**
     * Process a command
     *
     * @param viewClass class of child
     * @param command   command
     * @param data      data
     */
    void processViewCommand(Class viewClass, String command, Object data);
}
