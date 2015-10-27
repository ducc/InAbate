package com.inabate.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCDN extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    public void log(String message) {
        getLogger().info(message);
    }
}
