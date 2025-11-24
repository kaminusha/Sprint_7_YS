import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.Order;
import ru.yandex.praktikum.OrderSteps;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import static org.hamcrest.CoreMatchers.notNullValue;
import static java.util.concurrent.TimeUnit.DAYS;

// Тестируем классы для проверки получения списка заказов в систему
@Epic("Работа с заказами")
@Feature("Получение списка заказов")
public class GetListOfOrdersTest extends BaseTest {

    private OrderSteps orderSteps = new OrderSteps(); // Экземпляр класса для выполнения (API) по работе с заказами
    private Order order; // Объект заказа, который используем в тестах

    Faker faker = new Faker(new Locale("ru")); //  Генератор тестовых данных с локализацией на русский язык

    // Создаем объект заказа с тестовыми данными
    @Before
    public void setUp() {
        order = new Order();
        order.setFirstName(faker.name().firstName()); // Имя
        order.setLastName(faker.name().lastName()); // Фамилия
        order.setAddress(faker.address().streetAddress()); // Адрес
        order.setMetroStation(String.valueOf(ThreadLocalRandom.current().nextInt(1, 10))); // Метро
        order.setPhone(faker.regexify("\\+7[0-9]{11}")); // Телефон
        order.setRentTime(ThreadLocalRandom.current().nextInt(1, 14)); // Аренда
        order.setDeliveryData(faker.date().future(14, DAYS, "YYYY-MM-DD")); // Дата доставки
        order.setComment(faker.regexify("[a-я][A-Я]{100}")); // комментарии
    }

    // Тест получения списка заказов
    @Test
    @DisplayName("Получения списка заказов с созданным заказом")
    @Description("Проверяем, что система возвращает в тело ответа - список заказов")
    public void shouldReturnListOfOrders() {
        orderSteps.createOrder(order); // создаём заказ
        orderSteps.getListOfOrders() // получаем список заказов и проверяем ответ
                .statusCode(200)
                .body("orders", notNullValue());
    }

    // Очистка данных после выполнения теста
    @After
    public void tearDown () {
        try {
            Integer track = orderSteps // извлекаем трек‑номер из списка заказов
                    .getListOfOrders()
                    .extract().body().path("track");
            if (track != null) { // Если трек‑номер получен — отменяем заказ
                order.setTrack(track);
                orderSteps.cancelOrder(order);
            }
        } catch (Exception ignored) { // Игнорируем любые исключения (если заказ уже удалён)
        }
    }
}
