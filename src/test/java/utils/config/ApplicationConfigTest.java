package utils.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationConfigTest {

    @Test
    public void getStringProperty() {
        String property = ApplicationConfig.getStringProperty("logger.level", "INFO");
        assertEquals("FINE", property);
    }

    @Test
    public void getDefaultStringProperty() {
        String property = ApplicationConfig.getStringProperty("NON_EXISTENT.PROPERTY", "Hello");
        assertEquals("Hello", property);
    }

    @Test
    public void getIntegerProperty() {
        int property = ApplicationConfig.getIntegerProperty("pool.size", 10);
        assertEquals(20, property);
    }

    @Test
    public void getLongProperty() {
        int property = ApplicationConfig.getIntegerProperty("pool.await.termination.sleep.time", 99999);
        assertEquals(60000, property);
    }
}