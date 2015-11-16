package com.inabate.bungee;

import com.inabate.util.ReflectionUtils;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class PlayerListener implements Listener {

    private BungeeCDN cdn;

    public PlayerListener(BungeeCDN cdn) {
        this.cdn = cdn;
    }

    @EventHandler(priority=-32)
    public void onHandshake(PlayerHandshakeEvent event) {
        Channel channel;

        try {
            Object ch = ReflectionUtils.getPrivateField(event.getConnection().getClass(), event.getConnection(), Object.class, "ch");
            Method method = ch.getClass().getDeclaredMethod("getHandle", new Class[0]);
            channel = (Channel) method.invoke(ch, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String raw = event.getHandshake().getHost();
        cdn.getLogger().info("InAbate connection: " + raw);

        // Erm adam asked me to do this?
        // pnada was here
        if (!raw.contains("MineCDN")) event.getConnection().disconnect();

        String[] hostname = raw.split("//MineCDN//");

        if (hostname.length < 2) return;
        String vHost = hostname[0];

        try {
            ReflectionUtils.setFinalField(AbstractChannel.class, channel, "remoteAddress", new InetSocketAddress(hostname[1], event.getConnection().getAddress().getPort()));
            ReflectionUtils.setFinalField(event.getConnection().getClass(), event.getConnection(), "virtualHost", new InetSocketAddress(vHost, event.getHandshake().getPort()));
            ReflectionUtils.setFinalField(event.getHandshake().getClass(), event.getHandshake(), "host", vHost);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
