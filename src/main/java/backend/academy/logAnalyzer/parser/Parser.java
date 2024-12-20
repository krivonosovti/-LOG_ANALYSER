package backend.academy.logAnalyzer.parser;

import backend.academy.logAnalyzer.Analyze;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    PrintStream printStream = new PrintStream(System.out);
    private static final Logger LOGGER = Logger.getLogger(Analyze.class.getName());

    public Parser() {}

    public static Analyze getLineParser(String path, LocalDate from, LocalDate to) throws IOException {
        Parser parser = new Parser();
        return parser.get(path, from, to);
    }

    private Analyze get(String path, LocalDate from, LocalDate to) throws IOException {
        try {
            if (isValidURL(path)) {
                return readURL(path, from, to);
            } else {
                return readFiles(path, from, to);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File reading error. Path = " + path, e);
            throw e;
        }

    }

    private boolean isValidURL(String path) {
        try {
            URL url = new URL(path);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private Analyze readURL(String path, LocalDate from, LocalDate to) throws IOException {
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        Analyze statistic = new Analyze(from, to);
        try (BufferedReader reader
                 = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            LineParser.addRecord(reader, statistic);
            return statistic;
        }
    }

    private Analyze readFiles(String truePath, LocalDate from, LocalDate to) throws IOException {
        String mainFormat = ".txt";
        String pathString;
        if (truePath.lastIndexOf('*') == -1 && truePath.indexOf(mainFormat) == -1) {
            pathString = truePath + mainFormat;
        } else {
            pathString = truePath;
        }
        AtomicBoolean pathExists = new AtomicBoolean(false);
        Analyze analyze = new Analyze(from, to);
        String root = getRootDirectory(pathString);
        String pattern = getPattern(pathString, root);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        Files.walk(Paths.get(root))
            .filter(Files::isRegularFile)
            .filter(p -> matcher.matches(p.toAbsolutePath()))
            .forEach(p -> {
                pathExists.set(true);
                try (BufferedReader reader = new BufferedReader(new FileReader(p.toFile()))) {
                    LineParser.addRecord(reader, analyze);
                } catch (IOException e) { }
            });
        if (pathExists.get()) {
            return analyze;
        } else {
            throw new IOException("File not found");
        }
    }

    public static String getRootDirectory(String pathString) {
        String normalizedPath = pathString.replace('\\', '/');
        int firstSpecialCharIndex = normalizedPath.length() + 1;
        String specialChars = "[*?{";
        for (char specialChar : specialChars.toCharArray()) {
            int currentIndex = normalizedPath.indexOf(specialChar);
            if (currentIndex != -1 && currentIndex < firstSpecialCharIndex) {
                firstSpecialCharIndex = currentIndex;
            }
        }
        if (firstSpecialCharIndex > normalizedPath.length()) {
            return normalizedPath;
        }
        int lastSlashIndex = normalizedPath.lastIndexOf('/', firstSpecialCharIndex);
        return (lastSlashIndex == -1) ? "" : normalizedPath.substring(0, lastSlashIndex);
    }

    public static String getPattern(String pathString, String root) {
        String normalizedPath = pathString.replace('\\', '/');
        if (root.equals(normalizedPath)) {
            return "**";
        }
        if (normalizedPath.contains(":") || normalizedPath.startsWith("/")) {
            return normalizedPath;
        } else {
            String currentAbsolutePath = Paths.get("").toAbsolutePath().toString().replace('\\', '/');
            return currentAbsolutePath + "/" + normalizedPath;
        }
    }


}
