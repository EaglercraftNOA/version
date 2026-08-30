package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShapelessRecipe implements IRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack recipeOutput;
   private final NonNullList<Ingredient> recipeItems;

   public ShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
      this.id = idIn;
      this.group = groupIn;
      this.recipeOutput = recipeOutputIn;
      this.recipeItems = recipeItemsIn;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SHAPELESS;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGroup() {
      return this.group;
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
         int i = 0;

         for(int j = 0; j < inv.getHeight(); ++j) {
            for(int k = 0; k < inv.getWidth(); ++k) {
               ItemStack itemstack = inv.getStackInSlot(k + j * inv.getWidth());
               if (!itemstack.isEmpty()) {
                  ++i;
                  recipeitemhelper.accountStack(new ItemStack(itemstack.getItem()));
               }
            }
         }

         return i == this.recipeItems.size() && recipeitemhelper.canCraft(this, (IntList)null);
      }
   }

   public ItemStack getCraftingResult(IInventory inv) {
      return this.recipeOutput.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return width * height >= this.recipeItems.size();
   }

   public static class Serializer implements IRecipeSerializer<ShapelessRecipe> {
      public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JsonUtils.getString(json, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(JsonUtils.getJsonArray(json, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemstack = ShapedRecipe.deserializeItem(JsonUtils.getJsonObject(json, "result"));
            return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for(int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
            if (!ingredient.hasNoMatchingItems()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public String getId() {
         return "crafting_shapeless";
      }

      public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         int i = buffer.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for(int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.read(buffer));
         }

         ItemStack itemstack = buffer.readItemStack();
         return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
      }

      public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
         buffer.writeString(recipe.group);
         buffer.writeVarInt(recipe.recipeItems.size());

         for(Ingredient ingredient : recipe.recipeItems) {
            ingredient.write(buffer);
         }

         buffer.writeItemStack(recipe.recipeOutput);
      }
   }
}
