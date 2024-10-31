package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static backend.academy.logAnalyzer.utils.DateUtils.parseDate;

public class StatisticsCollector {

    private final static double PERCENTILE = 0.95;
    private String fromFilter;
    private String toFilter;
    private int totalRequests;
    private final Map<String, Integer> resourceCount = new HashMap<>();
    private final Map<Integer, Integer> statusCodeCount = new HashMap<>();
    private int totalResponseSize;
    private List<Integer> responseSizes = new ArrayList<>();

    // Метод для сбора статистики
    public void collectStatistics(List<LogRecord> records, String from, String to) {
        totalRequests = records.size();
        LocalDate fromDate = from != null ? parseDate(from) : null;
        LocalDate toDate = to != null ? parseDate(to) : null;

        fromFilter = fromDate != null ? fromDate.toString() : "-";
        toFilter = toDate != null ? toDate.toString() : "-";

        for (LogRecord logRecord : records) {
            // Подсчет запросов по ресурсам
            resourceCount.merge(logRecord.getRequest(), 1, Integer::sum);
            // Подсчет кодов ответа
            statusCodeCount.merge(logRecord.getStatusCode(), 1, Integer::sum);
            // Суммирование размера ответов
            totalResponseSize += logRecord.getResponseSize();
            responseSizes.add(logRecord.getResponseSize());
        }
    }

    // Получение общего количества запросов
    public int getTotalRequests() {
        return totalRequests;
    }

    // Получение наиболее часто запрашиваемых ресурсов
    public List<Map.Entry<String, Integer>> getMostRequestedResources(int limit) {
        return resourceCount.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Получение наиболее часто встречающихся кодов ответа
    public List<Map.Entry<Integer, Integer>> getMostFrequentStatusCodes(int limit) {
        return statusCodeCount.entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    // Получение среднего размера ответа
    public double getAverageResponseSize() {
        return totalRequests > 0 ? (double) totalResponseSize / totalRequests : 0;
    }

    // Получение 95-го перцентиля размера ответа
    public double get95thPercentileResponseSize() {
        if (responseSizes.isEmpty()) {
            return 0;
        }
        Collections.sort(responseSizes);
        int index = (int) Math.ceil(PERCENTILE * responseSizes.size()) - 1;
        return responseSizes.get(index);
    }

    public String getFromFilter() {
        return fromFilter;
    }

    public String getToFilter() {
        return toFilter;
    }
}
