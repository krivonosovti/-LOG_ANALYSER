package backend.academy.logAnalyzer.utils;

import backend.academy.logAnalyzer.LogAnalyzer;
import backend.academy.logAnalyzer.model.LogRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Logger LOGGER = Logger.getLogger(LogAnalyzer.class.getName());
    // Формат даты "yyyy-MM-dd" для парсинга входящих дат
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Метод для парсинга строки в LocalDate
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.SEVERE, "Invalid date format: " + dateStr);
            return null;
        }
    }

    // Фильтрация записей по датам (от и до)
    public static List<LogRecord> filterRecordsByDate(List<LogRecord> records, String from, String to) {
        LocalDate fromDate = from != null ? parseDate(from) : null;
        LocalDate toDate = to != null ? parseDate(to) : null;

        return records.stream()
            .filter(logRecord -> {
                LocalDate recordDate = parseLogDate(logRecord.getTimestamp());
                // Фильтрация по диапазону дат
                if (recordDate == null) {
                    return false;
                }
                boolean afterFrom = fromDate == null || !recordDate.isBefore(fromDate);
                boolean beforeTo = toDate == null || !recordDate.isAfter(toDate);
                return afterFrom && beforeTo;
            })
            .collect(Collectors.toList());
    }

    // Метод для парсинга временной метки из строки лога (например, "[31/Aug/2024:15:30:00 +0000]")
    private static LocalDate parseLogDate(String logDate) {
        try {
            // Формат даты лога: "31/Aug/2024:15:30:00 +0000"
            DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            return LocalDate.from(LocalDateTime.parse(logDate, logFormatter));
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.SEVERE, "Invalid log date format: " + logDate);
            return null;
        }
    }
}
