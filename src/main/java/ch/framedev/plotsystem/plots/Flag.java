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
     * Flag for enabling or disabling PVP in the Plot
     */
    PVP("PVP", "PVP in Plot"),

    /**
     * Flag for enabling or disabling explosions in the Plot
     */
    EXPLOSIONS("EXPLOSIONS", "Explosions in Plot"),

    /**
     * Flag for enabling or disabling Redstone mechanics in the Plot
     */
    REDSTONE("REDSTONE", "Redstone in Plot"),

    /**
     * Flag for allowing interactions with chests, crafting tables,
     * and other interactable blocks using right-click
     */
    INTERACT("INTERACT", "Interact with Entities in Plot"),

    /**
     * Flag for restricting players from using chat in the Plot
     */
    DENY_CHAT("DENY_CHAT", "Deny Chat in Plot"),

    /**
     * Flag for disabling monster spawns in the Plot
     */
    MONSTERS("MONSTERS", "Disable Monsters in Plot"),

    /**
     * Flag for disabling animal spawns in the Plot
     */
    ANIMALS("ANIMALS", "Disable Animals in Plot"),

    /**
     * Flag for enabling fall damage within the Plot
     */
    FALL_DAMAGE("FALL_DAMAGE", "Enable Fall Damage in Plot"),

    /**
     * Flag for allowing mobs to deal damage to players in the Plot
     */
    MOB_DAMAGE("MOB_DAMAGE", "Enable Mob Damage in Plot"),

    /**
     * Flag for marking the Plot as available for selling
     */
    SELL("SELL", "Flag to Sell"),

    /**
     * Flag for protecting farmland from destruction in the Plot
     */
    FARM_PROTECT("FARM_PROTECT", "Destroy Farms"),

    /**
     * Flag for banning specific players from entering the Plot
     */
    PLAYERS_BANNED("PLAYERS_BANNED", "Player Banned"),

    /**
     * Flag for enabling or disabling monster spawners in the Plot
     */
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
