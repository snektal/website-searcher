package controller;

import concurrent.ThreadPool;
import exception.WebSearcherException;
import reader.Protocol;
import reader.UrlReader;
import utils.logger.ApplicationLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Controller {

    private final List<Callback> callbacks;

    public Controller(Callback... callbacks) {
        this.callbacks = new CopyOnWriteArrayList<>(callbacks);
    }

    public void submitSearchTasks(List<String> urlsToSearch) {
        urlsToSearch.forEach(this::submitTask);

        ThreadPool.getInstance().awaitTermination();
    }

    private void submitTask(String url) {
        ApplicationLogger.log(Level.FINE, "Submitting a task {0}", url);
        ThreadPool.getInstance().add(() -> {
            try {
                Map<String, String> pageContents = getLoadedContents(url, Protocol.HTTP);
                if(pageContents.get(url).contains("301 Moved Permanently")) {
                    throw new WebSearcherException(pageContents.get(url));
                }
                loadedPage(url, pageContents.get(url));

            } catch (Exception e) {
                ApplicationLogger.log(Level.FINE, "Failed task to load page {0}", url);
                couldNotLoadPage(url, e);
            }
        });
    }

    private Map<String, String> getLoadedContents(String url, Protocol protocol) {
        Map<String, String> pageContents = new UrlReader().readPage(url, protocol);
        ApplicationLogger.log(Level.FINE, "Page Contents -> {0}", pageContents);
        return pageContents;
    }

    private void loadedPage(String url, String pageContext) {
        callbacks.forEach(callback -> callback.loadedPage(url, pageContext));
    }

    private void couldNotLoadPage(String url, Throwable e) {
        callbacks.forEach(callback -> callback.couldNotLoadPage(url, e));
    }

}
