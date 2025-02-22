package ch.framedev.plotsystem.utils;



/*
 * ch.framedev.plotsystem.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 22.02.2025 17:09
 */

import ch.framedev.plotsystem.plots.Plot;

import java.util.List;

public interface IDatabase {

    void createTable();
    void insertPlot(Plot plot);
    void updatePlot(Plot plot);
    boolean existsPlot(int plotId);
    Plot getPlotById(int id);
    void deletePlot(Plot plot);
    List<Plot> getPlots();
}
