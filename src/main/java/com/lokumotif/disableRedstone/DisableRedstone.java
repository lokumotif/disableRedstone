package com.lokumotif.disableRedstone;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Set;
import java.util.EnumSet;

public final class DisableRedstone extends JavaPlugin implements Listener {
    
    private static final Set<Material> REDSTONE_COMPONENTS = EnumSet.of(
        Material.REDSTONE_WIRE,
        Material.REDSTONE_TORCH,
        Material.REDSTONE_WALL_TORCH,
        Material.REPEATER,
        Material.COMPARATOR,
        Material.OBSERVER,
        Material.REDSTONE_BLOCK,
        Material.LEVER,
        Material.STONE_BUTTON,
        Material.POLISHED_BLACKSTONE_BUTTON,
        Material.OAK_BUTTON, Material.SPRUCE_BUTTON, Material.BIRCH_BUTTON,
        Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON,
        Material.MANGROVE_BUTTON, Material.CHERRY_BUTTON, Material.BAMBOO_BUTTON,
        Material.CRIMSON_BUTTON, Material.WARPED_BUTTON,
        Material.STONE_PRESSURE_PLATE,
        Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
        Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
        Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE,
        Material.BIRCH_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE,
        Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE,
        Material.MANGROVE_PRESSURE_PLATE, Material.CHERRY_PRESSURE_PLATE,
        Material.BAMBOO_PRESSURE_PLATE, Material.CRIMSON_PRESSURE_PLATE,
        Material.WARPED_PRESSURE_PLATE,
        Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL,
        Material.TARGET,
        Material.WAXED_COPPER_BULB, Material.WAXED_EXPOSED_COPPER_BULB, Material.WAXED_WEATHERED_COPPER_BULB,
        Material.WAXED_OXIDIZED_COPPER_BULB,
        Material.SCULK_SENSOR, Material.CALIBRATED_SCULK_SENSOR,
        Material.SCULK_SHRIEKER,
        Material.TRIPWIRE_HOOK,
        Material.DAYLIGHT_DETECTOR,
        Material.CRAFTER,
        Material.FURNACE,
        Material.CHEST, Material.TRAPPED_CHEST,
        Material.WAXED_COPPER_CHEST,
        Material.BARREL,
        Material.NOTE_BLOCK,
        Material.OAK_SHELF,
        Material.IRON_DOOR,
        Material.OAK_DOOR,
        Material.SPRUCE_DOOR,
        Material.BIRCH_DOOR,
        Material.JUNGLE_DOOR,
        Material.ACACIA_DOOR,
        Material.DARK_OAK_DOOR,
        Material.MANGROVE_DOOR,
        Material.CHERRY_DOOR,
        Material.BAMBOO_DOOR,
        Material.CRIMSON_DOOR,
        Material.WARPED_DOOR,
        Material.COPPER_DOOR,
        Material.EXPOSED_COPPER_DOOR,
        Material.WEATHERED_COPPER_DOOR,
        Material.OXIDIZED_COPPER_DOOR,
        Material.WAXED_COPPER_DOOR,
        Material.WAXED_EXPOSED_COPPER_DOOR,
        Material.WAXED_WEATHERED_COPPER_DOOR,
        Material.WAXED_OXIDIZED_COPPER_DOOR
    );
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("disableRedstone enabled: Redstone mechanics are disabled.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRedstone(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPhysics(BlockPhysicsEvent event) {
        if (REDSTONE_COMPONENTS.contains(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null &&
            REDSTONE_COMPONENTS.contains(event.getClickedBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDispense(BlockDispenseEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.DISPENSER || type == Material.DROPPER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onExplode(BlockExplodeEvent event) {
        // Prevent redstone-triggered TNT/block explosions from being used as a redstone mechanism.
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() != null && event.getEntity().getType().name().equals("PRIMED_TNT")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHopperMove(InventoryMoveItemEvent event) {
        // Cancel hopper transfers. This also prevents redstone-controlled hopper systems.
        if (event.getSource().getType().name().contains("HOPPER") ||
            event.getDestination().getType().name().contains("HOPPER") ||
            event.getInitiator().getType().name().contains("HOPPER")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHopperPickup(InventoryPickupItemEvent event) {
        if (event.getInventory().getType().name().contains("HOPPER")) {
            event.setCancelled(true);
        }
    }    
}
