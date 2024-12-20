package backend.academy.logAnalyzer.parser;

import backend.academy.logAnalyzer.Analyze;
import backend.academy.logAnalyzer.LogRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {

    String logPattern = "^" // Начало строки
        + "(?<remoteAddr>[^ ]+)"             // IP-адрес клиента или хост (не содержит пробелов)
        + " - "                              // Разделитель
        + "(?<remoteUser>[^ ]+)"             // Имя пользователя клиента (если есть), иначе "-"
        + " \\["                             // Открывающая скобка даты
        + "(?<dateLocal>[^:]+)"              // Локальная дата (до первого двоеточия)
        + "[^\\]]+"                          // Остальная часть даты (включая временную зону)
        + "\\] \\\""                         // Закрывающая скобка даты и открывающая кавычка запроса
        + "(?<requestType>[A-Z]+)"           // Тип HTTP-запроса (например, GET, POST)
        + " "                                // Пробел
        + "(?<requestPath>[^ ]+)"            // Запрашиваемый путь (не содержит пробелов)
        + " "                                // Пробел
        + "(?<requestProtocol>[^\\\"]+)"     // Протокол HTTP (например, HTTP/1.1)
        + "\\\" "                            // Закрывающая кавычка протокола и пробел
        + "(?<status>[1-5]\\d{2})"           // HTTP-статус (трёхзначное число, от 100 до 599)
        + " "                                // Пробел
        + "(?<bodyBytesSent>\\d+)"           // Количество отправленных байт (число)
        + " \\\""                            // Пробел и открывающая кавычка для Referer
        + "(?<httpReferer>[^\\\"]+)"         // Поле Referer (URL или "-")
        + "\\\" \\\""                        // Закрывающая кавычка Referer и открывающая кавычка User-Agent
        + "(?<httpUserAgent>[^\\\"]+)"       // Поле User-Agent (информация о клиенте)
        + "\\\"$";                           // Закрывающая кавычка User-Agent и конец строки

    public LineParser() {}

    public static void addRecord(BufferedReader reader, Analyze statistic) throws IOException {
        LineParser lineParser = new LineParser();
        lineParser.add(reader, statistic);
    }

    private void add(BufferedReader reader, Analyze statistic) throws IOException {
        String line;
        Optional<LogRecord> optLogRecord;

        while ((line = reader.readLine()) != null) {
                optLogRecord = readRecord(line);
                optLogRecord.ifPresent(statistic::addRecord);
        }
    }

    private Optional<LogRecord> readRecord(String recordLine) {
        Pattern pattern = Pattern.compile(logPattern);
        Matcher matcher = pattern.matcher(recordLine);
        if (matcher.find()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
            LocalDate dateLocal = LocalDate.parse(matcher.group("dateLocal"), formatter);
            return Optional.of(new LogRecord(
                matcher.group("remoteAddr"),
                dateLocal,
                matcher.group("requestPath"),
                Integer.parseInt(matcher.group("status")),
                Long.parseLong(matcher.group("bodyBytesSent"))));
        }
        return Optional.empty();
    }

    public static Optional<LogRecord> parseLogLine(String recordLine) {
        LineParser parser = new LineParser();
        return parser.readRecord(recordLine);
    }
}
