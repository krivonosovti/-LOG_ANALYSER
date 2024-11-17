package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import backend.academy.logAnalyzer.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTests {

    private List<LogRecord> logRecords;

    @BeforeEach
    void setUp() {
        logRecords = new ArrayList<>();
        logRecords.add(new LogRecord("192.168.0.1", "31/Oct/2024:12:00:00 +0000", "GET /index.html",200, 512));
        logRecords.add(new LogRecord("192.168.0.2", "01/Nov/2024:15:30:00 +0000", "GET /about.html", 404, 128));
        logRecords.add(new LogRecord("192.168.0.3", "02/Nov/2024:10:15:00 +0000", "GET /contact.html",500, 256));
    }

    @Test
    void testParseDate_ValidDate() {
        String dateStr = "2024-11-01";
        LocalDate expectedDate = LocalDate.of(2024, 11, 1);
        LocalDate parsedDate = DateUtils.parseDate(dateStr);
        assertEquals(expectedDate, parsedDate, "Parsed date should match the expected date");
    }

    @Test
    void testParseDate_InvalidDate() {
        String dateStr = "invalid-date";
        LocalDate parsedDate = DateUtils.parseDate(dateStr);
        assertNull(parsedDate, "Parsed date should be null for invalid date format");
    }

    @Test
    void testFilterRecordsByDate_NoFilter() {
        List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(logRecords, null, null);
        assertEquals(logRecords.size(), filteredRecords.size(), "All records should be returned when no date filter is applied");
    }

    @Test
    void testFilterRecordsByDate_ValidRange() {
        String from = "2024-10-31";
        String to = "2024-11-01";
        List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(logRecords, from, to);
        assertEquals(2, filteredRecords.size(), "Should return 2 records within the specified date range");
    }

    @Test
    void testFilterRecordsByDate_NoRecordsInRange() {
        String from = "2024-11-03";
        String to = "2024-11-05";
        List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(logRecords, from, to);
        assertTrue(filteredRecords.isEmpty(), "No records should be returned when no records fall within the date range");
    }

    @Test
    void testFilterRecordsByDate_OnlyFromDate() {
        String from = "2024-11-01";
        List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(logRecords, from, null);
        assertEquals(2, filteredRecords.size(), "Should return 1 record starting from the specified date");
    }

    @Test
    void testFilterRecordsByDate_OnlyToDate() {
        String to = "2024-11-01";
        List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(logRecords, null, to);
        assertEquals(2, filteredRecords.size(), "Should return 2 records up to the specified date");
    }
}
