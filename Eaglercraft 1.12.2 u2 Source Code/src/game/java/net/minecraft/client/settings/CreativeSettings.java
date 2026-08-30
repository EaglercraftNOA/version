package net.minecraft.client.settings;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreativeSettings {
	private static final Logger field_192566_b = LogManager.getLogger();
	protected Minecraft field_192565_a;
	private final VFile2 field_192567_c;
	private final HotbarSnapshot[] field_192568_d = new HotbarSnapshot[9];

	public CreativeSettings(Minecraft p_i47395_1_, String p_i47395_2_) {
		this.field_192565_a = p_i47395_1_;
		this.field_192567_c = new VFile2(p_i47395_2_, "hotbar.nbt");

		for (int i = 0; i < 9; ++i) {
			this.field_192568_d[i] = new HotbarSnapshot();
		}

		this.func_192562_a();
	}

	public void func_192562_a() {
		try {
			byte[] data = this.field_192567_c.getAllBytes();
			if (data == null) {
				return;
			}
			NBTTagCompound nbttagcompound = CompressedStreamTools
					.read(new DataInputStream(new ByteArrayInputStream(data)));

			if (nbttagcompound == null) {
				return;
			}

			for (int i = 0; i < 9; ++i) {
				this.field_192568_d[i].func_192833_a(nbttagcompound.getTagList(String.valueOf(i), 10));
			}
		} catch (Exception exception) {
			field_192566_b.error("Failed to load creative mode options", (Throwable) exception);
		}
	}

	public void func_192564_b() {
		try {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			for (int i = 0; i < 9; ++i) {
				nbttagcompound.setTag(String.valueOf(i), this.field_192568_d[i].func_192834_a());
			}

			ByteArrayOutputStream data = new ByteArrayOutputStream();
			CompressedStreamTools.write(nbttagcompound, new DataOutputStream(data));
			this.field_192567_c.setAllBytes(data.toByteArray());
		} catch (Exception exception) {
			field_192566_b.error("Failed to save creative mode options", (Throwable) exception);
		}
	}

	public HotbarSnapshot func_192563_a(int p_192563_1_) {
		return this.field_192568_d[p_192563_1_];
	}
}