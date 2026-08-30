package net.peyton.eagler.minecraft.suppliers;

import net.minecraft.src.TileEntity;

public interface TileEntitySupplier<T extends TileEntity> {
	T createTileEntity();
}