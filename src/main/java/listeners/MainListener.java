package listeners;

import executable.MyBot;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainListener extends ListenerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(MainListener.class);
    private final MyBot bot;

    public MainListener(MyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        super.onReady(event);
        System.out.println("Le bot est prêt !");
        LOG.info("Le bot est prêt !");

    }


    public MyBot getBot() {
        return bot;
    }
}
