package executable;

import com.github.oscar0812.pokeapi.utils.Client;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import game.Launcher;
import game.model.api.PokemonAPI;
import listeners.ButtonListener;
import listeners.CommandListener;
import listeners.MainListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBot {

//    File path = new File("src/main/resources/images"); // base path of the images
//
//    // load source images
//    BufferedImage background = null;
//    BufferedImage overlay = null;
//    BufferedImage overlay2 = null;
//        try {
//        background = ImageIO.read(new File(path, "bg.jpg"));
//        overlay = ImageIO.read(new File(path, "132.png"));
//        overlay2 = ImageIO.read(new File(path, "25.png"));
//    } catch (
//    IOException e) {
//        throw new RuntimeException(e);
//    }
//    // create the new image, canvas size is the max. of both image sizes
////        int w = Math.max(background.getWidth(), overlay.getWidth());
////        int h = Math.max(background.getHeight(), overlay.getHeight());
//    int w = 200;
//    int h = 120;
//    BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//
//    // paint both images, preserving the alpha channels
//    Graphics g = combined.getGraphics();
//        g.drawImage(background, 0, 0, null);
//        g.drawImage(overlay, 0, 60, null);
//        g.drawImage(overlay2, 100, 0, null);
//
//        g.dispose();
//
//// Save as new image
//        try {
//        ImageIO.write(combined, "PNG", new File(path, "combined.png"));
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    }
//}

    private final EventWaiter eventWaiter = new EventWaiter();
    private final Logger logger = LoggerFactory.getLogger(MyBot.class);
    private JDA jda;
    private List<Long> lockedUsers;

    public static void main(String[] args) throws InterruptedException {
        try {
            // args[0] est le token du bot
            new MyBot().start(args[0]);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void start(String token) throws InterruptedException {
        String log4jConfPath = "src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        JDABuilder builder = JDABuilder.createDefault(token)
                // Disable parts of the cache
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                // Enable the bulk delete event
                .setBulkDeleteSplittingEnabled(false)
                // Set activity (like "playing Something")
                .setActivity(Activity.playing("!help"))
                //listeners
                .addEventListeners(
                        new MainListener(this),
                        new CommandListener(this),
                        new Launcher(this),
                        eventWaiter,
                        new ButtonListener());

        //Intents
        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS);


        this.jda = builder.build();
        this.lockedUsers = Collections.synchronizedList(new ArrayList<>());
        jda.awaitReady();
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public JDA getJda() {
        return jda;
    }

    public void setLockedUsers(List<Long> lockedUsers) {
        this.lockedUsers = lockedUsers;
    }

    public List<Long> getLockedUsers() {
        return lockedUsers;
    }

    public void lock(User user){
        lockedUsers.add(user.getIdLong());
    }

    public void unlock(User user){
        lockedUsers.removeIf(u -> u == user.getIdLong());
    }
}
