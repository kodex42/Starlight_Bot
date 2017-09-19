package StarlightBotOrigin.starlightbot.classes.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import StarlightBotOrigin.starlightbot.interfaces.Command;

/**
 * Created by Azuris on 2017-05-29.
 */
public class GoogleCommand implements Command {
    public static final String HELP = "\tCommand: google\n\t\tUsage: -google search <query>";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        String cmdlet = args.length > 0 ? args[0] : "";

        switch (cmdlet) {
            case "search":
                ArrayList<String> list = new ArrayList<>();
                list.addAll(Arrays.asList(args));
                list.remove(0);
                args = new String[args.length-1];
                args = list.toArray(args);

                event.getChannel().sendMessage("Searching Top 3 Results").queue();

                StringBuilder search = new StringBuilder();

                for (int i = 0; i < args.length; i++) {
                    search.append(args[i]).append(" ");
                }

                try {
                    google(search.toString().trim(), event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                event.getChannel().sendMessage(help()).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) { }

    @Override
    public String help() {
        return HELP;
    }

    private void google(String search, MessageReceivedEvent event) throws IOException {
        String google = "http://www.google.com/search?q=";
        String charset = "UTF-8";
        String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!
        search.replaceAll(" ", "%20");

        StringBuilder results = new StringBuilder();

        Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");

        int i = 0;
        for (Element link : links) {
            if (i >= 3)
                break;

            String title = link.text();
            String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

            if (!url.startsWith("http")) {
                continue; // Ads/news/etc.
            }

            results.append("Title: ").append(title).append("\n").append("URL: ").append(url).append("\n");
            i++;
        }
        event.getChannel().sendMessage(results.toString()).queue();
    }
}
