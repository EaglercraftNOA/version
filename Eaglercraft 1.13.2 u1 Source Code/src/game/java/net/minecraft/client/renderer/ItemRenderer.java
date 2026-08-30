package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> ITEM_MODEL_BLACKLIST = Sets.newHashSet(Items.AIR);
   private static final EnumFacing[] facings = EnumFacing._VALUES;
   public float zLevel;
   private final ItemModelMesher itemModelMesher;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final Random random = new net.lax1dude.eaglercraft.v1_8.Random();

   public ItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
      this.textureManager = textureManagerIn;
      this.itemModelMesher = new ItemModelMesher(modelManagerIn);

      for(Item item : IRegistry.ITEM) {
         if (!ITEM_MODEL_BLACKLIST.contains(item)) {
            this.itemModelMesher.register(item, new ModelResourceLocation(IRegistry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = itemColorsIn;
   }

   public ItemModelMesher getItemModelMesher() {
      return this.itemModelMesher;
   }

   private void renderModel(IBakedModel model, ItemStack stack) {
      this.renderModel(model, -1, stack);
   }

   private void renderModel(IBakedModel model, int color) {
      this.renderModel(model, color, ItemStack.EMPTY);
   }

   private void renderModel(IBakedModel model, int color, ItemStack stack) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
      long i = 42L;

      for(EnumFacing enumfacing : facings) {
         this.random.setSeed(42L);
         this.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, enumfacing, this.random), color, stack);
      }

      this.random.setSeed(42L);
      this.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, (EnumFacing)null, this.random), color, stack);
      tessellator.draw();
   }

   public void renderItem(ItemStack stack, IBakedModel model) {
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         if (model.isBuiltInRenderer()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(stack);
         } else {
            this.renderModel(model, stack);
            if (stack.hasEffect()) {
               this.renderModelEffect(model);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   private void renderModelEffect(IBakedModel model) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      this.textureManager.bindTexture(RES_ITEM_GLINT);
      GlStateManager.matrixMode(5890);
      long i = Util.milliTime();
      GlStateManager.pushMatrix();
      GlStateManager.scalef(8.0F, 8.0F, 8.0F);
      float f = (float)(i % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translatef(f, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      this.renderModel(model, -8372020);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef(8.0F, 8.0F, 8.0F);
      float f1 = (float)(i % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translatef(-f1, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      this.renderModel(model, -8372020);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void renderEffect(TextureManager textureManagerIn, Runnable renderModelFunction, int scale) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      textureManagerIn.bindTexture(RES_ITEM_GLINT);
      GlStateManager.matrixMode(5890);
      long i = Util.milliTime();
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)scale, (float)scale, (float)scale);
      float f = (float)(i % 3000L) / 3000.0F / (float)scale;
      GlStateManager.translatef(f, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      renderModelFunction.run();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)scale, (float)scale, (float)scale);
      float f1 = (float)(i % 4873L) / 4873.0F / (float)scale;
      GlStateManager.translatef(-f1, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      renderModelFunction.run();
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      textureManagerIn.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void putQuadNormal(BufferBuilder renderer, BakedQuad quad) {
      Vec3i vec3i = quad.getFace().getDirectionVec();
      renderer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
   }

   private void renderQuad(BufferBuilder renderer, BakedQuad quad, int color) {
      renderer.addVertexData(quad.getVertexData());
      renderer.putColor4(color);
      this.putQuadNormal(renderer, quad);
   }

   private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
      boolean flag = color == -1 && !stack.isEmpty();
      int i = 0;

      for(int j = quads.size(); i < j; ++i) {
         BakedQuad bakedquad = quads.get(i);
         int k = color;
         if (flag && bakedquad.hasTintIndex()) {
            k = this.itemColors.getColor(stack, bakedquad.getTintIndex());
            k = k | -16777216;
         }

         this.renderQuad(renderer, bakedquad, k);
      }

   }

   public boolean shouldRenderItemIn3D(ItemStack stack) {
      IBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);
      return ibakedmodel == null ? false : ibakedmodel.isGui3d();
   }

   public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType cameraTransformType) {
      if (!stack.isEmpty()) {
         IBakedModel ibakedmodel = this.getModelWithOverrides(stack);
         this.renderItemModel(stack, ibakedmodel, cameraTransformType, false);
      }
   }

   public IBakedModel getItemModelWithOverrides(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entitylivingbaseIn) {
      IBakedModel ibakedmodel = this.itemModelMesher.getItemModel(stack);
      Item item = stack.getItem();
      return !item.hasCustomProperties() ? ibakedmodel : this.getModelWithOverrides(ibakedmodel, stack, worldIn, entitylivingbaseIn);
   }

   public IBakedModel getModelWithOverrides(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
      Item item = stack.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelMesher.getItemModel(stack);
      }

      return !item.hasCustomProperties() ? ibakedmodel : this.getModelWithOverrides(ibakedmodel, stack, worldIn, entityIn);
   }

   public IBakedModel getModelWithOverrides(ItemStack stack) {
      return this.getItemModelWithOverrides(stack, (World)null, (EntityLivingBase)null);
   }

   private IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
      IBakedModel ibakedmodel = model.getOverrides().getModelWithOverrides(model, stack, worldIn, entityIn);
      return ibakedmodel == null ? this.itemModelMesher.getModelManager().getMissingModel() : ibakedmodel;
   }

   public void renderItem(ItemStack stack, EntityLivingBase entitylivingbaseIn, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
      if (!stack.isEmpty() && entitylivingbaseIn != null) {
         IBakedModel ibakedmodel = this.getModelWithOverrides(stack, entitylivingbaseIn.world, entitylivingbaseIn);
         this.renderItemModel(stack, ibakedmodel, transform, leftHanded);
      }
   }

   protected void renderItemModel(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
      if (!stack.isEmpty()) {
         GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
         GlStateManager.enableTexture2D();
         this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         ItemCameraTransforms itemcameratransforms = bakedmodel.getItemCameraTransforms();
         ItemCameraTransforms.applyTransformSide(itemcameratransforms.getTransform(transform), leftHanded);
         if (this.isThereOneNegativeScale(itemcameratransforms.getTransform(transform))) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }

         this.renderItem(stack, bakedmodel);
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      }
   }

   private boolean isThereOneNegativeScale(ItemTransformVec3f itemTranformVec) {
      return itemTranformVec.scale.getX() < 0.0F ^ itemTranformVec.scale.getY() < 0.0F ^ itemTranformVec.scale.getZ() < 0.0F;
   }

   public void renderItemIntoGUI(ItemStack stack, int x, int y) {
      this.renderItemModelIntoGUI(stack, x, y, this.getModelWithOverrides(stack));
   }

   protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
      GlStateManager.pushMatrix();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      GlStateManager.enableTexture2D();
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.clear(256);
      if (bakedmodel.isBuiltInRenderer()) {
         GlStateManager.disableCull();
      }

      this.setupGuiTransform(x, y, bakedmodel.isGui3d());
      bakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
      this.renderItem(stack, bakedmodel);
      GlStateManager.enableCull();
      GlStateManager.enableDepthTest();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }

   private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
      GlStateManager.translatef((float)xPosition, (float)yPosition, 100.0F + this.zLevel);
      GlStateManager.translatef(8.0F, 8.0F, 0.0F);
      GlStateManager.scalef(1.0F, -1.0F, 1.0F);
      GlStateManager.scalef(16.0F, 16.0F, 16.0F);
      if (isGui3d) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }

   public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
      this.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, stack, xPosition, yPosition);
   }

   public void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase entityIn, ItemStack itemIn, int x, int y) {
      if (!itemIn.isEmpty()) {
         this.zLevel += 50.0F;

         try {
            this.renderItemModelIntoGUI(itemIn, x, y, this.getItemModelWithOverrides(itemIn, (World)null, entityIn));
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> {
               return String.valueOf((Object)itemIn.getItem());
            });
            crashreportcategory.addDetail("Item Damage", () -> {
               return String.valueOf(itemIn.getDamage());
            });
            crashreportcategory.addDetail("Item NBT", () -> {
               return String.valueOf((Object)itemIn.getTag());
            });
            crashreportcategory.addDetail("Item Foil", () -> {
               return String.valueOf(itemIn.hasEffect());
            });
            throw new ReportedException(crashreport);
         }

         this.zLevel -= 50.0F;
      }
   }

   public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
      this.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, (String)null);
   }

   public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
      if (!stack.isEmpty()) {
         if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            fr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         if (stack.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            float f = (float)stack.getDamage();
            float f1 = (float)stack.getMaxDamage();
            float f2 = Math.max(0.0F, (f1 - f) / f1);
            int i = Math.round(13.0F - f * 13.0F / f1);
            int j = MathHelper.hsvToRGB(f2 / 3.0F, 1.0F, 1.0F);
            this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
            this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
         float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getInstance().getRenderPartialTicks());
         if (f3 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture2D();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            this.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

      }
   }

   private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
      renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.itemModelMesher.rebuildCache();
   }
}
