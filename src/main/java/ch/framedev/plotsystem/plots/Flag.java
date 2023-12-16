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

    /**
     * Flag for PVP on the Plot
     */
    PVP("PVP", "PVP in Plot"),

    /**
     * Flag for enable or Disable Explosions in the Plot
     */
    EXPLOSIONS("EXPLOSIONS", "Explosions in Plot"),

    /**
     * Activate or deactivate Redstone in the Plot
     */
    REDSTONE("REDSTONE", "Redstone in Plot"),

    /**
     * Interact with Chest, Crafting Tables or other Blocks where you can use Right Click
     */
    INTERACT("INTERACT", "Interact with Entities in Plot"),
    DENY_CHAT("DENY_CHAT", "Deny Chat in Plot"),
    MONSTERS("MONSTERS", "Disable Monsters in Plot"),
    ANIMALS("ANIMALS", "Disable Animals in Plot"),
    FALL_DAMAGE("FALL_DAMAGE", "Enable Fall Damage in Plot"),
    MOB_DAMAGE("MOB_DAMAGE", "Enable Mob Damage in Plot"),
    SELL("SELL", "Flag to Sell"),
    FARM_PROTECT("FARM_PROTECT", "Destroy Farms"),
    PLAYERS_BANNED("PLAYERS_BANNED", "Player Banned"),
    MONSTER_SPAWNER("MONSTER_SPAWNER", "Enable Monster Spawner");

    private final String type;
    private final String description;

    Flag(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static Flag getFlag(String type) {
        for (Flag flag : Flag.values()) {
            if (Objects.equals(flag.getType(), type)) return flag;
        }
        return null;
    }
}
