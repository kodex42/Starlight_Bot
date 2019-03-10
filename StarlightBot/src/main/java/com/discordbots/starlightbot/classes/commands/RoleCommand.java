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

import java.util.Arrays;
import java.awt.Color;

/**
 * Created by Azuris on 2019-03-09.
 */
public class RoleCommand implements Command {
    static final String HELP = "\tCommand: role\n\t\tUsage:" +
		"\n\t\t-role set <personal role name>" + 
		"\n\t\t-role color <hex value ex: '80AAFF'>" +
		"\n\t\t-role @able <0 (off) or 1 (on)>" +
		"\n\t\t-role give <access role name>";

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        String cmdlet = args.length > 0 ? args[0].toLowerCase() : "";
		
		// Get the roles of the user that sent the message
		List<Role> userRoles = event.getMember().getRoles();
		List<String> nonPrivateRoleNames = Arrays.asList(App.RESERVED_ROLES);
		String privateRoles;
		for (int i = 0; i < userRoles.size(); i++) {
			String rName = userRoles.get(i).getName();
			// Does the user have a private role?
			if (nonPrivateRoleNames.contains(rName))
				privateRole = rName;
				break;
		}

        switch (cmdlet) {
			case "set":		// Set the personal role for the user
				if (privateRole.equals(""))
					setRole(privateRole, event);
				else
					event.getChannel().sendMessage("You can't assign yourself more than one personal role. Contact your moderators if you would like a name change for your personal role.").queue();
				break;
			case "color":	// Change the role color of the personal role belonging to the user
				if (!privateRole.equals(""))
					editRoleColor(privateRole, args[1], event);
				else
					event.getChannel().sendMessage("You don't have a personal role to change the color of. Give yourself a personal role with the command -role set <personal role name>.").queue();
				break;
			case "@able":
				if (!privateRole.equals(""))
					makeAtable(privateRole, Boolean.valueOf(args[1]), event);
				else
					event.getChannel().sendMessage("You can't make your personal role @able if you don't have one. Give yourself a personal role with the command -role set <personal role name>.").queue();
				break;
			case "give":	// Give the requested access role to the user
				if (!Arrays.asList(App.BANLIST).contains(privateRole))
					giveRole(args[1], event);
				else
					if (privateRole().equals(""))
						event.getChannel().sendMessage("You must have your own personal role in order to give yourself access roles. Give yourself a personal role with the command -role set <personal role name>.").queue();
					else
						event.getChannel().sendMessage("You are currently banned from assigning yourself access roles. Contact your moderators to request access roles.").queue();
				break;
            default:		// Improper command use or no commandlet given, send Help
                event.getChannel().sendMessage(help()).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) { }

    @Override
    public String help() {
        return HELP;
    }

	private void setRole(String roleName, MessageRecievedEvent event) {
		// Set the user's personal role
	}

	private void editRoleColor(String roleName, String hexColor, MessageRecievedEvent event) {
		// Did we get a proper hex code?
		if (isNumeric(hexColor)) {
			// Change the role's color here
			List<Roles> roles = MessageListener.jda.getGuildsByName("Kingdom", false).get(0).getRoles();
			for (int i = 0; i < roles.size(); i++) {
				Role r = roles.get(i);
				if (r.getName().equals(roleName)) {
					RoleManager rm = new RoleManager(r);
					Color color = new Color(
						Integer.valueOf(hexColor.substring(0, 2), 16),
					    Integer.valueOf(hexColor.substring(2, 4), 16),
						Integer.valueOf(hexColor.substring(4, 6), 16)
					);
					rm.setColor(color);
					event,getChannel().sendMessage("Your new role color has been set.").queue();
				}
			}
		}
		else
			event.getChannel().sendMessage(hexColor + " is not a valid hexadecimal value. Please only use valid hexadecimal values for color changes.").queue();
	}

	private static boolean isNumeric(String cadena) {
	    if (cadena.length() == 0 || (cadena.charAt(0) != '-' && Character.digit(cadena.charAt(0), 16) == -1))
			return false;
		if (cadena.length() == 1 && cadena.charAt(0) == '-')
		    return false;
	    for (int i = 1 ; i < cadena.length() ; i++)
			if (Character.digit(cadena.charAt(i), 16) == -1)
				return false;
		return true;
	}

	private void makeAtable(String roleName, boolean atable, MessageRecievedEvent event) {
		// Make the user's personal role @able
		List<Roles> roles = MessageListener.jda.getGuildsByName("Kingdom", false).get(0).getRoles();
		for (int i = 0; i < roles.size(); i++) {
			Role r = roles.get(i);
			if (r.getName().equals(roleName)) {
				RoleManager rm = new RoleManager(r);
				rm.setMentionable(atable);
				break;
			}
		}
		event.getChannel().sendMessage("Your role " + roleName + " is now @able.").queue();
	}

	private void giveRole(String roleName, MessageRecievedEvent event) {
		// Give the user the requested access role
		switch(roleName) {
			case "Unfiltered":
				// Give unfiltered
				break;
			case "Homework":
				// Give homework pass
				break;
			default:
				event.getChannel().sendMessage("There is no access role named " + roleName).queue();
		}
	}
}
