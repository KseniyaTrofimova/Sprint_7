package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.Courier;
import org.example.steps.CourierSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest extends BaseTest {

    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private Integer courierId = null;

    @Before
    public void setUp() {
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000)
                        .setParam("http.socket.timeout", 30000));
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        courier = new Courier(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        courierSteps.createCourier(courier)
                .statusCode(SC_CREATED);

        courierId = courierSteps.loginCourier(courier)
                .statusCode(SC_OK)
                .extract().path("id");
    }

    @Test
    @DisplayName("Проверка авторизации курьера")
    @Description("Метод для авторизации курьера")
    public void shouldLoginCourierTest() {
        courierSteps.loginCourier(courier)
                .statusCode(SC_OK)
                .body("id", notNullValue());

    }

    @Test
    @DisplayName("Проверка авторизации курьера без обязательного поля логин")
    @Description("Метод для авторизации курьера без заполненного поля логин")
    public void shouldRequiredFieldAuthorizationLogin() {
        Courier noLoginCourier = new Courier(null, courier.getPassword(), null);
        courierSteps.loginCourier(noLoginCourier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера без обязательного поля пароль")
    @Description("Метод для авторизации курьера без заполненного поля пароль")
    public void shouldRequiredFieldAuthorizationPassword() {
        Courier noPasswordCourier = new Courier(courier.getLogin(), null, null);
        courierSteps.loginCourier(noPasswordCourier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера с неверным полем логин")
    @Description("Метод для авторизации курьера с неверно заполненным полем логин")
    public void shouldAuthorizationInvalidLogin() {
        Courier invalidLoginCourier = new Courier(
                "invalid_" + RandomStringUtils.randomAlphabetic(10),
                courier.getPassword(),
                null
        );

        courierSteps.loginCourier(invalidLoginCourier)
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера с неверным полем пароль")
    @Description("Метод для авторизации курьера с неверно заполненным полем пароль")
    public void shouldAuthorizationInvalidPassword() {
        Courier invalidPasswordCourier = new Courier(
                courier.getLogin(),
                "wrong_" + RandomStringUtils.randomAlphabetic(10),
                null
        );

        courierSteps.loginCourier(invalidPasswordCourier)
                .statusCode(SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            try {
                Thread.sleep(1000);
                courierSteps.deleteCourier(courierId)
                        .statusCode(anyOf(is(SC_OK), is(SC_NOT_FOUND)));
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера: " + e.getMessage());
            }
        }
    }
}

