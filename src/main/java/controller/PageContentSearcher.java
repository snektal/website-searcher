package controller;

import utils.config.ApplicationConfig;
import utils.logger.ApplicationLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PageContentSearcher implements Callback {

    private Map<String, String> results;
    private List<String> searchTerms;

    public PageContentSearcher() {
        this.searchTerms = loadSearchTerms();
        ApplicationLogger.log(Level.FINE, "Search terms passed {0}", searchTerms);
        results = new HashMap<>();
    }

    public List<String> getSearchTerms() {
        return searchTerms;
    }

    @Override
    public synchronized void loadedPage(String url, String pageContext) {
        parseForSearchTerms(url, pageContext);
    }

    @Override
    public synchronized void couldNotLoadPage(String url, Throwable e) {
        results.putIfAbsent(url, e.toString());
    }

    public synchronized Map<String, String> getResults() {
        return results;
    }

    private void parseForSearchTerms(String url, String pageContext) {
        String pageContextToLower = pageContext.toLowerCase();
        long matchCount = searchTerms.stream()
                .map(String::toLowerCase)
                .filter(pageContextToLower::contains)
                .count();

        results.putIfAbsent(url, String.valueOf(matchCount > 0));
    }

    private static List<String> loadSearchTerms() {
        return new ArrayList<>(
                Arrays.asList(ApplicationConfig
                        .getStringProperty("web.page.content.search.terms", "there,now")
                        .split(",")
                )
        );
    }
}
