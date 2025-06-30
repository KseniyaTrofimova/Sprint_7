package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class OrderSteps {

    private static String CREATE_ORDER = "/api/v1/orders";
    private static String CANCEL_ORDER = "/api/v1/orders/cancel";

    @Step("Метод создания заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Метод получения списка заказов")
    public ValidatableResponse getOrdersList() {
        return given()
                .when()
                .get(CREATE_ORDER)
                .then();
    }

    @Step("Метод для отмены заказа по трек-номеру")
    public ValidatableResponse cancelOrder(int track) {
        return given()
                .body(Collections.singletonMap("track", track))
                .when()
                .put(CANCEL_ORDER)
                .then();
    }

    @Step("Метод для проверки доступности API")
    public ValidatableResponse checkApiAvailable() {
        return given()
                .when().get(CREATE_ORDER)
                .then().statusCode(anyOf(is(SC_OK), is(SC_NOT_FOUND)));
    }
}
