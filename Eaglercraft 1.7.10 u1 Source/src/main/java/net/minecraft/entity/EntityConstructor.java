package net.minecraft.entity;

import net.minecraft.world.World;

public interface EntityConstructor<T> {

    T createEntity(World world);

}