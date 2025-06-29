package org.example.steps;

import io.restassured.response.ValidatableResponse;
import org.example.model.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CourierSteps {

    private static String COURIER = "/api/v1/courier/";
    private static String LOGIN_COURIER = "/api/v1/courier/login";

    public ValidatableResponse createCourier(Courier courier){
        return given()
                .body(courier)
                .when()
                .post(COURIER)
                .then();
    }

    public ValidatableResponse loginCourier(Courier courier){
        return given()
                .body(courier)
                .when()
                .post(LOGIN_COURIER)
                .then();
    }

    public ValidatableResponse deleteCourier(Integer courierId) {
        return given()
                .pathParam("id", courierId)
                .when()
                .delete(COURIER + "/{id}")
                .then();
    }

    public Integer getCourierId(Courier courier) {
        return loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }
}
