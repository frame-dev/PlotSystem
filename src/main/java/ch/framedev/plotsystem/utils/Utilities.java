package ch.framedev.plotsystem.utils;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {

    public void sendHelp(CommandSender sender) {
        for (String message : getHelpMessages()) {
            sender.sendMessage(message);
        }
    }

    private List<String> getHelpMessages() {
        return Arrays.asList(
                "§aUse §6/claim §afor creating a plot within two locations. When LimitedBlocks is enabled, you can only claim a limited amount of blocks. \n" +
                        "To set the locations, use §6/pmarker to mark them!",
                "§aUse §6/plot setowner <PlayerName> §ato set a new owner for the plot you're standing in. §6You must be the owner of the plot!",
                "§aUse §6/plot flag add <Flag Name> §ato add a flag to the plot you're standing in. §6You must be the owner of the plot!",
                "§aUse §6/plot flag remove <Flag Name> §ato remove a flag from the plot you're standing in. §6You must be the owner of the plot!",
                "§aUse §6/plot addmember <PlayerName> §ato add a player as a member to the plot you're standing in. §6You must be the owner of the plot!",
                "§aUse §6/plot removemember <PlayerName> §ato remove a player as a member from the plot you're standing in. §6You must be the owner of the plot!",
                "§aUse §6/plot sethome §ato set the home location for the plot you're standing in.",
                "§aUse §6/plot home §ato teleport to the home of your first plot.",
                "§aUse §6/plot home <Id> §ato teleport to the home of a specific plot.",
                "§aUse §6/plot info §ato list all information about the plot you're standing in."
        );
    }
}
