package me.zimzaza4.modelengineemotes.config;

import lombok.AllArgsConstructor;
import me.zimzaza4.modelengineemotes.emote.Emote;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EmoteConfig {
    private File config;
    public List<Emote> load() {
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(config);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        List<Emote> emotes = new ArrayList<>();
        for (String key : yml.getConfigurationSection("emotes").getKeys(false)) {
            @Nullable ConfigurationSection section = yml.getConfigurationSection("emotes." + key);
            if (section != null) {
                emotes.add(new Emote(key,
                        section.getString("model", "player"),
                        section.getString("animation", "idle"),
                        section.getBoolean("require-permission", true),
                        section.getBoolean("stop-when-move", true),
                        section.getInt("max-lasting-tick", -1)));
            }
        }
        return emotes;
    }


}
