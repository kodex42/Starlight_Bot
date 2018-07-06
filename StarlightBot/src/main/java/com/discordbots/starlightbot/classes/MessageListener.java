package com.discordbots.starlightbot.classes;

import com.discordbots.starlightbot.classes.commands.SearchCommand;
import com.discordbots.starlightbot.classes.commands.HelpCommand;
import com.discordbots.starlightbot.classes.commands.InitializeCommand;
import com.discordbots.starlightbot.classes.commands.PingCommand;
import com.discordbots.starlightbot.classes.utils.CommandParser;
import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Azuris on 2017-05-23.
 */
public class MessageListener extends ListenerAdapter {
    private static JDA jda;
    private static HashMap<String, Command> commands = new HashMap<>();
    private static final CommandParser parser = new CommandParser();

    static final int TEST_VERSION = 1;
    static final int PROD_VERSION = 0;
    static final int currentVer = PROD_VERSION;
    public static boolean initialized = false;
    public static MessageChannel logChannel;

    public static void main(String[] args)
            throws LoginException, InterruptedException {
        jda = new JDABuilder(AccountType.BOT).setToken(App.TOKEN[currentVer]).buildBlocking();
        jda.addEventListener(new MessageListener());
        jda.setAutoReconnect(true);

        commands.put("ping", new PingCommand());
        commands.put("s", new SearchCommand());
        commands.put("help", new HelpCommand());
        commands.put("init", new InitializeCommand());
        HelpCommand.serverName = "Starlight";
        HelpCommand.supremeExecutiveOverlord = jda.getUserById("158745818688389121").getAsMention();
    }

    private void parseNonCommand(MessageReceivedEvent event) {
        String channelName = event.getChannel().getName();
        Message msg = event.getMessage();

        switch (channelName) {
            case "images":
                msg.getAttachments();
                break;
                default:
                    Parser.log(event);

        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {


        if (event.getMessage().getContentDisplay().startsWith("-"))     // Catch commands
            Parser.parse(parser.parse(event.getMessage().getContentDisplay(), event));
        else if (initialized && event.getAuthor() != jda.getSelfUser()) // Parse everything else
            parseNonCommand(event);

        if (!event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }
    }

    static class Parser {
        static void parse(CommandParser.CommandContainer cmd) {
            if (commands.containsKey(cmd.invoke)) {
                boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

                if (safe) {
                    cmd.event.getTextChannel().canTalk();
                    commands.get(cmd.invoke).action(cmd.args, cmd.event);
                    commands.get(cmd.invoke).executed(true, cmd.event);
                } else {
                    commands.get(cmd.invoke).executed(false, cmd.event);
                }
            }
        }

        private static void log(MessageReceivedEvent event) {
            String logMessage = "`[" + new Date().toString() + "] : " +
                    "[" + event.getChannel().getName() + "] : " +
                    "[" + event.getAuthor().getName() + "]` : " +
                    "**" + event.getMessage().getContentDisplay() + "**\n";
            logChannel.sendMessage(logMessage).queue();
        }
    }
}