package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private NBTTagCompound hostPlayerData;

   public IntegratedPlayerList(IntegratedServer server) {
      super(server);
      this.setViewDistance(10);
   }

   protected void writePlayerData(EntityPlayerMP playerIn) {
      if (playerIn.getName().getString().equals(this.getServer().getServerOwner())) {
         this.hostPlayerData = playerIn.writeWithoutTypeId(new NBTTagCompound());
      }

      super.writePlayerData(playerIn);
   }

   public ITextComponent canPlayerLogin(EaglerSocketAddress p_206258_1_, GameProfile p_206258_2_) {
      return (ITextComponent)(p_206258_2_.getName().equalsIgnoreCase(this.getServer().getServerOwner()) && this.getPlayerByUsername(p_206258_2_.getName()) != null ? new TextComponentTranslation("multiplayer.disconnect.name_taken") : super.canPlayerLogin(p_206258_1_, p_206258_2_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public NBTTagCompound getHostPlayerData() {
      return this.hostPlayerData;
   }
}
