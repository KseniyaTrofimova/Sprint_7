package org.example;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Order;
import org.example.steps.OrderSteps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderTest extends BaseTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private final List<String> colors;

    public OrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        };
    }

    @Test
    @DisplayName("Создание заказа и проверка списка заказов")
    @Step("Метод для создания заказа и проверки отображения в списке заказов")
    public void createOrderAndCheckList() {
        Order order = new Order();
        if (colors != null) {
            order.setColors(colors);
        }

        orderSteps.createOrder(order)
                .statusCode(201)
                .body("track", notNullValue());

        orderSteps.getOrdersList()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("orders.id", everyItem(notNullValue()));
    }
}