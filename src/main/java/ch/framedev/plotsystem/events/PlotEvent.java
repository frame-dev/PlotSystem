package ch.framedev.plotsystem.events;

import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class PlotEvent extends Event {

    private final Plot plot;

    protected PlotEvent(@NotNull Plot plot) {
        this.plot = plot;
    }

    public @NotNull Plot getPlot() {
        return plot;
    }
}
