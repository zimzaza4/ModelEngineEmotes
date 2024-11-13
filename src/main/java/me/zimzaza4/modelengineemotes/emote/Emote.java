package me.zimzaza4.modelengineemotes.emote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Emote {
    public String id;
    public String model;
    public String animation;
    public boolean requirePermission;
    public boolean stopWhenMove;
    public int maxLastingTick = -1;


}
