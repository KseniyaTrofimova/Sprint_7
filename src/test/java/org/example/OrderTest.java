package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;
import org.example.steps.OrderSteps;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderTest extends BaseTest  {
    private final OrderSteps orderSteps = new OrderSteps();
    private final List<String> colors;
    private final List<Integer> createdOrderTracks = new ArrayList<>();

    public OrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {Collections.singletonList("BLACK")},
                {Collections.singletonList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        };
    }

    @Test
    @DisplayName("Создание заказа с различными цветами")
    @Description("Проверка создания заказов с разными комбинациями цветов")
    public void createOrder() {
        Order order = new Order();
        if (colors != null) {
            order.setColors(colors);
        } else {
            order.setColors(Collections.emptyList());
        }
        System.out.println("Создаю заказ с цветами: " + order.getColors());

        int track = orderSteps.createOrder(order)
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .extract().path("track");

        System.out.println("Заказ создан, track: " + track);
        createdOrderTracks.add(track);
    }

    @After
    public void tearDown() {
        for (Integer track : createdOrderTracks) {
            try {
                System.out.println("Попытка отменить заказ с track=" + track);

                ValidatableResponse response = orderSteps.cancelOrder(track)
                        .log().all();

                int statusCode = response.extract().statusCode();

                if (statusCode == SC_OK) {
                    System.out.println("✅ Заказ успешно отменен");
                } else {
                    System.out.println("❌ Не удалось отменить заказ. Код: " + statusCode);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Ошибка при отмене заказа: " + e.getMessage());
                e.printStackTrace();
            }
        }
        createdOrderTracks.clear();
        System.out.println("Очистка завершена");
    }
    }