import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import org.junit.Before;
import ru.yandex.praktikum.util.Url;


public class BaseTest { // Базовый тестовый класс, содержит общую конфигурацию для всех тестов API

    @Before
    public void startUp() { // Настройка спецификации запроса
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(Url.WEBSITE) // Устанавливаем базовый URL API
                .setContentType(ContentType.JSON) // Указываем, что контент — JSON
                .build();
        RestAssured.config = RestAssured // Настройка логирования
                .config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()); // Включаем логирование только при ошибках валидации
    }
}
