package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

    @ExtendWith(MockitoExtension.class)
    class LogFileReaderTests {

        @Test
        void testReadLogsFromLocalFile() throws IOException {

            LogFileReader logFileReader = new LogFileReader();

            // Указываем путь к файлу
            String path = "src/test/resources/logAnalyzer/dummyFile.txt";

            List<LogRecord> actualLogs = logFileReader.readLogs(path);

            // Подготавливаем ожидаемые данные
            List<LogRecord> expectedLogs = new ArrayList<>();
            expectedLogs.add(new LogRecord("93.180.71.3", "17/May/2015:08:05:32 +0000", "GET /downloads/product_1 HTTP/1.1", 304, 0));
            expectedLogs.add(new LogRecord("79.136.114.202", "04/Jun/2015:07:06:35 +0000", "GET /downloads/product_1 HTTP/1.1", 404, 334));
            expectedLogs.add(new LogRecord("54.205.82.23", "04/Jun/2015:00:06:45 +0000", "GET /downloads/product_2 HTTP/1.1", 200, 17600009));

            // Проверяем результат
            assertEquals(expectedLogs, actualLogs);
        }
    }


//    @Test
//    void testReadLogsFromUrl() throws IOException {
//        // Запускаем mock сервер и добавляем в ответ строку логов
//        mockWebServer.enqueue(new MockResponse()
//            .setBody("127.0.0.1 - - [01/Nov/2024:10:00:00 +0000] \"GET /index.html HTTP/1.1\" 200 512\n")
//            .setResponseCode(200));
//        mockWebServer.start();
//
//        String url = mockWebServer.url("/test_log").toString();
//
//        // Мокаем LogParser для разбора строки
//        LogRecord mockRecord = mock(LogRecord.class);
//        LogParser logParser = mock(LogParser.class);
//        when(logParser.parseLine("127.0.0.1 - - [01/Nov/2024:10:00:00 +0000] \"GET /index.html HTTP/1.1\" 200 512")).thenReturn(mockRecord);
//
//        List<LogRecord> records = logFileReader.readLogs(url);
//
//        assertEquals(1, records.size(), "Должна быть одна запись из URL");
//        assertEquals(mockRecord, records.get(0), "Запись должна соответствовать ожидаемой");
//    }

