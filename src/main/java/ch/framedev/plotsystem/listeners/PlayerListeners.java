package ch.framedev.plotsystem.listeners;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.main.PlotSystemAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
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
                        if (!event.getPlayer().hasPermission("plotsystem.limitedplot.nolimit")) {
                            if (!plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                                plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getLimitedAmount());
                            } else {
                                plugin.getLimitedHashMap().remove(event.getPlayer());
                                plugin.getLimitedHashMap().put(event.getPlayer(), plugin.getLimitedAmount());
                            }
                        } else {
                            if (!plugin.getLimitedHashMap().containsKey(event.getPlayer())) {
                                plugin.getLimitedHashMap().put(event.getPlayer(), Long.MAX_VALUE);
                            } else {
                                plugin.getLimitedHashMap().remove(event.getPlayer());
                                plugin.getLimitedHashMap().put(event.getPlayer(), Long.MAX_VALUE);
                            }
                        }
                    }
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(plugin.getPrefix() + "§aData Loaded!!!").create());
                if (plugin.getConfig().getBoolean("Title")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (PlotSystemAPI.getAPI().isInPlot(event.getPlayer())) {
                                if (event.getPlayer().getLocale().startsWith("en_")) {
                                    if (PlotSystemAPI.getAPI().getPlayerLocationPlot(event.getPlayer()) != null) {
                                        event.getPlayer().sendTitle("§aWelcome to the Server!", "§aYou are in the Plot from §6" + Bukkit.getOfflinePlayer(PlotSystemAPI.getAPI().getOwner(PlotSystemAPI.getAPI().getPlayerLocationPlot(event.getPlayer()))).getName(), 40, 100, 40);
                                    } else {
                                        event.getPlayer().sendTitle("§aWelcome to the Server!", "§cThis Plot isn't Claimed!", 40, 80, 40);
                                    }
                                } else if (event.getPlayer().getLocale().startsWith("de")) {
                                    if (PlotSystemAPI.getAPI().getPlayerLocationPlot(event.getPlayer()) != null) {
                                        event.getPlayer().sendTitle("§aWillkommen auf dem Server!", "§aDu stehst im Plot von §6" + Bukkit.getOfflinePlayer(PlotSystemAPI.getAPI().getOwner(PlotSystemAPI.getAPI().getPlayerLocationPlot(event.getPlayer()))).getName(), 40, 100, 40);
                                    } else {
                                        event.getPlayer().sendTitle("§aWillkomen auf dem Server!", "§cDieses Plot wurde noch nicht beansprucht!", 40, 80, 40);
                                    }
                                }
                            } else {
                                if (event.getPlayer().getLocale().startsWith("en_")) {
                                    event.getPlayer().sendTitle("§aWelcome to the Server!", event.getPlayer().getName(), 20, 80, 20);
                                } else if (event.getPlayer().getLocale().startsWith("de")) {
                                    event.getPlayer().sendTitle("§aWillkommen auf dem Server!", event.getPlayer().getName(), 20, 80, 20);
                                }
                            }
                        }
                    }.runTaskLater(plugin, 120);
                }
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
