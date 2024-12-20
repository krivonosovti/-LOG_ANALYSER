package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.parser.LineParser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalyzeTest {
    Analyze analyze = new Analyze(null, null);
    Analyze analyzeWithDate = new Analyze(LocalDate.of(2015,1,1),
        LocalDate.of(2018,1,1));
    List<LogRecord> records = new ArrayList<>();

    @BeforeEach
    void setRecords() {
        String[] logRecords = {"54.84.255.104 - - [20/May/2010:07:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 100 333 \"-\" \"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"\n",
            "172.29.141.101 - - [20/May/2012:07:05:54 +0000] \"GET /downloads/product_2 HTTP/1.1\" 404 444 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"\n",
            "78.108.122.1 - - [20/May/2015:08:05:41 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 555 \"-\" \"Chef Knife/11.10.4 (ruby-1.9.3-p484; ohai-6.20.0; x86_64-linux; +http://opscode.com)\"\n",
            "184.73.132.8 - - [20/May/2017:08:05:30 +0000] \"GET /downloads/product_1 HTTP/1.1\" 300 666 \"-\" \"Chef Client/11.4.4 (ruby-1.9.3-p286; ohai-6.16.0; x86_64-linux; +http://opscode.com)\"\n",
            "148.251.2.47 - - [20/May/2019:08:05:15 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 777 \"-\" \"Wget/1.13.4 (linux-gnu)\""};
        for (String log : logRecords) {
            Optional<LogRecord> opt = LineParser.parseLogLine(log);
            opt.ifPresent(records::add);
        }
    }

    void analyzeRecords(Analyze logAnalyze) {
        for (LogRecord log : records) {
            logAnalyze.addRecord(log);
        }
    }

    @Test
    public void testAddRecords_WithoutDate() {
        analyzeRecords(analyze);
        assertEquals(5, analyze.getRequestsCount());
    }

    @Test
    public void testAddRecords_WithDate() {
        analyzeRecords(analyzeWithDate);
        assertEquals(2, analyzeWithDate.getRequestsCount());
    }

    @Test
    public void testCheckDate_WithoutDate() {
        assertTrue(analyze.dataFilter(records.get(0)));
        assertTrue(analyze.dataFilter(records.get(1)));
        assertTrue(analyze.dataFilter(records.get(2)));
        assertTrue(analyze.dataFilter(records.get(3)));
        assertTrue(analyze.dataFilter(records.get(4)));
    }

    @Test
    public void testCheckDate_WithDate() {
        assertFalse(analyzeWithDate.dataFilter(records.get(0)));
        assertFalse(analyzeWithDate.dataFilter(records.get(1)));
        assertTrue(analyzeWithDate.dataFilter(records.get(2)));
        assertTrue(analyzeWithDate.dataFilter(records.get(3)));
        assertFalse(analyzeWithDate.dataFilter(records.get(4)));
    }

    @Test
    public void testFinishAnalyze_ValidAnalyze() {
        analyzeRecords(analyze);
        analyze.calculateStatistic();
        assertEquals(555, analyze.getAverageResponseSize());
        assertEquals(555, analyze.getPercentile50());
        assertEquals(777, analyze.getPercentile95());
    }

    @Test
    public void testFinishAnalyze_EmptyToAnalyze() {
        analyze.calculateStatistic();
        assertEquals(0, analyze.getAverageResponseSize());
        assertEquals(0, analyze.getPercentile50());
        assertEquals(0, analyze.getPercentile95());
    }

    @Test
    public void testGetPercentile_ValidPercentile() {
        analyzeRecords(analyze);
        assertEquals(555, analyze.getPercentile(0.5));
        assertEquals(333, analyze.getPercentile(0.2));
        assertEquals(333, analyze.getPercentile(0));
        assertEquals(777, analyze.getPercentile(1));
    }

    @Test
    public void testGetPercentile_OutOfBoundsPercentile() {
        analyzeRecords(analyze);
        assertEquals(0, analyze.getPercentile(-0.1));
        assertEquals(0, analyze.getPercentile(1.1));
    }

    @Test
    public void testGetPercentile_EmptyAnalyze() {
        assertEquals(0, analyze.getPercentile(0.5));
    }

    @Test
    void testCalculateStatistics() {
        // Создаем объект Analyze с произвольным временным диапазоном
        Analyze analyze = new Analyze(null, null);

        // Добавляем записи
        analyze.addRecord(new LogRecord("127.0.0.1", LocalDate.of(2024, 10, 12), "/index.html", 200, 1024));
        analyze.addRecord(new LogRecord("192.168.0.1", LocalDate.of(2024, 10, 13), "/api/data", 201, 2048));
        analyze.addRecord(new LogRecord("127.0.0.1", LocalDate.of(2024, 10, 14), "/home", 404, 512));

        // Рассчитываем статистику
        analyze.calculateStatistic();

        // Проверка общего количества запросов
        assertEquals(3, analyze.getRequestsCount(), "Количество запросов должно быть 3");

        // Проверка среднего размера ответа
        long expectedAverage = (1024 + 2048 + 512) / 3;
        assertEquals(expectedAverage, analyze.getAverageResponseSize(), "Средний размер ответа рассчитан неверно");

        // Проверка 50-го перцентиля
        assertEquals(1024, analyze.getPercentile50(), "50-й перцентиль рассчитан неверно");

        // Проверка 95-го перцентиля
        assertEquals(2048, analyze.getPercentile95(), "95-й перцентиль рассчитан неверно");

        // Проверка распределения ресурсов
        assertEquals(1, analyze.getResourcesAmount().get("/index.html"), "Запросы к /index.html должны быть равны 1");
        assertEquals(1, analyze.getResourcesAmount().get("/api/data"), "Запросы к /api/data должны быть равны 1");
        assertEquals(1, analyze.getResourcesAmount().get("/home"), "Запросы к /home должны быть равны 1");

        // Проверка распределения кодов ответа
        assertEquals(1, analyze.getCodesAmount().get(200), "Код 200 должен встречаться 1 раз");
        assertEquals(1, analyze.getCodesAmount().get(201), "Код 201 должен встречаться 1 раз");
        assertEquals(1, analyze.getCodesAmount().get(404), "Код 404 должен встречаться 1 раз");
    }

    @Test
    void testEmptyAnalyze() {
        // Создаем пустой объект Analyze
        Analyze analyze = new Analyze(null, null);

        // Рассчитываем статистику
        analyze.calculateStatistic();

        // Проверки
        assertEquals(0, analyze.getRequestsCount(), "Количество запросов должно быть 0");
        assertEquals(0, analyze.getAverageResponseSize(), "Средний размер ответа должен быть 0");
        assertEquals(0, analyze.getPercentile50(), "50-й перцентиль должен быть 0");
        assertEquals(0, analyze.getPercentile95(), "95-й перцентиль должен быть 0");
    }

    @Test
    void testFilterByDateRange() {
        // Создаем объект Analyze с диапазоном дат
        Analyze analyze = new Analyze(LocalDate.of(2024, 10, 12), LocalDate.of(2024, 10, 14));

        // Добавляем записи
        analyze.addRecord(new LogRecord("127.0.0.1", LocalDate.of(2024, 10, 11), "/old", 200, 1024)); // Вне диапазона
        analyze.addRecord(new LogRecord("127.0.0.1", LocalDate.of(2024, 10, 12), "/index.html", 200, 1024)); // В диапазоне
        analyze.addRecord(new LogRecord("192.168.0.1", LocalDate.of(2024, 10, 13), "/api/data", 201, 2048)); // В диапазоне
        analyze.addRecord(new LogRecord("127.0.0.1", LocalDate.of(2024, 10, 14), "/home", 404, 512)); // Вне диапазона

        // Рассчитываем статистику
        analyze.calculateStatistic();

        // Проверка общего количества запросов
        assertEquals(2, analyze.getRequestsCount(), "Должно быть 2 запроса в заданном диапазоне");

        // Проверка среднего размера ответа
        long expectedAverage = (1024 + 2048) / 2;
        assertEquals(expectedAverage, analyze.getAverageResponseSize(), "Средний размер ответа рассчитан неверно");
    }
}
