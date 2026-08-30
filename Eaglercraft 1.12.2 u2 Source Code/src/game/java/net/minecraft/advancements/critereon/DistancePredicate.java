package net.minecraft.advancements.critereon;

import javax.annotation.Nullable;

import org.json.JSONObject;

import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
	public static final DistancePredicate field_193423_a = new DistancePredicate(MinMaxBounds.field_192516_a,
			MinMaxBounds.field_192516_a, MinMaxBounds.field_192516_a, MinMaxBounds.field_192516_a,
			MinMaxBounds.field_192516_a);
	private final MinMaxBounds field_193424_b;
	private final MinMaxBounds field_193425_c;
	private final MinMaxBounds field_193426_d;
	private final MinMaxBounds field_193427_e;
	private final MinMaxBounds field_193428_f;

	public DistancePredicate(MinMaxBounds p_i47542_1_, MinMaxBounds p_i47542_2_, MinMaxBounds p_i47542_3_,
			MinMaxBounds p_i47542_4_, MinMaxBounds p_i47542_5_) {
		this.field_193424_b = p_i47542_1_;
		this.field_193425_c = p_i47542_2_;
		this.field_193426_d = p_i47542_3_;
		this.field_193427_e = p_i47542_4_;
		this.field_193428_f = p_i47542_5_;
	}

	public boolean func_193422_a(double p_193422_1_, double p_193422_3_, double p_193422_5_, double p_193422_7_,
			double p_193422_9_, double p_193422_11_) {
		float f = (float) (p_193422_1_ - p_193422_7_);
		float f1 = (float) (p_193422_3_ - p_193422_9_);
		float f2 = (float) (p_193422_5_ - p_193422_11_);

		if (this.field_193424_b.func_192514_a(MathHelper.abs(f))
				&& this.field_193425_c.func_192514_a(MathHelper.abs(f1))
				&& this.field_193426_d.func_192514_a(MathHelper.abs(f2))) {
			if (!this.field_193427_e.func_192513_a((double) (f * f + f2 * f2))) {
				return false;
			} else {
				return this.field_193428_f.func_192513_a((double) (f * f + f1 * f1 + f2 * f2));
			}
		} else {
			return false;
		}
	}

	public static DistancePredicate func_193421_a(@Nullable Object p_193421_0_) {
		if (p_193421_0_ != null) {
			JSONObject jsonobject = (JSONObject) p_193421_0_;
			MinMaxBounds minmaxbounds = MinMaxBounds.func_192515_a(jsonobject.getOptional("x"));
			MinMaxBounds minmaxbounds1 = MinMaxBounds.func_192515_a(jsonobject.getOptional("y"));
			MinMaxBounds minmaxbounds2 = MinMaxBounds.func_192515_a(jsonobject.getOptional("z"));
			MinMaxBounds minmaxbounds3 = MinMaxBounds.func_192515_a(jsonobject.getOptional("horizontal"));
			MinMaxBounds minmaxbounds4 = MinMaxBounds.func_192515_a(jsonobject.getOptional("absolute"));
			return new DistancePredicate(minmaxbounds, minmaxbounds1, minmaxbounds2, minmaxbounds3, minmaxbounds4);
		} else {
			return field_193423_a;
		}
	}
}
