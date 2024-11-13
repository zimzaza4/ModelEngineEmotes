package me.zimzaza4.modelengineemotes;

import lombok.Getter;
import me.zimzaza4.modelengineemotes.command.EmoteCommand;
import me.zimzaza4.modelengineemotes.emote.EmoteManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ModelEngineEmotes extends JavaPlugin {

    public static ModelEngineEmotes INSTANCE;
    @Getter
    private static EmoteManager emoteManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        emoteManager = new EmoteManager(new File(INSTANCE.getDataFolder(), "emotes"));
        emoteManager.reloadConfigFolder();
        EmoteCommand command = new EmoteCommand();
        Bukkit.getPluginCommand("megemote").setExecutor(command);
        Bukkit.getPluginCommand("megemote").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
