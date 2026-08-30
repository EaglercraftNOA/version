package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTreeManager implements IResourceManagerReloadListener {
   public static final SearchTreeManager.Key<ItemStack> ITEMS = new SearchTreeManager.Key<>();
   public static final SearchTreeManager.Key<RecipeList> RECIPES = new SearchTreeManager.Key<>();
   private final Map<SearchTreeManager.Key<?>, SearchTree<?>> trees = Maps.newHashMap();

   public void onResourceManagerReload(IResourceManager resourceManager) {
      for(SearchTree<?> searchtree : this.trees.values()) {
         searchtree.recalculate();
      }

   }

   public <T> void register(SearchTreeManager.Key<T> key, SearchTree<T> searchTreeIn) {
      this.trees.put(key, searchTreeIn);
   }

   public <T> ISearchTree<T> get(SearchTreeManager.Key<T> key) {
      return (ISearchTree<T>) this.trees.get(key);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }
}
