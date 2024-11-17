package backend.academy.logAnalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogReport {
    private static final Logger LOGGER = Logger.getLogger(LogAnalyzer.class.getName());

    //CHECKSTYLE:OFF
    public void generateReport(StatisticsCollector collector, String format, PrintStream printStream) {
        StringBuilder report = new StringBuilder();

        // Генерация заголовка
        report.append("#### Общая информация\n\n");
        report.append("|        Метрика        |     Значение |\n");
        report.append("|:---------------------:|-------------:|\n");
        report.append("|       Файл(-ы)        | `access.log` |\n");
        report.append("|    Начальная дата     | ").append(collector.getFromFilter()).append(" |\n");
        report.append("|     Конечная дата     | ").append(collector.getToFilter()).append(" |\n");
        report.append("|  Количество запросов  | ").append(collector.getTotalRequests()).append(" |\n");
        report.append("| Средний размер ответа  | ").append(collector.getAverageResponseSize()).append("b |\n");
        report.append("|   95p размера ответа   | ").append(collector.get95thPercentileResponseSize())
            .append("b |\n\n");

        // Генерация статистики по ресурсам
        report.append("#### Запрашиваемые ресурсы\n\n");
        report.append("|     Ресурс      | Количество |\n");
        report.append("|:---------------:|-----------:|\n");

        for (Map.Entry<String, Integer> entry : collector.getMostRequestedResources(10)) {
            report.append("| ").append(entry.getKey()).append(" | ").append(entry.getValue()).append(" |\n");
        }

        // Генерация статистики по кодам ответа
        report.append("\n#### Коды ответа\n\n");
        report.append("| Код |          Имя          | Количество |\n");
        report.append("|:---:|:---------------------:|-----------:|\n");

        for (Map.Entry<Integer, Integer> entry : collector.getMostFrequentStatusCodes(10)) {
            report.append("| ").append(entry.getKey()).append(" | ").append(getStatusName(entry.getKey()))
                .append(" | ").append(entry.getValue()).append(" |\n");
        }

        // Запись отчета в файл
        writeReportToFile(report.toString(), format, printStream);
    }

    // Получение имени статуса по коду ответа
    /**
     * Возвращает текстовое описание для указанного HTTP-кода состояния.
     *
     * @param statusCode HTTP-код состояния
     * @return текстовое описание состояния или "Unknown", если код неизвестен
     */
    private String getStatusName(int statusCode) {
        return switch (statusCode) {
            case 100 -> "Continue";
            case 101 -> "Switching Protocols";
            case 200 -> "OK";
            case 201 -> "Created";
            case 202 -> "Accepted";
            case 203 -> "Non-Authoritative Information";
            case 204 -> "No Content";
            case 205 -> "Reset Content";
            case 206 -> "Partial Content";
            case 300 -> "Multiple Choices";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 303 -> "See Other";
            case 304 -> "Not Modified";
            case 307 -> "Temporary Redirect";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 408 -> "Request Timeout";
            case 409 -> "Conflict";
            case 410 -> "Gone";
            case 500 -> "Internal Server Error";
            case 501 -> "Not Implemented";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case 504 -> "Gateway Timeout";
            case 505 -> "HTTP Version Not Supported";
            default -> "Unknown"; // Неизвестный или неподдерживаемый код
        };
    }

    //CHECKSTYLE:ON

    // Метод для записи отчета в файл
    private void writeReportToFile(String reportContent, String format, PrintStream printStream) {
        String fileName = "log_report." + format;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(reportContent);
            printStream.println("Report generated: " + fileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing report to file: " + e.getMessage(), e);
        }
    }
}
