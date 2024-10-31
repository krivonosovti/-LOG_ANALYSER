package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor") public class LogParser {
    private static final Logger LOGGER = Logger.getLogger(LogAnalyzer.class.getName());

    //CHECKSTYLE:OFF
    private static final String LOG_PATTERN =
            // Начало строки
            "^(\\S+) "
                    // Группа 1: IP-адрес клиента (один или несколько непробельных символов)
                   + "- (\\S+) "
                    // Группа 2: Идентификатор пользователя (если отсутствует, заменено на "-")
                   + "\\[(.*?)\\] "
                    // Группа 3: Дата и время запроса, заключенные в квадратные скобки
                   + "\"(.*?)\" "
                    // Группа 4: Строка запроса, например, "GET /index.html HTTP/1.1"
                   + "(\\d{3}) "
                    // Группа 5: HTTP-код ответа из трех цифр
                   + "(\\d+) "
                    // Группа 6: Размер ответа в байтах (одно или несколько чисел)
                   + "\"(.*?)\" "
                    // Группа 7: Заголовок Referer (если присутствует), заключенный в кавычки
                   + "\"(.*?)\""
                    // Группа 8: Заголовок User-Agent (если присутствует), заключенный в кавычки
                   + "$";
    // Конец строки

    private static final Pattern PATTERN = Pattern.compile(LOG_PATTERN);

    // Метод для парсинга строки лога в объект LogRecord
    public static LogRecord parseLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            try {
                return new LogRecord(
                    matcher.group(1),  // IP адрес
                    matcher.group(3),  // Время
                    matcher.group(4),  // Запрос
                    Integer.parseInt(matcher.group(5)),  // Код ответа
                    Integer.parseInt(matcher.group(6))   // Размер ответа
                );
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing line: " + line);
            }
        }
        return null;
    }
    //CHECKSTYLE:ON
}
