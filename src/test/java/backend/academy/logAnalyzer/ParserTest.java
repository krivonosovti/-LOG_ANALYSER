package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.parser.LineParser;
import backend.academy.logAnalyzer.parser.Parser;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import javax.sound.sampled.Line;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ParserTest {

    Analyze analyze;
    @Test
    void testReadDummyFile() {
        // Путь к файлу dummyFile.txt в ресурсах
        String filePath = "src/test/resources/logAnalyzer/LOG/logs_5.txt"; //.txt

        try {
            // Чтение логов
            analyze = Parser.getLineParser(filePath, null, null);
            analyze.calculateStatistic();

            // Проверки
            assertNotNull(analyze, "Analyze объект не должен быть null");
            assertEquals(11, analyze.getRequestsCount(), "Должно быть 5 запроса");
            assertEquals(3674, analyze.getAverageResponseSize() *11, "Средний размер ответа должен быть корректным");

        } catch (Exception e) {
            fail("Тестирование чтения файла завершилось ошибкой: " + e.getMessage());
        }
    }

    @Test
    void testReadDummyFiles() {
        // Путь к файлу dummyFile.txt в ресурсах
        String filePath = "src/test/resources/logAnalyzer/LOG/logs/*";

        try {
            // Чтение логов
            analyze = Parser.getLineParser(filePath, null, null);
            analyze.calculateStatistic();

            // Проверки
            assertNotNull(analyze, "Analyze объект не должен быть null");
            assertEquals(10, analyze.getRequestsCount(), "Должно быть 5 запроса");
            assertEquals(2330, analyze.getAverageResponseSize() *10, "Средний размер ответа должен быть корректным");

        } catch (Exception e) {
            fail("Тестирование чтения файла завершилось ошибкой: " + e.getMessage());
        }
    }

    @Test
    void testEmptyDummyFile() {
        // Путь к пустому файлу dummyFile.txt
        String filePath = "src/test/resources/logAnalyzer/emptyDummyFile";

        try {
            // Чтение логов
            analyze = Parser.getLineParser(filePath, null, null);

            // Проверки
            assertNotNull(analyze, "Analyze объект должен быть создан даже для пустого файла");
            assertEquals(0, analyze.getRequestsCount(), "Для пустого файла количество запросов должно быть 0");

        } catch (Exception e) {
            fail("Тестирование пустого файла завершилось ошибкой: " + e.getMessage());
        }
    }

    @Test
    void testReadURL() throws IOException {
//        // Эмуляция URL с логами (например, через WireMock)
//        String url = "http://localhost:8080/test_logs";
//        WireMockServer wireMockServer = new WireMockServer(8080);
//        wireMockServer.start();
//        wireMockServer.stubFor(WireMock.get("/test_logs")
//            .willReturn(WireMock.aResponse()
//                .withBody("127.0.0.1 - - [12/Oct/2024:13:45:30 +0000] \"GET /index.html HTTP/1.1\" 200 1024\n" +
//                    "127.0.0.1 - - [13/Oct/2024:13:45:30 +0000] \"POST /api/data HTTP/1.1\" 201 2048")));

        String url = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";  // Чтение логов
        analyze = Parser.getLineParser(url, null, null);
        analyze.calculateStatistic();

        // Проверки
        assertNotNull(analyze, "Analyze объект не должен быть null");
        assertEquals(51462, analyze.getRequestsCount(), "Должно быть 2 запроса");
        assertEquals(659509, analyze.getAverageResponseSize(), "Средний размер ответа должен быть корректным");

//        // Останавливаем сервер
//        wireMockServer.stop();
    }
    @Test
    public void testGetLogRecords_InvalidPathURL() throws IOException {
        String path = "http://InvalidPathAbsolytnoTochno";
        assertThrows(IOException.class, () -> Parser.getLineParser(path, null, null));
    }

    @Test
    public void testGetLogRecords_LocalDirectory() throws Exception {
        String path = "src/test/resources/logAnalyzer/tester/*";                       // в папке два файла
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3+5, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/logs/*";                         // в папке один файл
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(7, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/**";                            // два файла в этой папке, один во внутренней
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3+5+7, analyze.getRequestsCount());

        String wrongPath = "src/test/resources/logAnalyzer/tester/logs/loggers";      // папки не существует
        assertThrows(IOException.class, () -> Parser.getLineParser(wrongPath, null, null));
    }



    @Test
    public void testGetLogRecords_MaskOfFile() throws Exception {
        String path = "src/test/resources/logAnalyzer/tester/lo*";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(8, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/*.txt";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/*3.txt";            // один файл подходит
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/log[ert]*";              // один файл подходит
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/log?s*";              // один файл подходит
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/log*s*";             // два файла подходят
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3+5, analyze.getRequestsCount());

        String wrongPath = "src/test/resources/logAnalyzer/tester/*.pdf";                     // файл не существует
        assertThrows(IOException.class, () -> Parser.getLineParser(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_MaskOfDirectory() throws Exception {
        String path = "src/test/resources/logAnalyzer/tester/**/logers_7.txt";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(7, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/**/logs_5.txt";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(16, analyze.getRequestsCount());

        String wrongPath = "src/test/resources/logAnalyzer/tester/**/no.txt";
        assertThrows(IOException.class, () -> Parser.getLineParser(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_MaskOfFileAndDirectory() throws Exception {
        String path = "src/test/resources/logAnalyzer/**/log[ert]*t";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(20, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/**log*.txt";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(36, analyze.getRequestsCount());

        path = "src/test/resources/logAnalyzer/tester/**loge*.txt";
        analyze = Parser.getLineParser(path, null, null);
        assertEquals(3+7, analyze.getRequestsCount());

        String wrongPath = "src/test/resources/logAnalyzer/tester/**/nonme.txt";
        assertThrows(IOException.class, () -> Parser.getLineParser(wrongPath, null, null));
    }

    @Test
    public void testGetLogRecords_InvalidPathLocalFile() throws Exception {
        String path = "InvalidPathAbsolytnoTochno";
        assertThrows(IOException.class, () -> Parser.getLineParser(path, null, null));
    }
    @Test
    public void testGetRootDirectory_WithoutMask() {
        String path = "C:\\Users\\Anyone\\Desktop\\file.txt";
        String expected = "C:/Users/Anyone/Desktop/file.txt";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop";
        expected = "C:/Users/Anyone/Desktop";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "Desktop/Something/Else";
        assertEquals(path, Parser.getRootDirectory(path));
    }

    @Test
    public void testGetRootDirectory_WithMask() {
        String path = "C:\\Users\\Anyone\\Desktop\\file*";
        String expected = "C:/Users/Anyone/Desktop";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop\\file.t?t";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\Desktop\\f[ioe]le.txt";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "C:\\Users\\Anyone\\D?sktop\\image.png";
        expected = "C:/Users/Anyone";
        assertEquals(expected, Parser.getRootDirectory(path));

        path = "C:\\Users\\Any[on]e\\De?ktop\\fi*.txt";
        expected = "C:/Users";
        assertEquals(expected, Parser.getRootDirectory(path));
    }

    @Test
    public void testGetRootDirectory_EmptyPath() {
        String path = "";
        assertEquals(path, Parser.getRootDirectory(path));
    }

    @Test
    public void testGetPattern_SameRoot() {
        String path = "C:\\Users\\Anyone\\Desktop\\file.txt";
        String root = "C:/Users/Anyone/Desktop/file.txt";
        assertEquals("**", Parser.getPattern(path, root));

        path = "LOG/loges_3.txt";
        root = "LOG/loges_3.txt";
        assertEquals("**", Parser.getPattern(path, root));
    }

    @Test
    public void testGetPattern_AbsolutePath() {
        String path = "C:\\Users\\Anyone\\Desktop\\file*";
        String root = "C:/Users/Anyone/Desktop";
        assertEquals("C:/Users/Anyone/Desktop/file*", Parser.getPattern(path, root));

        path = "/Users/Anyone/Desktop/file*";
        assertEquals("/Users/Anyone/Desktop/file*", Parser.getPattern(path, root));
    }

    @Test
    public void testGetPattern_RelativePath() {
        String path = "LOG/log*";
        String root = "LOG";
        String expected = Paths.get("").toAbsolutePath().toString().replace('\\', '/') + "/" + path;
        assertEquals(expected, Parser.getPattern(path, root));
    }

    @Test
    public void testGetPattern_EmptyPath() {
        String path = "";
        String root = "root";
        String expected = Paths.get("").toAbsolutePath().toString().replace('\\', '/') + "/";
        assertEquals(expected, Parser.getPattern(path, root));
    }

}
