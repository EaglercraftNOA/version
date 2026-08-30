package net.minecraft.src;

public class RenderCow extends RenderLiving {
	public RenderCow(ModelBase var1, float var2) {
		super(var1, var2);
	}

	public void a(EntityCow var1, double var2, double var4, double var6, float var8, float var9) {
		super.a(var1, var2, var4, var6, var8, var9);
	}

	public void a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityCow)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityCow)var1, var2, var4, var6, var8, var9);
	}
}
