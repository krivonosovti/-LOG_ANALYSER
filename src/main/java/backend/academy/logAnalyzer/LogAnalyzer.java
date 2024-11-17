package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.model.LogRecord;
import backend.academy.logAnalyzer.utils.DateUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public final class LogAnalyzer {
    private LogAnalyzer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static PrintStream printStream = new PrintStream(System.out);
    private static final Logger LOGGER = Logger.getLogger(LogAnalyzer.class.getName());

    @SuppressWarnings("checkstyle:UncommentedMain") public static void main(String @NotNull [] args) {
        String[] parameters = parseArguments(args);

        String path = parameters[0];
        String from = parameters[1];
        String to = parameters[2];
        String format = parameters[2 + 1];

        if (!isPathValid(path)) {
            printUsage();
            return;
        }

        executeAnalysis(path, from, to, format);
    }

    @SuppressWarnings("checkstyle:ModifiedControlVariable") private static String[] parseArguments(String[] args) {
        String path = null;
        String from = null;
        String to = null;
        String format = "markdown"; // По умолчанию markdown

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "analyzer":
                    break;
                case "--path":
                    if (i + 1 < args.length) {
                        path = args[++i];
                    }
                    break;
                case "--from":
                    if (i + 1 < args.length) {
                        from = args[++i];
                    }
                    break;
                case "--to":
                    if (i + 1 < args.length) {
                        to = args[++i];
                    }
                    break;
                case "--format":
                    if (i + 1 < args.length) {
                        format = args[++i];
                    }
                    break;
                default:
                    printStream.println("Unknown argument: " + args[i]);
            }
        }

        return new String[]{path, from, to, format};
    }

    private static boolean isPathValid(String path) {
        return path != null;
    }

    private static void printUsage() {
        printStream.println("Usage: analyzer --path <log path> [--from <start date>] "
            + "[--to <end date>] [--format <markdown|adoc>]");
    }

    private static void executeAnalysis(String path, String from, String to, String format) {
        LogFileReader fileReader = new LogFileReader();
        StatisticsCollector collector = new StatisticsCollector();
        LogReport report = new LogReport();

        try {
            List<LogRecord> records = fileReader.readLogs(path);
            List<LogRecord> filteredRecords = DateUtils.filterRecordsByDate(records, from, to);
            collector.collectStatistics(filteredRecords, from, to);
            report.generateReport(collector, format, printStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading log files: " + e.getMessage(), e);
        }
    }

}
