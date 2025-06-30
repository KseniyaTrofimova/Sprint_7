package org.example;

import io.qameta.allure.Description;
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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierTest extends BaseTest {
    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private Integer courierId = null;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        courier = new Courier();
        courier.setLogin(RandomStringUtils.randomAlphabetic(6));
        courier.setPassword(RandomStringUtils.randomAlphabetic(6));
        courier.setFirstName(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    @DisplayName("Проверка создания курьера")
    @Description("Метод для создания курьера")
    public void shouldCreateCourierTest() {
        courierSteps.createCourier(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));

        courierId = courierSteps.loginCourier(courier)
                .statusCode(SC_OK)
                .extract().path("id");
    }

    @Test
    @DisplayName("Проверка создания двух одинаковых курьеров")
    @Description("Метод для создания одинаковых курьеров")
    public void shouldCreateIdenticalCourier() {
        courierSteps.createCourier(courier)
                .statusCode(SC_CREATED);

        courierId = courierSteps.loginCourier(courier)
                .statusCode(SC_OK)
                .extract().path("id");

        courierSteps.createCourier(courier)
                .statusCode(SC_CONFLICT)
                .body("message", startsWith("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля логин")
    @Description("Метод для создания курьера без заполненного поля логин")
    public void shouldRequiredFieldLogin() {
        courier.setLogin(null);
        courierSteps.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля пароль")
    @Description("Метод для создания курьера без заполненного поля пароль")
    public void shouldRequiredFieldPassword() {
        courier.setPassword(null);
        courierSteps.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId)
                    .statusCode(anyOf(is(SC_OK), is(SC_NOT_FOUND)));
            }
        }
    }




