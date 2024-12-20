package backend.academy.logAnalyzer;

import java.time.LocalDate;

public record LogRecord(String remoteAddr, LocalDate date, String requestPath, int status, long bodyBytesSent) {
}
