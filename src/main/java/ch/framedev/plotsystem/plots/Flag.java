package ch.framedev.plotsystem.plots;

import java.util.Objects;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.plotsystem.plots
 * / ClassName Flags
 * / Date: 11.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */
public enum Flag {

    PVP("PVP"),
    EXPLOSIONS("EXPLOSIONS"),
    REDSTONE("REDSTONE"),
    INTERACT("INTERACT"),
    DENY_CHAT("DENY_CHAT"),
    MONSTERS("MONSTERS"),
    ANIMALS("ANIMALS"),
    FALL_DAMAGE("FALL_DAMAGE"),
    MOB_DAMAGE("MOB_DAMAGE"),
    SELL("SELL"),
    FARM_PROTECT("FARM_PROTECT");

    private final String type;

    Flag(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Flag getFlag(String type) {
        for (Flag flag : Flag.values()) {
            if (Objects.equals(flag.getType(), type)) return flag;
        }
        return null;
    }
}
