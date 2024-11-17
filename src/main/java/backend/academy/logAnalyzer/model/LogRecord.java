package backend.academy.logAnalyzer.model;

import java.util.Objects;

public class LogRecord {
    private String ipAddress;
    private String timestamp;
    private String request;
    private int statusCode;
    private int responseSize;

    public LogRecord(String ipAddress, String timestamp, String request, int statusCode, int responseSize) {
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.request = request;
        this.statusCode = statusCode;
        this.responseSize = responseSize;
    }

    // Геттеры
    public String getIpAddress() {
        return ipAddress;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRequest() {
        return request;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    // Переопределяем equals
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogRecord logRecord = (LogRecord) o;
        return statusCode == logRecord.statusCode
            && responseSize == logRecord.responseSize
            && Objects.equals(ipAddress, logRecord.ipAddress)
            && Objects.equals(timestamp, logRecord.timestamp)
            && Objects.equals(request, logRecord.request);
    }

    // Переопределяем hashCode
    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, timestamp, request, statusCode, responseSize);
    }
}
