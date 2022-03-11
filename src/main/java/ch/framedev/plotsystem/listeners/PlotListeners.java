package ch.framedev.plotsystem.listeners;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Plot Listeners
 * <p>
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.listeners
 * / ClassName PlotListeners
 * / Date: 11.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotListeners implements Listener {

    public PlotListeners(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        onUpdate();
    }

    /**
     * Used for the Plot listeners to protect
     *
     * @param event the BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (Plot.isLocationInPlot(event.getBlock().getLocation())) {
            Plot plot = Plot.getPlot(event.getBlock().getLocation());
            if (plot == null) return;
            Material material = event.getBlock().getType();
            if (material == Material.REDSTONE_WIRE || material == Material.REDSTONE_BLOCK
                    || material == Material.REDSTONE_LAMP || material == Material.REDSTONE_TORCH
                    || material == Material.REPEATER || material == Material.COMPARATOR)
                if (!plot.hasFlag(Flag.REDSTONE))
                    event.setCancelled(true);
            if (material == Material.TNT || material == Material.TNT_MINECART)
                if (!plot.hasFlag(Flag.EXPLOSIONS))
                    event.setCancelled(true);
            if (plot.hasOwner()) {
                if (plot.isOwner(event.getPlayer()) || plot.isMember(event.getPlayer())) {
                    if (event.getBlock().getType() == Material.SPAWNER && !plot.hasFlag(Flag.MONSTER_SPAWNER)) {
                        event.setCancelled(true);
                    }
                }
                if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                    if (plot.getMembers() == null) {
                        if (!event.getPlayer().hasPermission("plotsystem.admin.break"))
                            event.setCancelled(true);
                    } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                        if (!event.getPlayer().hasPermission("plotsystem.admin.break"))
                            event.setCancelled(true);
                    }
            } else {
                if (!event.getPlayer().hasPermission("plotsystem.admin.break"))
                    event.setCancelled(true);
            }
        }
    }

    /**
     * Used for Protecting the Plot
     *
     * @param event Block Place Event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        if (Plot.isLocationInPlot(event.getBlockPlaced().getLocation())) {
            Plot plot = Plot.getPlot(event.getBlockPlaced().getLocation());
            if (plot == null) return;
            Material material = event.getBlockPlaced().getType();
            if (material == Material.REDSTONE_WIRE || material == Material.REDSTONE_BLOCK
                    || material == Material.REDSTONE_LAMP || material == Material.REDSTONE_TORCH
                    || material == Material.REPEATER || material == Material.COMPARATOR)
                if (!plot.hasFlag(Flag.REDSTONE))
                    event.setCancelled(true);
            if (material == Material.TNT || material == Material.TNT_MINECART)
                if (!plot.hasFlag(Flag.EXPLOSIONS)) {
                    event.setCancelled(true);
                } else {
                    if (plot.hasOwner()) {
                        if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId())) {
                            if (plot.getMembers() == null) {
                                if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                                    event.setCancelled(true);
                            } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                                if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                                    event.setCancelled(true);
                            } else {
                                players.add(event.getPlayer().getUniqueId());
                                event.setCancelled(false);
                            }
                        } else {
                            players.add(event.getPlayer().getUniqueId());
                            event.setCancelled(false);
                        }
                    } else {
                        if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                            event.setCancelled(true);
                    }
                }
            if (plot.hasOwner()) {
                if (plot.isOwner(event.getPlayer()) || plot.isMember(event.getPlayer())) {
                    if (event.getBlock().getType() == Material.SPAWNER && !plot.hasFlag(Flag.MONSTER_SPAWNER)) {
                        event.setCancelled(true);
                    }
                }
                if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                    if (plot.getMembers() == null) {
                        if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                            event.setCancelled(true);
                    } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                        if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                            event.setCancelled(true);
                    }
            } else {
                if (!event.getPlayer().hasPermission("plotsystem.admin.place"))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                if (Plot.isPlayerInPlotStatic((Player) event.getDamager())) {
                    Plot plot = Plot.getPlot(event.getDamager().getLocation());
                    if (plot == null) return;
                    if (plot.hasOwner()) {
                        if (!plot.hasFlag(Flag.PVP)) {
                            if (!event.getEntity().hasPermission("plotsystem.admin.pvp"))
                                event.setCancelled(true);
                        }
                    } else {
                        if (!event.getEntity().hasPermission("plotsystem.admin.pvp")) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageCause(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (Plot.isPlayerInPlotStatic((Player) event.getEntity())) {
                Plot plot = Plot.getPlot(event.getEntity().getLocation());
                if (plot == null) return;
                if (plot.hasOwner()) {
                    if (!plot.hasFlag(Flag.FALL_DAMAGE)) {
                        if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
                            event.setCancelled(true);
                    } else if (!plot.hasFlag(Flag.MOB_DAMAGE))
                        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                            event.setCancelled(true);
                } else {
                    if (!event.getEntity().hasPermission("plotsystem.admin.ignore")) {
                        event.setCancelled(true);
                    }
                }
            }
    }

    /**
     * Protect your chests and other things in your Plot
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            if (Plot.isPlayerInPlotStatic(event.getPlayer())) {
                Plot plot = Plot.getPlot(player.getEyeLocation());
                if (plot == null) return;
                if (plot.hasOwner()) {
                    if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                        if (plot.getMembers() == null) {
                            if (!plot.hasFlag(Flag.INTERACT))
                                if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                                    event.setCancelled(true);
                        } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                            if (!plot.hasFlag(Flag.INTERACT))
                                if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                                    event.setCancelled(true);
                        }
                } else {
                    if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                        event.setCancelled(true);
                }
            }
        } else if (Plot.isLocationInPlot(event.getClickedBlock().getLocation())) {
            Plot plot = Plot.getPlot(event.getClickedBlock().getLocation());
            if (plot == null) return;
            if (plot.hasOwner()) {
                if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                    if (plot.getMembers() == null) {
                        if (!plot.hasFlag(Flag.INTERACT))
                            if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                                event.setCancelled(true);
                    } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                        if (!plot.hasFlag(Flag.INTERACT))
                            if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                                event.setCancelled(true);
                    }
            } else {
                if (!event.getPlayer().hasPermission("plotsystem.admin.interact"))
                    event.setCancelled(true);
            }
        }
    }

    // The Players for the Explosions
    List<UUID> players = new ArrayList<>();

    @EventHandler
    public void onExplodeEntity(EntityExplodeEvent event) {
        if (Plot.isLocationInPlot(event.getEntity().getLocation())) {
            Plot plot = Plot.getPlot(event.getEntity().getLocation());
            if (plot == null) {
                event.setCancelled(true);
                return;
            }
            if (plot.hasOwner()) {
                if (!plot.hasFlag(Flag.EXPLOSIONS)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Protect Explosions when in Plot and has not Flag "EXPLOSION"
     *
     * @param event
     */
    @EventHandler
    public void onExplode(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            for (Block block : getNearbyBlocks(event.getEntity().getLocation(), event.getRadius())) {
                if (Plot.isLocationInPlot(block.getLocation())) {
                    Plot plot = Plot.getPlot(block.getLocation());
                    if (plot == null) {
                        event.setCancelled(true);
                        return;
                    }
                    UUID[] uuids = new UUID[1];
                    for (UUID uuid : players) {
                        if (plot.hasOwner()) {
                            if (plot.getOwners() != null && !plot.getOwners().contains(uuid))
                                if (plot.getMembers() == null) {
                                    event.setCancelled(true);
                                    uuids[0] = uuid;
                                } else if (!plot.getMembers().contains(uuid)) {
                                    event.setCancelled(true);
                                    uuids[0] = uuid;
                                }
                        }
                    }
                    if (uuids[0] != null)
                        players.remove(uuids[0]);
                }
            }
        }
    }

    public ArrayList<Block> getNearbyBlocks(Location location, float radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (float x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (float y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (float z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    if (location.getWorld() == null) continue;
                    blocks.add(location.getWorld().getBlockAt((int) x, (int) y, (int) z));
                }
            }
        }
        return blocks;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Plot.isPlayerInPlotStatic(event.getPlayer())) {
            Plot plot = Plot.getPlot(event.getPlayer().getLocation());
            if (plot == null) return;
            if (plot.hasOwner()) {
                if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                    if (plot.getMembers() == null) {
                        if (plot.isPlayerBanned(event.getPlayer()) && !event.getPlayer().hasPermission("plotsystem.admin.ignore")) {
                            event.getPlayer().teleport(event.getFrom());
                            event.getPlayer().sendMessage("/p home");
                        }
                    } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                        if (plot.isPlayerBanned(event.getPlayer()) && !event.getPlayer().hasPermission("plotsystem.admin.ignore")) {
                            event.getPlayer().teleport(event.getFrom());
                            event.getPlayer().sendMessage("/p home");
                        }
                    }
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (Plot.isPlayerInPlotStatic(event.getPlayer())) {
            Plot plot = Plot.getPlot(event.getPlayer().getLocation());
            if (plot == null) return;
            if (plot.hasOwner()) {
                if (plot.getOwners() != null && !plot.getOwners().contains(event.getPlayer().getUniqueId()))
                    if (plot.getMembers() == null || plot.getMembers().isEmpty()) {
                        if (plot.hasFlag(Flag.DENY_CHAT))
                            if (!event.getPlayer().hasPermission("plotsystem.admin.ignorechat"))
                                event.setCancelled(true);
                    } else if (!plot.getMembers().contains(event.getPlayer().getUniqueId())) {
                        if (plot.hasFlag(Flag.DENY_CHAT))
                            if (!event.getPlayer().hasPermission("plotsystem.admin.ignorechat"))
                                event.setCancelled(true);
                    }
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            if (Plot.isLocationInPlot(event.getBlock().getLocation())) {
                if (Plot.getPlot(event.getBlock().getLocation()) != null && Plot.getPlot(event.getBlock().getLocation()).hasFlag(Flag.FARM_PROTECT))
                    event.setCancelled(true);
            }
        }
    }

    /**
     * Used for the Flags Flag.ANIMALS and Flag.MONSTERS
     * This despawns the entities if they not allowed
     */
    public void onUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : Bukkit.getWorld(Objects.requireNonNull(Main.getInstance().getConfig().getString("PlotWorld"))).getEntities()) {
                    if (Plot.isLocationInPlot(entity.getLocation())) {
                        Plot plot = Plot.getPlot(entity.getLocation());
                        if (plot == null) continue;
                        if (!plot.hasFlag(Flag.MONSTERS)) {
                            if (entity instanceof Monster) {
                                if (plot.inPlot(entity.getLocation())) {
                                    entity.remove();
                                }
                            }
                        }
                        if (!plot.hasFlag(Flag.ANIMALS)) {
                            if (entity instanceof Animals) {
                                if (plot.inPlot(entity.getLocation())) {
                                    entity.remove();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
