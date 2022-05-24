package ch.framedev.plotsystem.plots;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.plots
 * / ClassName PlotsSetup
 * / Date: 12.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlotsSetup {

    private final Cuboid cuboid;

    public PlotsSetup(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    /**
     * Create Plots in all Selected Chunks
     *
     * @param startId Start Plot ID, increases every new Plot
     */
    public void createFromChunks(int startId) throws Exception {
        if(cuboid == null) throw new Exception("Cuboid is Null");
        int id = startId;
        List<Plot> plots = new ArrayList<>();
        for (Chunk chunk : cuboid.getChunks()) {
            Plot plot = new Plot(id, chunk);
            id++;
            plots.add(plot);
        }
        for(Plot plot : plots) {
            for (Block corner : plot.getCuboid().corners()) {
                corner.getWorld().getHighestBlockAt(corner.getLocation()).getLocation().add(0,1,0).getBlock().setType(Material.GLOWSTONE);
            }
            plot.createPlot();
        }
        Main.getInstance().setPlots(plots);
    }
}
