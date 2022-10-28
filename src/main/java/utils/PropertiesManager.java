package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {

    volatile static PropertiesManager instance;

    private static final String PROPERTIES_PATH = "config.properties";

    private static Properties properties;

    private final Logger logger = LoggerFactory.getLogger(PropertiesManager.class);

    /**
     * Constructeur.
     */
    PropertiesManager() {
        properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(PROPERTIES_PATH)));
        } catch (Exception e) {
           logger.error("Erreur chargement du fichier des propriétés", e);
        }
    }

    /**
     * Retourne l'instance de PropertiesManager.
     */
    public static PropertiesManager getInstance() {
        if (instance == null) {
            synchronized (PropertiesManager.class) {
                if (instance == null) {
                    instance = new PropertiesManager();
                }
            }
        }
        return instance;
    }

    public String getProp(String id) {
        return properties.getProperty(id);
    }

    public String getImage(String id) {
        return getProp("images." + id);
    }
}
