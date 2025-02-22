package ch.framedev.plotsystem.utils;

import ch.framedev.plotsystem.main.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

/**
 * / This Plugin was Created by FrameDev
 * / Package : ch.framedev.plotsystem.utils
 * / ClassName VaultManager
 * / Date: 09.12.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class VaultManager {

    private Economy economy;

    public VaultManager() {
        if (setupEconomy())
            Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§aVault Enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return economy;
    }
}
