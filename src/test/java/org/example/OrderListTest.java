package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.model.Order;
import org.example.steps.OrderSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderListTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private Integer createdOrderTrack = null;

    @Before
    public void setUp() {
        try {
            orderSteps.checkApiAvailable();
        } catch (Exception e) {
            throw new RuntimeException("API недоступен: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка получения непустого списка заказов")
    public void shouldGetNonEmptyOrdersList() {
        Order testOrder = new Order();
        testOrder.setColors(List.of("BLACK"));
        System.out.println("Создаем тестовый заказ...");

        createdOrderTrack = orderSteps.createOrder(testOrder)
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract().path("track");
        System.out.println("Заказ создан, track: " + createdOrderTrack);

        System.out.println("Проверяем список заказов...");
        orderSteps.getOrdersList()
                .statusCode(SC_OK)
                .body("orders", not(empty()))
                .body("orders.size()", greaterThan(0));
        System.out.println("Проверка списка заказов завершена");
    }

    @After
    public void tearDown() {
        if (createdOrderTrack != null) {
            try {
                orderSteps.cancelOrder(createdOrderTrack)
                        .statusCode(anyOf(is(SC_OK), is(SC_NOT_FOUND)));
            } catch (Exception e) {
                System.err.println("Ошибка при отмене заказа: " + e.getMessage());
            }
        }
    }
}
