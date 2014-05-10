package pl.edu.agh.kaflog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class KaflogProperties {

    private static Logger LOGGER = LoggerFactory.getLogger(KaflogProperties.class);
    private static Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(KaflogProperties.class.getClassLoader().getResourceAsStream("kaflog.properties"));
        } catch (IOException e) {
            LOGGER.error("Fatal error during initialization: ", e);
        }
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }

    public static String getProperty(String name) {
        return PROPERTIES.getProperty(name);
    }

    public static int getIntProperty(String name) {
        try {
            return Integer.parseInt(PROPERTIES.getProperty(name));
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    public static boolean getBoolProperty(String name) {
        return Boolean.parseBoolean(PROPERTIES.getProperty(name));
    }
}
