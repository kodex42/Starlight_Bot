package StarlightBotOrigin.starlightbot.classes.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import StarlightBotOrigin.starlightbot.interfaces.Command;

/**
 * Created by Azuris on 2017-05-23.
 */
public class PingCommand implements Command {
    public static final String HELP = "\tCommand: ping\n\t\tUsage: -ping";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getChannel().sendMessage("PONG").queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) { }

    @Override
    public String help() {
        return HELP;
    }
}
