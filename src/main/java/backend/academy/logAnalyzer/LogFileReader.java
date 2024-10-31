package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogFileReader {
    public List<LogRecord> readLogs(String path) throws IOException {

        List<LogRecord> records = new ArrayList<>();

        if (path.startsWith("http://") || path.startsWith("https://")) {

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(path).openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LogRecord logRecord = LogParser.parseLine(line);
                    if (logRecord != null) {
                        records.add(logRecord);
                    }
                }
            }
        } else {
            try (Stream<String> lines = Files.lines(Paths.get(path))) {
                lines.forEach(line -> {
                    LogRecord logRecord = LogParser.parseLine(line);
                if (logRecord != null) {
                    records.add(logRecord);
                }
                });
            }
        }
        return records;
    }
}
