package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.main.PlotSystemAPI;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotManager;
import ch.framedev.plotsystem.plots.PlotStatus;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import java.util.Objects;
import java.util.UUID;

/**
 * Plot Commands
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
        Objects.requireNonNull(plugin.getCommand("plot")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("plot")).setTabCompleter(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String arg0 = args[0];
            if(arg0.equalsIgnoreCase("help")) {
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
                    helpMessages.add("§aWith the Command §6/plot sethome <id> §acan be set the Home from the Plot you stand in!");
                    helpMessages.add("§aWith the Command §6/plot home <Id> §ayou can Teleport you to the Home from the Number!");
                    helpMessages.add("§aWith the Command §6/plot info §athis list you all Infos about the Plot you stand in!");
                    helpMessages.add("§aWith the Command §6/plot list §ayou can list your plots.");
                    helpMessages.add("§aWith the Command §6/plot stats §ayou can see plot statistics.");
                    helpMessages.add("§aWith the Command §6/plot flags §ayou can list available flags.");
                    helpMessages.add("§aWith the Command §6/plot unclaim §ayou can release the plot you stand in.");
                    helpMessages.add("§aWith the Command §6/plot flag clear §ayou can remove all flags from the current plot.");
                    helpMessages.add("§aWith the Command §6/plot flag defaults §ayou can apply the configured default flags.");
                    helpMessages.add("§aWith the Command §6/plot gamemode <mode|clear> §aan admin can auto-change gamemode inside the plot.");
                    for (String message : helpMessages) {
                        sender.sendMessage(message);
                    }
                }
            }
            if (arg0.equalsIgnoreCase("claim")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.claim")) return true;
                Plot plot = getCurrentPlot(player, "§cDu stehst nicht in einem Plot!");
                if (plot == null) return true;
                if (PlotSystemAPI.getAPI().claimPlot(plot, player)) {
                    player.sendMessage(plugin.getPrefix() + "§aPlot has been Claimed!");
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cPlot could not be claimed!");
                }
            }
            if (arg0.equalsIgnoreCase("buy")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.buy")) return true;
                Plot plot = getCurrentPlot(player, "§cDu stehst nicht in einem Plot!");
                if (plot == null) return true;
                if (plot.isBuyable()) {
                    if (plot.buyPlot(player)) {
                        plot.setStatus(PlotStatus.CLAIMED);
                        plot.createPlot();
                        player.sendMessage(plugin.getPrefix() + "§aDir gehört nun dieses Plot!");
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cFehler beim Kaufen dieses Plots!");
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + "§cDieses Plot kann nicht gekauft werden!");
                }
            }
            if (arg0.equalsIgnoreCase("info")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.info")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot found!");
                if (plot == null) return true;
                sendPlotInfo(player, plot);
                return true;
            }
            if (arg0.equalsIgnoreCase("home")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.home")) return true;
                List<Integer> plotHomes = PlotManager.getInstance().getPlotHomes(player);
                if (plotHomes.isEmpty()) {
                    player.sendMessage(plugin.getPrefix() + "§cNo Plot has been Found!");
                    return true;
                }
                Plot plot = Plot.getPlot(plotHomes.get(0));
                if (plot == null || plot.getHome() == null) {
                    player.sendMessage(plugin.getPrefix() + "§cNo Plot has been Found!");
                    return true;
                }
                player.teleport(plot.getHome());
                player.sendMessage(plugin.getPrefix() + "§aYou have been Teleported to your First Plot Home!");
            }
            if (arg0.equalsIgnoreCase("sethome")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.sethome")) return true;
                Plot plot = getCurrentPlot(player, "§aNo Plot has been Found!");
                if (plot == null) return true;
                if (plot.isOwner(player) && PlotSystemAPI.getAPI().setHome(plot, player.getLocation())) {
                    player.sendMessage("Plot Home Created! ID : " + plot.getId());
                }
            }
            if (arg0.equalsIgnoreCase("list")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.list")) return true;
                sendPlotList(player, player, PlotSystemAPI.getAPI().getPlayerPlots(player));
                return true;
            }
            if (arg0.equalsIgnoreCase("stats")) {
                if (!sender.hasPermission("plotsystem.plot.stats")) {
                    sender.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    return true;
                }
                sendPlotStats(sender);
                return true;
            }
            if (arg0.equalsIgnoreCase("flags")) {
                if (!sender.hasPermission("plotsystem.flags.info")) {
                    sender.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
                    return true;
                }
                sendFlagList(sender);
                return true;
            }
            if (arg0.equalsIgnoreCase("unclaim")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.unclaim")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                if (!plot.isOwner(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                if (PlotSystemAPI.getAPI().unclaimPlot(plot)) {
                    player.sendMessage(plugin.getPrefix() + "§aPlot has been unclaimed!");
                }
                return true;
            }
            if (arg0.equalsIgnoreCase("delete")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.admin.delete")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                if (PlotSystemAPI.getAPI().deletePlot(plot)) {
                    player.sendMessage(plugin.getPrefix() + "§aPlot has been deleted!");
                }
                return true;
            }
            if (arg0.equalsIgnoreCase("sellcancel")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.setprice")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                if (!plot.isOwner(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                if (PlotSystemAPI.getAPI().cancelSale(plot)) {
                    player.sendMessage(plugin.getPrefix() + "§aDieses Plot steht nicht mehr zum Verkauf!");
                }
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("flag")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.flag")) return true;
                Plot plot = getCurrentPlot(player, "§cDu stehst nicht in einem Plot!");
                if (plot == null) return true;
                if (!isPlotOwner(player, plot)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    if (PlotSystemAPI.getAPI().clearFlags(plot)) {
                        player.sendMessage(plugin.getPrefix() + "§aAll flags have been removed from this plot.");
                    }
                    return true;
                }
                if (args[1].equalsIgnoreCase("defaults")) {
                    if (PlotSystemAPI.getAPI().applyDefaultFlags(plot)) {
                        player.sendMessage(plugin.getPrefix() + "§aDefault flags have been applied to this plot.");
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.list.others")) return true;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                sendPlotList(player, target, PlotSystemAPI.getAPI().getPlayerPlots(target));
                return true;
            }
            if (args[0].equalsIgnoreCase("gamemode")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.admin.gamemode")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                if (!plot.isOwner(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                GameMode gameMode = parseGameMode(player, args[1]);
                if (!args[1].equalsIgnoreCase("clear") && gameMode == null) return true;
                if (PlotSystemAPI.getAPI().setGameMode(plot, gameMode)) {
                    if (gameMode == null) {
                        player.sendMessage(plugin.getPrefix() + "§aPlot gamemode has been cleared.");
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§aPlot gamemode has been set to §6" + gameMode.name() + "§a.");
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("setprice")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.setprice")) return true;
                double price;
                try {
                    price = Double.parseDouble(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(plugin.getPrefix() + "§cPlease enter a valid price.");
                    return true;
                }
                if (price < 0) {
                    player.sendMessage(plugin.getPrefix() + "§cPrice cannot be negative.");
                    return true;
                }
                Plot plot = getCurrentPlot(player, "§cPlot is null!");
                if (plot == null) return true;
                if (plot.isOwner(player)) {
                    if (PlotSystemAPI.getAPI().setForSale(plot, price)) {
                        player.sendMessage(plugin.getPrefix() + "§aDu hast denn Preis auf §6" + price + " §agesetzt! §aDieses Plot kann nun gekauft werden für §6" + price + "!");
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("home")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.home")) return true;
                try {
                    int home = Integer.parseInt(args[1]);
                    Plot plotHome = PlotManager.getInstance().getPlotHomeByID(player, home);
                    if (plotHome == null || plotHome.getHome() == null) {
                        player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                        return true;
                    }
                    player.teleport(plotHome.getHome());
                    player.sendMessage(plugin.getPrefix() + "§aYou have been Teleported to the Home from Plot Number §6" + home);
                } catch (Exception e) {
                    player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                    return true;
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

                Plot plot = getCurrentPlot(player, "§cNot Plot found!");
                if (plot == null) {
                    player.sendMessage(plugin.getPrefix() + "§cPlot is null!");
                    return true;
                }
                if (!plot.isOwner(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }

                OfflinePlayer member = Bukkit.getOfflinePlayer(args[1]);
                if (PlotSystemAPI.getAPI().addMember(plot, member)) {
                    player.sendMessage("§aMember wurde hinzugefügt §6" + member.getName());
                }
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

                Plot plot = getCurrentPlot(player, "§cNot Plot found!");
                if (plot == null) {
                    player.sendMessage(plugin.getPrefix() + "§cPlot is null!");
                    return true;
                }
                if (!plot.isOwner(player)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }

                OfflinePlayer member = Bukkit.getOfflinePlayer(args[1]);
                if (PlotSystemAPI.getAPI().removeMember(plot, member)) {
                    player.sendMessage("§cMember wurde entfernt §6" + member.getName());
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("setowner")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.setowner")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                OfflinePlayer newOwner = Bukkit.getOfflinePlayer(args[1]);
                if (PlotSystemAPI.getAPI().transferPlot(plot, newOwner)) {
                    player.sendMessage("§aOwner wurde geändert auf §6" + newOwner.getName());
                }
            }
            if (args[0].equalsIgnoreCase("removeowner")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.removeowner")) return true;
                Plot plot = getCurrentPlot(player, "§cNo Plot has been Found!");
                if (plot == null) return true;
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
                if (PlotSystemAPI.getAPI().removeOwner(plot, owner)) {
                    player.sendMessage("§cOwner §6" + owner.getName() + " §cwurde entfernt!");
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("flag")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.flag")) return true;
                Plot plot = getCurrentPlot(player, "§cDu stehst nicht in einem Plot!");
                if (plot == null) return true;
                if (!isPlotOwner(player, plot)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                Flag flag = parseFlag(player, args[2]);
                if (flag == null) return true;
                if (args[1].equalsIgnoreCase("add")) {
                    if (PlotSystemAPI.getAPI().addFlag(plot, flag)) {
                        player.sendMessage("§aFlag §6" + flag + " §awurde hinzugefügt!");
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (PlotSystemAPI.getAPI().removeFlag(plot, flag)) {
                        player.sendMessage("§aFlag §6" + flag + " §awurde entfernt!");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("banplayer")) {
                Player player = requirePlayer(sender);
                if (player == null || !requirePermission(player, "plotsystem.plot.ban")) return true;
                Plot plot = getCurrentPlot(player, "§cDu stehst nicht in einem Plot!");
                if (plot == null) return true;
                if (!isPlotOwner(player, plot)) {
                    player.sendMessage(plugin.getPrefix() + "§cDu bist nicht der Besitzer von diesem Plot!");
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (args[1].equalsIgnoreCase("add")) {
                    teleportFromOwnedPlot(player, target);
                    if (PlotSystemAPI.getAPI().banPlayer(plot, target)) {
                        player.sendMessage("§6" + target.getName() + " §awurde gebannt!");
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (PlotSystemAPI.getAPI().unbanPlayer(plot, target)) {
                        player.sendMessage("§6" + target.getName() + " §awurde entbannt!");
                    }
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
                        int home = Integer.parseInt(args[2]);
                        Plot plotHome = PlotManager.getInstance().getPlotHomeByID(offPlayer, home);
                        if (plotHome == null || plotHome.getHome() == null) {
                            player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                            return true;
                        }
                        player.teleport(plotHome.getHome());
                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                        player.sendMessage(plugin.getPrefix() + "§cPlot Home not found!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("flag")) {
                List<String> flags = new ArrayList<>();
                for (Flag flag : Flag.values()) {
                    flags.add(flag.getType());
                }
                return filteredSuggestions(flags, args[2]);
            } else if (args[0].equalsIgnoreCase("visit")) {
                List<String> homes = new ArrayList<>();
                if (sender instanceof Player)
                    for (int homeInts : PlotManager.getInstance().getPlotHomes(Bukkit.getOfflinePlayer(args[1]))) {
                        homes.add(homeInts + "");
                    }
                return filteredSuggestions(homes, args[2]);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("flag")) {
                return filteredSuggestions(actionCommands(), args[1]);
            } else if (args[0].equalsIgnoreCase("banplayer")) {
                return filteredSuggestions(actionCommands(), args[1]);
            } else if (args[0].equalsIgnoreCase("gamemode")) {
                return filteredSuggestions(gameModeCommands(), args[1]);
            }
        } else if (args.length == 1) {
            return filteredSuggestions(plotCommands(), args[0]);
        }
        return null;
    }

    private Player requirePlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        sender.sendMessage(plugin.getPrefix() + "§cYou must be a player to use this command!");
        return null;
    }

    private boolean requirePermission(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        }
        player.sendMessage(plugin.getPrefix() + "§cNo Permissions!");
        return false;
    }

    private Plot getCurrentPlot(Player player, String notFoundMessage) {
        if (!Plot.isPlayerInPlotStatic(player)) {
            player.sendMessage(plugin.getPrefix() + notFoundMessage);
            return null;
        }
        Plot plot = Plot.getPlot(player.getLocation());
        if (plot == null) {
            player.sendMessage(plugin.getPrefix() + notFoundMessage);
        }
        return plot;
    }

    private void sendPlotInfo(Player player, Plot plot) {
        player.sendMessage("§aID : §6" + plot.getId());
        if (plot.hasOwner()) {
            player.sendMessage("§aOwner : §6" + Bukkit.getOfflinePlayer(plot.getOwner()).getName());
        } else {
            player.sendMessage("§cNot Claimed!");
        }
        player.sendMessage("§aStatus : §6" + plot.getStatus().name());
        player.sendMessage("§aOwners : §6" + getPlayerNames(plot.getOwners()));
        player.sendMessage("§aMembers : §6" + getPlayerNames(plot.getMembers()));
        player.sendMessage("§aBannedPlayers : §6" + getPlayerNames(plot.getBannedPlayers()));
        List<String> flags = new ArrayList<>();
        for (Flag flag : plot.getFlags()) {
            flags.add(flag.getType());
        }
        player.sendMessage("§aFlags : §6" + flags);
        player.sendMessage("§aGameMode : §6" + (plot.getGameMode() == null ? "NONE" : plot.getGameMode().name()));
        player.sendMessage("§aAverage Light Level : §6" + plot.getCuboid().getAverageLightLevel());
        player.sendMessage("§aPrice : §6" + plot.getPrice());
    }

    private void sendPlotList(Player player, OfflinePlayer target, List<Plot> plots) {
        player.sendMessage(plugin.getPrefix() + "§aPlots from §6" + target.getName() + "§a: §6" + plots.size());
        if (plots.isEmpty()) {
            player.sendMessage(plugin.getPrefix() + "§cNo plots found.");
            return;
        }
        for (Plot plot : plots) {
            Location home = plot.getHome();
            String worldName = home == null || home.getWorld() == null ? "unknown" : home.getWorld().getName();
            player.sendMessage("§7- §aID §6" + plot.getId() + " §7| §aStatus §6" + plot.getStatus().name()
                    + " §7| §aWorld §6" + worldName + " §7| §aPrice §6" + plot.getPrice());
        }
    }

    private void sendPlotStats(CommandSender sender) {
        PlotSystemAPI api = PlotSystemAPI.getAPI();
        sender.sendMessage(plugin.getPrefix() + "§aPlot Statistics");
        sender.sendMessage("§aStorage : §6" + api.getStorageBackend());
        sender.sendMessage("§aTotal Plots : §6" + api.getPlotCount());
        sender.sendMessage("§aClaimed Plots : §6" + api.getClaimedPlotCount());
        sender.sendMessage("§aAvailable Plots : §6" + api.getAvailablePlotCount());
        sender.sendMessage("§aBuyable Plots : §6" + api.getBuyablePlots().size());
        sender.sendMessage("§aTotal Plot Blocks : §6" + api.getTotalPlotBlocks());
        sender.sendMessage("§aClaimed Blocks : §6" + api.getTotalClaimedBlocks());
    }

    private void sendFlagList(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + "§aAvailable Flags");
        for (Flag flag : Flag.values()) {
            sender.sendMessage("§7- §6" + flag.getType() + " §7- §a" + flag.getDescription());
        }
    }

    private boolean isPlotOwner(Player player, Plot plot) {
        return plot.hasOwner() && plot.getOwners().contains(player.getUniqueId());
    }

    private Flag parseFlag(Player player, String value) {
        try {
            return Flag.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(plugin.getPrefix() + "§cUnknown Flag!");
            return null;
        }
    }

    private GameMode parseGameMode(Player player, String value) {
        if (value.equalsIgnoreCase("clear")) return null;
        try {
            return GameMode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(plugin.getPrefix() + "§cUnknown GameMode!");
            return null;
        }
    }

    private void teleportFromOwnedPlot(Player owner, OfflinePlayer target) {
        if (!target.isOnline()) return;
        Player targetPlayer = target.getPlayer();
        if (targetPlayer == null || !Plot.isPlayerInPlotStatic(targetPlayer)) return;

        Plot currentPlot = Plot.getPlot(targetPlayer.getLocation());
        if (currentPlot == null || !isPlotOwner(owner, currentPlot)) return;

        for (Block block : currentPlot.getCuboid().corners()) {
            Location location = block.getLocation();
            if (location.getWorld() == null) return;
            targetPlayer.teleport(location.getWorld().getHighestBlockAt(location).getLocation());
            return;
        }
    }

    private List<String> getPlayerNames(List<UUID> uuids) {
        List<String> names = new ArrayList<>();
        if (uuids == null) return names;
        for (UUID uuid : uuids) {
            names.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return names;
    }

    private List<String> filteredSuggestions(List<String> options, String prefix) {
        List<String> suggestions = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(option);
            }
        }
        Collections.sort(suggestions);
        return suggestions;
    }

    private List<String> actionCommands() {
        List<String> commands = new ArrayList<>();
        commands.add("add");
        commands.add("remove");
        commands.add("clear");
        commands.add("defaults");
        return commands;
    }

    private List<String> gameModeCommands() {
        List<String> commands = new ArrayList<>();
        for (GameMode gameMode : GameMode.values()) {
            commands.add(gameMode.name().toLowerCase());
        }
        commands.add("clear");
        return commands;
    }

    private List<String> plotCommands() {
        List<String> commands = new ArrayList<>();
        commands.add("claim");
        commands.add("addmember");
        commands.add("home");
        commands.add("buy");
        commands.add("sethome");
        commands.add("setowner");
        commands.add("banplayer");
        commands.add("flag");
        commands.add("visit");
        commands.add("removemember");
        commands.add("removeowner");
        commands.add("setprice");
        commands.add("sellcancel");
        commands.add("list");
        commands.add("stats");
        commands.add("flags");
        commands.add("unclaim");
        commands.add("delete");
        commands.add("gamemode");
        return commands;
    }
}
