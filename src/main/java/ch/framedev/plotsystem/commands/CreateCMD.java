package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotsSetup;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.commands
 * / ClassName CreateCMD
 * / Date: 11.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class CreateCMD implements CommandExecutor, Listener {

    private HashMap<Player, HashMap<String, Location>> locations;

    public CreateCMD(Main plugin) {
        this.locations = new HashMap<>();
        plugin.getCommand("create").setExecutor(this);
        plugin.getCommand("pos1").setExecutor(this);
        plugin.getCommand("pos2").setExecutor(this);
        plugin.getCommand("createplot").setExecutor(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("create")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!locations.isEmpty() && locations.containsKey(player)) {
                    new PlotsSetup(new Cuboid(locations.get(player).get("1"), locations.get(player).get("2")))
                            .create(Plot.getHighestId() + 1);
                    player.sendMessage("§aPlots Created!");
                } else {
                    player.sendMessage("§cNo Positions Set!");
                }
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("createplot")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!Plot.isLocationInPlot(player.getLocation()))
                    new Plot(Plot.getHighestId() + 1, player.getLocation().getChunk()).createPlot();
            }
        }
        if (command.getName().equalsIgnoreCase("pos1")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (locations.isEmpty()) {
                    Location location = player.getLocation();
                    location.setY(1);
                    HashMap<String, Location> locs = new HashMap<>();
                    locs.put("1", location);
                    locations.put(player, locs);
                } else {
                    if (locations.containsKey(player) && locations.get(player) != null) {
                        locations.get(player).remove("1");
                    }
                    Location location = player.getLocation();
                    location.setY(1);
                    locations.get(player).put("1", location);
                }
                player.sendMessage("§aPosition 1 Set!");
            }
        }
        if (command.getName().equalsIgnoreCase("pos2")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (locations.isEmpty()) {
                    Location location = player.getLocation();
                    location.setY(200);
                    HashMap<String, Location> locs = new HashMap<>();
                    locs.put("2", location);
                    locations.put(player, locs);
                } else {
                    if (locations.containsKey(player) && locations.get(player) != null) {
                        locations.get(player).remove("2");
                    }
                    Location location = player.getLocation();
                    location.setY(200);
                    locations.get(player).put("2", location);
                }
                player.sendMessage("§aPosition 2 Set!");
            }
        }
        return false;
    }
}
