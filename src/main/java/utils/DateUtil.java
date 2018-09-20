package utils;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class DateUtil {

    private DateUtil() {}

    public static String getFileTimestamp() {
        LocalDateTime date = LocalDateTime.now();
        return date.format(ISO_LOCAL_DATE_TIME).replaceAll(":", "-");
    }
}
