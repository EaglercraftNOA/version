package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenAlert extends GuiScreen {
   private final Runnable field_201552_h;
   protected final ITextComponent field_201548_a;
   protected final ITextComponent field_201550_f;
   private final List<String> field_201553_i = Lists.newArrayList();
   protected String field_201551_g;
   private int field_201549_s;

   public GuiScreenAlert(Runnable p_i48623_1_, ITextComponent p_i48623_2_, ITextComponent p_i48623_3_) {
      this(p_i48623_1_, p_i48623_2_, p_i48623_3_, "gui.back");
   }

   public GuiScreenAlert(Runnable p_i49786_1_, ITextComponent p_i49786_2_, ITextComponent p_i49786_3_, String p_i49786_4_) {
      this.field_201552_h = p_i49786_1_;
      this.field_201548_a = p_i49786_2_;
      this.field_201550_f = p_i49786_3_;
      this.field_201551_g = I18n.format(p_i49786_4_);
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 168, this.field_201551_g) {
         public void onClick(double mouseX, double mouseY) {
            GuiScreenAlert.this.field_201552_h.run();
         }
      });
      this.field_201553_i.clear();
      this.field_201553_i.addAll(this.fontRenderer.listFormattedStringToWidth(this.field_201550_f.getFormattedText(), this.width - 50));
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.field_201548_a.getFormattedText(), this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.field_201553_i) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   public void tick() {
      super.tick();
      if (--this.field_201549_s == 0) {
         for(GuiButton guibutton : this.buttons) {
            guibutton.enabled = true;
         }
      }

   }
}
