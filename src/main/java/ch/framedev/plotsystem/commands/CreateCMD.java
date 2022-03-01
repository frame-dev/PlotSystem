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

import java.util.ArrayList;
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
    private final Main plugin;

    public CreateCMD(Main plugin) {
        this.plugin = plugin;
        this.locations = new HashMap<>();
        plugin.getCommand("create").setExecutor(this);
        plugin.getCommand("pos1").setExecutor(this);
        plugin.getCommand("pos2").setExecutor(this);
        plugin.getCommand("createplot").setExecutor(this);
        plugin.getCommand("claim").setExecutor(this);
        plugin.getCommand("pmarker").setExecutor(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("create")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.admin.create")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                    return true;
                }
                if (!locations.isEmpty() && locations.containsKey(player)) {
                    try {
                        new PlotsSetup(new Cuboid(locations.get(player).get("1"), locations.get(player).get("2")))
                                .create(Plot.getHighestId() + 1);
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
            if (!sender.hasPermission("plotsystem.plot.createplot")) {
                sender.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!Plot.isLocationInPlot(player.getLocation())) {
                    Plot plot = new Plot(Plot.getHighestId() + 1, player.getLocation().getChunk());
                    for (Block corner : plot.getCuboid().corners()) {
                        corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
                    }
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
                if (!locations.isEmpty() && locations.containsKey(player)) {
                    Cuboid cuboid = new Cuboid(locations.get(player).get("1"), locations.get(player).get("2"));
                    ArrayList<Boolean> success = new ArrayList<>();
                    if (plugin.isLimitedClaim()) {
                        if (plugin.getLimitedHashMap().containsKey(player)) {
                            if (Plot.getPlots() == null) {
                                Plot plot = new Plot(Plot.getHighestId() + 1, locations.get(player).get("1"), locations.get(player).get("2"));
                                long blocks = cuboid.getBlocks().size();
                                if (blocks > plugin.getLimitedHashMap().get(player)) {
                                    player.sendMessage(plugin.getPrefix() + "§cNo more blocks available! §6Your Blocks §6" + plugin.getLimitedHashMap().get(player) + " Needed Blocks §a" + blocks);
                                    return true;
                                }
                                for (Block corner : plot.getCuboid().corners()) {
                                    corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
                                }
                                plot.setOwner(player.getUniqueId());
                                plot.setStatus(PlotStatus.CLAIMED);
                                plot.createPlot();
                                player.teleport(player.getWorld().getHighestBlockAt(locations.get(player).get("1")).getLocation());
                                player.sendMessage("§6Plot Created and Claimed!");
                                locations.get(player).remove("1");
                                locations.get(player).remove("2");
                                long newBlocks = plugin.getLimitedHashMap().get(player);
                                newBlocks -= cuboid.getBlocks().size();
                                plugin.getConfig().set(player.getName(), newBlocks);
                                plugin.saveConfig();
                                plugin.getLimitedHashMap().remove(player);
                                plugin.getLimitedHashMap().put(player, newBlocks);
                                player.sendMessage(plugin.getPrefix() + "§aNew Blocks Available §6" + newBlocks);
                            } else {
                                long blocks = cuboid.getBlocks().size();
                                if (blocks > plugin.getLimitedHashMap().get(player)) {
                                    player.sendMessage(plugin.getPrefix() + "§cNo more blocks available! §6Your Blocks §6" + plugin.getLimitedHashMap().get(player) + " Needed Blocks §a" + blocks);
                                    return true;
                                }
                                for (Plot plot : Plot.getPlots()) {
                                    for (Block block : plot.getCuboid().getBlocks()) {
                                        if (!cuboid.contains(block.getLocation())) {
                                            success.add(true);
                                        } else {
                                            success.add(false);
                                        }
                                    }
                                }
                                if (!success.contains(false)) {
                                    Plot plot = new Plot(Plot.getHighestId() + 1, locations.get(player).get("1"), locations.get(player).get("2"));
                                    for (Block corner : plot.getCuboid().corners()) {
                                        corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
                                    }
                                    plot.setOwner(player.getUniqueId());
                                    plot.setStatus(PlotStatus.CLAIMED);
                                    plot.createPlot();
                                    player.teleport(player.getWorld().getHighestBlockAt(locations.get(player).get("1")).getLocation());
                                    player.sendMessage("§6Plot Created and Claimed!");
                                    locations.get(player).remove("1");
                                    locations.get(player).remove("2");
                                    long newBlocks = plugin.getLimitedHashMap().get(player);
                                    newBlocks -= cuboid.getBlocks().size();
                                    plugin.getConfig().set(player.getName(), newBlocks);
                                    plugin.saveConfig();
                                    plugin.getLimitedHashMap().remove(player);
                                    plugin.getLimitedHashMap().put(player, newBlocks);
                                    player.sendMessage(plugin.getPrefix() + "§aNew Blocks Available §6" + newBlocks);
                                } else {
                                    player.sendMessage("§cSome Locations are in other Plots!");
                                }
                            }
                        }
                    } else {
                        if (Plot.getPlots() == null) {
                            Plot plot = new Plot(Plot.getHighestId() + 1, locations.get(player).get("1"), locations.get(player).get("2"));
                            for (Block corner : plot.getCuboid().corners()) {
                                corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
                            }
                            plot.setOwner(player.getUniqueId());
                            plot.setStatus(PlotStatus.CLAIMED);
                            plot.createPlot();
                            player.teleport(player.getWorld().getHighestBlockAt(locations.get(player).get("1")).getLocation());
                            player.sendMessage("§6Plot Created and Claimed!");
                            locations.get(player).remove("1");
                            locations.get(player).remove("2");
                        } else {
                            for (Plot plot : Plot.getPlots()) {
                                for (Block block : plot.getCuboid().getBlocks()) {
                                    if (!cuboid.contains(block.getLocation())) {
                                        success.add(true);
                                    } else {
                                        success.add(false);
                                    }
                                }
                            }
                            if (!success.contains(false)) {
                                Plot plot = new Plot(Plot.getHighestId() + 1, locations.get(player).get("1"), locations.get(player).get("2"));
                                for (Block corner : plot.getCuboid().corners()) {
                                    corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
                                }
                                plot.setOwner(player.getUniqueId());
                                plot.setStatus(PlotStatus.CLAIMED);
                                plot.createPlot();
                                player.teleport(player.getWorld().getHighestBlockAt(locations.get(player).get("1")).getLocation());
                                player.sendMessage("§6Plot Created and Claimed!");
                                locations.get(player).remove("1");
                                locations.get(player).remove("2");
                            } else {
                                player.sendMessage("§cSome Locations are in other Plots!");
                            }
                        }
                    }
                } else {
                    player.sendMessage("§cNo Positions Set!");
                }
            }
        }
        if (command.getName().

                equalsIgnoreCase("pmarker")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("plotsystem.plot.marker")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§cNo Permissions!");
                    return true;
                }
                ItemStack itemStack = new ItemStack(Material.STICK);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName("§aMarker");
                itemStack.setItemMeta(meta);
                player.getInventory().addItem(itemStack);
                player.sendMessage(plugin.getPrefix() + "§aLeft Click on a Block set's the First Location \n" +
                        "Right Click on a Block set's the Second Location! Or yust use /pos1 /pos2");
            }
        }
        if (command.getName().

                equalsIgnoreCase("pos1")) {
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
        if (command.getName().

                equalsIgnoreCase("pos2")) {
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

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (!event.getItem().hasItemMeta()) return;
            if (!event.getItem().getItemMeta().hasDisplayName()) return;
            if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aMarker")) {
                Player player = event.getPlayer();
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (locations.isEmpty()) {
                        Location location = event.getClickedBlock().getLocation();
                        location.setY(1);
                        HashMap<String, Location> locs = new HashMap<>();
                        locs.put("1", location);
                        locations.put(player, locs);
                    } else {
                        if (locations.containsKey(player) && locations.get(player) != null) {
                            locations.get(player).remove("1");
                        }
                        Location location = event.getClickedBlock().getLocation();
                        location.setY(1);
                        locations.get(player).put("1", location);
                    }
                    player.sendMessage("§aPosition 1 Set!");
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (locations.isEmpty()) {
                        Location location = event.getClickedBlock().getLocation();
                        location.setY(200);
                        HashMap<String, Location> locs = new HashMap<>();
                        locs.put("2", location);
                        locations.put(player, locs);
                    } else {
                        if (locations.containsKey(player) && locations.get(player) != null) {
                            locations.get(player).remove("2");
                        }
                        Location location = event.getClickedBlock().getLocation();
                        location.setY(200);
                        locations.get(player).put("2", location);
                    }
                    player.sendMessage("§aPosition 2 Set!");
                }
                event.setCancelled(true);
            }
        }
    }
}
