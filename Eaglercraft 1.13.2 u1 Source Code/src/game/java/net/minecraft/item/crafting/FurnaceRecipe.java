package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FurnaceRecipe implements IRecipe {
   private final ResourceLocation id;
   private final String group;
   private final Ingredient input;
   private final ItemStack output;
   private final float experience;
   private final int cookingTime;

   public FurnaceRecipe(ResourceLocation p_i48715_1_, String p_i48715_2_, Ingredient p_i48715_3_, ItemStack p_i48715_4_, float p_i48715_5_, int p_i48715_6_) {
      this.id = p_i48715_1_;
      this.group = p_i48715_2_;
      this.input = p_i48715_3_;
      this.output = p_i48715_4_;
      this.experience = p_i48715_5_;
      this.cookingTime = p_i48715_6_;
   }

   public boolean matches(IInventory inv, World worldIn) {
      return inv instanceof TileEntityFurnace && this.input.test(inv.getStackInSlot(0));
   }

   public ItemStack getCraftingResult(IInventory inv) {
      return this.output.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.SMELTING;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.input);
      return nonnulllist;
   }

   public float getExperience() {
      return this.experience;
   }

   public ItemStack getRecipeOutput() {
      return this.output;
   }

   @OnlyIn(Dist.CLIENT)
   public String getGroup() {
      return this.group;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public static class Serializer implements IRecipeSerializer<FurnaceRecipe> {
      public FurnaceRecipe read(ResourceLocation recipeId, JsonObject json) {
         String s = JsonUtils.getString(json, "group", "");
         Ingredient ingredient;
         if (JsonUtils.isJsonArray(json, "ingredient")) {
            ingredient = Ingredient.deserialize(JsonUtils.getJsonArray(json, "ingredient"));
         } else {
            ingredient = Ingredient.deserialize(JsonUtils.getJsonObject(json, "ingredient"));
         }

         String s1 = JsonUtils.getString(json, "result");
         Item item = IRegistry.ITEM.get(new ResourceLocation(s1));
         if (item != null) {
            ItemStack itemstack = new ItemStack(item);
            float lvt_8_1_ = JsonUtils.getFloat(json, "experience", 0.0F);
            int lvt_9_1_ = JsonUtils.getInt(json, "cookingtime", 200);
            return new FurnaceRecipe(recipeId, s, ingredient, itemstack, lvt_8_1_, lvt_9_1_);
         } else {
            throw new IllegalStateException(s1 + " did not exist");
         }
      }

      public FurnaceRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
         String s = buffer.readString(32767);
         Ingredient ingredient = Ingredient.read(buffer);
         ItemStack itemstack = buffer.readItemStack();
         float f = buffer.readFloat();
         int i = buffer.readVarInt();
         return new FurnaceRecipe(recipeId, s, ingredient, itemstack, f, i);
      }

      public void write(PacketBuffer buffer, FurnaceRecipe recipe) {
         buffer.writeString(recipe.group);
         recipe.input.write(buffer);
         buffer.writeItemStack(recipe.output);
         buffer.writeFloat(recipe.experience);
         buffer.writeVarInt(recipe.cookingTime);
      }

      public String getId() {
         return "smelting";
      }
   }
}
