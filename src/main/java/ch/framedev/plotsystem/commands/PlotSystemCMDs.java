package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.commands
 * / ClassName PlotSystemCMDs
 * / Date: 01.03.22
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotSystemCMDs implements CommandExecutor {

    private final Main plugin;

    public PlotSystemCMDs(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("plotsystem").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("plotsystem")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("plotsystem.help")) {
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
                    for (String message : helpMessages) {
                        sender.sendMessage(message);
                    }
                }
            }
        }
        return false;
    }
}
