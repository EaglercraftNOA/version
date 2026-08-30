package net.minecraft.src;

class LogoEffectRandomizer {
	public double field_1312_a;
	public double field_1311_b;
	public double field_1314_c;
	final GuiMainMenu mainMenu;

	public LogoEffectRandomizer(GuiMainMenu var1, int var2, int var3) {
		this.mainMenu = var1;
		this.field_1312_a = this.field_1311_b = (double)(10 + var3) + GuiMainMenu.getRand().nextDouble() * 32.0D + (double)var2;
	}

	public void func_875_a() {
		this.field_1311_b = this.field_1312_a;
		if(this.field_1312_a > 0.0D) {
			this.field_1314_c -= 0.6D;
		}

		this.field_1312_a += this.field_1314_c;
		this.field_1314_c *= 0.9D;
		if(this.field_1312_a < 0.0D) {
			this.field_1312_a = 0.0D;
			this.field_1314_c = 0.0D;
		}

	}
}
