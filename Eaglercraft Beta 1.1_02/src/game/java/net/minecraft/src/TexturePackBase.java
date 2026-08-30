package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.client.Minecraft;

public abstract class TexturePackBase {
	public String texturePackFileName;
	public String firstDescriptionLine;
	public String secondDescriptionLine;
	public String field_6488_d;

	public void func_6482_a() {
	}

	public void closeTexturePackFile() {
	}

	public void func_6485_a(Minecraft var1) throws IOException {
	}

	public void func_6484_b(Minecraft var1) {
	}

	public void func_6483_c(Minecraft var1) {
	}

	public InputStream func_6481_a(String var1) {
		return EagRuntime.getRequiredResourceStream(var1);
	}
}
