package report;

import controller.PageContentSearcher;
import utils.logger.ApplicationLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ReportWriter {
    private static int pad = 30;
    private static String reportFileName = "results_%1$s.txt";

    private ReportWriter() {}

    public static void generateReport(String timestamp, PageContentSearcher pageContentSearcher) {
        reportFileName = String.format(reportFileName, timestamp);

        ApplicationLogger.log(Level.INFO, "Generating and writing report to a file {0}", reportFileName);

        List<String> searchTerms = pageContentSearcher.getSearchTerms();
        Map<String, String> results = pageContentSearcher.getResults();

        List<String> report = generateReport(searchTerms, results);
        writeReport(report);
        ApplicationLogger.log(Level.INFO, "Report generation is completed");

    }

    private static void writeReport(List<String> report) {
        Path path = Paths.get(reportFileName);
        try {
            Files.write(path, report, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ApplicationLogger.log(Level.SEVERE, "Failure to write a report -> {0}", e);
        }
    }

    private static List<String> generateReport(List<String> searchTerms, Map<String, String> results) {
        List<String> report = new ArrayList<>();
        report.add("Searched Terms Passed: ");
        report.add(searchTerms.toString());
        report.add(padRight("Site Url", pad)
                        .concat(": ")
                        .concat("Matched Searched Terms?"));
        report.add("========================================================");

        List<String> linesToWrite = results.entrySet().stream()
                .map(e -> padRight(e.getKey(), pad).concat(": ").concat(e.getValue()))
                .collect(Collectors.toList());

        report.addAll(linesToWrite);
        return report;
    }

    /**
     * pad with " " to the right to the given length (n)
     *
     * @param s - string to pad
     * @param n - size of the column
     * @return - padded to the right string
     */
    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String getReportFileName() {
        return reportFileName;
    }
}
