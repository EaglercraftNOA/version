package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class Criterion {
	private final ICriterionInstance field_192147_a;

	public Criterion(ICriterionInstance p_i47470_1_) {
		this.field_192147_a = p_i47470_1_;
	}

	public Criterion() {
		this.field_192147_a = null;
	}

	public void func_192140_a(PacketBuffer p_192140_1_) {
	}

	public static Criterion func_192145_a(JSONObject p_192145_0_) {
		ResourceLocation resourcelocation = new ResourceLocation(p_192145_0_.getString("trigger"));
		ICriterionTrigger<?> icriteriontrigger = CriteriaTriggers.func_192119_a(resourcelocation);

		if (icriteriontrigger == null) {
			throw new JSONException("Invalid criterion trigger: " + resourcelocation);
		} else {
			ICriterionInstance icriterioninstance = icriteriontrigger
					.func_192166_a(p_192145_0_.optJSONObject("conditions", new JSONObject()));
			return new Criterion(icriterioninstance);
		}
	}

	public static Criterion func_192146_b(PacketBuffer p_192146_0_) {
		return new Criterion();
	}

	public static Map<String, Criterion> func_192144_b(JSONObject p_192144_0_) {
		Map<String, Criterion> map = Maps.<String, Criterion>newHashMap();

		for (Entry<String, Object> entry : p_192144_0_.entrySet()) {
			map.put(entry.getKey(), func_192145_a((JSONObject) entry.getValue()));
		}

		return map;
	}

	public static Map<String, Criterion> func_192142_c(PacketBuffer p_192142_0_) {
		Map<String, Criterion> map = Maps.<String, Criterion>newHashMap();
		int i = p_192142_0_.readVarIntFromBuffer();

		for (int j = 0; j < i; ++j) {
			map.put(p_192142_0_.readStringFromBuffer(32767), func_192146_b(p_192142_0_));
		}

		return map;
	}

	public static void func_192141_a(Map<String, Criterion> p_192141_0_, PacketBuffer p_192141_1_) {
		p_192141_1_.writeVarIntToBuffer(p_192141_0_.size());

		for (Entry<String, Criterion> entry : p_192141_0_.entrySet()) {
			p_192141_1_.writeString(entry.getKey());
			((Criterion) entry.getValue()).func_192140_a(p_192141_1_);
		}
	}

	@Nullable
	public ICriterionInstance func_192143_a() {
		return this.field_192147_a;
	}
}
