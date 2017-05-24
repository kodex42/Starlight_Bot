package com.discordbots.starlightbot.classes;

import com.discordbots.starlightbot.classes.commands.PingCommand;
import com.discordbots.starlightbot.classes.utils.CommandParser;
import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Azuris on 2017-05-23.
 */
public class MessageListener extends ListenerAdapter {
    private static JDA jda;
    private static HashMap<String, Command> commands = new HashMap<>();
    private static HashMap<String, File> channelStreams = new HashMap<>();
    private static final CommandParser parser = new CommandParser();

    public static void main(String[] args)
            throws LoginException, RateLimitedException, InterruptedException {
        jda = new JDABuilder(AccountType.BOT).setToken(App.TOKEN).buildBlocking();
        jda.addEventListener(new MessageListener());
        jda.setAutoReconnect(true);

        for (TextChannel channel : jda.getTextChannels()) {
            channelStreams.put(channel.getName(), new File("A:\\CHAT LOGS\\" + channel.getName() + ".txt"));
        }

        commands.put("ping", new PingCommand());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith("-") && !Objects.equals(event.getMessage().getAuthor().getId(), event.getJDA().getSelfUser().getId()))
            Parser.parse(parser.parse(event.getMessage().getContent(), event));
        else {
            try {
                Parser.log(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        }

        private static void log(MessageReceivedEvent event) throws IOException {
            String messageLog = "[" + new Date().toString() + "]:[" + event.getChannel().getName() + "]:[" + event.getAuthor().getName() + "]:" + event.getMessage().getContent() + "\n";
            FileOutputStream fout;
            File file;

            if (channelStreams.containsKey(event.getChannel().getName())) {
                fout = new FileOutputStream(channelStreams.get(event.getChannel().getName()), true);
            } else {
                file = new File("A:\\CHAT LOGS\\" + event.getChannel().getName() + ".txt");
                fout = new FileOutputStream(file, true);
                channelStreams.put(event.getChannel().getName(), file);
            }

            fout.write(messageLog.getBytes());
            fout.close();
        }
    }
}