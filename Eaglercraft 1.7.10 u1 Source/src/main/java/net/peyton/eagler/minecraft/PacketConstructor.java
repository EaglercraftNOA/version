package net.peyton.eagler.minecraft;

import net.minecraft.network.Packet;

public interface PacketConstructor<T extends Packet> {
	T createPacket();
}
