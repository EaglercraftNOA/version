package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

public class Base64 {
	public static ByteBuf decode(ByteBuf var2) {
		int var3 = var2.readableBytes();
		byte[] var4 = new byte[var3];
		var2.readBytes(var4);
		String var5 = new String(var4, Charsets.UTF_8);
		byte[] var6 = java.util.Base64.getDecoder().decode(var5);
		ByteBuf var7 = io.netty.buffer.Unpooled.wrappedBuffer(var6);
		return var7;
	}
}
