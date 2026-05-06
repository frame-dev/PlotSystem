package ch.framedev.plotsystem.events;

import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlotCreateEvent extends PlotEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer owner;
    private boolean cancelled;

    public PlotCreateEvent(@NotNull Plot plot, @Nullable OfflinePlayer owner) {
        super(plot);
        this.owner = owner;
    }

    public @Nullable OfflinePlayer getOwner() {
        return owner;
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
