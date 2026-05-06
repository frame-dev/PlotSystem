# PlotSystem

PlotSystem is a Spigot plot-claiming plugin for Minecraft servers. It lets players claim protected areas, manage members and owners, set homes, sell plots through Vault, control plot behavior with flags, and expose plot data through a Java API for addon plugins.

Created by FrameDev  
[Website](https://framedev.ch)

## Features

- Claim plots from two selected positions.
- Protect plots from unauthorized block breaking, block placing, interaction, PVP, explosions, redstone, fire, liquid flow, item access, and more.
- Add and remove plot members.
- Add, remove, and transfer plot owners.
- Ban players from entering plots.
- Set plot homes and teleport to owned or visited plot homes.
- Sell and buy plots with Vault economy support.
- List your plots or another player’s plots.
- Show plot statistics for storage, claimed plots, available plots, buyable plots, and claimed blocks.
- Set an admin-controlled plot gamemode that automatically applies on enter and restores the previous gamemode on exit.
- Configure default flags for new plots.
- Store plots in `plots.yml`, MySQL, SQLite, or MongoDB.
- Use Bukkit events and the `PlotSystemAPI` from addon plugins.

## Requirements

- Spigot-compatible server.
- Java 11 or newer.
- Vault is optional, but required for plot buying and selling.

The plugin is built against Spigot API `1.17.1` and declares Bukkit `api-version: 1.13`.

## Installation

1. Build or download the plugin jar.
2. Place the jar in your server `plugins` folder.
3. Start the server once to generate `plugins/PlotSystem/config.yml`.
4. Edit the config if needed.
5. Restart the server.

## Basic Usage

Players create plots by selecting two corners and claiming the area.

- `/pmarker` gives the marker item.
- `/pos1` sets the first corner.
- `/pos2` sets the second corner.
- `/claim` claims the selected area.
- `/plot info` shows information about the plot you are standing in.
- `/plot home` teleports to your first plot home.

Admins can create plots directly with `/create`, `/createplot`, and the admin create permissions.

## Configuration

Default config:

```yaml
Prefix: '&6[&aPlot&bSystem&6] &c>> &7'

MaxLower: 0
Title: false

DefaultFlags:
  - REDSTONE
  - FALL_DAMAGE
  - MOB_DAMAGE

PlotWorld: world

MaxBlockClaim:
  Limited: true
  MaxPlots: 1
  Amount: 12500000
```

`DefaultFlags` are applied to new plots when they are saved. Add any valid flag name to enable it by default.

`MaxBlockClaim.Limited` enables claim limits.

`MaxBlockClaim.MaxPlots` is the maximum number of plots a player may claim while limited claiming is enabled. The default is `1`.

`MaxBlockClaim.Amount` is the default block allowance. This is a block count, not a plot count.

## Storage

PlotSystem can store plots in `plots.yml` or in a database. File storage is used when `database.use` is `false`.

Supported database backends:

- MySQL
- SQLite
- MongoDB

Example database config:

```yaml
database:
  use: true
  tableName: "plotsystem_data"
  preferred: "mysql" # mysql, sqlite, or mongodb

  mysql:
    host: "localhost"
    port: 3306
    database: "database"
    user: "root"
    password: "password"

  sqlite:
    database: "plot.db"
    path: "plugins/PlotSystem"

  mongodb:
    host: "localhost"
    port: 27017
    database: "database"
    username: "username"
    password: "password"
```

Database drivers are bundled into the plugin jar. If the preferred backend cannot be initialized, PlotSystem falls back to the first available backend in this order: MySQL, SQLite, MongoDB.

## Commands

Main commands:

- `/plot help` shows plot help.
- `/plot claim` claims the plot you are standing in.
- `/plot info` shows current plot information.
- `/plot list` lists your plots.
- `/plot list <player>` lists another player’s plots.
- `/plot stats` shows storage and plot totals.
- `/plot flags` lists all available flags and descriptions.
- `/plot home` teleports to your first plot home.
- `/plot home <id>` teleports to one of your plot homes by id.
- `/plot visit <player> <id>` visits another player’s plot home.
- `/plot sethome` sets the home of the plot you are standing in.
- `/plot addmember <player>` adds a member to the current plot.
- `/plot removemember <player>` removes a member from the current plot.
- `/plot setowner <player>` transfers the current plot to another owner.
- `/plot removeowner <player>` removes an owner from the current plot.
- `/plot banplayer add <player>` bans a player from the current plot.
- `/plot banplayer remove <player>` unbans a player from the current plot.
- `/plot flag add <flag>` adds a flag to the current plot.
- `/plot flag remove <flag>` removes a flag from the current plot.
- `/plot flag clear` removes all flags from the current plot.
- `/plot flag defaults` applies the configured default flags to the current plot.
- `/plot setprice <price>` puts the current plot up for sale.
- `/plot sellcancel` removes the sale price from the current plot.
- `/plot buy` buys the current plot if it is for sale.
- `/plot unclaim` releases the current plot.
- `/plot delete` deletes the current plot for admins.
- `/plot gamemode <survival|creative|adventure|spectator>` lets admins switch players into that mode while they are inside the plot.
- `/plot gamemode clear` disables automatic gamemode switching for the current plot.

Aliases for `/plot`:

- `/p`
- `/plots`

Selection and creation commands:

- `/pmarker`
- `/pos1`
- `/pos2`
- `/claim`
- `/create`
- `/createplot`

Plugin info command:

- `/plotsystem`

## Plot Flags

Flags change what is allowed inside a plot. They can be managed with `/plot flag add <flag>`, `/plot flag remove <flag>`, `/plot flag clear`, and `/plot flag defaults`.

Available flags:

- `PVP`: allows player versus player combat.
- `EXPLOSIONS`: allows TNT and explosions.
- `REDSTONE`: allows redstone mechanics.
- `INTERACT`: allows visitors to interact with blocks/entities.
- `DENY_CHAT`: blocks visitor chat inside the plot.
- `MONSTERS`: allows monsters.
- `ANIMALS`: allows animals.
- `FALL_DAMAGE`: allows fall damage.
- `MOB_DAMAGE`: allows mob damage.
- `SELL`: marks a plot as buyable.
- `FARM_PROTECT`: protects farmland from fading.
- `PLAYERS_BANNED`: marks that the plot has banned players.
- `MONSTER_SPAWNER`: allows monster spawner placement/breaking.
- `FIRE_SPREAD`: allows fire ignition, burning, and spread.
- `LIQUID_FLOW`: allows water and lava flow.
- `ITEM_DROP`: allows visitors to drop items.
- `ITEM_PICKUP`: allows visitors to pick up items.
- `HUNGER`: allows hunger loss.
- `ENDER_PEARL`: allows visitors to use ender pearls.
- `DENY_COMMANDS`: blocks visitor commands while inside the plot, except plot commands.
- `DOORS`: allows visitors to use doors, trapdoors, and fence gates.
- `SWITCHES`: allows visitors to use buttons, levers, and pressure plates.
- `VEHICLES`: allows visitors to enter vehicles.
- `ARMOR_STANDS`: allows visitors to interact with armor stands.
- `ITEM_FRAMES`: allows visitors to interact with item frames.
- `POTIONS`: allows splash and lingering potion effects.
- `PROJECTILES`: allows visitors to launch projectiles.
- `FLY`: allows visitor flight.
- `CROP_TRAMPLE`: allows farmland trampling.

## Permissions

The wildcard permission is `plotsystem.*`.

Admin permissions:

- `plotsystem.admin.break`
- `plotsystem.admin.place`
- `plotsystem.admin.pvp`
- `plotsystem.admin.interact`
- `plotsystem.admin.ignore`
- `plotsystem.admin.ignorechat`
- `plotsystem.admin.ignorecommands`
- `plotsystem.admin.itemdrop`
- `plotsystem.admin.itempickup`
- `plotsystem.admin.hunger`
- `plotsystem.admin.enderpearl`
- `plotsystem.admin.doors`
- `plotsystem.admin.switches`
- `plotsystem.admin.vehicles`
- `plotsystem.admin.armorstands`
- `plotsystem.admin.itemframes`
- `plotsystem.admin.projectiles`
- `plotsystem.admin.fly`
- `plotsystem.admin.gamemode`
- `plotsystem.admin.create`
- `plotsystem.admin.createplot`
- `plotsystem.admin.delete`

Plot permissions:

- `plotsystem.plot.marker`
- `plotsystem.plot.claim`
- `plotsystem.plot.unclaim`
- `plotsystem.plot.buy`
- `plotsystem.plot.home`
- `plotsystem.plot.sethome`
- `plotsystem.plot.setprice`
- `plotsystem.plot.list`
- `plotsystem.plot.list.others`
- `plotsystem.plot.stats`
- `plotsystem.plot.visit`
- `plotsystem.plot.ban`
- `plotsystem.plot.flag`
- `plotsystem.plot.removeowner`
- `plotsystem.plot.setowner`
- `plotsystem.plot.removemember`
- `plotsystem.plot.addmember`
- `plotsystem.plot.info`

Other permissions:

- `plotsystem.flags.info`
- `plotsystem.help`

All permissions default to operators in `plugin.yml`.

## API

Other plugins can access PlotSystem through `PlotSystemAPI.getAPI()`. The API is designed for addon plugins that need to read plot data, check permissions, create or update plots, enforce claim limits, or react to PlotSystem events.

Add PlotSystem as a plugin dependency or soft dependency in your addon:

```yaml
depend:
  - PlotSystem
```

or:

```yaml
softdepend:
  - PlotSystem
```

Basic usage:

``` java
PlotSystemAPI api = PlotSystemAPI.getAPI();

Plot plot = api.getPlotAt(player);
if (plot != null && api.isTrusted(plot, player)) {
    api.addFlag(plot, Flag.INTERACT);
}
```

### API Availability

- `PlotSystemAPI.getAPI()` returns the active API instance after PlotSystem has enabled.
- `getInstance()` is an alias for `getAPI()`.
- `isReady()` returns whether the plugin singleton is available.
- `getPlugin()` returns the PlotSystem `Main` plugin instance.

### Plot Lookups

- `getPlots()` returns all plots as an immutable list.
- `getPlotById(id)` returns a plot by numeric id or `null`.
- `findPlotById(id)` returns an `Optional<Plot>`.
- `getPlotAt(location)`, `getPlotAt(player)`, and `getPlotAt(block)` return the plot at a position.
- `findPlotAt(location)` returns an `Optional<Plot>`.
- `getPlayerLocationPlot(player)` is a compatibility alias for `getPlotAt(player)`.
- `getPlayerPlots(player)` returns plots where the player is an owner or member.
- `getOwnedPlots(player)` returns plots owned by the player.
- `getMemberPlots(player)` returns plots where the player is a member.
- `getPlayerPlotIds(player)` returns the ids of a player’s plots.
- `getPlotsByStatus(status)` returns plots with a specific `PlotStatus`.
- `getPlotsWithFlag(flag)` returns plots containing a flag.
- `getPlotsWithoutFlag(flag)` returns plots that do not contain a flag.
- `getBuyablePlots()` returns plots that are currently for sale.
- `getPlotsInWorld(world)` and `getPlotsInWorld(worldName)` return plots in a world.

Example:

```java
List<Plot> forSale = api.getBuyablePlots();
List<Plot> creativePlots = api.getPlotsByStatus(PlotStatus.CLAIMED);
Optional<Plot> maybePlot = api.findPlotAt(player.getLocation());
```

### Plot Details

- `getOwner(plot)` returns the primary owner UUID.
- `getOwnerPlayer(plot)` returns the primary owner as an `OfflinePlayer`.
- `getOwners(plot)` returns owner UUIDs.
- `getOwnerPlayers(plot)` returns owner `OfflinePlayer` objects.
- `getMembers(plot)` returns member UUIDs.
- `getMemberPlayers(plot)` returns member `OfflinePlayer` objects.
- `getBannedPlayers(plot)` returns banned `OfflinePlayer` objects.
- `getPlotHome(plot)` returns the plot home.
- `getPlotStatus(plot)` returns the plot status.
- `getPlotStatusId(plot)` returns the status id, or `-1` if unavailable.
- `getGameMode(plot)` returns the plot gamemode, or `null` when no automatic gamemode is set.
- `getPlotWorld(plot)` returns the loaded world for the plot.
- `isPlotLoaded(plot)` checks whether the plot world is currently loaded.
- `getPlotSize(plot)` returns the plot volume in blocks.

### Flags

- `getAvailableFlags()` returns all supported `Flag` values.
- `getAvailableFlagNames()` returns all supported flag names.
- `getFlags(plot)` returns a plot’s flags as an array.
- `getFlag(name)` parses a flag by name.
- `hasFlag(plot, flag)` checks if a plot has a flag.
- `addFlag(plot, flags...)` adds one or more flags and saves the plot.
- `removeFlag(plot, flags...)` removes one or more flags and saves the plot.
- `setFlag(plot, flag, enabled)` toggles a flag.
- `clearFlags(plot)` removes all flags.
- `applyDefaultFlags(plot)` applies the configured default flags.

Example:

``` java
Flag flag = api.getFlag("PVP");
if (flag != null && !api.hasFlag(plot, flag)) {
    api.addFlag(plot, flag);
}
```

### Access Checks

- `isInPlot(location)`, `isInPlot(block)`, and `isInPlot(player)` check whether something is inside a plot.
- `isClaimed(plot)` checks whether a plot has an owner.
- `isBuyable(plot)` checks whether a plot is for sale.
- `isOwner(plot, player)` checks owner access.
- `isMember(plot, player)` checks member access.
- `isTrusted(plot, player)` checks owner or member access.
- `isBanned(plot, player)` checks whether a player is banned.
- `canEnter(player, plot)` checks ban/admin-ignore access.
- `canBreak(player, location)` checks build protection for breaking.
- `canPlace(player, location)` checks build protection for placing.
- `canInteract(player, location)` checks interaction protection.

### Claim Limits

- `isLimitedClaim()` checks whether limited claiming is enabled.
- `getLimitedBlocksDefault()` returns `MaxBlockClaim.Amount`.
- `getMaxClaimedPlotsDefault()` returns `MaxBlockClaim.MaxPlots`.
- `getRemainingClaimBlocks(player)` returns a player’s remaining claim blocks.
- `setRemainingClaimBlocks(player, blocks)` sets and saves a player’s remaining claim blocks.
- `addRemainingClaimBlocks(player, blocks)` adds or removes claim blocks and saves the value.
- `getOwnedPlotCount(player)` returns the number of plots owned by a player.
- `getMemberPlotCount(player)` returns the number of plots where a player is a member.
- `getRemainingPlotClaims(player)` returns remaining plot claims, or `Integer.MAX_VALUE` if limited claiming is disabled.
- `canClaimMorePlots(player)` checks the plot-count limit.
- `canClaimBlocks(player, blocks)` checks the block allowance.
- `canClaim(player, blocks)` checks both plot-count and block allowance limits.

Example:

``` java
long neededBlocks = 5000L;
if (api.canClaim(player, neededBlocks)) {
    api.addRemainingClaimBlocks(player, -neededBlocks);
}
```

### Plot Mutations

Most mutation helpers call Bukkit events and save the plot automatically.

- `createPlot(firstCorner, secondCorner)` creates an unclaimed plot.
- `createPlot(firstCorner, secondCorner, owner)` creates a plot and optionally claims it.
- `claimPlot(plot, owner)` claims an unclaimed plot.
- `transferPlot(plot, owner)` changes the primary owner.
- `unclaimPlot(plot)` clears owner, members, bans, price, and sale state.
- `savePlot(plot)` saves a plot.
- `deletePlot(plot)` deletes a plot.
- `addMember(plot, player)` and `removeMember(plot, player)` manage members.
- `addOwner(plot, player)` and `removeOwner(plot, player)` manage owners.
- `banPlayer(plot, player)` and `unbanPlayer(plot, player)` manage plot bans.
- `setHome(plot, location)` changes the plot home.
- `setPrice(plot, price)` sets the price and updates the `SELL` flag.
- `setForSale(plot, price)` sets price and status to `FOR_SALE`.
- `cancelSale(plot)` clears the sale price and restores claimed/unclaimed status.
- `setGameMode(plot, gameMode)` sets or clears automatic plot gamemode.
- `setPlotStatus(plot, status)` updates plot status.

Example:

``` java
Plot plot = api.createPlot(pos1, pos2, player);
if (plot != null) {
    api.setHome(plot, player.getLocation());
    api.setGameMode(plot, GameMode.ADVENTURE);
}
```

### Statistics And Storage

- `getPlotCount()` returns total plots.
- `getClaimedPlotCount()` returns claimed plots.
- `getAvailablePlotCount()` returns unclaimed plots.
- `getTotalPlotBlocks()` returns total plotted blocks.
- `getTotalClaimedBlocks()` returns claimed plotted blocks.
- `getHighestPlotId()` returns the current highest plot id.
- `isDatabaseEnabled()` checks whether database storage is active.
- `getStorageBackend()` returns `file`, `mysql`, `sqlite`, `mongodb`, or `database`.

### Quick Method Index

- `getPlots()`, `getPlotById(id)`, `findPlotById(id)`, `getPlotAt(location/player/block)`
- `getPlayerPlots(player)`, `getOwnedPlots(player)`, `getMemberPlots(player)`, `getPlayerPlotIds(player)`
- `getPlotsByStatus(status)`, `getPlotsWithFlag(flag)`, `getPlotsWithoutFlag(flag)`, `getBuyablePlots()`, `getPlotsInWorld(world/name)`
- `getOwnerPlayer(plot)`, `getOwnerPlayers(plot)`, `getMemberPlayers(plot)`, `getBannedPlayers(plot)`
- `isOwner(plot, player)`, `isMember(plot, player)`, `isTrusted(plot, player)`, `isBanned(plot, player)`, `canEnter(player, plot)`
- `canBreak(player, location)`, `canPlace(player, location)`, `canInteract(player, location)`, `canClaim(player, blocks)`
- `createPlot(firstCorner, secondCorner, owner)`, `claimPlot(plot, owner)`, `transferPlot(plot, owner)`, `unclaimPlot(plot)`, `savePlot(plot)`, `deletePlot(plot)`
- `addMember/removeMember`, `addOwner/removeOwner`, `banPlayer/unbanPlayer`, `addFlag/removeFlag`, `setFlag`, `clearFlags`, `applyDefaultFlags`
- `setHome`, `setPrice`, `setForSale`, `cancelSale`, `setGameMode(plot, gameMode)`, `setPlotStatus(plot, status)`
- `getRemainingClaimBlocks(player)`, `setRemainingClaimBlocks(player, blocks)`, `addRemainingClaimBlocks(player, blocks)`, `getRemainingPlotClaims(player)`
- `getPlotCount()`, `getClaimedPlotCount()`, `getAvailablePlotCount()`, `getTotalPlotBlocks()`, `getTotalClaimedBlocks()`, `getPlotSize(plot)`
- `getStorageBackend()` returns `file`, `mysql`, `sqlite`, or `mongodb`

## Events

PlotSystem publishes Bukkit events for addon plugins:

- `PlotCreateEvent`
- `PlotClaimEvent`
- `PlotUnclaimEvent`
- `PlotDeleteEvent`
- `PlotUpdateEvent`

All lifecycle events are cancellable. `PlotUpdateEvent` includes an `UpdateType` so addons can react to member, owner, ban, flag, default flag, home, price, gamemode, and save changes.

## Build From Source

```shell
git clone https://github.com/frame-dev/PlotSystem
cd PlotSystem
mvn clean package
```

The compiled plugin jar is created in `target/`.

## Notes

- Vault is required only for buying and selling plots.
- Admin-only plot gamemode changes require `plotsystem.admin.gamemode`.
- Database support is optional; file storage works out of the box.
- The plugin writes a `permissions.txt` file into the plugin folder on startup.
