package com.discordbots.starlightbot.interfaces;

import net.dv8tion.jda.core.entities.User;

/**
 * Created by Azuris on 2018-10-02.
 */
public interface AdminOnly {

    boolean isAdmin(User user);
}
