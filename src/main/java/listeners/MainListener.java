package listeners;

import executable.MyBot;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class MainListener extends ListenerAdapter {

    private final MyBot bot;

    public MainListener(MyBot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        super.onReady(event);
        System.out.println("Le bot est prêt !");
    }


    public MyBot getBot() {
        return bot;
    }
}
