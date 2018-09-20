package reader;

import exception.WebSearcherException;
import http.HttpConnectionManager;
import utils.logger.ApplicationLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class UrlReader {
    private static Pattern pattern = Pattern.compile("<script(.*)>(.*)</script>");
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";

    public Map<String, String> readPage(String link, Protocol protocol)  {
        Map<String, String> linkContents = new HashMap<>();
        StringBuilder contents = new StringBuilder();
        try {
            HttpURLConnection conn = HttpConnectionManager.create(link, protocol);
            if (conn != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    contents.append(reader.lines().collect(joining(SPACE)));
                }

                ApplicationLogger.log(Level.FINE, "Execute successfully {0}", link);
            }

            linkContents.put(link, stripTags(contents.toString()));

        } catch (IOException e) {
            String msg = MessageFormat.format("Failed to execute - {0}. Exception -> {1}", link, e);
            ApplicationLogger.log(Level.SEVERE, msg);

            linkContents.put(link, msg);
            throw new WebSearcherException (msg);
        }
        return linkContents;
    }

    public List<String> getUrls(String url) {
        Protocol protocol = Protocol.IGNORE;
        String contents = read(url, protocol);

        return Arrays.stream(contents.split(System.getProperty("line.separator")))
                .filter(Objects::nonNull)
                .skip(1)
                .map(l -> l.split(COMMA)[1].replaceAll(QUOTE, EMPTY_STRING))
                .collect(toList());
    }

    private String stripTags(String contents) {
        String contentStripped = removeScriptContent(contents);
        return contentStripped.replaceAll("\\<.*?>", EMPTY_STRING);
    }

    private String removeScriptContent(String contents) {
        if(!contents.isEmpty()) {
            Matcher matcher = pattern.matcher(contents);
            while (matcher.find()) {
                contents = contents.replace(matcher.group(1), EMPTY_STRING);
            }
            return contents;
        }
        return contents;
    }



    private String read(String link, Protocol protocol) {
        StringBuilder contents = new StringBuilder();
        try {
            HttpURLConnection conn = HttpConnectionManager.create(link, protocol);
            if (conn != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    contents.append(reader.lines().collect(joining(System.getProperty("line.separator"))));
                }
                ApplicationLogger.log(Level.FINE, "Execute successfully {0}", link);
            }
        } catch (IOException e) {
            ApplicationLogger.log(Level.SEVERE, "Failed to execute - {0}. Exception -> {1}", link, e);
        }
        return contents.toString();
    }

}
