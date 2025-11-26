package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Создание курьера")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then();
    }

    @Step("Получение списка заказа")
    public ValidatableResponse getListOfOrders () {
        return given()
                .get ("/api/v1/orders")
                .then();
    }

    @Step("Закрытие заказа")
    public ValidatableResponse cancelOrder (Order order) {
        return given()
                .body(order)
                .when()
                .put("/api/v1/orders/cancel")
                .then();
    }

}
