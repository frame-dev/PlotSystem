package ch.framedev.plotsystem.main;

import ch.framedev.plotsystem.plots.Flag;
import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
}
