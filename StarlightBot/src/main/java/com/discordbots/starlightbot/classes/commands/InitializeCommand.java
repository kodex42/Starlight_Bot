package com.discordbots.starlightbot.classes.commands;

import com.discordbots.starlightbot.classes.MessageListener;
import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InitializeCommand implements Command {

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) { return true; }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        MessageListener.logChannel = event.getChannel();
        event.getChannel().sendMessage(help()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        MessageListener.initialized = true;
    }

    @Override
    public String help() {
        return "Bot Initialized, Channel Locked for Logging";
    }
}
