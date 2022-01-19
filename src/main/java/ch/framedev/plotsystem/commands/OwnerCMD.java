package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.commands
 * / ClassName OwnerCMD
 * / Date: 11.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class OwnerCMD implements CommandExecutor {

    private final Main plugin;

    public OwnerCMD(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("claim")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage(plugin.getPrefix() + "§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    plot.setOwner(player.getUniqueId());
                    player.sendMessage(plugin.getPrefix() + "§aClaimed!");
                    plot.createPlot();
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cDu stehst nicht in einem Plot!");
                }
            }
        }
        if (command.getName().equalsIgnoreCase("setowner")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("null");
                        return true;
                    }
                    plot.setOwner(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                    plot.removeOwner(player.getUniqueId());
                    plot.createPlot();
                    player.sendMessage(plugin.getPrefix() + "§aOwner wurde geändert auf §6" + Bukkit.getOfflinePlayer(args[0]).getName());
                }
            }
        }
        if (command.getName().equalsIgnoreCase("addmember")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("null");
                        return true;
                    }
                    plot.addMember(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
                    plot.createPlot();
                    player.sendMessage(plugin.getPrefix() + "§aMember wurde hinzugefügt §6" + Bukkit.getOfflinePlayer(args[0]).getName());
                }
            }
        }
        if (command.getName().equalsIgnoreCase("plotinfo")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    if (Plot.getPlot(player.getLocation()) == null) {
                        System.out.println("null");
                        return true;
                    }
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("null");
                        return true;
                    }
                    player.sendMessage("§aID : §6" + plot.getId());
                    if (plot.hasOwner()) {
                        player.sendMessage("§aOwner : §6" + Bukkit.getOfflinePlayer(plot.getOwner()).getName());
                    } else {
                        player.sendMessage("§cNot Claimed!");
                    }
                    List<String> owners = new ArrayList<>();
                    for (UUID uuid : plot.getOwners()) {
                        owners.add(Bukkit.getOfflinePlayer(uuid).getName());
                    }
                    player.sendMessage("§aOwners : §6" + owners);
                    List<String> members = new ArrayList<>();
                    if (plot.getMembers() == null) plot.setMembers(new ArrayList<>());
                    for (UUID uuid : plot.getMembers()) {
                        members.add(Bukkit.getOfflinePlayer(uuid).getName());
                    }
                    player.sendMessage("§aMembers : §6" + members);
                    List<String> banned = new ArrayList<>();
                    for (UUID uuid : plot.getBannedPlayers()) {
                        banned.add(Bukkit.getOfflinePlayer(uuid).getName());
                    }
                    player.sendMessage("§aBannedPlayers : §6" + banned);
                    List<String> flags = new ArrayList<>();
                    for (Flag flag : plot.getFlags()) {
                        flags.add(flag.getType());
                    }
                    player.sendMessage("§aFlags : §6" + flags);
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cNo Plot found");
                    return true;
                }
            }
        }
        if (command.getName().equalsIgnoreCase("flag")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage(plugin.getPrefix() + "§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (plot.hasOwner()) {
                                if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                    Flag flag = Flag.valueOf(args[1]);
                                    plot.addFlag(flag);
                                    player.sendMessage(plugin.getPrefix() + "§aFlag §6" + flag + " §awurde hinzugefügt!");
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (plot.hasOwner()) {
                                if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                    Flag flag = Flag.valueOf(args[1]);
                                    plot.removeFlag(flag);
                                    player.sendMessage(plugin.getPrefix() + "§aFlag §6" + flag + " §awurde entfernt!");
                                }
                            }
                        }
                        plot.createPlot();
                    }
                }
            }
        }
        if (command.getName().equalsIgnoreCase("phome")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Plot plot = Plot.getPlot(player.getUniqueId());
                if (plot == null) {
                    System.out.println("plot is null");
                    return true;
                }
                player.teleport(plot.getHome());
            }
        }
        if (command.getName().equalsIgnoreCase("psethome")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        System.out.println("plot is null");
                        return true;
                    }
                    if (plot.isOwner(player)) {
                        plot.setHome(player.getLocation());
                        plot.createPlot();
                    }
                }
            }
        }
        if (command.getName().equalsIgnoreCase("bannplayer")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage(plugin.getPrefix() + "§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("add")) {

                            if (plot.hasOwner()) {
                                if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                    if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                                        if (Plot.isPlayerInPlotStatic(Bukkit.getPlayer(args[1]))) {
                                            if (Plot.getPlot(Bukkit.getPlayer(args[1]).getLocation()) != null) {
                                                if (Plot.getPlot(Bukkit.getPlayer(args[1]).getLocation()).hasOwner() && Plot.getPlot(Bukkit.getPlayer(args[1]).getLocation()).getOwners().contains(player.getUniqueId())) {
                                                    Block[] blocks = Plot.getPlot(Bukkit.getPlayer(args[1]).getLocation()).getCuboid().corners();
                                                    for (Block block : blocks) {
                                                        Location location = block.getLocation();
                                                        Bukkit.getPlayer(args[1]).teleport(location.getWorld().getHighestBlockAt(location).getLocation());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    plot.addBannedPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                                    player.sendMessage(plugin.getPrefix() + "§6" + Bukkit.getOfflinePlayer(args[1]).getName() + " §awurde gebannt!");
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (plot.hasOwner()) {
                                if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                    plot.removeBannedPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                                    player.sendMessage(plugin.getPrefix() + "§6" + Bukkit.getOfflinePlayer(args[1]).getName() + " §awurde entbannt!");
                                }
                            }
                        }
                        plot.createPlot();
                    }
                }
            }
        }
        return false;
    }
}
