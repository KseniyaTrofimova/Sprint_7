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
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        courier = new Courier();
        courier.setLogin(RandomStringUtils.randomAlphabetic(6));
        courier.setPassword(RandomStringUtils.randomAlphabetic(6));
        courier.setFirstName(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    @DisplayName("Проверка авторизации курьера")
    @Step("Метод для авторизации курьера")
    public void shouldLoginCourierTest() {
        courierSteps.createCourier(courier);
        courierSteps.loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue());

    }

    @Test
    @DisplayName("Проверка авторизации курьера без обязательного поля логин")
    @Step("Метод для авторизации курьера без заполненного поля логин")
    public void shouldRequiredFieldAuthorizationLogin() {
        courierSteps.createCourier(courier);
        courier.setLogin(null);
        courierSteps.loginCourier(courier)
                .statusCode(400)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера без обязательного поля пароль")
    @Step("Метод для авторизации курьера без заполненного поля пароль")
    public void shouldRequiredFieldAuthorizationPassword() {
        courierSteps.createCourier(courier);
        courier.setPassword(null);
        courierSteps.loginCourier(courier)
                .statusCode(400)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера с неверным полем логин")
    @Step("Метод для авторизации курьера с неверно заполненным полем логин")
    public void shouldAuthorizationInvalidLogin() {
        courier.setLogin("loginov");
        courier.setPassword("password");
        courierSteps.createCourier(courier);
        courier.setLogin("logi");
        courierSteps.loginCourier(courier)
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера с неверным полем пароль")
    @Step("Метод для авторизации курьера с неверно заполненным полем пароль")
    public void shouldAuthorizationInvalidPassword() {
        courier.setLogin("gorbunov");
        courier.setPassword("password");
        courierSteps.createCourier(courier);
        courier.setPassword("pasword");
        courierSteps.loginCourier(courier)
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
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
