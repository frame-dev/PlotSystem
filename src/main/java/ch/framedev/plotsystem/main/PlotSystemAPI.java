package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.Location;
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

    private static PlotSystemAPI instance;
    @SuppressWarnings("unused")
    private final Main plugin;

    protected PlotSystemAPI() {
        this.plugin = Main.getInstance();
        instance = this;
    }

    public static PlotSystemAPI getInstance() {
        return instance;
    }

    public static PlotSystemAPI getAPI() {
        return instance;
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

    public List<UUID> getMembers(Plot plot) {
        return plot.getMembers();
    }

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
}
