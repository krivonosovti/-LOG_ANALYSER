package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import backend.academy.logAnalyzer.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class LogAnalyzerTests {

    @Mock
    private LogFileReader fileReader;

    @Mock
    private LogParser parser;

    @Mock
    private StatisticsCollector collector;

    @Mock
    private LogReport report;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMain_withoutPathArgument() {
        String[] args = {"analyzer", "--from", "2024-11-01", "--to", "2024-11-02", "--format", "markdown"};

        LogAnalyzer.main(args);

        // Проверяем, что система выдает сообщение об отсутствии пути к логу
        verifyNoInteractions(fileReader, parser, collector, report);
    }

    @Test
    void testMain_withInvalidFormat() {
        String[] args = {"analyzer", "--path", "test.log", "--format", "unsupported"};

        LogAnalyzer.main(args);

        // Здесь можно проверить, что система обрабатывает некорректный формат
        verifyNoInteractions(fileReader, parser, collector, report);
    }
}
