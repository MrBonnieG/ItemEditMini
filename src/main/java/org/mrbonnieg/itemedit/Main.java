package org.mrbonnieg.itemedit;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements CommandExecutor, TabCompleter {
    private Component reloadMsg;
    private Component notPlayer;
    private Component noPermissions;
    private Component noItem;
    private Component noArgs;
    private Component nameUsage;
    private Component loreUsageMain;
    private Component nameReset;
    private Component nameChanged;
    private Component loreUsage;
    private Component invalidIndex;
    private Component invalidLine;
    private Component loreLineChanged;
    private Component loreTextRequired;
    private Component loreLineAdded;
    private Component loreLineRemoved;
    private Component loreReset;
    private Component numberRequired;

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable(){
        saveDefaultConfig();
        loadMessages();

        getCommand("ie").setExecutor(this);
        getCommand("ie").setTabCompleter(this);
        getLogger().log(Level.INFO, "Plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin disabled");
    }

    private void loadMessages() {

        notPlayer = msg("messages.system.not-player", "<red>Only players can use this command!");
        noPermissions = msg("messages.system.no-permissions", "<red>You don't have permission!");
        noItem = msg("messages.system.no-item", "<red>You must hold an item in your hand!");
        noArgs = msg("messages.system.no-args", "<red>Usage: /ie <name|lore>");
        nameUsage = msg("messages.usage.name", "<red>Usage: /ie name <text|reset>");
        loreUsageMain = msg("messages.usage.lore-main", "<red>Usage: /ie lore <set|add|remove|reset>");
        loreUsage = msg("messages.usage.lore-set", "<red>Usage: /ie lore set <line> <text>");
        invalidIndex = msg("messages.error.invalid-index", "<red>Invalid line index!");
        invalidLine = msg("messages.error.invalid-line", "<red>Line number must be greater than 0.");
        numberRequired = msg("messages.error.invalid-number", "<red>You must enter a valid number!");
        loreTextRequired = msg("messages.error.empty-text", "<red>You must enter text!");
        nameReset = msg("messages.success.name-reset", "<green>Item name has been reset.");
        nameChanged = msg("messages.success.name-changed", "<green>Item name has been changed.");
        loreLineChanged = msg("messages.success.lore-changed", "<green>Lore line has been changed.");
        loreLineAdded = msg("messages.success.lore-added", "<green>Lore line has been added.");
        loreLineRemoved = msg("messages.success.lore-removed", "<green>Lore line has been removed.");
        loreReset = msg("messages.success.lore-reset", "<green>Lore has been reset.");
        reloadMsg = msg("messages.system.reload", "<green>Config reloaded!");
    }

    private Component color(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return miniMessage.deserialize(text);
    }

    private Component msg(String path, String def) {
        String raw = getConfig().getString(path);
        return color(raw != null ? raw : def);
    }

    private ItemMeta meta(Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return null;
        return item.getItemMeta();
    }

    private ItemStack item(Player p) {
        return p.getInventory().getItemInMainHand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("itemedit.reload")) { sender.sendMessage(noPermissions); return true; }
            reloadConfig();
            loadMessages();
            sender.sendMessage(reloadMsg);
            return true;
        }

        if (!(sender instanceof Player player)) { sender.sendMessage(notPlayer); return true; }
        if (!sender.hasPermission("itemedit.use")) { sender.sendMessage(noPermissions); return true; }
        if (args.length == 0) { player.sendMessage(noArgs); return true; }

        ItemMeta meta = meta(player);
        ItemStack item = item(player);
        if (meta == null) { player.sendMessage(noItem); return true; }

        switch (args[0].toLowerCase()) {
            case "name" -> {
                if (args.length < 2) { player.sendMessage(nameUsage); return true; }
                if (args[1].equalsIgnoreCase("reset")) {
                    meta.displayName(null);
                    item.setItemMeta(meta);
                    player.sendMessage(nameReset);
                    return true;
                }
                String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                meta.displayName(color(text));
                item.setItemMeta(meta);
                player.sendMessage(nameChanged);
            }

            case "lore" -> {
                if (args.length < 2) { player.sendMessage(loreUsageMain); return true; }
                List<Component> lore = (meta.hasLore() && meta.lore() != null) ? new ArrayList<>(meta.lore()) : new ArrayList<>();
                switch (args[1].toLowerCase()) {
                    case "set" -> {
                        if (args.length < 4) { player.sendMessage(loreUsage); return true; }
                        try {
                            int line = Integer.parseInt(args[2]) - 1;
                            if (line < 0) { player.sendMessage(invalidLine); return true; }
                            String text = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                            if (text.isBlank()) { player.sendMessage(loreTextRequired); return true; }
                            while (lore.size() <= line) { lore.add(Component.empty()); }
                            lore.set(line, color(text));
                            meta.lore(lore);
                            item.setItemMeta(meta);
                            player.sendMessage(loreLineChanged);
                        } catch (NumberFormatException e) {
                            player.sendMessage(numberRequired);
                        }
                    }

                    case "add" -> {
                        if (args.length < 3) { player.sendMessage(loreTextRequired); return true; }
                        String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        if (text.isBlank()) { player.sendMessage(loreTextRequired); return true; }
                        lore.add(color(text));
                        meta.lore(lore);
                        item.setItemMeta(meta);
                        player.sendMessage(loreLineAdded);
                    }

                    case "remove" -> {
                        if (args.length < 3) { player.sendMessage(numberRequired); return true; }
                        try {
                            int id = Integer.parseInt(args[2]) - 1;
                            if (id < 0 || id >= lore.size()) { player.sendMessage(invalidIndex); return true; }
                            lore.remove(id);
                            meta.lore(lore.isEmpty() ? null : lore);
                            item.setItemMeta(meta);
                            player.sendMessage(loreLineRemoved);
                        } catch (NumberFormatException e) {
                            player.sendMessage(numberRequired);
                        }
                    }

                    case "reset" -> {
                        meta.lore(null);
                        item.setItemMeta(meta);
                        player.sendMessage(loreReset);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> base = new ArrayList<>();
            if (sender.hasPermission("itemedit.use")) {
                base.add("name");
                base.add("lore");
            }
            if (sender.hasPermission("itemedit.reload")) {
                base.add("reload");
            }
            return base;
        }

        if (args[0].equalsIgnoreCase("name")) {
            if (args.length == 2) return List.of("reset");
        }

        if (args[0].equalsIgnoreCase("lore")) {
            if (args.length == 2) return Arrays.asList("add", "set", "remove", "reset");
            if (args.length == 3 && args[1].equalsIgnoreCase("set")) return List.of("<line>");
            if (args.length == 3 && args[1].equalsIgnoreCase("remove")) return List.of("<line>");
        }

        return List.of();
    }
}