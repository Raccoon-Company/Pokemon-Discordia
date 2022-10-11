package game;

import executable.MyBot;
import utils.ButtonManager;
import utils.DiscordManager;
import utils.FileManager;
import utils.MessageManager;

public class Game {
    private MyBot bot;
    private Save save;
    private final DiscordManager discordManager;
    private final MessageManager messageManager;
    private final FileManager fileManager;
    private final ButtonManager buttonManager;

    public Game(MyBot bot, Save save) {
        this.bot = bot;
        this.save = save;
        this.fileManager = new FileManager(bot);
        this.messageManager = new MessageManager(bot);
        this.discordManager = new DiscordManager(bot);
        this.buttonManager = new ButtonManager(bot);
    }

    public void launch() {
        messageManager.send(save.getPrivilegedChannelId(), "SAVE de "+  save.getCampaign().getNom());
    }
}
