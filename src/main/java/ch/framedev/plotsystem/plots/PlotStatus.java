package ch.framedev.plotsystem.plots;

import org.jetbrains.annotations.Nullable;

public enum PlotStatus {

    NOT_SET(-1),
    NOT_CLAIMED(1),
    CLAIMED(2),
    FOR_SALE(3),
    SOLD(4);

    private final int statusId;

    PlotStatus(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public static @Nullable PlotStatus getPlotStatus(int statusId) {
        for (PlotStatus plotStatus : PlotStatus.values())
            if (statusId == plotStatus.statusId)
                return plotStatus;
        return null;
    }
}
