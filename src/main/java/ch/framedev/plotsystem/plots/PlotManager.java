package ch.framedev.plotsystem.plots;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.plots
 * / ClassName PlotManager
 * / Date: 21.10.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotManager {

    private static PlotManager instance;

    public PlotManager() {
        instance = this;
    }

    public static PlotManager getInstance() {
        return instance;
    }

    public List<Integer> getPlotHomes(OfflinePlayer player) {
        List<Integer> plotIds = new ArrayList<>();
        for (Plot plot : Plot.getPlayerPlots(player)) {
            plotIds.add(plot.getId());
        }
        return plotIds;
    }
}
