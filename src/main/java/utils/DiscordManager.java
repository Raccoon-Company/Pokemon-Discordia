package utils;

import executable.MyBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class DiscordManager {
    private final MyBot bot;


    public DiscordManager(MyBot instance) {
        bot = instance;
    }

    public MessageChannelUnion getChannelById(long id) {
        return bot.getJda().getChannelById(MessageChannelUnion.class, id);
    }

    public User getUserById(long id) {
        return bot.getJda().retrieveUserById(id).complete();
    }
}
