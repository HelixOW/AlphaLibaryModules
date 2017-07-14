package de.alphahelix.alminputs;

import de.alphahelix.almcore.ALModule;
import de.alphahelix.almfakeapi.fakeapi.utils.intern.FakeUtilBase;
import de.alphahelix.alminputs.events.ItemRenameEvent;
import de.alphahelix.alminputs.events.PlayerInputEvent;
import de.alphahelix.almnetty.PacketListenerAPI;
import de.alphahelix.almnetty.handler.PacketHandler;
import de.alphahelix.almnetty.handler.ReceivedPacket;
import de.alphahelix.almnetty.handler.SentPacket;
import de.alphahelix.almreflections.nms.BlockPos;
import de.alphahelix.almreflections.reflection.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class ALMInput implements ALModule {
    @Override
    public void load() {

    }

    @Override
    public void enable() {
        PacketListenerAPI.addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(SentPacket packet) {

            }

            @Override
            public void onReceive(ReceivedPacket packet) {
                if (packet.getPacketName().equals("PacketPlayInUpdateSign")) {
                    if (packet.getPlayer() == null) return;
                    BlockPos bp = ReflectionUtil.fromBlockPostition(packet.getPacketValue("a"));
                    if (bp.getX() == 0 && bp.getY() == 0 && bp.getZ() == 0) {
                        if (!SignGUI.getOpenGUIS().contains(packet.getPlayer().getName())) return;
                        int i = 0;
                        for (String line : (String[]) packet.getPacketValue(1)) {
                            if (i == 1)
                                Bukkit.getPluginManager().callEvent(new PlayerInputEvent(packet.getPlayer(), line));
                            i++;
                        }
                        SignGUI.getOpenGUIS().remove(packet.getPlayer().getName());
                    }
                } else if (packet.getPacketName().equals("PacketPlayInWindowClick")) {
                    if (packet.getPlayer() == null) return;
                    if (!AnvilGUI.getOpenGUIS().contains(packet.getPlayer().getName())) return;
                    InventoryView view = packet.getPlayer().getOpenInventory();

                    if (view != null && view.getTopInventory().getType() == InventoryType.ANVIL) {
                        int slot = (int) packet.getPacketValue("slot");

                        if (slot == 2) {
                            ItemStack is = null;
                            try {
                                is = (ItemStack) FakeUtilBase.itemstackAsBukkitCopy().invoke(null, packet.getPacketValue("item"));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            if (is == null) return;
                            if (is.hasItemMeta()) {
                                if (is.getItemMeta().hasDisplayName()) {
                                    ItemRenameEvent event = new ItemRenameEvent(packet.getPlayer(), view, is.getItemMeta().getDisplayName());
                                    Bukkit.getPluginManager().callEvent(event);
                                    Bukkit.getPluginManager().callEvent(new PlayerInputEvent(packet.getPlayer(), is.getItemMeta().getDisplayName()));

                                    AnvilGUI.getOpenGUIS().remove(packet.getPlayer().getName());

                                    if (event.isCancelled()) packet.getPlayer().closeInventory();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void disable() {

    }
}
