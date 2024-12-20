package backend.academy.logAnalyzer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Analyze {
    private HashMap<String, Long> resourcesAmount = new HashMap<>();         // частота запрашиваемых ресурсов
    private HashMap<Integer, Long> codesAmount = new HashMap<>();    // частота встречающихся кодов ответа
    private HashMap<String, Long> addrAmount = new HashMap<>();        // частота запросов отдельных IP-адресов
    private ArrayList<Long> responseSizes = new ArrayList<>();
    private LocalDate from;
    private LocalDate to;
    private long requestsCount = 0;
    private long averageResponseSize = 0;
    private long percentile50 = 0;
    private long percentile95 = 0;
    private static final double PER50 = 0.50;
    private static final double PER95 = 0.95;

    public Analyze(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public void addRecord(LogRecord logRecord) {
        if (dataFilter(logRecord)) {
            requestsCount++;
            addrAmount
                .put(logRecord.remoteAddr(), addrAmount.getOrDefault(logRecord.remoteAddr(), 0L) + 1);
            responseSizes.add(logRecord.bodyBytesSent());
            resourcesAmount
                .put(logRecord.requestPath(), resourcesAmount.getOrDefault(logRecord.requestPath(), 0L) + 1);
            codesAmount
                .put(logRecord.status(), codesAmount.getOrDefault(logRecord.status(), 0L) + 1);
        }
    }

    public boolean dataFilter(LogRecord logRecord) {
        return  (from == null || logRecord.date().isAfter(from) || logRecord.date().isEqual(from))
        && (to == null || logRecord.date().isBefore(to));
    }

    public void calculateStatistic() {
        if (requestsCount != 0) {
            averageResponseSize = responseSizes.stream()
                .mapToLong(Long::longValue)
                .sum()
                / requestsCount;
            percentile50 = getPercentile(PER50);
            percentile95 = getPercentile(PER95);
        }
    }

    public long getPercentile(double per) {
        if (per < 0 || per > 1 || requestsCount == 0) {
            return 0;
        }
        Collections.sort(responseSizes);
        int index = (per == 0) ? 0 : (int) Math.ceil(per * responseSizes.size()) - 1;
        return responseSizes.get(index);
    }

    public HashMap<String, Long> getResourcesAmount() {
        return resourcesAmount;
    }

    public HashMap<Integer, Long> getCodesAmount() {
        return codesAmount;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public long getRequestsCount() {
        return requestsCount;
    }

    public long getAverageResponseSize() {
        return averageResponseSize;
    }

    public long getPercentile50() {
        return percentile50;
    }

    public long getPercentile95() {
        return percentile95;
    }

    public HashMap<String, Long> getAddrAmount() {
        return addrAmount;
    }

    public ArrayList<Long> getResponseSizes() {
        return responseSizes;
    }

    public void setResourcesAmount(HashMap<String, Long> resourcesAmount) {
        this.resourcesAmount = resourcesAmount;
    }

    public void setCodesAmount(HashMap<Integer, Long> codesAmount) {
        this.codesAmount = codesAmount;
    }

    public void setAddrAmount(HashMap<String, Long> addrAmount) {
        this.addrAmount = addrAmount;
    }

    public void setResponseSizes(ArrayList<Long> responseSizes) {
        this.responseSizes = responseSizes;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public void setRequestsCount(long requestsCount) {
        this.requestsCount = requestsCount;
    }

    public void setAverageResponseSize(long averageResponseSize) {
        this.averageResponseSize = averageResponseSize;
    }

    public void setPercentile50(long percentile50) {
        this.percentile50 = percentile50;
    }

    public void setPercentile95(long percentile95) {
        this.percentile95 = percentile95;
    }
}


