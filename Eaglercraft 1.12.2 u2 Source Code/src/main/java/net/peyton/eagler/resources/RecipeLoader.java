package net.peyton.eagler.resources;

import java.util.List;

import org.json.JSONObject;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.util.ResourceLocation;

public class RecipeLoader {

	/*
	 * Loads only the base recipes included in the games assets
	 */
	public static List<String> loadRecipes() {
		List<String> list = Lists.newArrayList();

		String path = "/assets/minecraft/recipes/eagler_recipe_loader/recipes.json";
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
