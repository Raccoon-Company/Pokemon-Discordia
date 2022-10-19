package utils;

import commands.CommandManager;
import executable.MyBot;
import game.Save;
import game.model.PNJ;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private MyBot bot;
    private DiscordManager discordManager;
    private FileManager fileManager;

    /**
     * Constructeur.
     */
    public MessageManager(MyBot bot) {
        this.bot = bot;
        this.discordManager = new DiscordManager(bot);
        this.fileManager = new FileManager(bot);
    }

    public Message send(long id, String content) {
        MessageChannelUnion mcu = discordManager.getChannelById(id);
        return send(mcu, content);
    }

    public Message send(MessageChannelUnion channel, String content) {
        return channel.sendMessage(content).complete();
    }

    public boolean createPredicate(MessageReceivedEvent e, Save save) {
        return createPredicate(e, save.getPrivilegedChannelId(), save.getUserId());
    }

    public boolean createPredicate(MessageReceivedEvent e, long channelId, long userId) {
        if (e.getChannel().getIdLong() != channelId) // Check that channel is the same
        {
            return false;
        }

        if (e.getAuthor().isBot() || e.getMessage().getContentDisplay().startsWith(CommandManager.PREFIX)) {
            return false;
        }

        return e.getAuthor().getIdLong() == userId; // Check for same author
    }

    public Runnable timeout(MessageChannelUnion channel, User user) {
            bot.unlock(user);
            channel.sendMessage("Délai de réponse dépassé ! Relancez la commande /start pour continuer").queue();
        return () -> {
        };
    }

    public MessageCreateData createMessageThumbnail(PNJ pnj, String content, LayoutComponent lc) {
        return createMessageThumbnailAndImage(pnj, content, lc, null);
    }

    public MessageCreateData createMessageImage(String content, LayoutComponent lc, String image) {
        return createMessageThumbnailAndImage(null, content, lc, image);
    }

    public MessageCreateData createMessageThumbnailAndImage(PNJ pnj, String content, LayoutComponent lc, String image) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(0x5663F7)
                .setDescription(content);

        List<FileUpload> files = new ArrayList<>();

        if(pnj != null){
            embedBuilder.setAuthor(pnj.getNom(), null, "attachment://" + pnj.getIconPath());
            String thumbnailURL = fileManager.getFullPathToIcon(pnj.getIconPath());
            embedBuilder.setThumbnail("attachment://" + pnj.getIconPath());
            File thumbnailFile = new File(thumbnailURL);
            files.add(FileUpload.fromData(thumbnailFile, pnj.getIconPath()));
        }

        if (StringUtils.isNotEmpty(image)) {
            String imageURL = fileManager.getFullPathToImage(image);
            File imgFile = new File(imageURL);
            embedBuilder.setImage("attachment://" + imgFile.getName());
            files.add(FileUpload.fromData(imgFile, imgFile.getName()));
        }

        MessageCreateBuilder mcb = new MessageCreateBuilder();
        mcb.addFiles(files);

        if (lc != null) {
            mcb.addComponents(lc);
        }

        mcb.addEmbeds(embedBuilder.build());

        return mcb.build();
    }

    public MessageCreateData createMessageData(MessageCreateBuilder mcb) {
        return mcb.build();
    }
}
