package com.inabate.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCDN extends JavaPlugin {

    @Override
    public void onEnable() {
        ProtocolLibPacketAdapter adapter = new ProtocolLibPacketAdapter(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
    }

    public void log(String message) {
        getLogger().info(message);
    }
}
