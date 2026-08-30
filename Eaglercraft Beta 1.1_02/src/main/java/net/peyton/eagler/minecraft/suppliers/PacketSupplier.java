package net.peyton.eagler.minecraft.suppliers;

import net.minecraft.src.Packet;

public interface PacketSupplier<T extends Packet> {
	T createPacket();
}
