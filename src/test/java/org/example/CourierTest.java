package org.example;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.Courier;
import org.example.steps.CourierSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.Matchers.is;

public class CourierTest extends BaseTest {
    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        courier = new Courier();
        courier.setLogin("test_" + RandomStringUtils.randomAlphabetic(6));
        courier.setPassword(RandomStringUtils.randomAlphabetic(6));
        courier.setFirstName(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    @DisplayName("Проверка создания курьера")
    @Step("Метод для создания курьера")
    public void shouldCreateCourierTest() {
        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Проверка создания двух одинаковых курьеров")
    @Step("Метод для создания одинаковых курьеров")
    public void shouldCreateIdenticalCourier() {
        courier.setLogin("limonad");
        courier.setPassword("123qwe");
        courier.setFirstName("identical");
        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", is(true));

        courierSteps.createCourier(courier)
                .statusCode(409)
                .body("message", is("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля логин")
    @Step("Метод для создания курьера без заполненного поля логин")
    public void shouldRequiredFieldLogin() {
        courier.setLogin(null);
        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля пароль")
    @Step("Метод для создания курьера без заполненного поля пароль")
    public void shouldRequiredFieldPassword() {
        courier.setPassword(null);
        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courier.getLogin() != null && courier.getPassword() != null) {
            Integer courierId = courierSteps.loginCourier(courier)
                    .statusCode(200)
                    .extract().path("id");

            if (courierId != null) {
                courierSteps.deleteCourier(courierId)
                        .statusCode(200);
            }
        }
    }
}



