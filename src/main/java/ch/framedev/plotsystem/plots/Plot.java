package ch.framedev.plotsystem.plots;

import ch.framedev.plotsystem.main.Main;
import ch.framedev.plotsystem.utils.Cuboid;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Class for Plot
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.plotsystem.plots
 * / ClassName Plot
 * / Date: 10.09.21
 * / Project: PlotSystem
 * / Copyrighted by FrameDev
 */

public class Plot implements Serializable, ConfigurationSerializable {

    private static final long serialVersionUID = 5964991476365408930L;

    private int id;
    private UUID owner;
    private List<UUID> owners;
    private Cuboid cuboid;
    private List<Flag> flags;
    private List<UUID> bannedPlayers;
    private List<UUID> members;
    private String home;
    private double price = 0;
    private PlotStatus status;

    public Plot(int id, Location location1, Location location2) {
        this.id = id;
        location1.setY(1);
        location2.setY(250);
        this.cuboid = new Cuboid(location1, location2);
        this.flags = new ArrayList<>();
        this.owners = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.home = locationToString(cuboid.getCenter());
        this.status = PlotStatus.NOT_CLAIMED;
    }

    public Plot(Location location1, Location location2, int id, UUID owner) {
        this.id = id;
        this.owner = owner;
        this.owners = new ArrayList<>();
        owners.add(owner);
        this.flags = new ArrayList<>();
        location1.setY(1);
        location2.setY(250);
        this.cuboid = new Cuboid(location1, location2);
        this.bannedPlayers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.home = locationToString(cuboid.getCenter());
        this.status = PlotStatus.NOT_CLAIMED;
    }

    public Plot(int id, Chunk chunk) {
        this.id = id;
        Location location1 = chunk.getBlock(0, 1, 0).getLocation();
        Location location2 = chunk.getBlock(15, 250, 15).getLocation();
        this.cuboid = new Cuboid(location1, location2);
        this.flags = new ArrayList<>();
        this.owners = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.home = locationToString(cuboid.getCenter());
        this.status = PlotStatus.NOT_CLAIMED;
    }

    public Plot(int id, Cuboid cuboid, UUID owner) {
        this.id = id;
        this.owner = owner;
        this.owners = new ArrayList<>();
        owners.add(owner);
        this.flags = new ArrayList<>();
        this.cuboid = cuboid;
        this.bannedPlayers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.home = locationToString(cuboid.getCenter());
        this.status = PlotStatus.NOT_CLAIMED;
    }

    public Plot(Plot plot) {
        this(plot.id, plot.cuboid, plot.owner);
        this.owners = plot.owners;
        owners.add(owner);
        this.flags = plot.flags;
        this.bannedPlayers = plot.bannedPlayers;
        this.members = plot.bannedPlayers;
        this.home = plot.home;
        this.price = plot.price;
        this.status = plot.status;
    }

    @SuppressWarnings("unchecked")
    public Plot(Map<String, Object> map) {
        this.id = (int) map.get("id");
        if (map.get("owner") == null) {
            this.owner = null;
        } else
            this.owner = UUID.fromString((String) map.get("owner"));
        List<String> ownerUUIDs = (List<String>) map.get("owners");
        List<UUID> uuidsOwner = new ArrayList<>();
        for (String ow : ownerUUIDs) {
            uuidsOwner.add(UUID.fromString(ow));
        }
        this.owners = uuidsOwner;
        List<String> memberUUIDs = (List<String>) map.get("members");
        List<UUID> memberOwner = new ArrayList<>();
        for (String ow : memberUUIDs) {
            memberOwner.add(UUID.fromString(ow));
        }
        this.members = memberOwner;
        this.cuboid = (Cuboid) map.get("cuboid");
        List<String> bannedUUID = (List<String>) map.get("bannedplayers");
        List<UUID> bannedList = new ArrayList<>();
        for (String s : bannedUUID) {
            bannedList.add(UUID.fromString(s));
        }
        this.bannedPlayers = bannedList;
        List<String> flagList = (List<String>) map.get("flags");
        List<Flag> flags = new ArrayList<>();
        for (String s : flagList) {
            flags.add(Flag.getFlag(s));
        }
        this.flags = flags;
        this.home = (String) map.get("home");
        this.price = (double) map.get("price");
        this.status = PlotStatus.valueOf((String) map.get("status"));
    }

    public static Plot deserialize(Map<String, Object> map) {
        return new Plot(map);
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
        this.owners.add(owner);
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
        if (this.owner == null) this.owner = owner;
        if (!owners.contains(owner)) owners.add(owner);
    }

    public void removeOwner(UUID owner) {
        if (this.owners.size() == 1) this.owner = null;
        this.owners.remove(owner);
    }

    public void setOwners(List<UUID> owners) {
        if (owners.size() >= 1) this.owner = owners.get(0);
        this.owners = owners;
    }

    public List<UUID> getOwners() {
        return owners;
    }

    public boolean inPlot(Location location) {
        if (this.cuboid == null) throw new NullPointerException("Cuboid is Null");
        return cuboid.contains(location);
    }

    public boolean isPlayerInPlot(Player player) {
        Location location = player.getLocation();
        if (this.cuboid != null)
            return cuboid.contains(location);
        return false;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    public List<Flag> getFlags() {
        return flags;
    }

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
        return owner != null || !owners.isEmpty();
    }

    public void setBannedPlayers(List<UUID> bannedPlayers) {
        this.bannedPlayers = bannedPlayers;
    }

    public List<UUID> getBannedPlayers() {
        return bannedPlayers;
    }

    public void addBannedPlayer(UUID uuid) {
        if(!hasFlag(Flag.PLAYERS_BANNED))
            addFlag(Flag.PLAYERS_BANNED);
        if (!bannedPlayers.contains(uuid)) bannedPlayers.add(uuid);
    }

    public void removeBannedPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
        if(bannedPlayers.size() == 0)
            if(hasFlag(Flag.PLAYERS_BANNED))
                removeFlag(Flag.PLAYERS_BANNED);
    }

    public boolean isPlayerBanned(Player player) {
        return bannedPlayers.contains(player.getUniqueId());
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public List<UUID> getMembers() {
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
        addFlag(Flag.SELL);
    }

    public double getPrice() {
        return price;
    }

    public void setHome(Location location) {
        StringBuilder builder = new StringBuilder();
        if (location.getWorld() == null) return;
        builder.append(location.getWorld().getName())
                .append(";").append(location.getBlockX()).append(";").append(location.getBlockY()).append(";")
                .append(location.getBlockZ()).append(";").append(location.getYaw()).append(";").append(location.getPitch());
        this.home = builder.toString();
    }

    public String locationToString(Location location) {
        StringBuilder builder = new StringBuilder();
        if (location.getWorld() == null) return null;
        builder.append(location.getWorld().getName())
                .append(";").append(location.getBlockX()).append(";").append(location.getBlockY()).append(";")
                .append(location.getBlockZ()).append(";").append(location.getYaw()).append(";").append(location.getPitch());
        return builder.toString();
    }

    public Location locationFromString(String text) {
        String[] s = text.split(";");
        World world = Bukkit.getWorld(s[0]);
        int x = Integer.parseInt(s[1]);
        int y = Integer.parseInt(s[2]);
        int z = Integer.parseInt(s[3]);
        float yaw = Float.parseFloat(s[4]);
        float pitch = Float.parseFloat(s[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean isOwner(OfflinePlayer player) {
        if (!hasOwner()) return false;
        if (this.hasOwner()) {
            return this.getOwners() != null && this.getOwners().contains(player.getUniqueId());
        }
        return false;
    }

    public boolean isMember(OfflinePlayer player) {
        if (!hasOwner()) return false;
        if (this.hasOwner()) {
            return this.getMembers() != null && this.getMembers().contains(player.getUniqueId());
        }
        return false;
    }

    /**
     * Return Plot Home
     *
     * @return return Plot Home Location
     */
    public Location getHome() {
        if (home == null) {
            if (cuboid.getCenter().getWorld() == null) return null;
            return null;
        } else {
            return locationFromString(home);
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
                            createPlot();
                            return true;
                        }
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
                '}';
    }

    @SuppressWarnings("unchecked")
    public void createPlot() {
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
                e.printStackTrace();
            }
        } else {
            plots = (List<Plot>) cfg.getList("Plots");
            if (plots == null) return;
            plots.removeIf(plot -> plot.id == this.id);
            plots.add(this);
            cfg.set("Plots", plots);
            try {
                cfg.save(fileCfg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Plot getPlot(int id) {
        if (getPlots() == null) return null;
        for (Plot plot : getPlots()) {
            if (plot.getId() == id) return plot;
        }
        return null;
    }

    public static Plot getPlot(UUID owner) {
        if (getPlots() == null) return null;
        for (Plot plot : getPlots())
            if (plot.getOwner() == owner || plot.getOwners() != null && plot.getOwners().contains(owner)) return plot;
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Plot> getPlots() {
        File fileCfg = new File(Main.getInstance().getDataFolder(), "plots.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(fileCfg);
        if (cfg.contains("Plots"))
            return (List<Plot>) cfg.getList("Plots");
        return null;
    }

    public static boolean isPlayerInPlotStatic(Player player) {
        if (getPlots() == null) {
            return false;
        }
        List<Boolean> booleans = new ArrayList<>();
        for (Plot plot : getPlots()) {
            booleans.add(plot.isPlayerInPlot(player));
        }
        return booleans.contains(true);
    }

    public static boolean isLocationInPlot(Location location) {
        List<Boolean> booleans = new ArrayList<>();
        if (getPlots() != null) {
            for (Plot plot : getPlots()) {
                booleans.add(plot.inPlot(location));
            }
        }
        return booleans.contains(true);
    }

    public static Plot getPlot(Location location) {
        if (getPlots() == null) return null;
        for (Plot plot : getPlots()) {
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
        if (getPlots() == null) return plots;
        for (Plot plot : getPlots()) {
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
        if (getPlots() == null) return 0;
        int highest = 0;
        for (Plot plot : getPlots()) {
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
            if (this.owner != p.owner) return false;
            if (this.owners != p.owners) return false;
            if (this.cuboid != p.cuboid) return false;
            if (this.members != p.members) return false;
            if (!Objects.equals(this.home, p.home)) return false;
            if (this.bannedPlayers != p.bannedPlayers) return false;
            if (this.price != p.price) return false;
            return this.flags == p.flags;
        } else {
            return false;
        }
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
        map.put("cuboid", cuboid);
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
        return map;
    }

    @Override
    public Plot clone() {
        return new Plot(this);
    }
}
