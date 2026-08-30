package net.peyton.eagler.minecraft;

import net.minecraft.world.World;

public interface EntityConstructor<T> {

	T createEntity(World world);

}