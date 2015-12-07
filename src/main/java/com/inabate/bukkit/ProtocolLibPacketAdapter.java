package com.inabate.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.server.SocketInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import com.inabate.util.ReflectionUtils;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ProtocolLibPacketAdapter extends PacketAdapter {

    private BukkitCDN bukkitCdn = null;
    private String properField = null;

    public ProtocolLibPacketAdapter(BukkitCDN bukkitCdn) {
        super(bukkitCdn, PacketType.Handshake.Client.SET_PROTOCOL);
        this.bukkitCdn = bukkitCdn;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        String raw = event.getPacket().getStrings().read(0);
        if (!raw.contains("MineCDN")) event.setCancelled(true);

        String[] hostname = raw.split("//MineCDN//");

        if (hostname.length >= 2) {
            try {
                SocketInjector ignored = TemporaryPlayerFactory.getInjectorFromPlayer(event.getPlayer());

                Object injector = ReflectionUtils.getPrivateField(ignored.getClass(), ignored, "injector");
                Object networkManager = ReflectionUtils.getPrivateField(injector.getClass(), injector, "networkManager");

                if (this.properField == null) {
                    this.properField = ReflectionUtils.getProperField(networkManager.getClass());
                    this.bukkitCdn.getLogger().info("Got NetworkManager Socket Address Field: " + this.properField);
                }

                Channel channel = (Channel) ReflectionUtils.getPrivateField(injector.getClass(), injector, "originalChannel");

                InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
                InetSocketAddress newRemoteAddress = new InetSocketAddress(hostname[1], remoteAddress.getPort());

                try {
                    ReflectionUtils.setFinalField(networkManager.getClass(), networkManager, this.properField == null ? "l" : this.properField, newRemoteAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ReflectionUtils.setFinalField(AbstractChannel.class, channel, "remoteAddress", newRemoteAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }

            event.getPacket().getStrings().write(0, hostname[0]);
        }
    }
}
