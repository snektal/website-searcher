package reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.config.ApplicationConfig;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UrlReaderTest {

    private UrlReader urlReader ;

    @Before
    public void setUp()  {
        urlReader= new UrlReader();
    }

    @After
    public void tearDown()  {
        urlReader = null;
    }

    @Test
    public void readPage() {
        String pageUrl = "w3.org/";
        try {
            Map<String, String> contents = urlReader.readPage(pageUrl, Protocol.HTTP);
            assertFalse(0 == contents.get(pageUrl).length());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getUrls() {
        String url = "https://s3.amazonaws.com/fieldlens-public/urls.txt";
        List<String> urls = urlReader.getUrls(url);
        long expectedCount = 500;
        assertEquals(expectedCount, urls.size());
    }

    @Test
    public void removeScriptContent() {
    }

}