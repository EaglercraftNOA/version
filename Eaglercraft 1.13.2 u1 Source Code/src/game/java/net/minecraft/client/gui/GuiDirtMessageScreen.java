package net.minecraft.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDirtMessageScreen extends GuiScreen {
   private final String field_205029_a;

   public GuiDirtMessageScreen(String p_i48952_1_) {
      this.field_205029_a = p_i48952_1_;
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, this.field_205029_a, this.width / 2, 70, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }
}
