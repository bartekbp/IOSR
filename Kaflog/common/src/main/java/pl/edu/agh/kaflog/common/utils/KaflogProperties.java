package pl.edu.agh.kaflog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;


/**
 * This class holds properties shared across multiple modules.
 * It's contend is hold in kaflog.properties file.
 * It is aimed to keep configuration data.
 *
 * Singleton.
 */
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

    /**
     *
     * @return all properties from kaflog.properties file
     */
    public static Properties getProperties() {
        return PROPERTIES;
    }

    /**
     * Property with given name
     * @param name of property
     * @return value of property
     */
    public static String getProperty(String name) {
        return PROPERTIES.getProperty(name);
    }

    /**
     * Return property with given name converted from string to boolean
     * @param name of property
     * @return value of property
     */
    public static boolean getBoolProperty(String name) {
        return Boolean.parseBoolean(PROPERTIES.getProperty(name));
    }
}
