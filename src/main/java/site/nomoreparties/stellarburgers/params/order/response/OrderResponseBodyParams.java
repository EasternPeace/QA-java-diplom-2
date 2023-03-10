package site.nomoreparties.stellarburgers.params.order.response;

import site.nomoreparties.stellarburgers.params.IParams;

public class OrderResponseBodyParams implements IParams {
    String name;
    Order order;
    boolean success;

    public OrderResponseBodyParams(String name, Order order, boolean success) {
        this.name = name;
        this.order = order;
        this.success = success;
    }
}
