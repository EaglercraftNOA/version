package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
   private final RecipeSerializers.SimpleSerializer<?> serializer;

   public CustomRecipeBuilder(RecipeSerializers.SimpleSerializer<?> serializerIn) {
      this.serializer = serializerIn;
   }

   public static CustomRecipeBuilder customRecipe(RecipeSerializers.SimpleSerializer<?> serializerIn) {
      return new CustomRecipeBuilder(serializerIn);
   }

   public void build(Consumer<IFinishedRecipe> consumerIn, final String id) {
      consumerIn.accept(new IFinishedRecipe() {
         public JsonObject getRecipeJson() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("type", CustomRecipeBuilder.this.serializer.getId());
            return jsonobject;
         }

         public ResourceLocation getID() {
            return new ResourceLocation(id);
         }

         @Nullable
         public JsonObject getAdvancementJson() {
            return null;
         }

         @Nullable
         public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
         }
      });
   }
}
