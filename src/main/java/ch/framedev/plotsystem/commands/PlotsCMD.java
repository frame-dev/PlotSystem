package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem
 * / ClassName PlotsCMD
 * / Date: 19.10.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotsCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public PlotsCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("plot").setExecutor(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String arg0 = args[0];
            if (arg0.equalsIgnoreCase("claim")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if(!player.hasPermission("plotsystem.plot.claim")) {
                        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                        return true;
                    }
                    if (Plot.isPlayerInPlotStatic(player)) {
                        Plot plot = Plot.getPlot(player.getLocation());
                        if (plot == null) {
                            player.sendMessage("§cDu stehst nicht in einem Plot!");
                            return true;
                        }
                        plot.setOwner(player.getUniqueId());
                        player.sendMessage("§aClaimed!");
                        plot.createPlot();
                    } else {
                        player.sendMessage("§cDu stehst nicht in einem Plot!");
                    }
                }
            }
            if (arg0.equalsIgnoreCase("buy")) {
                Player player = (Player) sender;
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    if (plot.isBuyable()) {
                        if(plot.buyPlot(player)) {
                            player.sendMessage(plugin.getPrefix() + "§aDir gehört nun dieses Plot!");
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cFehler beim Kaufen dieses Plots!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cDieses Plot kann nicht gekauft werden!");
                    }
                }
            }
            if (arg0.equalsIgnoreCase("info")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.info")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }
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
                    player.sendMessage("§aAverage Light Level : §6" + plot.getCuboid().getAverageLightLevel());
                    player.sendMessage("§aPrice : §6" + plot.getPrice());
                } else {
                    player.sendMessage("§cNo Plot found");
                }
                return true;
            }
            if (arg0.equalsIgnoreCase("home")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!PlotManager.getInstance().getPlotHomes(player).isEmpty() && PlotManager.getInstance().getPlotHomes(player).size() > 0 && Plot.getPlot(PlotManager.getInstance().getPlotHomes(player).get(0)) == null) {
                        System.out.println("plot is null");
                    }
                    player.teleport(Plot.getPlot(PlotManager.getInstance().getPlotHomes(player).get(0)).getHome());
                }
            }
            if (arg0.equalsIgnoreCase("sethome")) {
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
        } else if (args.length == 2) {
            if(args[0].equalsIgnoreCase("setprice")) {
                double price = Double.parseDouble(args[1]);
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if(Plot.isPlayerInPlotStatic(player)) {
                        Plot plot = Plot.getPlot(player.getLocation());
                        if (plot == null) {
                            player.sendMessage(plugin.getPrefix() + "§cPlot is null!");
                            return true;
                        }
                        if(plot.isOwner(player)) {
                            plot.setPrice(price);
                            player.sendMessage(plugin.getPrefix() + "§aDu hast denn Preis auf §6" + price + " §agesetzt!");
                            plot.createPlot();
                            return true;
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("home")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    try {
                        if (!PlotManager.getInstance().getPlotHomes(player).isEmpty() && PlotManager.getInstance().getPlotHomes(player).size() > 0 && Plot.getPlot(PlotManager.getInstance().getPlotHomes(player).get(Integer.parseInt(args[1]) - 1)) == null) {
                            System.out.println("plot is null");
                            return true;
                        }
                        player.teleport(Plot.getPlot(PlotManager.getInstance().getPlotHomes(player).get(Integer.parseInt(args[1]) - 1)).getHome());
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                        return true;
                    }
                }
            }
            if (args[0].equalsIgnoreCase("addmember")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.addmember")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }

                if (!Plot.isPlayerInPlotStatic(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cNot Plot found!");
                    return true;
                }

                Plot plot = Plot.getPlot(player.getUniqueId());
                if (plot == null) {
                    player.sendMessage(plugin.getPrefix() + "§cPlot is null!");
                    return true;
                }

                plot.addMember(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                plot.createPlot();
                player.sendMessage("§aMember wurde hinzugefügt §6" + Bukkit.getOfflinePlayer(args[1]).getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("removemember")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.removemember")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }

                if (!Plot.isPlayerInPlotStatic(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cNot Plot found!");
                    return true;
                }

                Plot plot = Plot.getPlot(player.getUniqueId());
                if (plot == null) {
                    player.sendMessage(plugin.getPrefix() + "§cPlot is null!");
                    return true;
                }

                plot.removeMember(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                plot.createPlot();
                player.sendMessage("§cMember wurde entfernt §6" + Bukkit.getOfflinePlayer(args[1]).getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("setowner")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.setowner")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("null");
                        return true;
                    }
                    plot.removeOwner(player.getUniqueId());
                    plot.setOwner(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    plot.createPlot();
                    player.sendMessage("§aOwner wurde geändert auf §6" + Bukkit.getOfflinePlayer(args[1]).getName());
                }
            }
            if (args[0].equalsIgnoreCase("removeowner")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.removeowner")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("null");
                        return true;
                    }
                    plot.removeOwner(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    plot.createPlot();
                    player.sendMessage("§cOwner §6" + Bukkit.getOfflinePlayer(args[1]).getName() + " §cwurde entfernt!");
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("flag")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§cDu bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.flag")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage(plugin.getPrefix() + "§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("add")) {
                        if (plot.hasOwner()) {
                            if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                Flag flag = Flag.valueOf(args[2]);
                                plot.addFlag(flag);
                                player.sendMessage("§aFlag §6" + flag + " §awurde hinzugefügt!");
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (plot.hasOwner()) {
                            if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                Flag flag = Flag.valueOf(args[2]);
                                plot.removeFlag(flag);
                                player.sendMessage("§aFlag §6" + flag + " §awurde entfernt!");
                            }
                        }
                    }
                    plot.createPlot();
                }
            }
            if (args[0].equalsIgnoreCase("banplayer")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + "§c Du bist kein Spieler!");
                    return true;
                }

                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.ban")) {
                    player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                    return true;
                }
                if (Plot.isPlayerInPlotStatic(player)) {
                    Plot plot = Plot.getPlot(player.getLocation());
                    if (plot == null) {
                        player.sendMessage("§cDu stehst nicht in einem Plot!");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("add")) {

                        if (plot.hasOwner()) {
                            if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                if (Bukkit.getOfflinePlayer(args[2]).isOnline()) {
                                    if (Plot.isPlayerInPlotStatic(Bukkit.getPlayer(args[2]))) {
                                        if (Plot.getPlot(Bukkit.getPlayer(args[2]).getLocation()) != null) {
                                            if (Plot.getPlot(Bukkit.getPlayer(args[2]).getLocation()).hasOwner() && Plot.getPlot(Bukkit.getPlayer(args[2]).getLocation()).getOwners().contains(player.getUniqueId())) {
                                                Block[] blocks = Plot.getPlot(Bukkit.getPlayer(args[2]).getLocation()).getCuboid().corners();
                                                for (Block block : blocks) {
                                                    Location location = block.getLocation();
                                                    Bukkit.getPlayer(args[2]).teleport(location.getWorld().getHighestBlockAt(location).getLocation());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                plot.addBannedPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                                player.sendMessage("§6" + Bukkit.getOfflinePlayer(args[2]).getName() + " §awurde gebannt!");
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (plot.hasOwner()) {
                            if (plot.getOwners() != null && plot.getOwners().contains(player.getUniqueId())) {
                                plot.removeBannedPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                                player.sendMessage("§6" + Bukkit.getOfflinePlayer(args[2]).getName() + " §awurde entbannt!");
                            }
                        }
                    }
                    plot.createPlot();
                }
            }
            if (args[0].equalsIgnoreCase("visit")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!player.hasPermission("plotsystem.plot.visit")) {
                        player.sendMessage(plugin.getPrefix() + "§cKeine Permissions!");
                        return true;
                    }
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[1]);
                    try {
                        if (!PlotManager.getInstance().getPlotHomes(offPlayer).isEmpty() && PlotManager.getInstance().getPlotHomes(offPlayer).size() > 0 && Plot.getPlot(PlotManager.getInstance().getPlotHomes(offPlayer).get(Integer.parseInt(args[2]) - 1)) == null) {
                            System.out.println("plot is null");
                            return true;
                        }
                        player.teleport(Plot.getPlot(PlotManager.getInstance().getPlotHomes(offPlayer).get(Integer.parseInt(args[2]) - 1)).getHome());
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("flag")) {
                List<String> flags = new ArrayList<>();
                for (Flag flag : Flag.values()) {
                    flags.add(flag.getType());
                }
                List<String> empty = new ArrayList<>();
                for (String s : flags) {
                    if (s.toLowerCase().startsWith(args[2].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("flag")) {
                List<String> commands = new ArrayList<>();
                commands.add("add");
                commands.add("remove");
                List<String> empty = new ArrayList<>();
                for (String s : commands) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            } else if (args[0].equalsIgnoreCase("banplayer")) {
                List<String> commands = new ArrayList<>();
                commands.add("add");
                commands.add("remove");
                List<String> empty = new ArrayList<>();
                for (String s : commands) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            }
        } else if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("claim");
            commands.add("addmember");
            commands.add("phome");
            commands.add("buy");
            commands.add("psethome");
            commands.add("setowner");
            commands.add("banplayer");
            commands.add("flag");
            commands.add("visit");
            commands.add("removemember");
            commands.add("removeowner");
            commands.add("setprice");
            List<String> empty = new ArrayList<>();
            for (String s : commands) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    empty.add(s);
            }
            Collections.sort(empty);
            return empty;
        }
        return null;
    }
}
