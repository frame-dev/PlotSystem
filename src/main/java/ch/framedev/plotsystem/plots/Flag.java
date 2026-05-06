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
    MONSTER_SPAWNER("MONSTER_SPAWNER", "Enable Monster Spawner"),

    /**
     * Flag for allowing fire to ignite, burn, and spread in the Plot
     */
    FIRE_SPREAD("FIRE_SPREAD", "Allow fire spread and burning in Plot"),

    /**
     * Flag for allowing water and lava to flow in the Plot
     */
    LIQUID_FLOW("LIQUID_FLOW", "Allow water and lava flow in Plot"),

    /**
     * Flag for allowing visitors to drop items in the Plot
     */
    ITEM_DROP("ITEM_DROP", "Allow visitors to drop items in Plot"),

    /**
     * Flag for allowing visitors to pick up items in the Plot
     */
    ITEM_PICKUP("ITEM_PICKUP", "Allow visitors to pick up items in Plot"),

    /**
     * Flag for allowing hunger loss in the Plot
     */
    HUNGER("HUNGER", "Allow hunger loss in Plot"),

    /**
     * Flag for allowing visitors to teleport with ender pearls in the Plot
     */
    ENDER_PEARL("ENDER_PEARL", "Allow ender pearl teleporting in Plot"),

    /**
     * Flag for blocking visitor commands while they are inside the Plot
     */
    DENY_COMMANDS("DENY_COMMANDS", "Deny visitor commands in Plot"),

    /**
     * Flag for allowing visitors to use doors, trapdoors, and fence gates in the Plot
     */
    DOORS("DOORS", "Allow visitors to use doors in Plot"),

    /**
     * Flag for allowing visitors to use buttons, levers, and pressure plates in the Plot
     */
    SWITCHES("SWITCHES", "Allow visitors to use switches in Plot"),

    /**
     * Flag for allowing visitors to use vehicles in the Plot
     */
    VEHICLES("VEHICLES", "Allow visitors to use vehicles in Plot"),

    /**
     * Flag for allowing visitors to interact with armor stands in the Plot
     */
    ARMOR_STANDS("ARMOR_STANDS", "Allow visitors to use armor stands in Plot"),

    /**
     * Flag for allowing visitors to interact with item frames in the Plot
     */
    ITEM_FRAMES("ITEM_FRAMES", "Allow visitors to use item frames in Plot"),

    /**
     * Flag for allowing splash and lingering potion effects in the Plot
     */
    POTIONS("POTIONS", "Allow potion effects in Plot"),

    /**
     * Flag for allowing visitors to launch projectiles in the Plot
     */
    PROJECTILES("PROJECTILES", "Allow visitors to launch projectiles in Plot"),

    /**
     * Flag for allowing visitors to fly in the Plot
     */
    FLY("FLY", "Allow visitor flight in Plot"),

    /**
     * Flag for allowing farmland trampling in the Plot
     */
    CROP_TRAMPLE("CROP_TRAMPLE", "Allow farmland trampling in Plot");

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
