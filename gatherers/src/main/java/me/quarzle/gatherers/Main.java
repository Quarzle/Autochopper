package me.quarzle.gatherers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main extends JavaPlugin {
    public static FileConfiguration cfg;
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        File f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/gatherers-config/general.yml");
        if (f.exists()){
            cfg = YamlConfiguration.loadConfiguration(f);
        }else {
            try {
                Files.createDirectories(Path.of(Bukkit.getPluginsFolder().getAbsolutePath() + "/gatherers-config"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            cfg = YamlConfiguration.loadConfiguration(f);
            cfg.set("play-sound", true);
            cfg.set("block-break-limit.netherite_axe", 35);
            cfg.set("block-break-limit.diamond_axe", 25);
            cfg.set("block-break-limit.iron_axe", 15);
            cfg.set("block-break-limit.golden_axe", 12);
            cfg.set("block-break-limit.stone_axe", 8);
            cfg.set("block-break-limit.wooden_axe", 3);
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
