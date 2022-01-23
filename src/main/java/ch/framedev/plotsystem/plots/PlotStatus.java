package ch.framedev.plotsystem.plots;

import org.jetbrains.annotations.Nullable;

public enum PlotStatus {

    NOT_SET(-1),
    NOT_CLAIMED(1),
    CLAIMED(2),
    FOR_SALE(3),
    SOLD(4);

    private final int status;

    PlotStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static @Nullable PlotStatus getPlotStatus(int status) {
        for(PlotStatus plotStatus : PlotStatus.values())
            if(status == plotStatus.status) 
            return plotStatus; 
        return null;
    }
}
