package de.alphahelix.almnetty;

import de.alphahelix.almcore.ALModule;
import de.alphahelix.almcore.uuid.UUIDFetcher;
import de.alphahelix.almnetty.events.ArmorChangeEvent;
import de.alphahelix.almnetty.handler.PacketHandler;
import de.alphahelix.almnetty.handler.PacketOptions;
import de.alphahelix.almnetty.handler.ReceivedPacket;
import de.alphahelix.almnetty.handler.SentPacket;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ALMNetty implements ALModule {

    private static final PacketListenerAPI PACKET_LISTENER_API = new PacketListenerAPI();
    private static HashMap<UUID, Double> oldValues = new HashMap<>();

    @Override
    public void load() {
        PACKET_LISTENER_API.load();
    }

    @Override
    public void enable() {
        PACKET_LISTENER_API.init();

        PacketListenerAPI.addPacketHandler(new PacketHandler() {
            @Override
            @PacketOptions(forcePlayer = true)
            public void onSend(SentPacket packet) {
                if (packet.getPacketName().equals("PacketPlayOutUpdateAttributes")) {
                    Player p = packet.getPlayer();

                    if ((int) packet.getPacketValue("a") == p.getEntityId()) {
                        UUID id = UUIDFetcher.getUUID(p);
                        double nV = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
                        double oV;

                        if (oldValues.containsKey(id)) {
                            if (oldValues.get(id) != nV) {
                                oV = oldValues.get(id);
                                oldValues.put(id, nV);
                                ArmorChangeEvent ace = new ArmorChangeEvent(p, oV, nV);
                                Bukkit.getPluginManager().callEvent(ace);
                            }
                        } else {
                            oldValues.put(id, nV);
                            ArmorChangeEvent ace = new ArmorChangeEvent(p, 0.0, nV);
                            Bukkit.getPluginManager().callEvent(ace);
                        }
                    }
                }
            }

            @Override
            public void onReceive(ReceivedPacket packet) {

            }
        });
    }

    @Override
    public void disable() {
        PACKET_LISTENER_API.disable();
    }
}
