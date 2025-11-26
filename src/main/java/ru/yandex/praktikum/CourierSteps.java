package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import ru.yandex.praktikum.Courier;

public class CourierSteps {
    @Step("Создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    @Step("Логин курьера")
    public ValidatableResponse loginCourier (Courier courier) {
        return given()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    @Step ("Удаление курьера")
    public ValidatableResponse deleteCourier (Courier courier) {
        return given()
                .pathParams("id", courier.getId())
                .when()
                .delete("/api/v1/courier/{id}")
                .then();
    }
}
