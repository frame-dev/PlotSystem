package ch.framedev.plotsystem.events;

import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlotUnclaimEvent extends PlotEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID previousOwner;
    private boolean cancelled;

    public PlotUnclaimEvent(@NotNull Plot plot, @NotNull UUID previousOwner) {
        super(plot);
        this.previousOwner = previousOwner;
    }

    public @NotNull UUID getPreviousOwner() {
        return previousOwner;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
