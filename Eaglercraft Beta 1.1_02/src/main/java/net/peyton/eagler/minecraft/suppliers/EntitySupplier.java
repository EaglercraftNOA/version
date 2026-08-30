package net.peyton.eagler.minecraft.suppliers;

import net.minecraft.src.Entity;
import net.minecraft.src.World;

public interface EntitySupplier <T extends Entity> {

	T createEntity(World world);
	
}
