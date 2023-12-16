package ch.framedev.plotsystem.plots;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
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

    public List<Integer> getPlotHomes(Player player) {
        List<Integer> plotIds = new ArrayList<>();
        for (Plot plot : Plot.getPlayerPlots(player)) {
            plotIds.add(plot.getId());
        }
        return plotIds;
    }

    public HashMap<OfflinePlayer, Integer> getPlotHomesMap(OfflinePlayer player) {
        HashMap<OfflinePlayer, Integer> plotIds = new HashMap<>();
        for (Plot plot : Plot.getPlayerPlots(player)) {
            plotIds.put(player, plot.getId());
        }
        return plotIds;
    }

    public HashMap<Player, Integer> getPlotHomesMap(Player player) {
        HashMap<Player, Integer> plotIds = new HashMap<>();
        for (Plot plot : Plot.getPlayerPlots(player)) {
            plotIds.put(player, plot.getId());
        }
        return plotIds;
    }

    public Plot getPlotHomeByID(OfflinePlayer player, int id) {
        final Plot[] plot = {null};
        getPlotHomesMap(player).forEach((player1, integer) -> {
            if(integer == id)
                plot[0] = Plot.getPlot(id);
        });
        return plot[0];
    }

    public Plot getPlotHomeByID(Player player, int id) {
        final Plot[] plot = {null};
        getPlotHomesMap(player).forEach((player1, integer) -> {
            if(integer == id)
                plot[0] = Plot.getPlot(id);
        });
        return plot[0];
    }
}
