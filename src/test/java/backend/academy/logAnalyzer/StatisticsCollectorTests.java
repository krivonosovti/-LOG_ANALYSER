package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsCollectorTests {
    private StatisticsCollector collector;

    @BeforeEach
    void setUp() {
        collector = new StatisticsCollector();
    }

    @Test
    void testCollectStatistics() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /index.html", 200, 300),
            new LogRecord("192.168.1.3", "01/Jan/2021", "GET /about.html", 404, 150),
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /contact.html", 500, 1000),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /about.html", 404, 200)
        );

        // Сбор статистики
        collector.collectStatistics(records, "2021-01-01", "2021-01-31");

        // Проверка общего количества запросов
        assertEquals(5, collector.getTotalRequests());

        // Проверка значений фильтров
        assertEquals("2021-01-01", collector.getFromFilter());
        assertEquals("2021-01-31", collector.getToFilter());
    }

    @Test
    void testGetTotalRequests() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500)
        );

        collector.collectStatistics(records, null, null);

        // Проверка общего количества запросов
        assertEquals(1, collector.getTotalRequests());
    }

    @Test
    void testGetMostRequestedResources() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /index.html", 200, 300),
            new LogRecord("192.168.1.3", "01/Jan/2021", "GET /about.html", 404, 150)
        );

        collector.collectStatistics(records, null, null);

        // Проверка наиболее запрашиваемых ресурсов
        List<Map.Entry<String, Integer>> mostRequested = collector.getMostRequestedResources(2);
        assertEquals(2, mostRequested.size());
        assertEquals("GET /index.html", mostRequested.get(0).getKey());
        assertEquals(2, mostRequested.get(0).getValue());
    }

    @Test
    void testGetMostFrequentStatusCodes() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /index.html", 200, 300),
            new LogRecord("192.168.1.3", "01/Jan/2021", "GET /about.html", 404, 150),
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /contact.html", 500, 1000)
        );

        collector.collectStatistics(records, null, null);

        // Проверка наиболее частых кодов ответа
        List<Map.Entry<Integer, Integer>> frequentStatusCodes = collector.getMostFrequentStatusCodes(3);
        assertEquals(3, frequentStatusCodes.size());
        assertEquals(200, frequentStatusCodes.get(0).getKey());
        assertEquals(2, frequentStatusCodes.get(0).getValue());
    }

    @Test
    void testGetAverageResponseSize() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /index.html", 200, 300)
        );

        collector.collectStatistics(records, null, null);

        // Проверка среднего размера ответа
        assertEquals(400.0, collector.getAverageResponseSize(), 0.01);
    }

    @Test
    void testGet95thPercentileResponseSize() {
        // Создаем тестовые данные
        List<LogRecord> records = Arrays.asList(
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /index.html", 200, 500),
            new LogRecord("192.168.1.2", "01/Jan/2021", "GET /index.html", 200, 300),
            new LogRecord("192.168.1.3", "01/Jan/2021", "GET /about.html", 404, 150),
            new LogRecord("192.168.1.1", "01/Jan/2021", "GET /contact.html", 500, 1000)
        );

        collector.collectStatistics(records, null, null);

        // Проверка 95-го перцентиля размера ответа
        assertEquals(1000.0, collector.get95thPercentileResponseSize(), 0.01);
    }

    @Test
    void testGetFromFilter() {
        // Проверка значения фильтра
        collector.collectStatistics(Arrays.asList(), "2021-01-01", "2021-01-31");
        assertEquals("2021-01-01", collector.getFromFilter());
    }

    @Test
    void testGetToFilter() {
        // Проверка значения фильтра
        collector.collectStatistics(Arrays.asList(), "2021-01-01", "2021-01-31");
        assertEquals("2021-01-31", collector.getToFilter());
    }

    @Test
    void testGetAverageResponseSizeNoRequests() {
        // Проверка, когда запросов нет
        assertEquals(0.0, collector.getAverageResponseSize(), 0.01);
    }

    @Test
    void testGet95thPercentileResponseSizeNoResponses() {
        // Проверка, когда ответов нет
        assertEquals(0.0, collector.get95thPercentileResponseSize(), 0.01);
    }
}
