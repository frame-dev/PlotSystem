package ch.framedev.plotsystem.utils;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    public void sendHelp(CommandSender sender) {
        for (String message : getHelpMessages()) {
            sender.sendMessage(message);
        }
    }

    private List<String> getHelpMessages() {
        List<String> helpMessages = new ArrayList<>();
        helpMessages.add("§aUse §6/claim §afor Creating a Plot within two Locations. When LimitedBlocks is enabled you can Only Claim a limited amount of Blocks. \n" +
                "To set the Locations use §6/pmarker for setting the Locations!");
        helpMessages.add("§aWith the Command §6/plot setowner <PlayerName> §ayou can set the new Owner for the current Plot you stand in! §6You must be the Owner from the Plot!");
        helpMessages.add("§aWith the Command §6/plot flag add <Flag Name> §ayou can add a Flag for the current Plot you stand in! §6You must be the Owner from the Plot!");
        helpMessages.add("§aWith the Command §6/plot flag remove <Flag Name> §ayou can remove a Flag for the current Plot you stand in! §6You must be the Owner from the Plot!");
        helpMessages.add("§aWith the Command §6/plot addmember <PlayerName> §ayou can add a Player as Member to the Current Plot you stand in! §6You must be the Owner from the Plot!");
        helpMessages.add("§aWith the Command §6/plot removemember <PlayerName> §ayou can remove a Player as Member to the Current Plot you stand in! §6You must be the Owner from the Plot!");
        helpMessages.add("§aWith the Command §6/plot sethome §ayou can set the Home from the Plot you stand in!");
        helpMessages.add("§aWith the Command §6/plot home §ayou can Teleport you to the Home from your first plot");
        helpMessages.add("§aWith the Command §6/plot sethome §ayou can set the Home from the Plot you stand in!");
        helpMessages.add("§aWith the Command §6/plot home <Id> §ayou can Teleport you to the Home from the Number!");
        helpMessages.add("§aWith the Command §6/plot info §athis list you all Infos about the Plot you stand in!");
        return helpMessages;
    }
}
