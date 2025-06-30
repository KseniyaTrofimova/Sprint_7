package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Order;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

public class OrderLListTest {

    @Test
    @DisplayName("Создание заказа и проверка списка заказов")
    @Description("Метод для создания заказа и проверки отображения в списке заказов")
    public void createOrderAndCheckList() {
        Order order = new Order();
        if (colors != null) {
            order.setColors(colors);
        }

        orderSteps.createOrder(order)
                .statusCode(SC_CREATED)
                .body("track", notNullValue());

        orderSteps.getOrdersList()
                .statusCode(SC_OK)
                .body("orders", not(empty()))
                .body("orders.id", everyItem(notNullValue()));
    }
}
