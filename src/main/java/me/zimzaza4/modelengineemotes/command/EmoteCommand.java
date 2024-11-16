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
                commandSender.sendMessage(ModelEngineEmotes.message("reloading"));
                ModelEngineEmotes.getEmoteManager().reloadConfigFolder();
                commandSender.sendMessage(ModelEngineEmotes.message("reloaded"));
            }
            return true;
        }
        if (commandSender instanceof Player player) {
            Emote emote = ModelEngineEmotes.getEmoteManager().getEmote(args[0]);
            if (emote != null) {
                if (emote.requirePermission && !player.hasPermission("emote.use." + emote.id)) {
                    player.sendMessage(ModelEngineEmotes.message("no-permission"));
                    return false;
                }
                ModelEngineEmotes.getEmoteManager().playEmote(player, emote);
            } else {
                player.sendMessage(ModelEngineEmotes.message("emote-not-exist"));
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
