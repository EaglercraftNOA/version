package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultipartBakedModel implements IBakedModel {
   private final List<Pair<Predicate<IBlockState>, IBakedModel>> selectors;
   protected final boolean ambientOcclusion;
   protected final boolean gui3D;
   protected final TextureAtlasSprite particleTexture;
   protected final ItemCameraTransforms cameraTransforms;
   protected final ItemOverrideList overrides;
   private final Map<IBlockState, BitSet> field_210277_g = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());
   private final Random modelRandom = new net.lax1dude.eaglercraft.v1_8.Random();

   public MultipartBakedModel(List<Pair<Predicate<IBlockState>, IBakedModel>> p_i48273_1_) {
      this.selectors = p_i48273_1_;
      IBakedModel ibakedmodel = p_i48273_1_.iterator().next().getRight();
      this.ambientOcclusion = ibakedmodel.isAmbientOcclusion();
      this.gui3D = ibakedmodel.isGui3d();
      this.particleTexture = ibakedmodel.getParticleTexture();
      this.cameraTransforms = ibakedmodel.getItemCameraTransforms();
      this.overrides = ibakedmodel.getOverrides();
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand) {
      if (state == null) {
         return Collections.emptyList();
      } else {
         BitSet bitset = this.field_210277_g.get(state);
         if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.selectors.size(); ++i) {
               Pair<Predicate<IBlockState>, IBakedModel> pair = this.selectors.get(i);
               if (pair.getLeft().test(state)) {
                  bitset.set(i);
               }
            }

            this.field_210277_g.put(state, bitset);
         }

         List<BakedQuad> list = null;
         long k = rand.nextLong();
         Random random = this.modelRandom;

         for(int j = bitset.nextSetBit(0); j >= 0; j = bitset.nextSetBit(j + 1)) {
            random.setSeed(k);
            List<BakedQuad> list1 = this.selectors.get(j).getRight().getQuads(state, side, random);
            if (!list1.isEmpty()) {
               if (list == null) {
                  if (bitset.nextSetBit(j + 1) < 0) {
                     return list1;
                  }

                  list = Lists.newArrayList(list1);
               } else {
                  list.addAll(list1);
               }
            }
         }

         return list == null ? Collections.emptyList() : list;
      }
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3D;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particleTexture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<IBlockState>, IBakedModel>> selectors = Lists.newArrayList();

      public void putModel(Predicate<IBlockState> predicate, IBakedModel model) {
         this.selectors.add(Pair.of(predicate, model));
      }

      public IBakedModel build() {
         return new MultipartBakedModel(this.selectors);
      }
   }
}
