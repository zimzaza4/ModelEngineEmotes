package me.zimzaza4.modelengineemotes.emote;

import lombok.Getter;
import me.zimzaza4.modelengineemotes.ModelEngineEmotes;
import me.zimzaza4.modelengineemotes.config.EmoteConfig;
import me.zimzaza4.modelengineemotes.emote.task.EmoteTask;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EmoteManager {
    private File configFolder;
    private Map<String, Emote> registeredEmotes = new ConcurrentHashMap<>();

    @Getter
    private Map<Player, EmoteTask> playingEmotes = new ConcurrentHashMap<>();

    public EmoteManager(File configFolder) {
        this.configFolder = configFolder;
    }

    public List<String> getPlayerEmotes(Player player) {
        List<String> emotes = new ArrayList<>();
        for (Emote value : registeredEmotes.values()) {
            if (!value.requirePermission) {
                emotes.add(value.id);
                continue;
            }
            if (player.hasPermission("emote.use." + value.id)) {
                emotes.add(value.id);
            }
        }
        return emotes;
    }
    public Map<String, Emote> getEmotes() {
        return registeredEmotes;
    }

    public Emote getEmote(String id) {
        return registeredEmotes.get(id);
    }
    public void registerEmote(Emote emote) {
        registeredEmotes.put(emote.getId(), emote);
    }

    public void reloadConfigFolder() {
        ModelEngineEmotes.INSTANCE.reloadConfig();
        registeredEmotes.clear();
        try {
            configFolder.mkdirs();
            Files.walk(configFolder.toPath()).forEach(file -> {
                if (file.toFile().getName().endsWith(".yml")) {
                    try {
                        EmoteConfig config = new EmoteConfig(file.toFile());
                        for (Emote emote : config.load()) {
                            registerEmote(emote);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playEmote(Player player, Emote emote) {
        if (emote == null) {
            return;
        }
        if (player == null) {
            return;
        }
        EmoteTask task = new EmoteTask(emote, player);
        if (playingEmotes.containsKey(player)) {
            playingEmotes.get(player).getTask().cancel();
        }
        playingEmotes.put(player, task);
        task.play();
    }
    public void playEmote(Player player, String emoteId) {
        playEmote(player, registeredEmotes.get(emoteId));
    }
}

