package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IRecipeSerializer<T extends IRecipe> {
   T read(ResourceLocation recipeId, JsonObject json);

   T read(ResourceLocation recipeId, PacketBuffer buffer);

   void write(PacketBuffer buffer, T recipe);

   String getId();
}
