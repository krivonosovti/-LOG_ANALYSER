package backend.academy.logAnalyzer.report;

import backend.academy.logAnalyzer.Analyze;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;

public final class LogReport {
    private LogReport() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final Logger LOGGER = Logger.getLogger(Analyze.class.getName());

    // Форматы для таблиц
    private static final String MARKDOWN_TABLE_ROW_FORMAT = "| %s | %s |\n";
    private static final String MARKDOWN_TABLE_HEADER = "| %s | %s |\n|:---|---:|\n";
    private static final String ASCIIDOC_TABLE_ROW_FORMAT = "| %s | %s\n";
    private static final String ASCIIDOC_TABLE_HEADER = "[cols=\"2,1\", options=\"header\"]\n|===\n| %s | %s\n";

    // Константы для строк
    private static final String FORMAT_MARKDOWN = "markdown";
    private static final String METRIC = "Metric";
    private static final String VALUE = "Value";
    private static final String RESOURCE = "Resource";
    private static final String COUNT = "Count";
    private static final String CODE = "Code";
    private static final String NAME = "Name";
    private static final String TABLE_END = "|===\n";
    private static final String DASH = "-";

    public static void generateReport(Analyze analyze, String path, String format) {
        StringBuilder reportContent = new StringBuilder();
        boolean isMarkdown = FORMAT_MARKDOWN.equalsIgnoreCase(format);

        // Generate general information
        reportContent.append(generateGeneralInfo(analyze, path, isMarkdown));

        // Generate information about requested resources
        reportContent.append(generateResourcesInfo(analyze, isMarkdown));

        // Generate information about response codes
        reportContent.append(generateResponseCodesInfo(analyze, isMarkdown));

        // Save report to file
        saveReport(reportContent.toString(), isMarkdown ? FORMAT_MARKDOWN : "adoc");
    }

    private static String generateGeneralInfo(Analyze analyze, String path, boolean isMarkdown) {
        StringBuilder section = new StringBuilder(
            isMarkdown ? "#### General Information\n\n" : "== General Information\n\n");

        if (isMarkdown) {
            section.append(String.format(MARKDOWN_TABLE_HEADER, METRIC, VALUE));
        } else {
            section.append(String.format(ASCIIDOC_TABLE_HEADER, METRIC, VALUE));
        }

        section.append(formatRow(RESOURCE + "(s)", path, isMarkdown));
        section.append(formatRow("Start Date", Optional.ofNullable(analyze.getFrom())
            .map(Object::toString)
            .orElse(DASH), isMarkdown));
        section.append(formatRow("End Date", Optional.ofNullable(analyze.getTo())
            .map(Object::toString)
            .orElse(DASH), isMarkdown));
        section.append(formatRow("Request Count", String.valueOf(analyze.getRequestsCount()), isMarkdown));
        section.append(formatRow("Average Response Size", analyze.getAverageResponseSize() + "b", isMarkdown));
        section.append(formatRow("95th Percentile Response Size", analyze.getPercentile95() + "b", isMarkdown));
        section.append(formatRow("50th Percentile Response Size", analyze.getPercentile50() + "b", isMarkdown));

        if (!isMarkdown) {
            section.append(TABLE_END);
        }

        return section.toString();
    }

    private static String generateResourcesInfo(Analyze analyze, boolean isMarkdown) {
        StringBuilder section =
            new StringBuilder(isMarkdown ? "\n#### Requested Resources\n\n" : "\n== Requested Resources\n\n");

        if (isMarkdown) {
            section.append(String.format(MARKDOWN_TABLE_HEADER, RESOURCE, COUNT));
        } else {
            section.append(String.format(ASCIIDOC_TABLE_HEADER, RESOURCE, COUNT));
        }

        analyze.getResourcesAmount().forEach((resource, count) ->
            section.append(formatRow(resource, count.toString(), isMarkdown)));

        if (!isMarkdown) {
            section.append(TABLE_END);
        }

        return section.toString();
    }

    private static String generateResponseCodesInfo(Analyze analyze, boolean isMarkdown) {
        StringBuilder section = new StringBuilder(isMarkdown ? "\n#### Response Codes\n\n" : "\n== Response Codes\n\n");

        if (isMarkdown) {
            section.append(String.format(MARKDOWN_TABLE_HEADER, CODE, NAME, COUNT));
        } else {
            section.append(String.format(ASCIIDOC_TABLE_HEADER, CODE, NAME, COUNT));
        }

        analyze.getCodesAmount().forEach((code, count) -> {
            String description
                = HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code).getReasonPhrase() : "Unknown";
            section.append(formatCodeRow(code.toString(), description, count.toString(), isMarkdown));
        });

        if (!isMarkdown) {
            section.append(TABLE_END);
        }

        return section.toString();
    }

    private static String formatRow(String key, String value, boolean isMarkdown) {
        return String.format(isMarkdown ? MARKDOWN_TABLE_ROW_FORMAT : ASCIIDOC_TABLE_ROW_FORMAT, key, value);
    }

    private static String formatCodeRow(String col1, String col2, String col3, boolean isMarkdown) {
        if (isMarkdown) {
            // Format row for Markdown
            return String.format("| %s | %s | %s |\n", col1, col2, col3);
        } else {
            // Format row for Asciidoc
            return String.format("| %s | %s | %s\n", col1, col2, col3);
        }
    }

    private static void saveReport(String content, String extension) {
        String fileName = "log_report." + extension;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing report to file: " + e.getMessage(), e);
        }
    }
}
