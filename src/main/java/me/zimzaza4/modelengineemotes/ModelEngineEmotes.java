package me.zimzaza4.modelengineemotes;

import lombok.Getter;
import me.zimzaza4.modelengineemotes.command.EmoteCommand;
import me.zimzaza4.modelengineemotes.emote.EmoteManager;
import me.zimzaza4.modelengineemotes.emote.task.EmoteTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ModelEngineEmotes extends JavaPlugin {

    public static ModelEngineEmotes INSTANCE;
    @Getter
    private static EmoteManager emoteManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        emoteManager = new EmoteManager(new File(INSTANCE.getDataFolder(), "emotes"));
        saveDefaultConfig();
        emoteManager.reloadConfigFolder();
        EmoteCommand command = new EmoteCommand();
        Bukkit.getPluginCommand("megemote").setExecutor(command);
        Bukkit.getPluginCommand("megemote").setTabCompleter(command);
    }

    public static String message(String node) {
        return ChatColor.translateAlternateColorCodes('&', ModelEngineEmotes.INSTANCE.getConfig().getString("messages." + node, node));
    }

    @Override
    public void onDisable() {
        for (EmoteTask task : emoteManager.getPlayingEmotes().values()) {
            task.getTask().cancel();
        }
    }
}
