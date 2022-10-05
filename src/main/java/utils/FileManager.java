package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Save;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {

    public static final String SAVE_REPO = PropertiesManager.getInstance().getProp("saves-path");

    volatile static FileManager instance;

    /**
     * Constructeur.
     */
    FileManager() {
    }

    /**
     * Retourne l'instance de FileManager.
     */
    public static FileManager getInstance() {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    //Créer le répoertoire des saves pour ce joueur
    private void createSavesDirectoryIfNotExists(String idDiscord) {
        try {
            Files.createDirectories(Paths.get(getPersonalSaveRepo(idDiscord)));
        } catch (IOException ioException) {
            //TODO log erreurs
        }
    }

    public Save writeSave(Save save) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        createSavesDirectoryIfNotExists(save.getIdUser());
        File file = new File(getSavePath(save));
        try {
            objectMapper.writeValue(file, save);
//            Files.delete(file.toPath());
        } catch (IOException ioException) {
//TODO log error
        }
        return save;
    }

    public Save getSave(File file) {

        Save save = null;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        Path path = Paths.get(file.getPath());

        String fileContent = null;
        try {
            List<String> lines = Files.readAllLines(path);
            fileContent = lines.isEmpty() ? "{}" : lines.get(0);
        } catch (IOException ioException) {
//TODO log error
            System.err.println(ioException.getMessage());
        }

        try {
            save = objectMapper.readValue(fileContent, new TypeReference<Save>() {
            });

        } catch (JsonProcessingException e) {
//TODO log error
            System.err.println(e.getMessage());
        }
        return save;
    }


    public List<Save> getSaves(String idDiscord) {

        //On créé le fichier et le répertoire de save des campagnes s'il n'existe pas
        createSavesDirectoryIfNotExists(idDiscord);

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File(getPersonalSaveRepo(idDiscord));

        //conversion des fichiers en leur save
        return Arrays.stream(f.listFiles()).map(this::getSave).collect(Collectors.toList());
    }

    public String getPersonalSaveRepo(String idDiscord) {
        return SAVE_REPO + System.getProperty("file.separator") + idDiscord;
    }

    public String getSavePath(Save save) {
        return getPersonalSaveRepo(save.getIdUser()) + System.getProperty("file.separator") + save.getId() + ".json";
    }

    public String getFullPathToIcon(String nom) {
        URL is = getClass().getClassLoader().getResource("images/pnj/" + nom);
        if (is == null) {
            return null;
        }
        return is.getPath();

    }

    public String getFullPathToImage(String nom) {
        URL is = getClass().getClassLoader().getResource("images/background/" + nom);
        if (is == null) {
            return null;
        }
        return is.getPath();
    }
}
