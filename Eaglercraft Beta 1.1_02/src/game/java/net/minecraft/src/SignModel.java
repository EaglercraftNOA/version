package net.minecraft.src;

public class SignModel {
	public ModelRenderer field_1346_a = new ModelRenderer(0, 0);
	public ModelRenderer field_1345_b;

	public SignModel() {
		this.field_1346_a.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
		this.field_1345_b = new ModelRenderer(0, 14);
		this.field_1345_b.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
	}

	public void func_887_a() {
		this.field_1346_a.render(1.0F / 16.0F);
		this.field_1345_b.render(1.0F / 16.0F);
	}
}
