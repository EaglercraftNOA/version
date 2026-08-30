package net.peyton.eagler.resources;

import java.util.List;

import org.json.JSONObject;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.util.ResourceLocation;

public class AdvancementLoader {

	public static List<String> loadAdvancements() {
		List<String> list = Lists.newArrayList();

		String path = "/assets/minecraft/advancements/advancements.json";
		if (EagRuntime.getResourceExists(path)) {
			String json = EagRuntime.getResourceString(path);
			JSONObject jsonObj = new JSONObject(json);

			for (String s : JSONObject.getNames(jsonObj)) {
				list.add(s);
			}
		}
		return list;
	}

}
