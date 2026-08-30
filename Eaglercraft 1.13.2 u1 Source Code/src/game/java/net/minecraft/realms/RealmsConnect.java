package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted;

   public RealmsConnect(RealmsScreen onlineScreenIn) {
      this.onlineScreen = onlineScreenIn;
   }

   public void connect(final String p_connect_1_, final int p_connect_2_) {
      Realms.setConnectedToRealms(true);
      if (this.aborted) {
         return;
      }
      ServerData serverdata = new ServerData(p_connect_1_, p_connect_1_ + ":" + p_connect_2_, false);
      Minecraft.getInstance().displayGuiScreen(new GuiConnecting(this.onlineScreen.getProxy(), Minecraft.getInstance(), serverdata));
   }

   public void abort() {
      this.aborted = true;
      Realms.clearResourcePack();
   }

   public void tick() {
   }
}
