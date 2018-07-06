package com.discordbots.starlightbot.classes.commands;

import com.discordbots.starlightbot.interfaces.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Azuris on 2017-05-29.
 */
public class SearchCommand implements Command {
    static final String HELP = "\tCommand: s (search)\n\t\tUsage:\n\t\t-s google <query>\n\t\t-s define <word>";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        StringBuilder search = new StringBuilder();
        String cmdlet = args.length > 0 ? args[0].toLowerCase() : "";

        // Was a commandlet called?
        if (!cmdlet.equals("")) {
            // Set up the arguments
            args = initArgs(args);

            for (String arg : args) {
                search.append(arg).append(" ");
            }
        }

        switch (cmdlet) {
            case "google":
                if (!search.toString().isEmpty()) {
                    // We want to be able to send a TEMPORARY message to the user
                    Message msg = new MessageBuilder().append("Searching Top 3 Results...").build();
                    event.getChannel().sendMessage(msg).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));

                    try {
                        event.getChannel().sendMessage(google(search.toString().trim())).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    event.getChannel().sendMessage("I can't search for nothing :expressionless:").queue();
                break;

            case "define":
                if (!search.toString().isEmpty()) { // Did we get any words to search?
                    // We want to be able to send a TEMPORARY message to the user
                    Message msg = new MessageBuilder().append("Searching Definitions...").build();
                    event.getChannel().sendMessage(msg).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));

                    // Check only the first word given
                    String arg = args[0];
                    JSONObject jResults = null;

                    try {   // Might not find definition
                        jResults = new JSONObject(define(arg.trim()))
                                .getJSONArray("results").getJSONObject(0)
                                .getJSONArray("lexicalEntries").getJSONObject(0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        event.getChannel().sendMessage("Error: could not find definition for " + arg).queue();
                        break;
                    }

                    // Further parsing and formatting of JSON
                    JSONArray jDefs = jResults
                            .getJSONArray("entries").getJSONObject(0)
                            .getJSONArray("senses");

                    String word = "**" + arg.substring(0,1).toUpperCase() + arg.substring(1).toLowerCase() + "**";
                    String category = "*" + jResults.getString("lexicalCategory") + "*";

                    String notes = "";
                    try {
                        notes = jResults.getJSONArray("entries").getJSONObject(0)
                                .getJSONArray("notes").getJSONObject(0)
                                .getString("text");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // There could be many definitions to a word
                    String[] definitions = new String[jDefs.length()];

                    // Add each definition to an array for later parsing
                    for (int i = 0; i < jDefs.length(); i++) {
                        JSONObject obj = jDefs.getJSONObject(i);
                        definitions[i] = obj.getJSONArray("definitions").getString(0);
                    }

                    // Format the response
                    StringBuilder def = new StringBuilder("");
                    for (String definition : definitions) {
                        def.append("\n").append("- " + definition.substring(0, 1).toUpperCase() + definition.substring(1));
                    }

                    // Send the response to the channel
                    event.getChannel().sendMessage(word + "\n" + category + "\n").tts(true).queue();
                    event.getChannel().sendMessage(def.toString()).queue();
                    if (!notes.isEmpty()) // Some entries have notes, display any existing
                        event.getChannel().sendMessage("\n\nNotes: " + notes).queue();
                }
                else    // No words given
                    event.getChannel().sendMessage("I can't define nothing. :expressionless:").queue();
                break;

            default:    // Improper command use or no commandlet given, send Help
                event.getChannel().sendMessage(help()).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) { }

    @Override
    public String help() {
        return HELP;
    }

    private String[] initArgs(String[] args){
        if (args.length == 0)
            return new String[]{""};

        ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
        list.remove(0);
        args = new String[args.length-1];
        args = list.toArray(args);
        return args;
    }

    private String google(String search) throws IOException {
        String google = "http://www.google.com/search?q=";
        search = search.replaceAll(" ", "%20");

        StringBuilder results = new StringBuilder();

        String userAgent = "StarlightBot 0.5";
        String charset = "UTF-8";
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
        return results.toString();
    }

    private String define(String search) throws FileNotFoundException {
        final String app_id = "d3e1a0ec";
        final String app_key = "a5eb38f00f7bfb2ac0cf41671f874cbd";

        try {
            // Set up the connection
            URL url = new URL(dictionaryEntries(search));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // Read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();

        }
        catch (Exception e) {   // No results found or word does not exist
            e.printStackTrace();
            return "Could not find any results for " + search;
        }
    }

    private String dictionaryEntries(String word) {
        final String language = "en";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }


}
