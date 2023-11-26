package me.quarzle.gatherers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtility {
    private static Map<UUID, Map<String, Object>> customPlayerData = new HashMap<>();
    public static void storeCustomData(Player player, String key, Object data){
        if (key==null&&data==null){
            customPlayerData.put(player.getUniqueId(), null);
        }else {
            Map<String, Object> dataMap = customPlayerData.get(player.getUniqueId());
            if (dataMap == null) {
                dataMap = new HashMap<>();
            }
            dataMap.put(key, data);
            customPlayerData.put(player.getUniqueId(), dataMap);
        }
    }
    public static Object getCustomData(Player player, String key){
        Map<String, Object> dataMap = customPlayerData.get(player.getUniqueId());
        if (dataMap==null){
            return null;
        }
        return dataMap.get(key);
    }
}
