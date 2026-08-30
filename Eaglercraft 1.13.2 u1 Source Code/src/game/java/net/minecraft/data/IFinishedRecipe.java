package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface IFinishedRecipe {
   JsonObject getRecipeJson();

   ResourceLocation getID();

   @Nullable
   JsonObject getAdvancementJson();

   @Nullable
   ResourceLocation getAdvancementID();
}
