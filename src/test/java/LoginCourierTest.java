import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import static org.hamcrest.CoreMatchers.notNullValue;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Courier;
import ru.yandex.praktikum.CourierSteps;
import static org.hamcrest.CoreMatchers.equalTo;


// Тестируем классы проверки авторизации курьера в систему (позитивные и негативные сценарии входа)
@Epic("Авторизация")
@Feature("Вход в систему")
public class LoginCourierTest extends BaseTest {

    private CourierSteps courierSteps = new CourierSteps(); // Экземпляр класса для выполнения шагов по работе с курьерами (API-запросы)
    private Courier courier; // Объект курьера, с которым будут проводиться тестовые сценарии

    Faker faker = new Faker(); // Генератор тестовых данных

    // Создаёт нового курьера с рандомными валидными данными:
    @Before
    public void setUp() {
        courier = new Courier();
        courier
                .setLogin(faker.regexify("[a-z]{8}")) // логин: 8 строчных латинских букв;
                .setPassword(faker.regexify("[a-z][A-Z]{8}")); // пароль: 1 строчная буква + 8 заглавных букв.

        courierSteps.createCourier(courier); // Отправляем запрос на создание курьера в систему
    }

    // Тест успешного входа курьера в систему
    @Test
    @DisplayName("Успешный вход курьера в систему")
    @Description("Проверка авторизации с корректными логином и паролем")
    public void shouldLoginCourierTest() {
        courierSteps
                .loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue());
    }

    // Тест входа без указания логина
    @Test
    @DisplayName("Ошибка при входе без логина")
    @Description("Проверка системы на отсутствие логина при авторизации")
    public void shouldNotLoginWithoutLoginTest() {
        courier.setLogin(null); // Обнуляем поле логина
        courierSteps
                .loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // Тест входа без указания пароля
    @Test
    @DisplayName("Ошибка при входе без пароля")
    @Description("Проверка системы на отсутствие пароля при авторизации")
    public void shouldNotLoginWithoutPasswordTest() {
        courier.setPassword(null); // Обнуляем поле пароля
        courierSteps
                .loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // Тест входа с некорректным (несуществующим) логином
    @Test
    @DisplayName("Ошибка при входе с неверным логином")
    @Description("Проверка системы на неверный логин при авторизации")
    public void shouldNotLoginWithIncorrectLoginTest() {
        courier.setLogin(faker.regexify("[a-z]{6}")); // Генерируем новый логин из 6 строчных букв
        courierSteps
                .loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // Тест входа с некорректным (неверным) паролем
    @Test
    @DisplayName("Ошибка при входе с неверным паролем")
    @Description("Проверка системы на неверный пароль при авторизации")
    public void shouldNotLoginWithIncorrectPasswordTest() {
        courier.setPassword(faker.regexify("[a-z][0-9]{6}")); // Генерируем пароль (1 строчная буква + 6 цифр)
        courierSteps
                .loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // Очистка данных после выполнения теста
    @After
    public void tearDown() {
        Integer id = null;
        try {
            // Авторизация, извлекаем ID курьера из ответа
            id = courierSteps.loginCourier(courier)
                    .extract().body().path("id");
        } catch (Exception e) { // В случае ошибки (неверный логин или пароль) — игнорируем
        }
        if (id !=null) {  // Если ID получен — удаляем курьера
            courier.setId(id);
            courierSteps.deleteCourier(courier);
        }
    }

}
