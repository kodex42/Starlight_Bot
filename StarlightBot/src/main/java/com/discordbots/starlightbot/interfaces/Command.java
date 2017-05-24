package com.discordbots.starlightbot.interfaces;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Azuris on 2017-05-23.
 */
public interface Command {

    public boolean called(String[] args, MessageReceivedEvent event);
    public void action(String[] args, MessageReceivedEvent event);
    public void executed(boolean success, MessageReceivedEvent event);
    public String help();
}
