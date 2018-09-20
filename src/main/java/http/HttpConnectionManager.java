package http;

import reader.Protocol;
import utils.logger.ApplicationLogger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        System.setProperty("http.agent", "Chrome");
    }

    private HttpConnectionManager() {
    }

    public static HttpURLConnection create(String link, Protocol protocol) {
        HttpURLConnection conn = null;
        try {
            switch (protocol) {
                case HTTPS:
                    conn = createSecureConnection("https://www." + link);
                    break;
                case HTTP:
                    conn = createConnection("http://www." + link);
                    break;
                case IGNORE:
                    conn = createConnection(link);
                    break;
            }
        } catch (IOException e) {
            ApplicationLogger.log(Level.SEVERE, "Failure to create a URLConnection -> {0}", e);
        }
        return conn;
    }

    private static HttpURLConnection createConnection(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1)");
        conn.setConnectTimeout(15000);

        return conn;

    }

    private static HttpURLConnection createSecureConnection(String link) {
        HttpURLConnection conn = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // no implementation
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // no implementation
                        }
                    }
            };
            activateTrustManager(trustAllCerts);
            conn = createConnection(link);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create connection for HTTPS " + link + ", Exception: " + e);
        }
        return conn;

    }

    private static void activateTrustManager(TrustManager[] trustAllCerts) {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to activate TrustManager , Exception: " + e);
        }
    }

}
