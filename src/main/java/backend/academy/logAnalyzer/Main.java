package backend.academy.logAnalyzer;

import backend.academy.logAnalyzer.parser.Parser;
import backend.academy.logAnalyzer.report.LogReport;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//CHECKSTYLE:OFF
public class Main {
    //CHECKSTYLE:ON
    @Parameter(names = "--path", required = true, description = "path to logs files")
    private static String path;
    @Parameter(names = "--from", converter = LocalDateConverter.class, description = "<since data> (влючая)")
    private static LocalDate from;
    @Parameter(names = "--to", converter = LocalDateConverter.class, description = "<to data> (не включая)")
    private static LocalDate to;
    @Parameter(names = "--format", description = "<markdown or adoc>")
    private static String format = "markdown";

    private static final PrintStream PRINT_STREAM = System.out;
    private static final Logger LOGGER = Logger.getLogger(Analyze.class.getName());

    public static void main(String[] args) {
        JCommander jCommander = JCommander.newBuilder().addObject(new Main()).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            jCommander.usage();
            return;
        }

        Optional<Analyze> optAnalyze = readLogs();
        if (optAnalyze.isEmpty()) {
            LOGGER.log(Level.SEVERE, "File reading error.");
        } else {
            optAnalyze.get().calculateStatistic();
            LogReport.generateReport(optAnalyze.get(), path, format);
            PRINT_STREAM.printf("%s file has created", format);
        }
    }

    public static Optional<Analyze> readLogs() {
        try {
            return Optional.of(Parser.getLineParser(path, from, to));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static class LocalDateConverter implements IStringConverter<LocalDate> {
        @Override
        public LocalDate convert(String value) {
            try {
                return LocalDate.parse(value);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Illegal data format. Expected yyyy-MM-dd");
                return null;
            }
        }
    }
}
