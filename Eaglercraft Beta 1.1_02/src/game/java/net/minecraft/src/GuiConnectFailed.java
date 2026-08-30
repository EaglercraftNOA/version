package net.minecraft.src;

public class GuiConnectFailed extends GuiScreen {
	private String errorMessage;
	private String errorDetail;

	public GuiConnectFailed(String var1, String var2, Object... var3) {
		StringTranslate var4 = StringTranslate.func_20162_a();
		this.errorMessage = var4.func_20163_a(var1);
		if(var3 != null) {
			this.errorDetail = var4.func_20160_a(var2, var3);
		} else {
			this.errorDetail = var4.func_20163_a(var2);
		}

	}

	public void updateScreen() {
	}

	protected void keyTyped(char var1, int var2) {
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.func_20162_a();
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.func_20163_a("gui.toMenu")));
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 16777215);
		this.drawCenteredString(this.fontRenderer, this.errorDetail, this.width / 2, this.height / 2 - 10, 16777215);
		super.drawScreen(var1, var2, var3);
	}
}
