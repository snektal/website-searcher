package controller;

public interface Callback {
    default void loadedPage(String url, String pageContext) {
    }

    default void couldNotLoadPage(String url, Throwable e) {
    }
}
