import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.Order;
import ru.yandex.praktikum.OrderSteps;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.hamcrest.CoreMatchers.notNullValue;

// Параметризованный тест для проверки создания заказа с различными цветами самокатов
@RunWith(Parameterized.class)
@Epic("Работа с заказами")
@Feature("Создание заказа")
public class CreateOrderParameterizedTest extends BaseTest {

    private OrderSteps orderSteps = new OrderSteps(); // Клиент API для работы с заказами
    private Order order; // Объект заказа, для отправления в API
    private final String[] color; // Параметр теста — массив цветов самоката

    Faker faker = new Faker(new Locale("ru")); // Генератор тестовых данных на русском языке

    public CreateOrderParameterizedTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() { // Передаем в объект Order случайные валидные значения
        order = new Order();
        order.setFirstName(faker.name().firstName()); // Имя
        order.setLastName(faker.name().lastName()); // Фамилия
        order.setAddress(faker.address().streetAddress()); // Адрес
        order.setMetroStation(String.valueOf(ThreadLocalRandom.current().nextInt(1,10))); // станцию метро
        order.setPhone(faker.regexify("+7[0-9]{11}")); // номер телефона
        order.setRentTime(ThreadLocalRandom.current().nextInt(1, 14)); // Срок аренды
        order.setDeliveryData(faker.date().future(14, DAYS, "YYYY-MM-DD")); // Дату доставки
        order.setComment(faker.regexify("[a-я][A-Я]{100}")); // Комментарии
    }

    @Parameterized.Parameters(name = "color: {0}")
    public static Object[][] getOrderData() {
        return new Object[][]{
                {new String[]{"BLACK"}}, // черный цвет
                {new String[]{"GREY"}}, // серый цвет
                {new String[]{"BLACK", "GREY"}}, // оба варианта цвета
                {new String[]{""}} // пустая строка
        };
    }

    // Тест - проверяем создание заказа с заданным цветом самоката
    @Test
    @DisplayName("Проверка создания заказа с разными цветами самоката")
    @Description("Проверка создания заказа с выбором цвета самоката")
    public void sholdCreateOrder() {
        order.setColor(color); // Устанавливаем параметр color в объект заказа
        orderSteps // Отправляем запрос на создание заказа и проверяем ответ
                .createOrder(order)
                .statusCode(201)
                .body("track", notNullValue());
    }

    // Очистка данных после выполнения теста
    @After
    public void tearDown() {
        Integer track = null;
        try { // Получаем track-номер из ответа на создание заказа
            track = orderSteps
                    .createOrder(order)
                    .extract().body().path("track");
        } catch (Exception ignored) {
        }
        if (track != null) {  // Если track получен, отправляем запрос на отмену заказа
            order.setTrack(track);
            orderSteps.cancelOrder(order);
        }
    }
}

