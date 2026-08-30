package net.peyton.eagler.minecraft;

import net.minecraft.entity.boss.EntityDragon;

public interface DragonPhaseConstructor<T> {

	T createPhase(EntityDragon phase);

}