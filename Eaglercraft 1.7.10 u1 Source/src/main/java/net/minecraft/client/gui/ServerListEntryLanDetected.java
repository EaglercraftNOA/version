package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.sp.lan.LANServerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
	private final GuiMultiplayer field_148289_a;
	private final LANServerList.LanServer field_148287_b;
	private final Minecraft field_148286_c = Minecraft.getMinecraft();
    private long field_148298_f = 0L;
	private static final String __OBFID = "CL_00000816";

	protected ServerListEntryLanDetected(GuiMultiplayer p_i45046_1_, LANServerList.LanServer p_i45046_2_) {
		this.field_148289_a = p_i45046_1_;
		this.field_148287_b = p_i45046_2_;
	}

	public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,
			Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
		this.field_148286_c.fontRenderer.drawString(I18n.format("lanServer.found", new Object[0]), p_148279_2_ + 32 + 3,
				p_148279_3_ + 1, 16777215);
		this.field_148286_c.fontRenderer.drawString(this.field_148287_b.getLanServerMotd(), p_148279_2_ + 32 + 3,
				p_148279_3_ + 12, 8421504);
	}

	public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
			int p_148278_6_) {
		this.field_148289_a.func_146790_a(p_148278_1_);
        
        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
			this.field_148289_a.func_146796_h();
		}
		this.field_148298_f = Minecraft.getSystemTime();
        
		return false;
	}

	public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
			int p_148277_6_) {
	}


    LANServerList.LanServer func_148285_a() {
        return field_148287_b;
    }
}
