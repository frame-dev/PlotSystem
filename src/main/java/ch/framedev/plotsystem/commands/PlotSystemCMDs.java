package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.utils.Utilities;
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

    public PlotSystemCMDs(Main plugin) {
        plugin.getCommand("plotsystem").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("plotsystem")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("plotsystem.help")) {
                    new Utilities().sendHelp(sender);
                }
            }
        }
        return false;
    }
}
