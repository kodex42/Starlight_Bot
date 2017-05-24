package com.discordbots.starlightbot.classes;

import com.discordbots.starlightbot.classes.commands.PingCommand;
import com.discordbots.starlightbot.classes.utils.CommandParser;
import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Azuris on 2017-05-23.
 */
public class MessageListener extends ListenerAdapter {
    private static JDA jda;
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, FileOutputStream> channelStreams = new HashMap<>();
    private static final CommandParser parser = new CommandParser();

    public static void main(String[] args)
            throws LoginException, RateLimitedException, InterruptedException {
        jda = new JDABuilder(AccountType.BOT).setToken(App.TOKEN).buildBlocking();
        jda.addEventListener(new MessageListener());
        jda.setAutoReconnect(true);

        for (TextChannel channel : jda.getTextChannels()) {
            try {
                channelStreams.put(channel.getName(), new FileOutputStream(new File("A:\\CHAT LOGS\\" + channel.getName())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        commands.put("ping", new PingCommand());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith("-") && !Objects.equals(event.getMessage().getAuthor().getId(), event.getJDA().getSelfUser().getId()))
            Parser.parse(parser.parse(event.getMessage().getContent(), event));

        if (!event.isFromType(ChannelType.PRIVATE)) {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContent());
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
            else {

            }
        }
    }
}