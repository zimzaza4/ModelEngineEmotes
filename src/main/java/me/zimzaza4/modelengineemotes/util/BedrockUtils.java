package me.zimzaza4.modelengineemotes.util;

import me.zimzaza4.geyserutils.common.animation.Animation;
import me.zimzaza4.geyserutils.spigot.api.EntityUtils;
import me.zimzaza4.geyserutils.spigot.api.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashSet;
import java.util.Set;

public class BedrockUtils {
    public static Set<Player> getAllBedrockPlayers() {
        Set<Player> players = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(onlinePlayer.getUniqueId())) {
                players.add(onlinePlayer);
            }
        }
        return players;
    }

    public static void playAnimation(Player player, String anim, boolean loop) {
        Animation.AnimationBuilder builder = Animation.builder().animation(anim);
        if (loop) {
            builder.nextState(anim);
        }
        for (Player bedrockPlayer : getAllBedrockPlayers()) {
            PlayerUtils.playEntityAnimation(bedrockPlayer, builder.build(), player);
        }
    }


    public static void playStopAnimation(Player player) {
        Animation.AnimationBuilder animation = Animation.builder()
                .stopExpression("!query.any_animation_finished")
                .animation("animation.player.holding");

        for (Player bedrockPlayer : getAllBedrockPlayers()) {
            PlayerUtils.playEntityAnimation(bedrockPlayer, animation.build(), player);
        }
    }



}
