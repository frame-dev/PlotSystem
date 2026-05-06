package ch.framedev.plotsystem.events;

import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlotClaimEvent extends PlotEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID previousOwner;
    private final OfflinePlayer newOwner;
    private boolean cancelled;

    public PlotClaimEvent(@NotNull Plot plot, @Nullable UUID previousOwner, @NotNull OfflinePlayer newOwner) {
        super(plot);
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    public @Nullable UUID getPreviousOwner() {
        return previousOwner;
    }

    public @NotNull OfflinePlayer getNewOwner() {
        return newOwner;
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
