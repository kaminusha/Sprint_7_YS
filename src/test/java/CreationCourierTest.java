import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Courier;
import ru.yandex.praktikum.CourierSteps;

import java.util.Locale;
import static org.hamcrest.CoreMatchers.equalTo;

@Feature("Создать курьера")
@Epic("Управление курьерами")
public class CreationCourierTest extends BaseTest {

    private CourierSteps courierSteps = new CourierSteps(); // Клиент API для работы с курьерами
    private Courier courier; // Объект курьера для тестов

    Faker faker = new Faker(new Locale("ru")); // Генератор тестовых данных

    @Before
    public void setUp () {
        courier = new Courier () // Создаём объект курьера с рандомными валидными данными
                .setLogin(generateRandomLogin()) // логин
                .setPassword(generateRandomPassword()) // пароль
                .setFirstName(generateRandomFirstName()); // имя
    }

    private String generateRandomLogin() {
        return  faker.regexify ("[a-z]{8}"); // Генерация случайного логина (8 строчных латинских букв)
    }

    private String generateRandomFirstName() {
        return  faker.name().firstName(); // Генерация случайного имени
    }

    private String generateRandomPassword() {
        return  faker.internet().password(8, 12, true, true); // Генерация случайного пароля
    }

    @Test
    @DisplayName("Успешное создание курьера с валидными данными")
    @Description("Создание курьера: переданы все обязательные поля")
    public void shouldCreateCourierTest() {
        courierSteps // Отправляем запрос на создание курьера
                .createCourier(courier)
                .statusCode(201) // Проверяем статус ответа
                .body("ok", equalTo(true)); // Проверяем тело ответа (должно быть "ok": true)
    }

    @Test
    @DisplayName("Ошибка при создании курьера с дублирующим логином")
    @Description("Создание двух курьеров с одинаковыми логинами, одинаковыми паролями")
    public void shouldNoTCreateCourierWithSameLoginTest() {
        courierSteps // Создаём курьера (первый раз — успешно)
                .createCourier(courier);

        courierSteps // Cоздать курьера с тем же логином
                .createCourier(courier) // Передаём тот же объект
                .statusCode(409) // Проверяем статус
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой.")); // Проверяем сообщение
    }

    @Test
    @DisplayName("Создание курьера при незаполненном логине")
    @Description("Создание курьера при пустом обязательном поле - логине")
    public void shouldNoTCreateCourierWithoutLoginTest() {
        courier.setLogin(null); // Обнуляем поле login у курьера
        courierSteps // Отправляем запрос с отсутствующим логином
                .createCourier(courier)
                .statusCode(400) // Проверка статуса
                .body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверка сообщения
    }

    @Test
    @DisplayName("Создание курьера при незаполненном пароле")
    @Description("Создание курьера при пустом обязательном поле - пароле")
    public void shouldNoTCreateCourierWithoutPasswordTest() {
        courier.setPassword(null); // Обнуляем поле password у курьера
        courierSteps // Отправляем запрос с отсутствующим паролем
                .createCourier(courier)
                .statusCode(400) // Проверка статуса
                .body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверка сообщения
    }

    // Очистка данных после выполнения теста
    @After
    public void tearDown() {
        Integer id = null;
        try {
            id = courierSteps.loginCourier(courier) // Авторизация созданного курьера (получение его ID)
                    .extract().body().path("id");
        } catch (Exception e) { // При ошибке (например, курьер не создан), логируем его
        }
        if (id !=null) { // Если ID получен — удаляем курьера
            courier.setId(id);
            courierSteps.deleteCourier(courier);
        } // Если id == null — курьер не был создан или не найден, удаление пропускается
    }
}
