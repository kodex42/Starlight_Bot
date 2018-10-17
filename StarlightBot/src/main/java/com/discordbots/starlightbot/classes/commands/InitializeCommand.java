package com.discordbots.starlightbot.classes.commands;

import com.discordbots.starlightbot.classes.App;
import com.discordbots.starlightbot.classes.MessageListener;
import com.discordbots.starlightbot.interfaces.AdminOnly;
import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InitializeCommand implements Command, AdminOnly {

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) { return true; }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (isAdmin(event.getAuthor())) {
            MessageListener.logChannel = event.getChannel();
            event.getChannel().sendMessage(help()).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        MessageListener.initialized = true;
    }

    @Override
    public String help() {
        return "Bot Initialized, Channel Locked for Logging at channel " +
                MessageListener.logChannel.getId();
    }

    @Override
    public boolean isAdmin(User user) {
        return user.getDiscriminator().equals(App.ADMIN_DISCRIMINATOR);
    }
}
