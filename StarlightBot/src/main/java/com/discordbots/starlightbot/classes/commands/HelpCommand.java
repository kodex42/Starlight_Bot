package com.discordbots.starlightbot.classes.commands;

import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Azuris on 2017-05-30.
 */
public class HelpCommand implements Command{
    public static String serverName = "";
    public static String seo = "";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) { return true; }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getChannel().sendMessage(help()).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) { }

    @Override
    public String help() {
        String helpString = "**&(StarlightBot) is an experimental Discord Bot written by " + seo + " in JAVA.**" +
                "\n" +
                "\n```The current features are as follows:" +
                "\n\tChat Logging: & logs chat data from all text channels it has access to." +
                "\n" +
                "\nCommands:" +
                "\n" + PingCommand.HELP +
                "\n" + SearchCommand.HELP +
                "```";
        helpString = helpString.replaceAll("&", serverName);

        return helpString;
    }
}
