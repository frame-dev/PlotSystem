package ch.framedev.plotsystem.commands;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotStatus;
import ch.framedev.plotsystem.plots.PlotsSetup;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.commands
 * / ClassName CreateCMD
 * / Date: 11.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class CreateCMD implements CommandExecutor, Listener {

    private final HashMap<Player, HashMap<String, Location>> locations;
    private final Main plugin;

    public CreateCMD(Main plugin) {
        this.plugin = plugin;
        this.locations = new HashMap<>();
        Objects.requireNonNull(plugin.getCommand("create")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("pos1")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("pos2")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("createplot")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("claim")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("pmarker")).setExecutor(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("create")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.admin.create")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                    return true;
                }
                if (hasSelection(player)) {
                    try {
                        new PlotsSetup(getSelection(player)).createFromChunks(Plot.getHighestId() + 1);
                    } catch (Exception e) {
                        player.sendMessage(Main.getInstance().getPrefix() + " §cPositions not set!");
                    }
                    player.sendMessage("§aPlots Created!");
                } else {
                    player.sendMessage("§cNo Positions Set!");
                }
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("createplot")) {
            if (!sender.hasPermission("plotsystem.admin.createplot")) {
                sender.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!Plot.isLocationInPlot(player.getLocation())) {
                    Plot plot = new Plot(Plot.getHighestId() + 1, player.getLocation().getChunk());
                    markPlotCorners(plot);
                    plot.createPlot();
                }
            }
        }
        if (command.getName().equalsIgnoreCase("claim")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.claim")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                    return true;
                }
                if (hasSelection(player)) {
                    claimSelection(player);
                } else {
                    player.sendMessage("§cNo Positions Set!");
                }
            }
        }
        if (command.getName().equalsIgnoreCase("pmarker")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.marker")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                    return true;
                }
                ItemStack itemStack = new ItemStack(Material.STICK);
                ItemMeta meta = itemStack.getItemMeta();
                if(meta == null) return false;
                meta.setDisplayName("§aMarker");
                itemStack.setItemMeta(meta);
                player.getInventory().addItem(itemStack);
                player.sendMessage(plugin.getPrefix() + "§aLeft Click on a Block set's the First Location \n" +
                        "Right Click on a Block set's the Second Location! Or just use /pos1 /pos2");
            }
        }
        if (command.getName().equalsIgnoreCase("pos1")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                setPosition(player, "1", player.getLocation(), 1);
                player.sendMessage("§aPosition 1 Set!");
            }
        }
        if (command.getName().equalsIgnoreCase("pos2")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                setPosition(player, "2", player.getLocation(), 200);
                player.sendMessage("§aPosition 2 Set!");
            }
        }
        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (!event.getItem().hasItemMeta()) return;
            if(event.getItem().getItemMeta() == null) return;
            if (!event.getItem().getItemMeta().hasDisplayName()) return;
            if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aMarker")) {
                Player player = event.getPlayer();
                if(event.getClickedBlock() == null) return;
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    setPosition(player, "1", event.getClickedBlock().getLocation(), 1);
                    player.sendMessage("§aPosition 1 Set!");
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    setPosition(player, "2", event.getClickedBlock().getLocation(), 200);
                    player.sendMessage("§aPosition 2 Set!");
                }
                event.setCancelled(true);
            }
        }
    }

    private boolean hasSelection(Player player) {
        Map<String, Location> selection = locations.get(player);
        return selection != null && selection.get("1") != null && selection.get("2") != null;
    }

    private Cuboid getSelection(Player player) {
        Map<String, Location> selection = locations.get(player);
        return new Cuboid(selection.get("1"), selection.get("2"));
    }

    private void setPosition(Player player, String position, Location baseLocation, int y) {
        Location location = baseLocation.clone();
        location.setY(y);
        locations.computeIfAbsent(player, ignored -> new HashMap<>()).put(position, location);
    }

    private void claimSelection(Player player) {
        Cuboid cuboid = getSelection(player);
        if (isOverlappingExistingPlot(cuboid)) {
            player.sendMessage("§cSome Locations are in other Plots!");
            return;
        }

        long claimedBlocks = cuboid.getBlocks().size();
        if (plugin.isLimitedClaim()) {
            if (hasReachedPlotLimit(player) || !consumeAvailableBlocks(player, claimedBlocks)) {
                return;
            }
        }

        Plot plot = new Plot(Plot.getHighestId() + 1, locations.get(player).get("1"), locations.get(player).get("2"));
        claimPlot(player, plot);
        player.teleport(player.getWorld().getHighestBlockAt(locations.get(player).get("1")).getLocation());
        player.sendMessage("§6Plot Created and Claimed!");
        locations.remove(player);
    }

    private boolean isOverlappingExistingPlot(Cuboid cuboid) {
        if (Plot.getPlots() == null) return false;
        for (Plot plot : Plot.getPlots()) {
            for (Block block : plot.getCuboid().getBlocks()) {
                if (cuboid.contains(block.getLocation())) return true;
            }
        }
        return false;
    }

    private boolean hasReachedPlotLimit(Player player) {
        int maxPlots = plugin.getMaxClaimedPlots();
        if (maxPlots <= 0) return false;
        int currentPlots = Plot.getPlayerPlots(player).size();
        if (currentPlots < maxPlots) return false;

        player.sendMessage(plugin.getPrefix() + "§cYou reached the plot claim limit! §6Your Plots §6" + currentPlots + " Max Plots §a" + maxPlots);
        return true;
    }

    private boolean consumeAvailableBlocks(Player player, long claimedBlocks) {
        long availableBlocks = plugin.getLimitedHashMap().getOrDefault(player, plugin.getLimitedAmount());
        if (claimedBlocks > availableBlocks) {
            player.sendMessage(plugin.getPrefix() + "§cNo more blocks available! §6Your Blocks §6" + availableBlocks + " Needed Blocks §a" + claimedBlocks);
            return false;
        }

        long remainingBlocks = availableBlocks - claimedBlocks;
        plugin.getConfig().set(player.getName(), remainingBlocks);
        plugin.saveConfig();
        plugin.getLimitedHashMap().put(player, remainingBlocks);
        player.sendMessage(plugin.getPrefix() + "§aNew Blocks Available §6" + remainingBlocks);
        return true;
    }

    private void claimPlot(Player player, Plot plot) {
        markPlotCorners(plot);
        plot.setOwner(player.getUniqueId());
        plot.setStatus(PlotStatus.CLAIMED);
        plot.createPlot();
    }

    private void markPlotCorners(Plot plot) {
        for (Block corner : plot.getCuboid().corners()) {
            if (corner.getWorld() != null) {
                corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
            }
        }
    }
}
