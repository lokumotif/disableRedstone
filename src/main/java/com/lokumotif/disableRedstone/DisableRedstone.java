package com.lokumotif.disableRedstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DisableRedstone extends JavaPlugin implements Listener, CommandExecutor {

    private final Set<Material> disabledBlocks = new HashSet<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        loadConfig();

        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand command = getCommand("disableRedstone");
        
        if (command != null) {
            command.setExecutor(this);
        } else {
            getLogger().severe("disableRedstone command could not be found in plugin.yml!");
        }

        getLogger().info("disableRedstone enabled.");
        
    }
    
    private boolean isRedstoneComponent(Material type) {
        return switch (type) {
            case REPEATER,
                 COMPARATOR,
                 POWERED_RAIL,
                 DETECTOR_RAIL,
                 ACTIVATOR_RAIL,
                 TARGET,
                 SCULK_SENSOR,
                 CALIBRATED_SCULK_SENSOR -> true;

            default -> false;
        };
    }

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!command.getName().equalsIgnoreCase("disableRedstone")) {
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
        
        sender.sendMessage("§euse this command: /disableRedstone reload");
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

        if (getConfig().getBoolean("features.redstone")) {
            return;
        }
    
        event.setNewCurrent(0);
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

    // Block interact redstones
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock() != null &&
            disabledBlocks.contains(event.getClickedBlock().getType())) {

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

    if (!getConfig().getBoolean("features.tnt-priming")) {
        event.setCancelled(true);
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
    
        Block block = event.getClickedBlock();
    
        if (block == null) {
            return;
        }
    
        // Disable Redstone components
        if (!getConfig().getBoolean("features.redstone")
                && isRedstoneComponent(block.getType())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onTntPrime(EntitySpawnEvent event) {

        if (getConfig().getBoolean("features.tnt-priming")) {
            return;
        }

        if (event.getEntityType() == EntityType.TNT) {
            event.setCancelled(true);
        }
    }

    }
