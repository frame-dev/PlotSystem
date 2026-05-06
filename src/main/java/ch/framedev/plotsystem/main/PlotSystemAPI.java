package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.events.PlotClaimEvent;
import ch.framedev.plotsystem.events.PlotCreateEvent;
import ch.framedev.plotsystem.events.PlotDeleteEvent;
import ch.framedev.plotsystem.events.PlotUnclaimEvent;
import ch.framedev.plotsystem.events.PlotUpdateEvent;
import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotStatus;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.main
 * / ClassName PlotSystemAPI
 * / Date: 19.01.22
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

@SuppressWarnings("unused")
public class PlotSystemAPI {

    private static PlotSystemAPI instance;

    protected PlotSystemAPI() {
        instance = this;
    }

    public static PlotSystemAPI getInstance() {
        return instance;
    }

    /**
     * This Method return this PlotSystemAPI
     *
     * @return Returns the PlotSystemAPI
     */
    public static PlotSystemAPI getAPI() {
        return instance;
    }

    public Main getPlugin() {
        return Main.getInstance();
    }

    public boolean isReady() {
        return Main.getInstance() != null;
    }

    /**
     * Returns the Default Flags for the Plots they can be changed in the config.yml from the PlotSystem
     *
     * @return return the Default Flags for the Plots
     */
    public List<String> getDefaultFlags() {
        return Collections.unmodifiableList(new ArrayList<>(Main.getInstance().getDefaultFlags()));
    }

    public boolean isLimitedClaim() {
        return Main.getInstance().isLimitedClaim();
    }

    public long getLimitedBlocksDefault() {
        return Main.getInstance().getLimitedAmount();
    }

    public int getMaxClaimedPlotsDefault() {
        return Main.getInstance().getMaxClaimedPlots();
    }

    public long getAvailableBlocksForPlayer(Player player) {
        return getRemainingClaimBlocks(player);
    }

    public long getRemainingClaimBlocks(@Nullable Player player) {
        if (player == null) return 0;
        return Main.getInstance().getLimitedHashMap().getOrDefault(player, Main.getInstance().getLimitedAmount());
    }

    public boolean setRemainingClaimBlocks(@Nullable Player player, long blocks) {
        if (player == null || blocks < 0) return false;
        Main.getInstance().getLimitedHashMap().put(player, blocks);
        Main.getInstance().getConfig().set(player.getName(), blocks);
        Main.getInstance().saveConfig();
        return true;
    }

    public boolean addRemainingClaimBlocks(@Nullable Player player, long blocks) {
        if (player == null) return false;
        long updatedBlocks = Math.max(0, getRemainingClaimBlocks(player) + blocks);
        return setRemainingClaimBlocks(player, updatedBlocks);
    }

    public int getOwnedPlotCount(@Nullable OfflinePlayer player) {
        return getOwnedPlots(player).size();
    }

    public int getMemberPlotCount(@Nullable OfflinePlayer player) {
        return getMemberPlots(player).size();
    }

    public int getRemainingPlotClaims(@Nullable OfflinePlayer player) {
        if (player == null) return 0;
        if (!isLimitedClaim()) return Integer.MAX_VALUE;
        return Math.max(0, getMaxClaimedPlotsDefault() - getOwnedPlotCount(player));
    }

    public boolean canClaimMorePlots(@Nullable OfflinePlayer player) {
        return player != null && (!isLimitedClaim() || getRemainingPlotClaims(player) > 0);
    }

    public boolean canClaimBlocks(@Nullable Player player, long blocks) {
        if (player == null || blocks < 0) return false;
        return !isLimitedClaim() || getRemainingClaimBlocks(player) >= blocks;
    }

    public boolean canClaim(@Nullable Player player, long blocks) {
        return player != null && canClaimMorePlots(player) && canClaimBlocks(player, blocks);
    }

    public boolean isDatabaseEnabled() {
        return Main.getInstance().isDatabaseSupported();
    }

    public String getStorageBackend() {
        if (!isDatabaseEnabled()) return "file";
        if (Main.getInstance().getDatabaseManager().isMysql()) return "mysql";
        if (Main.getInstance().getDatabaseManager().isSql()) return "sqlite";
        if (Main.getInstance().getDatabaseManager().isMongoDb()) return "mongodb";
        return "database";
    }

    public List<Plot> getPlots() {
        List<Plot> plots = Plot.getPlots();
        if (plots == null) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(plots));
    }

    public @Nullable Plot getPlotById(int id) {
        return Plot.getPlot(id);
    }

    public Optional<Plot> findPlotById(int id) {
        return Optional.ofNullable(getPlotById(id));
    }

    public @Nullable Plot getPlotAt(@Nullable Location location) {
        if (location == null) return null;
        return Plot.getPlot(location);
    }

    public Optional<Plot> findPlotAt(@Nullable Location location) {
        return Optional.ofNullable(getPlotAt(location));
    }

    public @Nullable Plot getPlotAt(@Nullable Block block) {
        if (block == null) return null;
        return getPlotAt(block.getLocation());
    }

    public @Nullable Plot getPlotAt(@Nullable Player player) {
        if (player == null) return null;
        return getPlotAt(player.getLocation());
    }

    public @Nullable Plot getPlayerLocationPlot(@Nullable Player player) {
        return getPlotAt(player);
    }

    public List<Plot> getPlayerPlots(@Nullable OfflinePlayer player) {
        if (player == null) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(Plot.getPlayerPlots(player)));
    }

    public List<Integer> getPlayerPlotIds(@Nullable OfflinePlayer player) {
        List<Integer> plotIds = new ArrayList<>();
        for (Plot plot : getPlayerPlots(player)) {
            plotIds.add(plot.getId());
        }
        return Collections.unmodifiableList(plotIds);
    }

    public List<Plot> getOwnedPlots(@Nullable OfflinePlayer player) {
        if (player == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (plot.isOwner(player)) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getMemberPlots(@Nullable OfflinePlayer player) {
        if (player == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (plot.isMember(player)) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getPlotsByStatus(@Nullable PlotStatus status) {
        if (status == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (plot.getStatus() == status) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getPlotsWithFlag(@Nullable Flag flag) {
        if (flag == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (plot.hasFlag(flag)) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getPlotsWithoutFlag(@Nullable Flag flag) {
        if (flag == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (!plot.hasFlag(flag)) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getBuyablePlots() {
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (plot.isBuyable()) plots.add(plot);
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getPlotsInWorld(@Nullable World world) {
        if (world == null) return Collections.emptyList();
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : getPlots()) {
            if (world.equals(getPlotWorld(plot))) {
                plots.add(plot);
            }
        }
        return Collections.unmodifiableList(plots);
    }

    public List<Plot> getPlotsInWorld(@Nullable String worldName) {
        if (worldName == null) return Collections.emptyList();
        return getPlotsInWorld(Bukkit.getWorld(worldName));
    }

    public int getPlotCount() {
        return getPlots().size();
    }

    public int getClaimedPlotCount() {
        int count = 0;
        for (Plot plot : getPlots()) {
            if (plot.hasOwner()) count++;
        }
        return count;
    }

    public int getAvailablePlotCount() {
        return getPlotCount() - getClaimedPlotCount();
    }

    public long getTotalClaimedBlocks() {
        long blocks = 0;
        for (Plot plot : getPlots()) {
            if (plot.hasOwner()) blocks += getPlotSize(plot);
        }
        return blocks;
    }

    public long getTotalPlotBlocks() {
        long blocks = 0;
        for (Plot plot : getPlots()) {
            blocks += getPlotSize(plot);
        }
        return blocks;
    }

    public long getPlotSize(@Nullable Plot plot) {
        if (plot == null || plot.getCuboid() == null) return 0;
        return (long) plot.getCuboid().getSizeX() * plot.getCuboid().getSizeY() * plot.getCuboid().getSizeZ();
    }

    public @Nullable World getPlotWorld(@Nullable Plot plot) {
        if (plot == null || plot.getCuboid() == null) return null;
        try {
            return plot.getCuboid().getWorld();
        } catch (IllegalStateException exception) {
            return null;
        }
    }

    public boolean isPlotLoaded(@Nullable Plot plot) {
        return getPlotWorld(plot) != null;
    }

    public boolean isClaimed(@Nullable Plot plot) {
        return plot != null && plot.hasOwner();
    }

    public boolean isBuyable(@Nullable Plot plot) {
        return plot != null && plot.isBuyable();
    }

    public boolean overlapsExistingPlot(@Nullable Location firstCorner, @Nullable Location secondCorner) {
        if (firstCorner == null || secondCorner == null) return false;
        return overlapsExistingPlot(new Cuboid(firstCorner, secondCorner), null);
    }

    public Flag[] getFlags(Plot plot) {
        return plot.getFlags().toArray(new Flag[0]);
    }

    public List<Flag> getAvailableFlags() {
        List<Flag> flags = new ArrayList<>();
        Collections.addAll(flags, Flag.values());
        return Collections.unmodifiableList(flags);
    }

    public List<String> getAvailableFlagNames() {
        List<String> flagNames = new ArrayList<>();
        for (Flag flag : Flag.values()) {
            flagNames.add(flag.getType());
        }
        return Collections.unmodifiableList(flagNames);
    }

    public Flag getFlag(String flag) {
        if (flag == null) return null;
        return Flag.getFlag(flag.toUpperCase());
    }

    public boolean hasFlag(@Nullable Plot plot, @Nullable Flag flag) {
        return plot != null && flag != null && plot.hasFlag(flag);
    }

    /**
     * @param plot the Selected Plot
     * @return returns a list of UUIDs of all Players
     */
    public List<UUID> getMembers(Plot plot) {
        return Collections.unmodifiableList(new ArrayList<>(plot.getMembers()));
    }

    /**
     * @param plot the selected Plot
     * @return returns a list of all Owners from the Plot
     */
    public List<UUID> getOwners(Plot plot) {
        return Collections.unmodifiableList(new ArrayList<>(plot.getOwners()));
    }

    public @Nullable UUID getOwner(@Nullable Plot plot) {
        return plot == null ? null : plot.getOwner();
    }

    public @Nullable OfflinePlayer getOwnerPlayer(@Nullable Plot plot) {
        UUID owner = getOwner(plot);
        return owner == null ? null : Bukkit.getOfflinePlayer(owner);
    }

    public List<OfflinePlayer> getOwnerPlayers(@Nullable Plot plot) {
        return getOfflinePlayers(plot == null ? Collections.emptyList() : plot.getOwners());
    }

    public List<OfflinePlayer> getMemberPlayers(@Nullable Plot plot) {
        return getOfflinePlayers(plot == null ? Collections.emptyList() : plot.getMembers());
    }

    public List<OfflinePlayer> getBannedPlayers(@Nullable Plot plot) {
        return getOfflinePlayers(plot == null ? Collections.emptyList() : plot.getBannedPlayers());
    }

    public @Nullable Location getPlotHome(@Nullable Plot plot) {
        return plot == null ? null : plot.getHome();
    }

    public boolean addFlag(@Nullable Plot plot, Flag... flags) {
        if (plot == null || flags == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.FLAG_ADDED, null)) return false;
        plot.addFlag(flags);
        plot.createPlot();
        return true;
    }

    public boolean removeFlag(@Nullable Plot plot, Flag... flags) {
        if (plot == null || flags == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.FLAG_REMOVED, null)) return false;
        plot.removeFlag(flags);
        plot.createPlot();
        return true;
    }

    public boolean setFlag(@Nullable Plot plot, @Nullable Flag flag, boolean enabled) {
        if (plot == null || flag == null) return false;
        return enabled ? addFlag(plot, flag) : removeFlag(plot, flag);
    }

    public boolean clearFlags(@Nullable Plot plot) {
        if (plot == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.FLAGS_CLEARED, null)) return false;
        plot.removeFlag(plot.getFlags().toArray(new Flag[0]));
        plot.createPlot();
        return true;
    }

    public boolean applyDefaultFlags(@Nullable Plot plot) {
        if (plot == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.DEFAULT_FLAGS_APPLIED, null)) return false;
        for (String defaultFlag : Main.getInstance().getDefaultFlags()) {
            Flag flag = getFlag(defaultFlag);
            if (flag != null) {
                plot.addFlag(flag);
            }
        }
        plot.createPlot();
        return true;
    }

    public boolean addMember(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.MEMBER_ADDED, player.getUniqueId())) return false;
        plot.addMember(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean removeMember(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.MEMBER_REMOVED, player.getUniqueId())) return false;
        plot.removeMember(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean addOwner(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.OWNER_ADDED, player.getUniqueId())) return false;
        plot.addOwner(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean removeOwner(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.OWNER_REMOVED, player.getUniqueId())) return false;
        plot.removeOwner(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean banPlayer(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.PLAYER_BANNED, player.getUniqueId())) return false;
        plot.addBannedPlayer(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean unbanPlayer(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        if (plot == null || player == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.PLAYER_UNBANNED, player.getUniqueId())) return false;
        plot.removeBannedPlayer(player.getUniqueId());
        plot.createPlot();
        return true;
    }

    public boolean setHome(@Nullable Plot plot, @Nullable Location location) {
        if (plot == null || location == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.HOME_CHANGED, null)) return false;
        plot.setHome(location);
        plot.createPlot();
        return true;
    }

    public boolean setGameMode(@Nullable Plot plot, @Nullable GameMode gameMode) {
        if (plot == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.GAMEMODE_CHANGED, null)) return false;
        plot.setGameMode(gameMode);
        plot.createPlot();
        return true;
    }

    public @Nullable GameMode getGameMode(@Nullable Plot plot) {
        return plot == null ? null : plot.getGameMode();
    }

    public boolean setPrice(@Nullable Plot plot, double price) {
        if (plot == null || price < 0) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.PRICE_CHANGED, null)) return false;
        plot.setPrice(price);
        plot.createPlot();
        return true;
    }

    public boolean setForSale(@Nullable Plot plot, double price) {
        if (plot == null || price <= 0) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.PRICE_CHANGED, null)) return false;
        plot.setPrice(price);
        plot.setStatus(PlotStatus.FOR_SALE);
        plot.createPlot();
        return true;
    }

    public boolean cancelSale(@Nullable Plot plot) {
        if (plot == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.PRICE_CHANGED, null)) return false;
        plot.setPrice(0);
        if (plot.hasOwner()) {
            plot.setStatus(PlotStatus.CLAIMED);
        } else {
            plot.setStatus(PlotStatus.NOT_CLAIMED);
        }
        plot.createPlot();
        return true;
    }

    /**
     * Return the Status of the giving Plot
     *
     * @param plot the Selected Plot to get the PlotStatus
     * @return return the PlotStatus from the Plot
     */
    public @Nullable PlotStatus getPlotStatus(@Nullable Plot plot) {
        return plot == null ? null : plot.getStatus();
    }

    public boolean setPlotStatus(@Nullable Plot plot, @Nullable PlotStatus status) {
        if (plot == null || status == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.STATUS_CHANGED, null)) return false;
        plot.setStatus(status);
        plot.createPlot();
        return true;
    }

    public @Nullable PlotStatus getPlotStatusById(int statusId) {
        return PlotStatus.getPlotStatus(statusId);
    }

    public int getPlotStatusById(@Nullable Plot plot) {
        return getPlotStatusId(plot);
    }

    public int getPlotStatusId(@Nullable Plot plot) {
        return plot == null || plot.getStatus() == null ? -1 : plot.getStatus().getStatusId();
    }

    /**
     * Return if the Location is inside a Plot
     *
     * @param location the Selected Location to check if it is in a Plot
     * @return return if the Location is inside a Plot
     */
    public boolean isInPlot(Location location) {
        return getPlotAt(location) != null;
    }

    /**
     * Return if the Block is inside a Plot
     *
     * @param block the Block to check if it is in a Plot
     * @return return if the Block is inside a Plot
     */
    public boolean isInPlot(Block block) {
        return getPlotAt(block) != null;
    }

    /**
     * Return if the Player is inside a Plot
     *
     * @param player the Selected Player to check if it is in a Plot
     * @return return if the Player is inside a Plot
     */
    public boolean isInPlot(Player player) {
        return getPlotAt(player) != null;
    }

    public boolean isOwner(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        return plot != null && player != null && plot.isOwner(player);
    }

    public boolean isMember(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        return plot != null && player != null && plot.isMember(player);
    }

    public boolean isTrusted(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        return isOwner(plot, player) || isMember(plot, player);
    }

    public boolean isBanned(@Nullable Plot plot, @Nullable OfflinePlayer player) {
        return plot != null && player != null && plot.getBannedPlayers().contains(player.getUniqueId());
    }

    public boolean canBreak(@Nullable Player player, @Nullable Location location) {
        return canAccess(player, getPlotAt(location), "plotsystem.admin.break");
    }

    public boolean canPlace(@Nullable Player player, @Nullable Location location) {
        return canAccess(player, getPlotAt(location), "plotsystem.admin.place");
    }

    public boolean canInteract(@Nullable Player player, @Nullable Location location) {
        Plot plot = getPlotAt(location);
        if (plot == null || player == null) return plot == null;
        if (!plot.hasOwner()) return player.hasPermission("plotsystem.admin.interact");
        return isTrusted(plot, player) || plot.hasFlag(Flag.INTERACT) || player.hasPermission("plotsystem.admin.interact");
    }

    public boolean canEnter(@Nullable Player player, @Nullable Plot plot) {
        if (plot == null || player == null) return plot == null;
        return !plot.hasOwner()
                || isTrusted(plot, player)
                || !isBanned(plot, player)
                || player.hasPermission("plotsystem.admin.ignore");
    }

    public boolean claimPlot(@Nullable Plot plot, @Nullable OfflinePlayer owner) {
        if (plot == null || owner == null || plot.hasOwner()) return false;
        PlotClaimEvent event = new PlotClaimEvent(plot, plot.getOwner(), owner);
        if (callCancellableEvent(event)) return false;
        plot.setOwner(owner.getUniqueId());
        plot.setStatus(PlotStatus.CLAIMED);
        plot.createPlot();
        return true;
    }

    public boolean transferPlot(@Nullable Plot plot, @Nullable OfflinePlayer newOwner) {
        if (plot == null || newOwner == null) return false;
        PlotClaimEvent event = new PlotClaimEvent(plot, plot.getOwner(), newOwner);
        if (callCancellableEvent(event)) return false;
        plot.setOwner(newOwner.getUniqueId());
        plot.setStatus(PlotStatus.CLAIMED);
        plot.createPlot();
        return true;
    }

    public boolean unclaimPlot(@Nullable Plot plot) {
        if (plot == null || plot.getOwner() == null) return false;
        PlotUnclaimEvent event = new PlotUnclaimEvent(plot, plot.getOwner());
        if (callCancellableEvent(event)) return false;
        plot.setOwner(null);
        plot.setOwners(Collections.emptyList());
        plot.setMembers(Collections.emptyList());
        plot.setBannedPlayers(Collections.emptyList());
        plot.setPrice(0);
        plot.setStatus(PlotStatus.NOT_CLAIMED);
        plot.createPlot();
        return true;
    }

    public @Nullable Plot createPlot(@NotNull Location firstCorner, @NotNull Location secondCorner, @Nullable OfflinePlayer owner) {
        Objects.requireNonNull(firstCorner, "firstCorner");
        Objects.requireNonNull(secondCorner, "secondCorner");
        Cuboid cuboid = new Cuboid(firstCorner, secondCorner);
        if (overlapsExistingPlot(cuboid, null)) return null;

        Plot plot = new Plot(Plot.getHighestId() + 1, firstCorner, secondCorner);
        if (owner != null) {
            plot.setOwner(owner.getUniqueId());
            plot.setStatus(PlotStatus.CLAIMED);
        } else {
            plot.setStatus(PlotStatus.NOT_CLAIMED);
        }
        PlotCreateEvent event = new PlotCreateEvent(plot, owner);
        if (callCancellableEvent(event)) return null;
        plot.createPlot();
        return plot;
    }

    public @Nullable Plot createPlot(@NotNull Location firstCorner, @NotNull Location secondCorner) {
        return createPlot(firstCorner, secondCorner, null);
    }

    public boolean savePlot(@Nullable Plot plot) {
        if (plot == null) return false;
        if (callUpdateEvent(plot, PlotUpdateEvent.UpdateType.SAVED, null)) return false;
        plot.createPlot();
        return true;
    }

    public boolean deletePlot(@Nullable Plot plot) {
        if (plot == null) return false;
        PlotDeleteEvent event = new PlotDeleteEvent(plot);
        if (callCancellableEvent(event)) return false;
        if (Main.getInstance().isDatabaseSupported()) {
            Main.getInstance().getIDatabase().deletePlot(plot);
            return true;
        }

        File file = new File(Main.getInstance().getDataFolder(), "plots.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Plot> plots = new ArrayList<>(getPlots());
        if (!plots.removeIf(existing -> existing.getId() == plot.getId())) return false;
        config.set("Plots", plots);
        try {
            config.save(file);
            return true;
        } catch (IOException exception) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Error while deleting plot " + plot.getId(), exception);
            return false;
        }
    }

    public int getHighestPlotId() {
        return Plot.getHighestId();
    }

    private List<OfflinePlayer> getOfflinePlayers(List<UUID> uuids) {
        List<OfflinePlayer> players = new ArrayList<>();
        for (UUID uuid : uuids) {
            if (uuid != null) {
                players.add(Bukkit.getOfflinePlayer(uuid));
            }
        }
        return Collections.unmodifiableList(players);
    }

    private boolean canAccess(@Nullable Player player, @Nullable Plot plot, String adminPermission) {
        if (plot == null || player == null) return plot == null;
        if (!plot.hasOwner()) return player.hasPermission(adminPermission);
        return isTrusted(plot, player) || player.hasPermission(adminPermission);
    }

    private boolean callUpdateEvent(Plot plot, PlotUpdateEvent.UpdateType type, UUID target) {
        return callCancellableEvent(new PlotUpdateEvent(plot, type, target));
    }

    private boolean callCancellableEvent(Cancellable event) {
        Bukkit.getPluginManager().callEvent((org.bukkit.event.Event) event);
        return event.isCancelled();
    }

    private boolean overlapsExistingPlot(Cuboid cuboid, @Nullable Plot ignoredPlot) {
        for (Plot plot : getPlots()) {
            if (plot.equals(ignoredPlot)) continue;
            for (Block block : plot.getCuboid().getBlocks()) {
                if (cuboid.contains(block.getLocation())) return true;
            }
        }
        return false;
    }
}
