package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then();
    }

    @Step("")
    public ValidatableResponse getListOfOrders () {
        return given()
                .get ("/api/v1/orders")
                .then();
    }

    @Step("")
    public ValidatableResponse cancelOrder (Order order) {
        return given()
                .body(order)
                .when()
                .put("/api/v1/orders/cancel")
                .then();
    }

}
