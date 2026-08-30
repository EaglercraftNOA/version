package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;

public interface IPhaseFactory<T extends IPhase> {
   T create(EntityDragon dragon);
}
