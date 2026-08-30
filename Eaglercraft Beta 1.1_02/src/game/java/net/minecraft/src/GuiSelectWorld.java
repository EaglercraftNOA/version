package net.minecraft.src;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.Minecraft;

public class GuiSelectWorld extends GuiScreen {
	protected GuiScreen parentScreen;
	protected String screenTitle = "Select world";
	private boolean selected = false;

	public GuiSelectWorld(GuiScreen var1) {
		this.parentScreen = var1;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.func_20162_a();
		this.screenTitle = var1.func_20163_a("selectWorld.title");
		String var2 = var1.func_20163_a("selectWorld.empty");
		String var3 = var1.func_20163_a("selectWorld.world");
		VFile2 var4 = Minecraft.getMinecraftDir();

		for(int var5 = 0; var5 < 5; ++var5) {
			NBTTagCompound var6 = World.func_629_a(var4, "World" + (var5 + 1));
			if(var6 == null) {
				this.controlList.add(new GuiButton(var5, this.width / 2 - 100, this.height / 6 + 24 * var5, "- " + var2 + " -"));
			} else {
				String var7 = var3 + " " + (var5 + 1);
				long var8 = var6.getLong("SizeOnDisk");
				var7 = var7 + " (" + (float)(var8 / 1024L * 100L / 1024L) / 100.0F + " MB)";
				this.controlList.add(new GuiButton(var5, this.width / 2 - 100, this.height / 6 + 24 * var5, var7));
			}
		}

		this.initGui2();
	}

	protected String getWorldName(int var1) {
		VFile2 var2 = Minecraft.getMinecraftDir();
		return World.func_629_a(var2, "World" + var1) != null ? "World" + var1 : null;
	}

	public void initGui2() {
		StringTranslate var1 = StringTranslate.func_20162_a();
		this.controlList.add(new GuiButton(5, this.width / 2 - 100, this.height / 6 + 120 + 12, var1.func_20163_a("selectWorld.delete")));
		this.controlList.add(new GuiButton(6, this.width / 2 - 100, this.height / 6 + 168, var1.func_20163_a("gui.cancel")));
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id < 5) {
				this.selectWorld(var1.id + 1);
			} else if(var1.id == 5) {
				this.mc.displayGuiScreen(new GuiDeleteWorld(this));
			} else if(var1.id == 6) {
				this.mc.displayGuiScreen(this.parentScreen);
			}

		}
	}

	public void selectWorld(int var1) {
		this.mc.displayGuiScreen((GuiScreen)null);
		if(!this.selected) {
			this.selected = true;
			this.mc.playerController = new PlayerControllerSP(this.mc);
			this.mc.func_6247_b("World" + var1);
			this.mc.displayGuiScreen((GuiScreen)null);
		}
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
		super.drawScreen(var1, var2, var3);
	}
}
