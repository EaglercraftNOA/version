package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySign extends TileEntity implements ICommandSource {
   public final ITextComponent[] signText = new ITextComponent[]{new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
   public int lineBeingEdited = -1;
   private boolean isEditable = true;
   private EntityPlayer player;
   private final String[] renderText = new String[4];

   public TileEntitySign() {
      super(TileEntityType.SIGN);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);

      for(int i = 0; i < 4; ++i) {
         String s = ITextComponent.Serializer.toJson(this.signText[i]);
         compound.putString("Text" + (i + 1), s);
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      this.isEditable = false;
      super.read(compound);

      for(int i = 0; i < 4; ++i) {
         String s = compound.getString("Text" + (i + 1));
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
         if (this.world instanceof WorldServer) {
            try {
               this.signText[i] = TextComponentUtils.updateForEntity(this.getCommandSource((EntityPlayerMP)null), itextcomponent, (Entity)null);
            } catch (CommandSyntaxException var6) {
               this.signText[i] = itextcomponent;
            }
         } else {
            this.signText[i] = itextcomponent;
         }

         this.renderText[i] = null;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getText(int line) {
      return this.signText[line];
   }

   public void setText(int line, ITextComponent p_212365_2_) {
      this.signText[line] = p_212365_2_;
      this.renderText[line] = null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getRenderText(int line, Function<ITextComponent, String> p_212364_2_) {
      if (this.renderText[line] == null && this.signText[line] != null) {
         this.renderText[line] = p_212364_2_.apply(this.signText[line]);
      }

      return this.renderText[line];
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   @OnlyIn(Dist.CLIENT)
   public void setEditable(boolean isEditableIn) {
      this.isEditable = isEditableIn;
      if (!isEditableIn) {
         this.player = null;
      }

   }

   public void setPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(EntityPlayer playerIn) {
      for(ITextComponent itextcomponent : this.signText) {
         Style style = itextcomponent == null ? null : itextcomponent.getStyle();
         if (style != null && style.getClickEvent() != null) {
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               playerIn.getServer().getCommandManager().handleCommand(this.getCommandSource((EntityPlayerMP)playerIn), clickevent.getValue());
            }
         }
      }

      return true;
   }

   public void sendMessage(ITextComponent component) {
   }

   public CommandSource getCommandSource(@Nullable EntityPlayerMP playerIn) {
      String s = playerIn == null ? "Sign" : playerIn.getName().getString();
      ITextComponent itextcomponent = (ITextComponent)(playerIn == null ? new TextComponentString("Sign") : playerIn.getDisplayName());
      return new CommandSource(this, new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D), Vec2f.ZERO, (WorldServer)this.world, 2, s, itextcomponent, this.world.getServer(), playerIn);
   }

   public boolean shouldReceiveFeedback() {
      return false;
   }

   public boolean shouldReceiveErrors() {
      return false;
   }

   public boolean allowLogging() {
      return false;
   }
}
