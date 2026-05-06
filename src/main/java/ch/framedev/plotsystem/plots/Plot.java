package ch.framedev.plotsystem.plots;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;

/**
 * Class for Plot
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.plotsystem.plots
 * / ClassName Plot
 * / Date: 10.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

@SuppressWarnings("unused")
public class Plot implements Serializable, ConfigurationSerializable {

    private static final long serialVersionUID = 5964991476365408930L;

    // Required for the Plot to be found
    private int id;

    private UUID owner;
    private List<UUID> owners;

    // Required for the Plot
    private Cuboid cuboid;

    private List<Flag> flags;
    private List<UUID> bannedPlayers;
    private List<UUID> members;

    private String home;
    private double price = 0;

    private PlotStatus status;
    private GameMode gameMode;

    public Plot(int id, Location location1, Location location2) {
        this.id = id;
        location1.setY(1);
        location2.setY(250);
        initialize(new Cuboid(location1, location2), null);
    }

    public Plot(Location location1, Location location2, int id, UUID owner) {
        this.id = id;
        location1.setY(1);
        location2.setY(250);
        initialize(new Cuboid(location1, location2), owner);
    }

    public Plot(int id, Chunk chunk) {
        this.id = id;
        Location location1 = chunk.getBlock(0, 1, 0).getLocation();
        Location location2 = chunk.getBlock(15, 250, 15).getLocation();
        initialize(new Cuboid(location1, location2), null);
    }

    public Plot(int id, Cuboid cuboid, UUID owner) {
        this.id = id;
        initialize(cuboid, owner);
    }

    private void initialize(Cuboid cuboid, UUID owner) {
        this.cuboid = cuboid;
        this.flags = new ArrayList<>();
        this.owners = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.home = locationToString(cuboid.getCenter());
        this.status = PlotStatus.NOT_CLAIMED;
        this.gameMode = null;
        setOwner(owner);
    }

    public Plot(Plot plot) {
        this(plot.id, plot.cuboid, plot.owner);
        this.owners = new ArrayList<>(plot.getOwners());
        this.flags = new ArrayList<>(plot.getFlags());
        this.bannedPlayers = new ArrayList<>(plot.getBannedPlayers());
        this.members = new ArrayList<>(plot.getMembers());
        this.home = plot.home;
        this.price = plot.price;
        this.status = plot.status;
        this.gameMode = plot.gameMode;
    }

    @SuppressWarnings("unchecked")
    public Plot(Map<String, Object> map) {
        this.id = asInt(map.get("id"));
        if (map.get("owner") == null) {
            this.owner = null;
        } else
            this.owner = UUID.fromString((String) map.get("owner"));
        List<String> ownerUUIDs = getStringList(map, "owners");
        List<UUID> uuidsOwner = new ArrayList<>();
        for (String ow : ownerUUIDs) {
            uuidsOwner.add(UUID.fromString(ow));
        }
        this.owners = uuidsOwner;
        List<String> memberUUIDs = getStringList(map, "members");
        List<UUID> memberOwner = new ArrayList<>();
        for (String ow : memberUUIDs) {
            memberOwner.add(UUID.fromString(ow));
        }
        this.members = memberOwner;
        Object cuboidValue = map.get("cuboid");
        if (cuboidValue instanceof Cuboid) {
            this.cuboid = (Cuboid) cuboidValue;
        } else if (cuboidValue instanceof Map) {
            this.cuboid = Cuboid.deserialize((Map<String, Object>) cuboidValue);
        } else {
            throw new IllegalArgumentException("Invalid cuboid data for plot " + id);
        }
        List<String> bannedUUID = getStringList(map, "bannedplayers");
        List<UUID> bannedList = new ArrayList<>();
        for (String s : bannedUUID) {
            bannedList.add(UUID.fromString(s));
        }
        this.bannedPlayers = bannedList;
        List<String> flagList = getStringList(map, "flags");
        List<Flag> flags = new ArrayList<>();
        for (String s : flagList) {
            Flag flag = Flag.getFlag(s);
            if (flag != null) flags.add(flag);
        }
        this.flags = flags;
        this.home = (String) map.get("home");
        this.price = map.containsKey("price") ? asDouble(map.get("price")) : 0;
        this.status = parseStatus(map.get("status"));
        this.gameMode = parseGameMode(map.get("gameMode"));
    }

    public static Plot deserialize(Map<String, Object> map) {
        return new Plot(map);
    }

    private static int asInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private static double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private static PlotStatus parseStatus(Object value) {
        if (value == null) return PlotStatus.NOT_CLAIMED;
        try {
            return PlotStatus.valueOf(String.valueOf(value));
        } catch (IllegalArgumentException exception) {
            return PlotStatus.NOT_CLAIMED;
        }
    }

    private static @Nullable GameMode parseGameMode(Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) return null;
        try {
            return GameMode.valueOf(String.valueOf(value).toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> getStringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }

    /**
     * Return the id of the Plot
     *
     * @return return the id of the Plot
     */
    public int getId() {
        return id;
    }

    /**
     * Set new ID for the Plot
     *
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the Owner of the Plot
     *
     * @return return the Owner of the Plot
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Set the new Owner of the Plot
     *
     * @param owner the Selected new Owner
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
        if (this.owners == null) this.owners = new ArrayList<>();
        if (owner != null && !this.owners.contains(owner)) this.owners.add(owner);
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    /**
     * Return the Cuboid for this Plot
     *
     * @return the Cuboid for this Plot
     */
    public Cuboid getCuboid() {
        return cuboid;
    }

    public void addOwner(UUID owner) {
        if (owner == null) return;
        if (this.owners == null) this.owners = new ArrayList<>();
        if (this.owner == null) this.owner = owner;
        if (!owners.contains(owner)) owners.add(owner);
    }

    public void removeOwner(UUID owner) {
        if (this.owners == null) this.owners = new ArrayList<>();
        this.owners.remove(owner);
        this.owner = this.owners.isEmpty() ? null : this.owners.get(0);
    }

    public void setOwners(List<UUID> owners) {
        this.owners = owners == null ? new ArrayList<>() : new ArrayList<>(owners);
        this.owner = this.owners.isEmpty() ? null : this.owners.get(0);
    }

    public List<UUID> getOwners() {
        if (owners == null) owners = new ArrayList<>();
        return owners;
    }

    /**
     * Check if the Location is in the Plot
     *
     * @param location the Location to check
     * @return returns if the Location is in the Plot or not
     */
    public boolean inPlot(Location location) {
        if (this.cuboid == null) throw new NullPointerException("Cuboid is Null");
        return cuboid.contains(location);
    }

    /**
     * Check if the Player is in a Plot
     *
     * @param player the Player to check
     * @return Return if the Player is in a Plot
     */
    public boolean isPlayerInPlot(Player player) {
        Location location = player.getLocation();
        if (this.cuboid != null)
            return cuboid.contains(location);
        return false;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags == null ? new ArrayList<>() : flags;
    }

    public List<Flag> getFlags() {
        if (flags == null) flags = new ArrayList<>();
        return flags;
    }

    /**
     * Add new Flag
     *
     * @param flag the selected Flag
     */
    public void addFlag(Flag flag) {
        if (!this.flags.contains(flag)) this.flags.add(flag);
    }

    public void addFlag(Flag... flags) {
        for (Flag flag : flags) {
            if (!this.flags.contains(flag)) this.flags.add(flag);
        }
    }

    public void removeFlag(Flag flag) {
        flags.remove(flag);
    }

    public void removeFlag(Flag... flags) {
        this.flags.removeAll(Arrays.asList(flags));
    }

    public boolean hasFlag(Flag flag) {
        if (this.flags.isEmpty()) return false;
        return flags.contains(flag);
    }

    public boolean hasOwner() {
        return owner != null || !getOwners().isEmpty();
    }

    public void setBannedPlayers(List<UUID> bannedPlayers) {
        this.bannedPlayers = bannedPlayers == null ? new ArrayList<>() : bannedPlayers;
    }

    public List<UUID> getBannedPlayers() {
        if (bannedPlayers == null) bannedPlayers = new ArrayList<>();
        return bannedPlayers;
    }

    public void addBannedPlayer(UUID uuid) {
        if (!hasFlag(Flag.PLAYERS_BANNED))
            addFlag(Flag.PLAYERS_BANNED);
        if (!bannedPlayers.contains(uuid)) bannedPlayers.add(uuid);
    }

    public void removeBannedPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
        if (bannedPlayers.isEmpty())
            if (hasFlag(Flag.PLAYERS_BANNED))
                removeFlag(Flag.PLAYERS_BANNED);
    }

    public boolean isPlayerBanned(Player player) {
        return bannedPlayers.contains(player.getUniqueId());
    }

    public void setMembers(List<UUID> members) {
        this.members = members == null ? new ArrayList<>() : members;
    }

    public List<UUID> getMembers() {
        if (members == null) members = new ArrayList<>();
        return members;
    }

    public void addMember(UUID uuid) {
        if (!members.contains(uuid)) members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void setPrice(double price) {
        this.price = price;
        if (price > 0) {
            this.addFlag(Flag.SELL);
        } else {
            this.removeFlag(Flag.SELL);
        }
    }

    public double getPrice() {
        return price;
    }

    public @Nullable GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(@Nullable GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setHome(Location location) {
        StringBuilder builder = new StringBuilder();
        if (location.getWorld() == null) return;
        builder.append(location.getWorld().getName())
                .append(";").append(location.getBlockX()).append(";").append(location.getBlockY()).append(";")
                .append(location.getBlockZ()).append(";").append(location.getYaw()).append(";").append(location.getPitch());
        this.home = builder.toString();
    }

    /**
     * Return the Location as String for serialization
     *
     * @param location the selected Location
     * @return return the Location as String
     */
    public String locationToString(Location location) {
        return getString(location);
    }

    private static @Nullable String getString(Location location) {
        StringBuilder builder = new StringBuilder();
        if (location.getWorld() == null) return null;
        builder.append(location.getWorld().getName())
                .append(";").append(location.getBlockX()).append(";").append(location.getBlockY()).append(";")
                .append(location.getBlockZ()).append(";").append(location.getYaw()).append(";").append(location.getPitch());
        return builder.toString();
    }

    /**
     * Return the Location from a String
     *
     * @param text the Location as String
     * @return return the Location from the String
     */
    public Location locationFromString(String text) {
        return getLocation(text);
    }

    private static @NotNull Location getLocation(String text) {
        String[] s = text.split(";");
        if (s.length < 6) throw new IllegalArgumentException("Invalid location string: " + text);
        World world = Bukkit.getWorld(s[0]);
        int x = Integer.parseInt(s[1]);
        int y = Integer.parseInt(s[2]);
        int z = Integer.parseInt(s[3]);
        float yaw = Float.parseFloat(s[4]);
        float pitch = Float.parseFloat(s[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Check if the Player is Owner
     *
     * @param player the Player to check if is Owner
     * @return return if the Player is Owner or not
     */
    public boolean isOwner(OfflinePlayer player) {
        if (!hasOwner()) return false;
        if (this.hasOwner()) {
            return this.getOwners() != null && this.getOwners().contains(player.getUniqueId());
        }
        return false;
    }

    /**
     * Check if Player is a Member from the Plot
     *
     * @param player the Player to check if is a Member
     * @return return if the Player is a Member of the Plot
     */
    public boolean isMember(OfflinePlayer player) {
        if (!hasOwner()) return false;
        if (this.hasOwner()) {
            return this.getMembers() != null && this.getMembers().contains(player.getUniqueId());
        }
        return false;
    }

    /**
     * Return Plot Home can return null
     *
     * @return return Plot Home Location
     */
    public @Nullable Location getHome() {
        if (home == null || home.trim().isEmpty()) {
            return cuboid == null ? null : cuboid.getCenter();
        }
        try {
            return locationFromString(home);
        } catch (RuntimeException ex) {
            Main.getInstance().getLogger().log(Level.WARNING, "Invalid home for plot " + id, ex);
            return cuboid == null ? null : cuboid.getCenter();
        }
    }

    public boolean buyPlot(OfflinePlayer newOwner) {
        if (price != 0) {
            if (Main.getInstance().getVaultManager() != null) {
                if (owner != null) {
                    if (Main.getInstance().getVaultManager().getEconomy().has(newOwner, price)) {
                        if (hasFlag(Flag.SELL)) {
                            Main.getInstance().getVaultManager().getEconomy().withdrawPlayer(newOwner, price);
                            Main.getInstance().getVaultManager().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(owner), price);
                            owner = null;
                            owners.clear();
                            members.clear();
                            setPrice(0);
                            setOwner(newOwner.getUniqueId());
                            removeFlag(getFlags().toArray(new Flag[0]));
                            this.status = PlotStatus.CLAIMED;
                            createPlot();
                            return true;
                        }
                    }
                } else {
                    if (Main.getInstance().getVaultManager().getEconomy().has(newOwner, price)) {
                        if (hasFlag(Flag.SELL)) {
                            Main.getInstance().getVaultManager().getEconomy().withdrawPlayer(newOwner, price);
                            owner = null;
                            owners.clear();
                            members.clear();
                            setPrice(0);
                            setOwner(newOwner.getUniqueId());
                            removeFlag(getFlags().toArray(new Flag[0]));
                            this.status = PlotStatus.CLAIMED;
                            createPlot();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean sellToNone() {
        if (price != 0) {
            if (Main.getInstance().getVaultManager() != null) {
                if (owner != null) {
                    if (hasFlag(Flag.SELL)) {
                        Main.getInstance().getVaultManager().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(owner), price);
                        owner = null;
                        owners.clear();
                        members.clear();
                        setPrice(price);
                        setOwner(null);
                        removeFlag(getFlags().toArray(new Flag[0]));
                        setStatus(PlotStatus.FOR_SALE);
                        createPlot();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isBuyable() {
        return price != 0 && hasFlag(Flag.SELL);
    }

    public void setStatus(PlotStatus status) {
        this.status = status;
    }

    /**
     * Return the PlotStatus from this Plot
     *
     * @return the PlotStatus from this Plot
     */
    public PlotStatus getStatus() {
        return status;
    }

    public void setStatus(int statusId) {
        this.status = PlotStatus.getPlotStatus(statusId);
    }

    @Override
    public String toString() {
        return "Plot{" +
                "id=" + id +
                ", owner=" + owner +
                ", owners=" + owners +
                ", cuboid=" + cuboid +
                ", flags=" + flags +
                ", bannedPlayers=" + bannedPlayers +
                ", members=" + members +
                ", home=" + home +
                ", gameMode=" + gameMode +
                '}';
    }

    public void createPlot() {
        if(Main.getInstance().isDatabaseSupported()) {
            if(Main.getInstance().getIDatabase() == null) {
                Main.getInstance().getLogger().severe("Database not initialized!");
                return;
            }
            if(Main.getInstance().getIDatabase().existsPlot(id)) {
                Main.getInstance().getIDatabase().updatePlot(this);
            } else {
                Main.getInstance().getIDatabase().insertPlot(this);
            }
        } else {
            File fileCfg = new File(Main.getInstance().getDataFolder(), "plots.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(fileCfg);
            List<Plot> plots = new ArrayList<>();
            if (!Main.getInstance().getDefaultFlags().isEmpty())
                for (String s : Main.getInstance().getDefaultFlags()) {
                    if (s != null && this.getFlags().isEmpty())
                        this.addFlag(Flag.getFlag(s));
                }
            if (!cfg.contains("Plots")) {
                plots.add(this);
                cfg.set("Plots", plots);
                try {
                    cfg.save(fileCfg);
                } catch (IOException e) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "Error", e);
                }
            } else {
                //noinspection unchecked
                plots = (List<Plot>) cfg.getList("Plots");
                if (plots == null) return;
                plots.removeIf(plot -> plot.id == this.id);
                plots.add(this);
                cfg.set("Plots", plots);
                try {
                    cfg.save(fileCfg);
                } catch (IOException e) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "Error", e);
                }
            }
        }
    }

    public static Plot getPlot(int id) {
        List<Plot> plots = getPlots();
        if (plots == null) return null;
        for (Plot plot : plots) {
            if (plot.getId() == id) return plot;
        }
        return null;
    }

    public static Plot getPlot(UUID owner) {
        if (owner == null) return null;
        List<Plot> plots = getPlots();
        if (plots == null) return null;
        for (Plot plot : plots)
            if (owner.equals(plot.getOwner()) || plot.getOwners().contains(owner)) return plot;
        return null;
    }

    public static List<Plot> getPlots() {
        if(Main.getInstance().isDatabaseSupported()) {
            if(Main.getInstance().getIDatabase() == null) {
                Main.getInstance().getLogger().severe("Database not initialized!");
                return null;
            }
            List<Plot> plots = Main.getInstance().getIDatabase().getPlots();
            return plots.isEmpty() ? null : plots;
        } else {
            File fileCfg = new File(Main.getInstance().getDataFolder(), "plots.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(fileCfg);
            if (cfg.contains("Plots"))
                //noinspection unchecked
                return (List<Plot>) cfg.getList("Plots");
            return null;
        }
    }

    public static boolean isPlayerInPlotStatic(Player player) {
        List<Plot> plots = getPlots();
        if (plots == null) {
            return false;
        }
        for (Plot plot : plots) {
            if (plot.isPlayerInPlot(player)) return true;
        }
        return false;
    }

    public static boolean isLocationInPlot(Location location) {
        List<Plot> plots = getPlots();
        if (plots == null) return false;
        for (Plot plot : plots) {
            if (plot.inPlot(location)) return true;
        }
        return false;
    }

    public static Plot getPlot(Location location) {
        List<Plot> plots = getPlots();
        if (plots == null) return null;
        for (Plot plot : plots) {
            if (plot.getCuboid().contains(location)) return plot;
        }
        return null;
    }

    /**
     * Return all Player Plots if is Owner or Member
     *
     * @param player the selected Player
     * @return return all Player Plots
     */
    public static List<Plot> getPlayerPlots(OfflinePlayer player) {
        List<Plot> plots = new ArrayList<>();
        List<Plot> allPlots = getPlots();
        if (allPlots == null) return plots;
        for (Plot plot : allPlots) {
            if (plot.isOwner(player) || plot.isMember(player))
                plots.add(plot);
        }
        return plots;
    }

    /**
     * Return the highest id of the created Plots
     *
     * @return return the highest id of Plots
     */
    public static int getHighestId() {
        List<Plot> plots = getPlots();
        if (plots == null) return 0;
        int highest = 0;
        for (Plot plot : plots) {
            if (plot.id > highest) highest = plot.id;
        }
        return highest;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof Plot) {
            Plot p = (Plot) o;
            if (this.id != p.id) return false;
            if (!Objects.equals(this.owner, p.owner)) return false;
            if (!Objects.equals(this.owners, p.owners)) return false;
            if (!Objects.equals(this.cuboid, p.cuboid)) return false;
            if (!Objects.equals(this.members, p.members)) return false;
            if (!Objects.equals(this.home, p.home)) return false;
            if (!Objects.equals(this.bannedPlayers, p.bannedPlayers)) return false;
            if (this.price != p.price) return false;
            if (this.gameMode != p.gameMode) return false;
            return Objects.equals(this.flags, p.flags);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, owners, cuboid, flags, bannedPlayers, members, home, price, gameMode);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        if (this.owner == null) {
            map.put("owner", null);
        } else
            map.put("owner", owner.toString());

        List<String> ownerUUID = new ArrayList<>();
        for (UUID uuid : owners) {
            ownerUUID.add(uuid.toString());
        }
        map.put("owners", ownerUUID);
        List<String> memberUUID = new ArrayList<>();
        for (UUID uuid : members) {
            memberUUID.add(uuid.toString());
        }
        map.put("members", memberUUID);
        map.put("cuboid", cuboid.serialize());
        List<String> bannedPlayersUUID = new ArrayList<>();
        for (UUID uuid : bannedPlayers) {
            bannedPlayersUUID.add(uuid.toString());
        }
        map.put("bannedplayers", bannedPlayersUUID);
        List<String> flagList = new ArrayList<>();
        for (Flag flag : flags) {
            flagList.add(flag.getType());
        }
        map.put("flags", flagList);
        map.put("home", home);
        map.put("price", price);
        map.put("status", status.name());
        map.put("gameMode", gameMode == null ? null : gameMode.name());
        return map;
    }

    /**
     * Clone this Plot
     *
     * @return return the Cloned Plot
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Plot clone() {
        return new Plot(this);
    }
}
