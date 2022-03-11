package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import ch.framedev.plotsystem.plots.PlotStatus;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.main
 * / ClassName PlotSystemAPI
 * / Date: 19.01.22
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotSystemAPI {

    // Instance for API and other stuffs
    private static PlotSystemAPI instance;

    /**
     * PlotSystem Constructor
     */
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

    /**
     * Returns the Default Flags for the Plots they can be changed in the config.yml from the PlotSystem
     *
     * @return return the Default Flags for the Plots
     */
    public List<String> getDefaultFlags() {
        return Main.getInstance().getDefaultFlags();
    }

    public boolean isLimitedClaim() {
        return Main.getInstance().isLimitedClaim();
    }

    public long getLimitedBlocksDefault() {
        return Main.getInstance().getLimitedAmount();
    }

    public long getAvailableBlocksForPlayer(Player player) {
        if (Main.getInstance().getLimitedHashMap().isEmpty()) return 0;
        if (!Main.getInstance().getLimitedHashMap().containsKey(player)) return 0;
        return Main.getInstance().getLimitedHashMap().get(player);
    }

    public @Nullable Plot getPlotById(int id) {
        return Plot.getPlot(id);
    }

    public @Nullable Plot getPlayerLocationPlot(Player player) {
        return Plot.getPlot(player.getLocation());
    }

    public Flag[] getFlags(Plot plot) {
        return plot.getFlags().toArray(new Flag[0]);
    }

    public Flag getFlag(String flag) {
        return Flag.getFlag(flag.toUpperCase());
    }

    /**
     * @param plot the Selected Plot
     * @return returns a list of UUIDs of all Players
     */
    public List<UUID> getMembers(Plot plot) {
        return plot.getMembers();
    }

    /**
     * @param plot the selected Plot
     * @return returns a list of all Owners from the Plot
     */
    public List<UUID> getOwners(Plot plot) {
        return plot.getOwners();
    }

    public UUID getOwner(Plot plot) {
        return plot.getOwner();
    }

    public @Nullable Location getPlotHome(Plot plot) {
        return plot.getHome();
    }

    public void addFlag(Plot plot, Flag... flags) {
        plot.addFlag(flags);
    }

    public void removeFlag(Plot plot, Flag... flags) {
        plot.removeFlag(flags);
    }

    /**
     * Return the Status of the giving Plot
     *
     * @param plot the Selected Plot to get the PlotStatus
     * @return return the PlotStatus from the Plot
     */
    public PlotStatus getPlotStatus(Plot plot) {
        return plot.getStatus();
    }

    public PlotStatus getPlotStatusById(int statusId) {
        return PlotStatus.getPlotStatus(statusId);
    }

    public int getPlotStatusById(Plot plot) {
        return plot.getStatus().getStatusId();
    }

    /**
     * Return if the Location is inside a Plot
     *
     * @param location the Selected Location to check if it is in a Plot
     * @return return if the Location is inside a Plot
     */
    public boolean isInPlot(Location location) {
        if (Plot.getPlots() == null) return false;
        boolean success = false;
        for (Plot plot : Plot.getPlots()) {
            if (plot.inPlot(location)) success = true;
        }
        return success;
    }

    /**
     * Return if the Block is inside a Plot
     *
     * @param block the Block to check if it is in a Plot
     * @return return if the Block is inside a Plot
     */
    public boolean isInPlot(Block block) {
        if (Plot.getPlots() == null) return false;
        boolean success = false;
        for (Plot plot : Plot.getPlots()) {
            if (plot.inPlot(block.getLocation())) success = true;
        }
        return success;
    }

    /**
     * Return if the Player is inside a Plot
     *
     * @param player the Selected Player to check if it is in a Plot
     * @return return if the Player is inside a Plot
     */
    public boolean isInPlot(Player player) {
        if (Plot.getPlots() == null) return false;
        boolean success = false;
        for (Plot plot : Plot.getPlots())
            if (plot.inPlot(player.getLocation())) success = true;
        return success;
    }
}
