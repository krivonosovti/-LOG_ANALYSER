package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogParserTests {

    @Test
    void testParseValidLogLine1() {
        String logLine = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";
        LogRecord expected = new LogRecord("93.180.71.3", "17/May/2015:08:05:32 +0000", "GET /downloads/product_1 HTTP/1.1", 304, 0);
        LogRecord actual = LogParser.parseLine(logLine);
        assertEquals(expected, actual, "Parsed LogRecord should match the expected values for logLine 1");
    }

    // Тест на некорректный формат строки (неверный лог)
    @Test
    void testParseInvalidLogLineFormat() {
        String invalidLogLine = "Invalid log line format";
        LogRecord actual = LogParser.parseLine(invalidLogLine);
        assertNull(actual, "Parsing an invalid log line format should return null");
    }

    // Тест на строку с отсутствующими полями
    @Test
    void testParseLogLineWithMissingFields() {
        String logLine = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304";
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing a log line with missing fields should return null");
    }

    // Тест на строку с неверным форматом IP-адреса
    @Test
    void testParseLogLineWithInvalidIPAddress() {
        String logLine = "invalid_ip - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"UserAgent\"";
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing a log line with an invalid IP address should return null");
    }

    // Тест на строку с некорректным кодом ответа (например, текст вместо числа)
    @Test
    void testParseLogLineWithInvalidStatusCode() {
        String logLine = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" ABC 0 \"-\" \"UserAgent\"";
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing a log line with an invalid status code should return null");
    }

    // Тест на строку с некорректным размером ответа (например, текст вместо числа)
    @Test
    void testParseLogLineWithInvalidResponseSize() {
        String logLine = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 XYZ \"-\" \"UserAgent\"";
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing a log line with an invalid response size should return null");
    }

    // Тест на корректную строку с дополнительными пробелами
    @Test
    void testParseLogLineWithExtraSpaces() {
        String logLine = " 93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\" ";
        LogRecord expected = new LogRecord("93.180.71.3", "17/May/2015:08:05:32 +0000", "GET /downloads/product_1 HTTP/1.1", 304, 0);
        LogRecord actual = LogParser.parseLine(logLine);
        assertEquals(expected, actual, "Parsed LogRecord should handle extra spaces and match the expected values");
    }

    // Тест на пустую строку
    @Test
    void testParseEmptyLogLine() {
        String logLine = "";
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing an empty log line should return null");
    }

    // Тест на строку, содержащую null
    @Test
    void testParseNullLogLine() {
        String logLine = null;
        LogRecord actual = LogParser.parseLine(logLine);
        assertNull(actual, "Parsing a null log line should return null");
    }
}
