package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMinecartCommandBlock extends EntityMinecart {
   private static final DataParameter<String> COMMAND = EntityDataManager.createKey(EntityMinecartCommandBlock.class, DataSerializers.STRING);
   private static final DataParameter<ITextComponent> LAST_OUTPUT = EntityDataManager.createKey(EntityMinecartCommandBlock.class, DataSerializers.TEXT_COMPONENT);
   private final CommandBlockBaseLogic commandBlockLogic = new EntityMinecartCommandBlock.MinecartCommandLogic();
   private int activatorRailCooldown;

   public EntityMinecartCommandBlock(World worldIn) {
      super(EntityType.COMMAND_BLOCK_MINECART, worldIn);
   }

   public EntityMinecartCommandBlock(World worldIn, double x, double y, double z) {
      super(EntityType.COMMAND_BLOCK_MINECART, worldIn, x, y, z);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(COMMAND, "");
      this.getDataManager().register(LAST_OUTPUT, new TextComponentString(""));
   }

   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.commandBlockLogic.read(compound);
      this.getDataManager().set(COMMAND, this.getCommandBlockLogic().getCommand());
      this.getDataManager().set(LAST_OUTPUT, this.getCommandBlockLogic().getLastOutput());
   }

   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      this.commandBlockLogic.write(compound);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.COMMAND_BLOCK;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.COMMAND_BLOCK.getDefaultState();
   }

   public CommandBlockBaseLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4) {
         this.getCommandBlockLogic().trigger(this.world);
         this.activatorRailCooldown = this.ticksExisted;
      }

   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      this.commandBlockLogic.tryOpenEditCommandBlock(player);
      return true;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (LAST_OUTPUT.equals(key)) {
         try {
            this.commandBlockLogic.setLastOutput(this.getDataManager().get(LAST_OUTPUT));
         } catch (Throwable var3) {
            ;
         }
      } else if (COMMAND.equals(key)) {
         this.commandBlockLogic.setCommand(this.getDataManager().get(COMMAND));
      }

   }

   public boolean ignoreItemEntityData() {
      return true;
   }

   public class MinecartCommandLogic extends CommandBlockBaseLogic {
      public WorldServer getWorld() {
         return (WorldServer)EntityMinecartCommandBlock.this.world;
      }

      public void updateCommand() {
         EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
         EntityMinecartCommandBlock.this.getDataManager().set(EntityMinecartCommandBlock.LAST_OUTPUT, this.getLastOutput());
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPositionVector() {
         return new Vec3d(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ);
      }

      @OnlyIn(Dist.CLIENT)
      public EntityMinecartCommandBlock getMinecart() {
         return EntityMinecartCommandBlock.this;
      }

      public CommandSource getCommandSource() {
         return new CommandSource(this, new Vec3d(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ), EntityMinecartCommandBlock.this.getPitchYaw(), this.getWorld(), 2, this.getName().getString(), EntityMinecartCommandBlock.this.getDisplayName(), this.getWorld().getServer(), EntityMinecartCommandBlock.this);
      }
   }
}
