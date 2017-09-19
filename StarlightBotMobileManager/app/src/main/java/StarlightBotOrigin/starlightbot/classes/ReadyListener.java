package StarlightBotOrigin.starlightbot.classes;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;

/**
 * Created by Azuris on 2017-05-23.
 */
public class ReadyListener implements EventListener {
    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException  {
        // Note: It is important to register your com.discordbots.starlightbot.classes.ReadyListener before building
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(App.TOKEN)
                .addEventListener(new ReadyListener())
                .buildBlocking();
        jda.setAutoReconnect(true);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }
}
