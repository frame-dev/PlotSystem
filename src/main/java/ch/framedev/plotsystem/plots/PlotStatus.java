package ch.framedev.plotsystem.plots;

import org.jetbrains.annotations.Nullable;

/**
 * This Class is for the Plot to set the Status
 *
 * @author framedev
 */
public enum PlotStatus {

    /**
     * (NOT_SET) means the Plot has not been initialized
     */
    NOT_SET(-1),

    /**
     * (NOT_CLAIMED) means the Plot has not been claimed yet
     */
    NOT_CLAIMED(1),

    /**
     * (CLAIMED) means the Plot has an Owner
     */
    CLAIMED(2),

    /**
     * (FOR_SALE) means the Plot can be sold
     */
    FOR_SALE(3),

    /**
     * (SOLD) means a Player has buy this Plot
     */
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
