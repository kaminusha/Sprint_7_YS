package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
import ru.yandex.praktikum.Courier;

public class CourierSteps {
    @Step("")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    @Step("")
    public ValidatableResponse loginCourier (Courier courier) {
        return given()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    @Step ("")
    public ValidatableResponse deleteCourier (Courier courier) {
        return given()
                .pathParams("id", courier.getId())
                .when()
                .delete("/api/v1/courier/{id}")
                .then();
    }
}
