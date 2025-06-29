package org.example.steps;

import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static String CREATE_ORDER = "/api/v1/orders";

    public ValidatableResponse createOrder(Order order){
        return given()
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    public ValidatableResponse getOrdersList() {
        return given()
                .when()
                .get(CREATE_ORDER)
                .then();
    }
}
