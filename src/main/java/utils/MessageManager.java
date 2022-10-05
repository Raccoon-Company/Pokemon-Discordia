package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    volatile static MessageManager instance;

    /**
     * Constructeur.
     */
    MessageManager() {
    }

    /**
     * Retourne l'instance de MessageManager.
     */
    public static MessageManager getInstance() {
        if (instance == null) {
            synchronized (MessageManager.class) {
                if (instance == null) {
                    instance = new MessageManager();
                }
            }
        }
        return instance;
    }

    public Message send(MessageChannelUnion channel, String content) {
        return channel.sendMessage(content).complete();
    }
    public Message sendMessageEmbedThumbnail(MessageChannelUnion channel, String content, String thumbnail, String authorName, int color) {
        return sendMessageEmbed(channel, content,  thumbnail, null, authorName, color, null);
    }
    public Message sendMessageEmbedThumbnail(MessageChannelUnion channel, String content, String thumbnail, String authorName, int color, List<Button> buttons) {
        return sendMessageEmbed(channel, content,  thumbnail, null, authorName, color, buttons);
    }

    public Message sendMessageEmbedImage(MessageChannelUnion channel, String content,  String image, String authorName, int color, List<Button> buttons) {
        return sendMessageEmbed(channel, content,  null, image, authorName, color, buttons);
    }

    public Message sendMessageEmbed(MessageChannelUnion channel, String content, String thumbnail, String image, String authorName, int color, List<Button> buttons) {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(color)
                .setAuthor(authorName, null, "attachment://thumbnail.png")
                .setDescription(content);

        List<FileUpload> files = new ArrayList<>();

        if (StringUtils.isNotEmpty(image)) {
            String imageURL = FileManager.getInstance().getFullPathToImage(image);
            embedBuilder.setImage("attachment://image.png");
            File imgFile = new File(imageURL);
            files.add(FileUpload.fromData(imgFile, "image.png"));
        } else if (StringUtils.isNotEmpty(thumbnail)) {
            String thumbnailURL = FileManager.getInstance().getFullPathToIcon(thumbnail);
            embedBuilder.setThumbnail("attachment://thumbnail.jpg");
            File thumbnailFile = new File(thumbnailURL);
            files.add(FileUpload.fromData(thumbnailFile, "thumbnail.jpg"));
        }

        if (StringUtils.isNotEmpty(image)) {
            String imageURL = FileManager.getInstance().getFullPathToImage(image);
            embedBuilder.setImage("attachment://image.png");
            File imgFile = new File(imageURL);
            files.add(FileUpload.fromData(imgFile, "image.png"));
        }

        MessageCreateBuilder mcb = new MessageCreateBuilder();
        mcb.addFiles(files);
        if(buttons != null){
            LayoutComponent lc = ActionRow.of(buttons);
            mcb.addComponents(lc);
        }

        mcb.addEmbeds(embedBuilder.build());
        return channel.sendMessage(mcb.build()).complete();
    }
}//TODO on n'a plus l'icone
