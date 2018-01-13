package me.huch.custombook;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaBook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class Book {
    private Core instance;

    Book(Core plugin) {
        this.instance = plugin;
    }

    public ItemStack build() {
        try {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            List<IChatBaseComponent> pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);

            int pageCount = instance.getConfig().getInt("pages-count");
            for (int i = 1; i <= pageCount; i++) {
                String pageS = "page" + i;
                int lineCount = instance.getConfig().getInt(pageS + ".line-count");
                TextComponent base = new TextComponent();
                for (int b = 1; b <= lineCount; b++) {
                    String lineText = instance.getConfig().getString(pageS + ".line" + b + ".text");
                    TextComponent textComponent = new TextComponent(lineText);
                    if (instance.getConfig().getBoolean(pageS + ".line" + b + ".clickable")) {
                        String clickAction = instance.getConfig().getString(pageS + ".line" + b + ".click-action");
                        String clickActionText = instance.getConfig().getString(pageS + ".line" + b + ".click-action-text");
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(clickAction), clickActionText));
                    }
                    if (instance.getConfig().getBoolean(pageS + ".line" + b + ".hoverover")) {
                        String hoverText = ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString(pageS + ".line" + b + ".hoverover-text"));
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                    }
                    if (instance.getConfig().getBoolean(pageS + ".line" + b + ".text-bold")) {
                        textComponent.setBold(true);
                    }
                    net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.valueOf(instance.getConfig().getString(pageS + ".line" + b + ".text-color").toUpperCase());
                    textComponent.setColor(color);

                    base.addExtra(textComponent);
                }

                IChatBaseComponent page = IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(base));
                pages.add(page);
            }

            meta.setTitle(ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("book-title")));
            meta.setAuthor(ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("book-author")));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', instance.getConfig().getString("book-display-name")));
            book.setItemMeta(meta);

            return book;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
