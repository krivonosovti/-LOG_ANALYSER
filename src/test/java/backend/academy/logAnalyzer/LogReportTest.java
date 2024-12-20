package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.Analyze;
import backend.academy.logAnalyzer.report.LogReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogReportTest {

    private final Path markdownReportPath = Path.of("log_report.markdown");
    private final Path asciidocReportPath = Path.of("log_report.adoc");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(markdownReportPath);
        Files.deleteIfExists(asciidocReportPath);
    }

    void testGenerateReportWithEmptyData() throws IOException {
        // Создаем объект Analyze с пустыми данными
        Analyze analyze = new Analyze(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30));

        // Генерация отчёта
        LogReport.generateReport(analyze, "/empty/logs", "markdown");

        // Проверка, что файл был создан
        assertTrue(Files.exists(markdownReportPath), "Markdown report file should be created for empty data");

        // Чтение файла и проверка содержимого
        String content = Files.readString(markdownReportPath);
        assertTrue(content.contains("#### General Information"), "Report should include general info section even if empty");
        assertTrue(content.contains("Request Count | 0"), "Empty report should show zero requests");
    }

    @Test
    void testSaveReportIOException() {
        // Создаем объект Analyze с тестовыми данными
        Analyze analyze = new Analyze(LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30));

        // Указываем некорректный путь
        String invalidPath = "/invalid/path";

        // Проверяем, что логирование ошибки происходит
        assertDoesNotThrow(() -> LogReport.generateReport(analyze, invalidPath, "markdown"),
            "Generate report should handle IO exceptions gracefully");
    }
}
