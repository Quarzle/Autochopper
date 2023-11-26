package me.quarzle.gatherers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static me.quarzle.gatherers.Main.cfg;

public class Events implements Listener {
    static Plugin thisPlugin = Main.getProvidingPlugin(Main.class);

    private Map<Player, Integer> chainBreakLength = new HashMap<>();
    
    @EventHandler
    private void onAxeClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (e.getItem()!=null && e.getAction().isRightClick()&&p.isSneaking()) {
            switch (e.getItem().getType()){
                case NETHERITE_AXE, DIAMOND_AXE, IRON_AXE, GOLDEN_AXE, STONE_AXE, WOODEN_AXE -> {
                    if (PlayerUtility.getCustomData(p, "axeEnabled")!=null && (boolean) PlayerUtility.getCustomData(p, "axeEnabled")) {
                        p.sendMessage(Component.text("Logging mode deactivated", Style.style(TextColor.color(75, 255, 247))));
                        PlayerUtility.storeCustomData(p, "axeEnabled", false);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 5f, 1f);
                    }else {
                        p.sendMessage(Component.text("Logging mode activated", Style.style(TextColor.color(58, 255, 93))));
                        PlayerUtility.storeCustomData(p, "axeEnabled", true);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 5f, 1f);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if (PlayerUtility.getCustomData(p, "axeEnabled")!=null && (boolean) PlayerUtility.getCustomData(p, "axeEnabled")) {
            switch (e.getBlock().getType()) {
                case OAK_LOG, CHERRY_LOG, ACACIA_LOG, DARK_OAK_LOG, BIRCH_LOG, JUNGLE_LOG, MANGROVE_LOG, SPRUCE_LOG -> {
                    switch (p.getInventory().getItemInMainHand().getType()) {
                        case NETHERITE_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.netherite_axe"));
                        case DIAMOND_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.diamond_axe"));
                        case IRON_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.iron_axe"));
                        case GOLDEN_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.golden_axe"));
                        case STONE_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.stone_axe"));
                        case WOODEN_AXE -> chainBreakLength.put(p, cfg.getInt("block-break-limit.wooden_axe"));
                        default -> chainBreakLength.put(p, null);
                    }
                    if (chainBreakLength.get(p) < 1) {
                        chainBreakLength.put(p, null);
                    }
                    if (chainBreakLength.get(p) != null) {
                        chainBreak(e.getBlock(), p);
                    }
                }
            }
        }
    }
    private void chainBreak(Block block, Player p){
        Material material = block.getType();
        iterateBreak(material, block.getLocation().clone().add(new Vector(0, 1, 0)), p);
        iterateBreak(material, block.getLocation().clone().add(new Vector(0, -1, 0)), p);
        iterateBreak(material, block.getLocation().clone().add(new Vector(0, 0, 1)), p);
        iterateBreak(material, block.getLocation().clone().add(new Vector(0, 0, -1)), p);
        iterateBreak(material, block.getLocation().clone().add(new Vector(1, 0, 0)), p);
        iterateBreak(material, block.getLocation().clone().add(new Vector(-1, 0, 0)), p);
    }
    private void iterateBreak(Material breakMat, Location blockLoc, Player p){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (chainBreakLength.get(p)!=null && blockLoc.getBlock().getType()==breakMat && chainBreakLength.get(p)>0) {
                    chainBreakLength.put(p, chainBreakLength.get(p)-1);
                    blockLoc.getBlock().breakNaturally(p.getInventory().getItemInMainHand());
                    if (cfg.getBoolean("play-sound")) {
                        blockLoc.getWorld().playSound(blockLoc, Sound.ENTITY_ARROW_SHOOT, 5f, 1.5f);
                    }
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(0, 1, 0)), p);
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(0, -1, 0)), p);
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(0, 0, 1)), p);
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(0, 0, -1)), p);
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(1, 0, 0)), p);
                    iterateBreak(breakMat, blockLoc.clone().add(new Vector(-1, 0, 0)), p);
                }
            }
        }.runTaskLater(thisPlugin, 2);
        if (chainBreakLength.get(p)<0){
            chainBreakLength.put(p, null);
        }
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerUtility.storeCustomData(p, null, null);
    }
    
}
