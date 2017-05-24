package com.discordbots.starlightbot.classes.commands;


import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Azuris on 2017-05-23.
 */
public class PingCommand implements Command {

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
        return "USAGE: -ping";
    }
}
