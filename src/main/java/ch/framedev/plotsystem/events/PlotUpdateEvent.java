package ch.framedev.plotsystem.events;

import ch.framedev.plotsystem.plots.Plot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlotUpdateEvent extends PlotEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UpdateType updateType;
    private final UUID target;
    private boolean cancelled;

    public PlotUpdateEvent(@NotNull Plot plot, @NotNull UpdateType updateType) {
        this(plot, updateType, null);
    }

    public PlotUpdateEvent(@NotNull Plot plot, @NotNull UpdateType updateType, @Nullable UUID target) {
        super(plot);
        this.updateType = updateType;
        this.target = target;
    }

    public @NotNull UpdateType getUpdateType() {
        return updateType;
    }

    public @Nullable UUID getTarget() {
        return target;
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

    public enum UpdateType {
        FLAG_ADDED,
        FLAG_REMOVED,
        FLAGS_CLEARED,
        DEFAULT_FLAGS_APPLIED,
        MEMBER_ADDED,
        MEMBER_REMOVED,
        OWNER_ADDED,
        OWNER_REMOVED,
        PLAYER_BANNED,
        PLAYER_UNBANNED,
        HOME_CHANGED,
        PRICE_CHANGED,
        GAMEMODE_CHANGED,
        STATUS_CHANGED,
        SAVED
    }
}
