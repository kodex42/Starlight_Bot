package StarlightBotOrigin.starlightbot.interfaces;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Azuris on 2017-05-23.
 */
public interface Command {

    boolean called(String[] args, MessageReceivedEvent event);
    void action(String[] args, MessageReceivedEvent event);
    void executed(boolean success, MessageReceivedEvent event);
    String help();
}
