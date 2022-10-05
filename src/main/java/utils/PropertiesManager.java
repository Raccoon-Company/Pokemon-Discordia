package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesManager {

    volatile static PropertiesManager instance;

    private static final String PROPERTIES_PATH = "config.properties";

    private static Properties properties;

    /**
     * Constructeur.
     */
    PropertiesManager() {
        properties = new Properties();
        try{
            properties.load(new FileInputStream(PROPERTIES_PATH));
        }catch (Exception e){
            //TODO log
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

    public String getProp(String id){
        return properties.getProperty(id);
    }

}
