package me.huch.custombook;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Core extends JavaPlugin implements Listener {
    private Book customBook;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        this.customBook = new Book(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player user = event.getPlayer();

        if (getConfig().getBoolean("first-join-only")) {
            if (!(user.hasPlayedBefore())) {
                delayedTask(user);
            }
        } else {
            delayedTask(user);
        }
    }

    private void delayedTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendPacket(customBook.build(), player);
            }
        }.runTaskLater(this, 5);
    }

    private void sendPacket(ItemStack book, Player player) {
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte)0);
        buf.writerIndex(1);

        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        player.getInventory().setItem(slot, old);

        if (getConfig().getBoolean("players-receive-the-book")) {
            int customSlot = getConfig().getInt("slot-for-the-book") - 1;
            player.getInventory().setItem(customSlot, book);
        }
    }

}
