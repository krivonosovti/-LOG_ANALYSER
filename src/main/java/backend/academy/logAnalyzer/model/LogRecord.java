package backend.academy.logAnalyzer.model;

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
}
