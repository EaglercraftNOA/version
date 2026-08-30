package net.minecraft.client.util;

import org.lwjgl.opengl.GL11;

import java.util.Locale;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

public class JsonBlendingMode {
	private static JsonBlendingMode field_148118_a = null;
	private final int field_148116_b;
	private final int field_148117_c;
	private final int field_148114_d;
	private final int field_148115_e;
	private final int field_148112_f;
	private final boolean field_148113_g;
	private final boolean field_148119_h;
	private static final String __OBFID = "CL_00001038";

	private JsonBlendingMode(boolean p_i45084_1_, boolean p_i45084_2_, int p_i45084_3_, int p_i45084_4_,
			int p_i45084_5_, int p_i45084_6_, int p_i45084_7_) {
		this.field_148113_g = p_i45084_1_;
		this.field_148116_b = p_i45084_3_;
		this.field_148114_d = p_i45084_4_;
		this.field_148117_c = p_i45084_5_;
		this.field_148115_e = p_i45084_6_;
		this.field_148119_h = p_i45084_2_;
		this.field_148112_f = p_i45084_7_;
	}

	public JsonBlendingMode() {
		this(false, true, 1, 0, 1, 0, 32774);
	}

	public JsonBlendingMode(int p_i45085_1_, int p_i45085_2_, int p_i45085_3_) {
		this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
	}

	public JsonBlendingMode(int p_i45086_1_, int p_i45086_2_, int p_i45086_3_, int p_i45086_4_, int p_i45086_5_) {
		this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
	}

	public void func_148109_a() {
		if (!this.equals(field_148118_a)) {
			if (field_148118_a == null || this.field_148119_h != field_148118_a.func_148111_b()) {
				field_148118_a = this;

				if (this.field_148119_h) {
					GL11.glDisable(GL11.GL_BLEND);
					return;
				}

				GL11.glEnable(GL11.GL_BLEND);
			}

			EaglercraftGPU.glBlendEquation(this.field_148112_f);

			if (this.field_148113_g) {
				GlStateManager.tryBlendFuncSeparate(this.field_148116_b, this.field_148114_d, this.field_148117_c,
						this.field_148115_e);
			} else {
				GL11.glBlendFunc(this.field_148116_b, this.field_148114_d);
			}
		}
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof JsonBlendingMode)) {
			return false;
		} else {
			JsonBlendingMode var2 = (JsonBlendingMode) p_equals_1_;
			return this.field_148112_f != var2.field_148112_f ? false
					: (this.field_148115_e != var2.field_148115_e ? false
							: (this.field_148114_d != var2.field_148114_d ? false
									: (this.field_148119_h != var2.field_148119_h ? false
											: (this.field_148113_g != var2.field_148113_g ? false
													: (this.field_148117_c != var2.field_148117_c ? false
															: this.field_148116_b == var2.field_148116_b)))));
		}
	}

	public int hashCode() {
		int var1 = this.field_148116_b;
		var1 = 31 * var1 + this.field_148117_c;
		var1 = 31 * var1 + this.field_148114_d;
		var1 = 31 * var1 + this.field_148115_e;
		var1 = 31 * var1 + this.field_148112_f;
		var1 = 31 * var1 + (this.field_148113_g ? 1 : 0);
		var1 = 31 * var1 + (this.field_148119_h ? 1 : 0);
		return var1;
	}

	public boolean func_148111_b() {
		return this.field_148119_h;
	}

	public static JsonBlendingMode parseBlendNode(org.json.JSONObject json) {
		if (json == null) {
			return new JsonBlendingMode();
		} else {
			int i = 32774;
			int j = 1;
			int k = 0;
			int l = 1;
			int i1 = 0;
			boolean flag = true;
			boolean flag1 = false;

			if (json.has("func") && json.get("func") instanceof String) {
				i = stringToBlendFunction((String) json.get("func"));
				if (i != 32774) {
					flag = false;
				}
			}

			if (json.has("srcrgb") && json.get("srcrgb") instanceof String) {
				j = stringToBlendFactor((String) json.get("srcrgb"));
				if (j != 1) {
					flag = false;
				}
			}

			if (json.has("dstrgb") && json.get("dstrgb") instanceof String) {
				k = stringToBlendFactor((String) json.get("dstrgb"));
				if (k != 0) {
					flag = false;
				}
			}

			if (json.has("srcalpha") && json.get("srcalpha") instanceof String) {
				l = stringToBlendFactor((String) json.get("srcalpha"));
				if (l != 1) {
					flag = false;
				}
				flag1 = true;
			}

			if (json.has("dstalpha") && json.get("dstalpha") instanceof String) {
				i1 = stringToBlendFactor((String) json.get("dstalpha"));
				if (i1 != 0) {
					flag = false;
				}
				flag1 = true;
			}

			if (flag) {
				return new JsonBlendingMode();
			} else {
				return flag1 ? new JsonBlendingMode(j, k, l, i1, i) : new JsonBlendingMode(j, k, i);
			}
		}
	}

	private static int stringToBlendFunction(String s) {
		s = s.trim().toLowerCase(Locale.ROOT);
		if ("add".equals(s)) {
			return 32774;
		} else if ("subtract".equals(s)) {
			return 32778;
		} else if ("reversesubtract".equals(s)) {
			return 32779;
		} else if ("reverse_subtract".equals(s)) {
			return 32779;
		} else if ("min".equals(s)) {
			return 32775;
		} else {
			return "max".equals(s) ? 32776 : 32774;
		}
	}

	private static int stringToBlendFactor(String s) {
		s = s.trim().toLowerCase(Locale.ROOT);
		s = s.replaceAll("_", "");
		s = s.replaceAll("one", "1");
		s = s.replaceAll("zero", "0");
		s = s.replaceAll("minus", "-");
		if ("0".equals(s)) {
			return 0;
		} else if ("1".equals(s)) {
			return 1;
		} else if ("srccolor".equals(s)) {
			return 768;
		} else if ("1-srccolor".equals(s)) {
			return 769;
		} else if ("dstcolor".equals(s)) {
			return 774;
		} else if ("1-dstcolor".equals(s)) {
			return 775;
		} else if ("srcalpha".equals(s)) {
			return 770;
		} else if ("1-srcalpha".equals(s)) {
			return 771;
		} else if ("dstalpha".equals(s)) {
			return 772;
		} else {
			return "1-dstalpha".equals(s) ? 773 : -1;
		}
	}

}
