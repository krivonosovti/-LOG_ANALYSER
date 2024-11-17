package backend.academy.logAnalyzer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LogReportTests {
    private LogReport logReport;
    private StatisticsCollector collector;

    @BeforeEach
    void setUp() {
        logReport = new LogReport();
        collector = mock(StatisticsCollector.class);

        // Настройка поведения фейкового объекта collector
        when(collector.getFromFilter()).thenReturn("01/Jan/2021");
        when(collector.getToFilter()).thenReturn("31/Dec/2021");
        when(collector.getTotalRequests()).thenReturn(100);
        when(collector.getAverageResponseSize()).thenReturn(200.0);
        when(collector.get95thPercentileResponseSize()).thenReturn(300.0);

        Map<String, Integer> resources = new HashMap<>();
        resources.put("/index.html", 50);
        resources.put("/about.html", 30);
        resources.put("/contact.html", 20);
        // Преобразуем ресурсы в список
        List<Map.Entry<String, Integer>> resourceList = new ArrayList<>(resources.entrySet());

        when(collector.getMostRequestedResources(10)).thenReturn(resourceList);

        Map<Integer, Integer> statusCodes = new HashMap<>();
        statusCodes.put(200, 70);
        statusCodes.put(404, 20);
        statusCodes.put(500, 10);
        List<Map.Entry<Integer, Integer>> mostFrequentList = new ArrayList<>(statusCodes.entrySet());
        when(collector.getMostFrequentStatusCodes(10)).thenReturn(mostFrequentList);
    }

    @Test
    void testGenerateReport() {
        // Redirect output stream to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Генерация отчета
        logReport.generateReport(collector, "txt", printStream);

        // Проверка, что отчет сгенерирован правильно
        String expectedOutput = "Report generated: log_report.txt";
        assertTrue(outputStream.toString().contains(expectedOutput));
    }
}
