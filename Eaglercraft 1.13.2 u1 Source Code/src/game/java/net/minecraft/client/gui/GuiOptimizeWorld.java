package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptimizeWorld extends GuiScreen {
   private static final Object2IntMap<DimensionType> PROGRESS_BAR_COLORS = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy()), (p_212346_0_) -> {
      p_212346_0_.put(DimensionType.OVERWORLD, -13408734);
      p_212346_0_.put(DimensionType.NETHER, -10075085);
      p_212346_0_.put(DimensionType.THE_END, -8943531);
      p_212346_0_.defaultReturnValue(-2236963);
   });
   private final GuiYesNoCallback field_212134_f;
   private final WorldOptimizer optimizer;

   public GuiOptimizeWorld(GuiYesNoCallback p_i49847_1_, String p_i49847_2_, ISaveFormat p_i49847_3_) {
      this.field_212134_f = p_i49847_1_;
      this.optimizer = new WorldOptimizer(p_i49847_2_, p_i49847_3_, p_i49847_3_.getWorldInfo(p_i49847_2_));
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 150, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptimizeWorld.this.optimizer.cancel();
            GuiOptimizeWorld.this.field_212134_f.confirmResult(false, 0);
         }
      });
   }

   public void tick() {
      if (this.optimizer.isFinished()) {
         this.field_212134_f.confirmResult(true, 0);
      }

   }

   public void onGuiClosed() {
      this.optimizer.cancel();
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("optimizeWorld.title", this.optimizer.getWorldName()), this.width / 2, 20, 16777215);
      int i = this.width / 2 - 150;
      int j = this.width / 2 + 150;
      int k = this.height / 4 + 100;
      int l = k + 10;
      this.drawCenteredString(this.fontRenderer, this.optimizer.getStatusText().getFormattedText(), this.width / 2, k - this.fontRenderer.FONT_HEIGHT - 2, 10526880);
      if (this.optimizer.getTotalChunks() > 0) {
         drawRect(i - 1, k - 1, j + 1, l + 1, -16777216);
         this.drawString(this.fontRenderer, I18n.format("optimizeWorld.info.converted", this.optimizer.getConverted()), i, 40, 10526880);
         this.drawString(this.fontRenderer, I18n.format("optimizeWorld.info.skipped", this.optimizer.getSkipped()), i, 40 + this.fontRenderer.FONT_HEIGHT + 3, 10526880);
         this.drawString(this.fontRenderer, I18n.format("optimizeWorld.info.total", this.optimizer.getTotalChunks()), i, 40 + (this.fontRenderer.FONT_HEIGHT + 3) * 2, 10526880);
         int i1 = 0;

         for(DimensionType dimensiontype : DimensionType.getAll()) {
            int j1 = MathHelper.floor(this.optimizer.func_212543_a(dimensiontype) * (float)(j - i));
            drawRect(i + i1, k, i + i1 + j1, l, PROGRESS_BAR_COLORS.getInt(dimensiontype));
            i1 += j1;
         }

         int k1 = this.optimizer.getConverted() + this.optimizer.getSkipped();
         this.drawCenteredString(this.fontRenderer, k1 + " / " + this.optimizer.getTotalChunks(), this.width / 2, k + 2 * this.fontRenderer.FONT_HEIGHT + 2, 10526880);
         this.drawCenteredString(this.fontRenderer, MathHelper.floor(this.optimizer.getTotalProgress() * 100.0F) + "%", this.width / 2, k + ((l - k) / 2 - this.fontRenderer.FONT_HEIGHT / 2), 10526880);
      }

      super.render(mouseX, mouseY, partialTicks);
   }
}
