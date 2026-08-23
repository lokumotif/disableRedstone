package com.lokumotif.disableRedstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DisableRedstone extends JavaPlugin implements Listener, CommandExecutor {

    private final Set<Material> disabledBlocks = new HashSet<>();

    @Override
    public void onEnable() {

        File configFile = new File(getDataFolder(), "config.yml");
        boolean configMissing = !configFile.exists();

        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand drCommand = getCommand("dr");
        
        if (drCommand != null) {
            drCommand.setExecutor(this);
        } else {
            getLogger().severe("dr command could not be found in plugin.yml!");
        }
        
        PluginCommand disableredstoneCommand = getCommand("disableredstone");
        
        if (disableredstoneCommand != null) {
            disableredstoneCommand.setExecutor(this);
        } else {
            getLogger().severe("disableRedstone command could not be found in plugin.yml!");
        }

        getLogger().info("disableRedstone enabled.");

        if (configMissing) {
            getLogger().warning("config.yml was missing and has been recreated!");
        }
    }
    
    private boolean isRedstoneComponent(Material type) {
        return switch (type) {
            case REDSTONE_BLOCK,
                 REDSTONE_WIRE,
                 REDSTONE_TORCH,
                 REDSTONE_WALL_TORCH,
                 REPEATER,
                 COMPARATOR,
                 OBSERVER,
                 POWERED_RAIL,
                 DETECTOR_RAIL,
                 ACTIVATOR_RAIL,
                 TARGET,
                 SCULK_SENSOR,
                 CALIBRATED_SCULK_SENSOR,
                 PISTON,
                 STICKY_PISTON,
                 DISPENSER,
                 DROPPER,
                 HOPPER,
                 NOTE_BLOCK,
                 CRAFTER,
                 TRAPPED_CHEST -> true;

            default -> false;
        };
    }
    
    private boolean isInteractionBlock(Material type) {
        return switch (type) {
            case HOPPER,
                 CRAFTER,
                 DISPENSER,
                 DROPPER,
                 REPEATER,
                 COMPARATOR,
                 END_CRYSTAL,
                 RESPAWN_ANCHOR,
                 JUKEBOX,
                 BARREL -> true;
    
            default -> false;
        };
    }

    private boolean isRedstoneLamp(Material type) {
        return switch (type) {
            case REDSTONE_LAMP,
                 COPPER_BULB,
                 EXPOSED_COPPER_BULB,
                 WEATHERED_COPPER_BULB,
                 OXIDIZED_COPPER_BULB,
                 WAXED_COPPER_BULB,
                 WAXED_EXPOSED_COPPER_BULB,
                 WAXED_WEATHERED_COPPER_BULB,
                 WAXED_OXIDIZED_COPPER_BULB -> true;
    
            default -> false;
        };
    }

    private boolean isDoors(Material type) {
        return switch (type) {
            case OAK_DOOR,
                 IRON_DOOR,
                 SPRUCE_DOOR,
                 BIRCH_DOOR,
                 JUNGLE_DOOR,
                 ACACIA_DOOR,
                 DARK_OAK_DOOR,
                 MANGROVE_DOOR,
                 CHERRY_DOOR,
                 PALE_OAK_DOOR,
                 CRIMSON_DOOR -> true;
    
            default -> false;
        };
    }

    private boolean isFenceGates(Material type) {
        return switch (type) {
            case OAK_FENCE_GATE,
                 SPRUCE_FENCE_GATE,
                 BIRCH_FENCE_GATE,
                 JUNGLE_FENCE_GATE,
                 ACACIA_FENCE_GATE,
                 DARK_OAK_FENCE_GATE,
                 MANGROVE_FENCE_GATE,
                 CHERRY_FENCE_GATE,
                 PALE_OAK_FENCE_GATE,
                 BAMBOO_FENCE_GATE,
                 CRIMSON_FENCE_GATE,
                 WARPED_FENCE_GATE -> true;
    
            default -> false;
        };
    }

    private boolean isTrapdoors(Material type) {
        return switch (type) {
            case OAK_TRAPDOOR,
                 IRON_TRAPDOOR,
                 SPRUCE_TRAPDOOR,
                 BIRCH_TRAPDOOR,
                 JUNGLE_TRAPDOOR,
                 ACACIA_TRAPDOOR,
                 DARK_OAK_TRAPDOOR,
                 MANGROVE_TRAPDOOR,
                 CHERRY_TRAPDOOR,
                 PALE_OAK_TRAPDOOR,
                 BAMBOO_TRAPDOOR,
                 CRIMSON_TRAPDOOR,
                 WARPED_TRAPDOOR -> true;
    
            default -> false;
        };
    }

    private boolean isButtons(Material type) {
        return switch (type) {
            case OAK_BUTTON,
                 STONE_BUTTON,
                 SPRUCE_BUTTON,
                 BIRCH_BUTTON,
                 JUNGLE_BUTTON,
                 ACACIA_BUTTON,
                 DARK_OAK_BUTTON,
                 MANGROVE_BUTTON,
                 CHERRY_BUTTON,
                 PALE_OAK_BUTTON,
                 BAMBOO_BUTTON,
                 CRIMSON_BUTTON,
                 WARPED_BUTTON -> true;
    
            default -> false;
        };
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!command.getName().equalsIgnoreCase("dr")
                && !command.getName().equalsIgnoreCase("disableredstone")) {
            return false;
        }
        
        if (!sender.hasPermission("disableredstone.reload")) {
            sender.sendMessage("§cYou have not permission to use this command.");
            return true;
        }
        
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        
            reloadConfig();
            loadConfig();
        
            sender.sendMessage("§aReloaded successfully.");
            return true;
        }
        
        sender.sendMessage("§euse this command: /disableredstone reload");
        return true;
    }

    private void loadConfig() {

        disabledBlocks.clear();

        List<String> blocks = getConfig().getStringList("disabled-blocks");

        for (String name : blocks) {

            try {
                Material material = Material.valueOf(name.toUpperCase());
                disabledBlocks.add(material);

            } catch (IllegalArgumentException e) {

                getLogger().warning(
                    "Invalid Material: " + name
                );
            }
        }

        getLogger().info(
            disabledBlocks.size() + " Block loaded successfully: " + disabledBlocks.size()
        );

        for (Material material : disabledBlocks) {
        getLogger().info("  - " + material.name());
        }
    }

    // Redstone blocking
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRedstone(BlockRedstoneEvent event) {

        if (!getConfig().getBoolean("features.redstone")) {
                && isRedstoneComponent(event.getBlock().getType())) {
            event.setNewCurrent(0);
        }
        
        if (!getConfig().getBoolean("features.doors")
                && isDoors(event.getBlock().getType())) {
            event.setNewCurrent(0);
        }

        if (!getConfig().getBoolean("features.fence-gates")
                && isFenceGates(event.getBlock().getType())) {
            event.setNewCurrent(0);
        }

        if (!getConfig().getBoolean("features.trapdoors")
                && isTrapdoors(event.getBlock().getType())) {
            event.setNewCurrent(0);
        }

        if (!getConfig().getBoolean("features.buttons")
                && isButtons(event.getBlock().getType())) {
            event.setNewCurrent(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRedstonePhysics(BlockPhysicsEvent event) {
    
        if (getConfig().getBoolean("features.redstone")) {
            return;
        }
    
        Block block = event.getBlock();
    
        if (block.getType() == Material.REDSTONE_BLOCK) {
            event.setCancelled(true);
        }
    }

    
    // Piston check
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {

        if (!getConfig().getBoolean("features.pistons")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {

        if (!getConfig().getBoolean("features.pistons")) {
            event.setCancelled(true);
        }
    }

    // Dispenser / Dropper
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDispense(BlockDispenseEvent event) {

        if (!getConfig().getBoolean("features.dispensers")) {

            Material type = event.getBlock().getType();

            if (type == Material.DISPENSER ||
                type == Material.DROPPER) {

                event.setCancelled(true);
            }
        }
    }

    // Block explosion
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {

        if (!getConfig().getBoolean("features.explosions")) {
            event.setCancelled(true);
        }
    }

    // Entity explosion
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {

    if (!getConfig().getBoolean("features.explosions")) {
        event.blockList().clear();
        }
    }


    // TNT interact block
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onTntPrime(TNTPrimeEvent event) {
    
        if (!getConfig().getBoolean("features.tnt")) {
            event.setCancelled(true);
        }
        
        if (!getConfig().getBoolean("features.redstone")) {
            if (event.getCause() == TNTPrimeEvent.PrimeCause.REDSTONE) {
                event.setCancelled(true);
            }
        }
    }
    

    // Hopper item transfer
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHopperMove(InventoryMoveItemEvent event) {
    
        if (!getConfig().getBoolean("features.hoppers")) {
    
            if (event.getSource().getType().name().contains("HOPPER") ||
                event.getDestination().getType().name().contains("HOPPER") ||
                event.getInitiator().getType().name().contains("HOPPER")) {
    
                event.setCancelled(true);
            }
        }
    }

    // Hopper item pickup
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHopperPickup(InventoryPickupItemEvent event) {
    
        if (!getConfig().getBoolean("features.hoppers")) {
    
            if (event.getInventory().getType().name().contains("HOPPER")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRedstoneInteract(PlayerInteractEvent event) {
    
        if (getConfig().getBoolean("features.redstone")) {
            return;
        }
    
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
    
        Block block = event.getClickedBlock();
    
        if (block == null) {
            return;
        }
    
        if (isRedstoneComponent(block.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onJukeboxInteract(PlayerInteractEvent event) {
    
        if (getConfig().getBoolean("features.jukebox-disc")) {
            return;
        }
    
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
    
        Block block = event.getClickedBlock();
    
        if (block != null && block.getType() == Material.JUKEBOX) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onJukeboxRedstone(BlockRedstoneEvent event) {
    
        if (getConfig().getBoolean("features.jukebox-redstone")) {
            return;
        }
    
        if (event.getBlock().getType() == Material.JUKEBOX) {
            event.setNewCurrent(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onLampPhysics(BlockPhysicsEvent event) {
    
        if (getConfig().getBoolean("features.redstone-lamp")) {
            return;
        }
    
        Block block = event.getBlock();
    
        if (!isRedstoneLamp(block.getType())) {
            return;
        }
    
        if (block.getBlockData() instanceof Lightable lightable) {
            if (lightable.isLit()) {
                lightable.setLit(false);
                block.setBlockData(lightable, false);
            }
        }
    }
}
