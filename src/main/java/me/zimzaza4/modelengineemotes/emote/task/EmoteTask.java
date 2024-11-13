package me.zimzaza4.modelengineemotes.emote.task;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zimzaza4.geyserutils.spigot.GeyserUtils;
import me.zimzaza4.modelengineemotes.ModelEngineEmotes;
import me.zimzaza4.modelengineemotes.emote.Emote;
import me.zimzaza4.modelengineemotes.util.BedrockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;

public class EmoteTask {
    Emote emote;
    Player player;
    @Getter
    BukkitRunnable task;

    public EmoteTask(Emote emote, Player player) {
        this.emote = emote;
        this.player = player;
    }

    public void play() {

        ModeledEntity modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(player);
        modeledEntity.getBase().getBodyRotationController().setPlayerMode(true);

        IEntityData data = modeledEntity.getBase().getData();
        modeledEntity.setBaseEntityVisible(true);
        if (data instanceof BukkitEntityData bukkitEntityData) {
            bukkitEntityData.getTracked().addForcedPairing(player);

            ActiveModel activeModel = ModelEngineAPI.createActiveModel(emote.getModel());
            modeledEntity.addModel(activeModel, true);

            ModelEngineAPI.getEntityHandler().setForcedInvisible(player, true);
            modeledEntity.addModel(activeModel, false).ifPresent(ActiveModel::destroy);
            activeModel.getBones().values().forEach((modelBone) -> {
                modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent((playerLimb) -> {
                    playerLimb.setTexture(player);
                });
            });


            for (Player v : Bukkit.getOnlinePlayers()) {
                boolean bedrock = FloodgateApi.getInstance().isFloodgatePlayer(v.getUniqueId());
                if (bedrock) {
                    bukkitEntityData.getTracked().addForcedHidden(v);
                } else {
                    v.hidePlayer(ModelEngineEmotes.INSTANCE, player);
                }
            }
        }

        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            ModelEngineAPI.getNMSHandler().getEntityHandler().setForcedInvisible(player, true);
        }
        ActiveModel model = ModelEngineAPI.createActiveModel(emote.getModel());
        modeledEntity.addModel(model,false);
        modeledEntity.setBaseEntityVisible(false);
        BlueprintAnimation anim = model.getBlueprint().getAnimations().get(emote.getAnimation());

        model.getBones().values().forEach(modelBone -> {
            applyItem(modelBone, player);
        });

        model.getAnimationHandler().playAnimation(emote.getAnimation(), 0.1, 0.1, 1, true);
        boolean loop = anim.getLoopMode() == BlueprintAnimation.LoopMode.LOOP;
        BedrockUtils.playAnimation(player, "animation.emote." + emote.getAnimation(), loop);
        task = new BukkitRunnable() {
            final Location location = player.getLocation();

            @Override
            public void run() {
                boolean moved = (player.getWorld() != location.getWorld()) || location.distance(player.getLocation()) > 0.2;
                if (player.isDead() || !player.isOnline() || (emote.stopWhenMove && moved)) {
                    this.cancel();
                }
            }

            @Override
            public synchronized void cancel() {
                super.cancel();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.showPlayer(ModelEngineEmotes.INSTANCE, player);
                }
                ModelEngineEmotes.getEmoteManager().getPlayingEmotes().remove(player);
                BedrockUtils.playStopAnimation(player);
                ModelEngineAPI.getNMSHandler().getEntityHandler().setForcedInvisible(player, false);
                model.destroy();
            }
        };
        task.runTaskTimer(ModelEngineEmotes.INSTANCE, 0, 2);
        if (emote.maxLastingTick != -1) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!task.isCancelled()) {
                        task.cancel();
                    }
                }
            }.runTaskLater(GeyserUtils.getInstance(), emote.maxLastingTick);
        }
    }

    private void applyItem(ModelBone bone, Player player) {
        if (bone == null) {
            return;
        }
        if (bone.getBoneBehavior(BoneBehaviorTypes.ITEM).isEmpty()) {
            return;
        }
        HeldItem item = bone.getBoneBehavior(BoneBehaviorTypes.ITEM).get();
        if (bone.getBoneId().toLowerCase().contains("rightitem")) {
            item.setItemProvider(() -> player.getEquipment().getItemInMainHand());
        }
        if (bone.getBoneId().toLowerCase().contains("leftitem")) {
            item.setItemProvider(() -> player.getEquipment().getItemInOffHand());
        }
        if (bone.getBoneId().equalsIgnoreCase("hat")) {
            item.setItemProvider(() -> player.getEquipment().getHelmet() == null ? new ItemStack(Material.AIR) : player.getEquipment().getHelmet());
            item.setDisplay(ItemDisplay.ItemDisplayTransform.HEAD);
        }


    }
}
