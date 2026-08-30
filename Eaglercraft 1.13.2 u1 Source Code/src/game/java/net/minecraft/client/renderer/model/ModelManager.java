package net.minecraft.client.renderer.model;

import java.util.Map;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager implements IResourceManagerReloadListener {
   private Map<ModelResourceLocation, IBakedModel> modelRegistry;
   private final TextureMap texMap;
   private final BlockModelShapes modelProvider;
   private IBakedModel defaultModel;

   public ModelManager(TextureMap textures) {
      this.texMap = textures;
      this.modelProvider = new BlockModelShapes(this);
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      ModelBakery modelbakery = new ModelBakery(resourceManager, this.texMap);
      this.modelRegistry = modelbakery.setupModelRegistry();
      this.defaultModel = this.modelRegistry.get(ModelBakery.MODEL_MISSING);
      this.modelProvider.reloadModels();
   }

   public IBakedModel getModel(ModelResourceLocation modelLocation) {
      return this.modelRegistry.getOrDefault(modelLocation, this.defaultModel);
   }

   public IBakedModel getMissingModel() {
      return this.defaultModel;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.modelProvider;
   }
}
