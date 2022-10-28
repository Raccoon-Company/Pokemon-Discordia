package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import executable.MyBot;
import game.Save;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {

    private final Logger logger = LoggerFactory.getLogger(FileManager.class);

    public static final String SAVE_REPO = PropertiesManager.getInstance().getProp("saves-path");

    private final MyBot bot;

    /**
     * Constructeur.
     */
    public FileManager(MyBot bot) {
        this.bot = bot;
    }

    //Créer le répoertoire des saves pour ce joueur
    private void createSavesDirectoryIfNotExists(long idDiscord) {
        try {
            Files.createDirectories(Paths.get(getPersonalSaveRepo(idDiscord)));
        } catch (IOException ioException) {
            logger.error("erreur création dossier de sauvegarde",ioException);
        }
    }

    public Save writeSave(Save save) {
        save.setLastPlayed(new Date());
        DiscordManager discordManager = new DiscordManager(bot);
        User user = discordManager.getUserById(save.getUserId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        createSavesDirectoryIfNotExists(user.getIdLong());
        File file = new File(getSavePath(save));
        try {
            objectMapper.writeValue(file, save);
        } catch (IOException ioException) {
            logger.error("Erreur écriture de la sauvegarde", ioException);
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
            logger.error("Erreur lecture de la sauvegarde", ioException);
            System.err.println(ioException.getMessage());
        }

        try {
            save = objectMapper.readValue(fileContent, new TypeReference<Save>() {
            });

        } catch (JsonProcessingException e) {
            logger.error("Erreur conversion de la sauvegarde", e);
            System.err.println(e.getMessage());
        }
        return save;
    }


    public List<Save> getSaves(long idDiscord) {

        //On créé le fichier et le répertoire de save des campagnes s'il n'existe pas
        createSavesDirectoryIfNotExists(idDiscord);

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File(getPersonalSaveRepo(idDiscord));

        //conversion des fichiers en leur save
        return Arrays.stream(f.listFiles()).map(this::getSave).collect(Collectors.toList());
    }

    public String getPersonalSaveRepo(long idDiscord) {
        return SAVE_REPO + System.getProperty("file.separator") + idDiscord;
    }

    public String getSavePath(Save save) {
        return getPersonalSaveRepo(save.getUserId()) + System.getProperty("file.separator") + save.getId() + ".json";
    }

    public String getFullPathToIcon(String nom) {
        URL is = getClass().getClassLoader().getResource("images/pnj/" + nom);
        if (is == null) {
            return null;
        }
        return is.getPath();

    }

    public String getFullPathToImage(String nom) {
        URL is = getClass().getClassLoader().getResource("images/" + nom);
        if (is == null) {
            return null;
        }
        return is.getPath();
    }

    public boolean deleteSave(long idUser, long idSave) {
        List<Save> saves = getSaves(idUser);
        return saves.stream().filter(s -> s.getId() == idSave).findAny().map(s -> {
            File file = new File(getSavePath(s));
            try {
                Files.delete(file.toPath());
                return true;
            } catch (IOException ioException) {
                logger.warn("Suppression de la sauvegarde " + idSave + " de l'user " + idUser + " impossible.");
                return false;
            }
        }).orElse(false);
    }
}
