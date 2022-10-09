package executable;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import game.Launcher;
import listeners.ButtonListener;
import listeners.CommandListener;
import listeners.MainListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class MyBot {

    private final EventWaiter eventWaiter = new EventWaiter();

    private JDA jda;

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
        jda.awaitReady();
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public JDA getJda() {
        return jda;
    }
}
