package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogRecordTests {
    private LogRecord logRecord;

    @BeforeEach
    void setUp() {
        logRecord = new LogRecord(
            "192.168.0.1",
            "2024-11-01T10:15:30",
            "GET /index.html HTTP/1.1",
            200,
            512
        );
    }

    @Test
    void testGetIpAddress() {
        assertEquals("192.168.0.1", logRecord.getIpAddress(), "IP адрес должен совпадать");
    }

    @Test
    void testGetTimestamp() {
        assertEquals("2024-11-01T10:15:30", logRecord.getTimestamp(), "Время запроса должно совпадать");
    }

    @Test
    void testGetRequest() {
        assertEquals("GET /index.html HTTP/1.1", logRecord.getRequest(), "Запрос должен совпадать");
    }

    @Test
    void testGetStatusCode() {
        assertEquals(200, logRecord.getStatusCode(), "Код статуса должен совпадать");
    }

    @Test
    void testGetResponseSize() {
        assertEquals(512, logRecord.getResponseSize(), "Размер ответа должен совпадать");
    }
}


