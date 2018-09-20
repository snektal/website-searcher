package app;

import controller.PageContentSearcher;
import controller.Controller;
import exception.WebSearcherException;
import reader.UrlReader;
import report.ReportWriter;
import utils.DateUtil;
import utils.config.ApplicationConfig;
import utils.logger.ApplicationLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class WebContentsSearcher {

    private static final String DEFAULT_URL = "https://s3.amazonaws.com/fieldlens-public/urls.txt";

    public static void main(String[] args) {

        String timestamp = DateUtil.getFileTimestamp();
        setApplicationLogger(timestamp);

        ApplicationLogger.log(Level.INFO, "app.WebContentsSearcher is Starting");
        List<String> urlsToSearch = getWebsiteUrls();

        PageContentSearcher pageContentSearcherCallback = new PageContentSearcher();
        Controller controller = new Controller(pageContentSearcherCallback);
        controller.submitSearchTasks(urlsToSearch);

        ReportWriter.generateReport(timestamp, pageContentSearcherCallback);

        ApplicationLogger.log(Level.INFO, "app.WebContentsSearcher is Completed");

        System.exit(0);
    }

    private static List<String> getWebsiteUrls() {

        String urls = ApplicationConfig.getStringProperty("web.link.to.urls", DEFAULT_URL);
        ApplicationLogger.log(Level.INFO, "Loading Urls from {0}", urls);

        UrlReader urlReader = new UrlReader();
        return urlReader.getUrls(urls);
    }

    private static void setApplicationLogger(String timestamp) {
        try {
            ApplicationLogger.setup(timestamp);
        } catch (IOException e) {
            throw new WebSearcherException("Problems with creating the log file " + e);
        }
    }

}
