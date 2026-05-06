package ch.framedev.plotsystem.listeners;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private BukkitTask task;
    private final Map<UUID, GameMode> previousGameModes = new HashMap<>();

    public PlotListeners(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        onUpdate();
    }

    public BukkitTask getTask() {
        return task;
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
            if (isTrusted(event.getPlayer(), plot) && event.getBlock().getType() == Material.SPAWNER && !plot.hasFlag(Flag.MONSTER_SPAWNER)) {
                event.setCancelled(true);
            }
            if (!canAccessPlot(event.getPlayer(), plot, "plotsystem.admin.break")) {
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
                    if (!canAccessPlot(event.getPlayer(), plot, "plotsystem.admin.place")) {
                        event.setCancelled(true);
                    } else {
                        players.add(event.getPlayer().getUniqueId());
                    }
                }
            if (isTrusted(event.getPlayer(), plot) && event.getBlock().getType() == Material.SPAWNER && !plot.hasFlag(Flag.MONSTER_SPAWNER)) {
                event.setCancelled(true);
            }
            if (!canAccessPlot(event.getPlayer(), plot, "plotsystem.admin.place")) {
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
                            if (!event.getDamager().hasPermission("plotsystem.admin.pvp"))
                                event.setCancelled(true);
                        }
                    } else {
                        if (!event.getDamager().hasPermission("plotsystem.admin.pvp")) {
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
                    }
                    if (!plot.hasFlag(Flag.MOB_DAMAGE))
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
                if (!canInteract(player, plot)) {
                    event.setCancelled(true);
                }
            }
        } else if (Plot.isLocationInPlot(event.getClickedBlock().getLocation())) {
            Plot plot = Plot.getPlot(event.getClickedBlock().getLocation());
            if (plot == null) return;
            if (event.getAction() == Action.PHYSICAL && shouldCancelVisitorAction(player, plot, Flag.SWITCHES, "plotsystem.admin.switches")) {
                event.setCancelled(true);
                return;
            }
            Material clickedType = event.getClickedBlock().getType();
            if (isDoorType(clickedType) && shouldCancelVisitorAction(player, plot, Flag.DOORS, "plotsystem.admin.doors")) {
                event.setCancelled(true);
                return;
            }
            if (isSwitchType(clickedType) && shouldCancelVisitorAction(player, plot, Flag.SWITCHES, "plotsystem.admin.switches")) {
                event.setCancelled(true);
                return;
            }
            if (!canInteract(player, plot)) {
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
     * @param event the event
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
                        if (plot.hasOwner() && !plot.getOwners().contains(uuid) && !plot.getMembers().contains(uuid)) {
                            event.setCancelled(true);
                            uuids[0] = uuid;
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
        if (event.getTo() == null) return;
        handlePlotGameMode(event.getPlayer(), Plot.getPlot(event.getFrom()), Plot.getPlot(event.getTo()));
        Plot plot = Plot.getPlot(event.getTo());
        if (plot != null) {
            if (plot.hasOwner() && !isTrusted(event.getPlayer(), plot) && plot.isPlayerBanned(event.getPlayer()) && !event.getPlayer().hasPermission("plotsystem.admin.ignore")) {
                event.getPlayer().teleport(event.getFrom());
                event.getPlayer().sendMessage("/p home");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        previousGameModes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (Plot.isPlayerInPlotStatic(event.getPlayer())) {
            Plot plot = Plot.getPlot(event.getPlayer().getLocation());
            if (plot == null) return;
            if (plot.hasOwner() && !isTrusted(event.getPlayer(), plot) && plot.hasFlag(Flag.DENY_CHAT) && !event.getPlayer().hasPermission("plotsystem.admin.ignorechat")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Plot plot = Plot.getPlot(event.getPlayer().getLocation());
        if (plot == null || !plot.hasOwner() || isTrusted(event.getPlayer(), plot)) return;
        if (!plot.hasFlag(Flag.DENY_COMMANDS) || event.getPlayer().hasPermission("plotsystem.admin.ignorecommands")) return;
        String command = event.getMessage().split(" ")[0].toLowerCase();
        if (command.equals("/plot") || command.equals("/p") || command.equals("/plots")) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(Main.getInstance().getPrefix() + "§cCommands are disabled in this plot.");
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Plot plot = Plot.getPlot(event.getPlayer().getLocation());
        if (plot == null || !plot.hasOwner() || isTrusted(event.getPlayer(), plot)) return;
        if (!plot.hasFlag(Flag.ITEM_DROP) && !event.getPlayer().hasPermission("plotsystem.admin.itemdrop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Plot plot = Plot.getPlot(event.getItem().getLocation());
        if (plot == null || !plot.hasOwner() || isTrusted(player, plot)) return;
        if (!plot.hasFlag(Flag.ITEM_PICKUP) && !player.hasPermission("plotsystem.admin.itempickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Plot plot = Plot.getPlot(player.getLocation());
        if (plot == null || !plot.hasOwner()) return;
        if (event.getFoodLevel() < player.getFoodLevel() && !plot.hasFlag(Flag.HUNGER) && !player.hasPermission("plotsystem.admin.hunger")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnderPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL || event.getTo() == null) return;
        Plot plot = Plot.getPlot(event.getTo());
        if (plot == null || !plot.hasOwner() || isTrusted(event.getPlayer(), plot)) return;
        if (!plot.hasFlag(Flag.ENDER_PEARL) && !event.getPlayer().hasPermission("plotsystem.admin.enderpearl")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if (!event.isFlying()) return;
        Plot plot = Plot.getPlot(event.getPlayer().getLocation());
        if (plot == null || !plot.hasOwner() || isTrusted(event.getPlayer(), plot)) return;
        if (!plot.hasFlag(Flag.FLY) && !event.getPlayer().hasPermission("plotsystem.admin.fly")) {
            event.setCancelled(true);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
        Plot plot = Plot.getPlot(event.getVehicle().getLocation());
        if (shouldCancelVisitorAction(player, plot, Flag.VEHICLES, "plotsystem.admin.vehicles")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Plot plot = Plot.getPlot(event.getRightClicked().getLocation());
        if (shouldCancelVisitorAction(event.getPlayer(), plot, Flag.ARMOR_STANDS, "plotsystem.admin.armorstands")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        Plot plot = Plot.getPlot(clicked.getLocation());
        if (clicked instanceof ItemFrame && shouldCancelVisitorAction(event.getPlayer(), plot, Flag.ITEM_FRAMES, "plotsystem.admin.itemframes")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;
        if (!(event.getEntity() instanceof ItemFrame)) return;
        Player player = (Player) event.getRemover();
        Plot plot = Plot.getPlot(event.getEntity().getLocation());
        if (shouldCancelVisitorAction(player, plot, Flag.ITEM_FRAMES, "plotsystem.admin.itemframes")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        Plot plot = Plot.getPlot(player.getLocation());
        if (shouldCancelVisitorAction(player, plot, Flag.PROJECTILES, "plotsystem.admin.projectiles")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            Plot plot = Plot.getPlot(entity.getLocation());
            if (plot != null && plot.hasOwner() && !plot.hasFlag(Flag.POTIONS)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onAreaEffectCloud(AreaEffectCloudApplyEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            Plot plot = Plot.getPlot(entity.getLocation());
            if (plot != null && plot.hasOwner() && !plot.hasFlag(Flag.POTIONS)) {
                event.getAffectedEntities().clear();
                return;
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        cancelFireEvent(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        cancelFireEvent(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            cancelFireEvent(event.getBlock().getLocation(), event);
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        Material material = event.getBlock().getType();
        if (material != Material.WATER && material != Material.LAVA) return;
        Plot fromPlot = Plot.getPlot(event.getBlock().getLocation());
        Plot toPlot = Plot.getPlot(event.getToBlock().getLocation());
        Plot plot = toPlot != null ? toPlot : fromPlot;
        if (plot != null && plot.hasOwner() && !plot.hasFlag(Flag.LIQUID_FLOW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getBlock().getType() != Material.FARMLAND) return;
        Plot plot = Plot.getPlot(event.getBlock().getLocation());
        if (plot != null && plot.hasOwner() && !plot.hasFlag(Flag.CROP_TRAMPLE)) {
            event.setCancelled(true);
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
        task = new BukkitRunnable() {
            @Override
            public void run() {
                String plotWorldName = Main.getInstance().getConfig().getString("PlotWorld");
                if (plotWorldName == null) return;
                World plotWorld = Bukkit.getWorld(plotWorldName);
                if (plotWorld == null) return;
                for (Entity entity : plotWorld.getEntities()) {
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

    private boolean canAccessPlot(Player player, Plot plot, String adminPermission) {
        if (!plot.hasOwner()) {
            return player.hasPermission(adminPermission);
        }
        return isTrusted(player, plot) || player.hasPermission(adminPermission);
    }

    private boolean canInteract(Player player, Plot plot) {
        if (!plot.hasOwner()) {
            return player.hasPermission("plotsystem.admin.interact");
        }
        return isTrusted(player, plot) || plot.hasFlag(Flag.INTERACT) || player.hasPermission("plotsystem.admin.interact");
    }

    private boolean isTrusted(Player player, Plot plot) {
        return plot.isOwner(player) || plot.isMember(player);
    }

    private void handlePlotGameMode(Player player, Plot fromPlot, Plot toPlot) {
        if (samePlot(fromPlot, toPlot)) return;
        GameMode targetGameMode = toPlot == null ? null : toPlot.getGameMode();
        if (targetGameMode != null) {
            previousGameModes.putIfAbsent(player.getUniqueId(), player.getGameMode());
            if (player.getGameMode() != targetGameMode) {
                player.setGameMode(targetGameMode);
            }
            return;
        }

        if (fromPlot == null || fromPlot.getGameMode() == null) return;
        restorePreviousGameMode(player);
    }

    private void restorePreviousGameMode(Player player) {
        GameMode previousGameMode = previousGameModes.remove(player.getUniqueId());
        if (previousGameMode == null || player.getGameMode() == previousGameMode) {
            return;
        }
        player.setGameMode(previousGameMode);
    }

    private boolean samePlot(Plot first, Plot second) {
        if (first == null || second == null) return first == second;
        return first.getId() == second.getId();
    }

    private boolean shouldCancelVisitorAction(Player player, Plot plot, Flag flag, String adminPermission) {
        return plot != null
                && plot.hasOwner()
                && !isTrusted(player, plot)
                && !plot.hasFlag(flag)
                && !player.hasPermission(adminPermission);
    }

    private boolean isDoorType(Material material) {
        return Tag.DOORS.isTagged(material) || Tag.TRAPDOORS.isTagged(material) || Tag.FENCE_GATES.isTagged(material);
    }

    private boolean isSwitchType(Material material) {
        return Tag.BUTTONS.isTagged(material)
                || Tag.PRESSURE_PLATES.isTagged(material)
                || material == Material.LEVER;
    }

    private void cancelFireEvent(Location location, org.bukkit.event.Cancellable event) {
        Plot plot = Plot.getPlot(location);
        if (plot != null && plot.hasOwner() && !plot.hasFlag(Flag.FIRE_SPREAD)) {
            event.setCancelled(true);
        }
    }
}
