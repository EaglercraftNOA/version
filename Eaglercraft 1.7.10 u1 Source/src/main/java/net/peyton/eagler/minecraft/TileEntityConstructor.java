package net.peyton.eagler.minecraft;

import net.minecraft.tileentity.TileEntity;

public interface TileEntityConstructor<T extends TileEntity> {
	T createTileEntity();
}