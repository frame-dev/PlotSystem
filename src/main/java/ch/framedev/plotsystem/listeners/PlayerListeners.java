package ch.framedev.plotsystem.listeners;

import ch.framedev.plotsystem.main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.listeners
 * / ClassName PlayerListeners
 * / Date: 22.01.22
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class PlayerListeners implements Listener {

    // Main Plugin extends from JavaPlugin
    private final Main plugin;

    public PlayerListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(plugin.getPrefix() + "§aData Loading...").create());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.isLimitedClaim()) {
                    if (plugin.getConfig().contains(event.getPlayer().getName())) {
                        if (!plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                            plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getConfig().getLong(event.getPlayer().getName()));
                        } else {
                            plugin.getLimitedHashMap().remove(event.getPlayer());
                            plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getConfig().getLong(event.getPlayer().getName()));
                        }
                    } else {
                        if (!plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                            plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getLimitedAmount());
                        } else {
                            plugin.getLimitedHashMap().remove(event.getPlayer());
                            plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getLimitedAmount());
                        }
                    }
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(plugin.getPrefix() + "§aData Loaded!!!").create());
            }
        }.runTaskLater(plugin, 120);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.isLimitedClaim()) {
            if (plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                plugin.getConfig().set(event.getPlayer().getName(), plugin.getLimitedHashMap().get(event.getPlayer()));
                plugin.saveConfig();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        if (plugin.isLimitedClaim()) {
            if (plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                plugin.getConfig().set(event.getPlayer().getName(), plugin.getLimitedHashMap().get(event.getPlayer()));
                plugin.saveConfig();
            }
        }
    }
}
