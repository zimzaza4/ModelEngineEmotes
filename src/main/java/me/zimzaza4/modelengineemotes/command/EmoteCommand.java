package me.zimzaza4.modelengineemotes.command;

import me.zimzaza4.modelengineemotes.ModelEngineEmotes;
import me.zimzaza4.modelengineemotes.emote.Emote;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmoteCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (commandSender.hasPermission("emote.admin.reload")) {
                commandSender.sendMessage("Reloading...");
                ModelEngineEmotes.getEmoteManager().reloadConfigFolder();
                commandSender.sendMessage("Reloaded");
            }
            return true;
        }
        if (commandSender instanceof Player player) {
            Emote emote = ModelEngineEmotes.getEmoteManager().getEmote(args[0]);
            if (emote != null) {
                if (emote.requirePermission && !player.hasPermission("emote.use." + emote.id)) {
                    player.sendMessage("");
                    return false;
                }
                player.sendMessage(emote.toString());
                ModelEngineEmotes.getEmoteManager().playEmote(player, emote);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            return ModelEngineEmotes.getEmoteManager().getPlayerEmotes(player);
        }
        return List.of();
    }
}
