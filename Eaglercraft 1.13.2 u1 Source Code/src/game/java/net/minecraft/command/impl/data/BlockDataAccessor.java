package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_BLOCK_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.block.invalid"));
   public static final DataCommand.IDataProvider DATA_PROVIDER = new DataCommand.IDataProvider() {
      public IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException {
         BlockPos blockpos = BlockPosArgument.getLoadedBlockPos(context, "pos");
         TileEntity tileentity = context.getSource().getWorld().getTileEntity(blockpos);
         if (tileentity == null) {
            throw BlockDataAccessor.DATA_BLOCK_INVALID_EXCEPTION.create();
         } else {
            return new BlockDataAccessor(tileentity, blockpos);
         }
      }

      public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action) {
         return builder.then(Commands.literal("block").then(action.apply(Commands.argument("pos", BlockPosArgument.blockPos()))));
      }
   };
   private final TileEntity tileEntity;
   private final BlockPos pos;

   public BlockDataAccessor(TileEntity tileEntityIn, BlockPos posIn) {
      this.tileEntity = tileEntityIn;
      this.pos = posIn;
   }

   public void mergeData(NBTTagCompound other) {
      other.putInt("x", this.pos.getX());
      other.putInt("y", this.pos.getY());
      other.putInt("z", this.pos.getZ());
      this.tileEntity.read(other);
      this.tileEntity.markDirty();
      IBlockState iblockstate = this.tileEntity.getWorld().getBlockState(this.pos);
      this.tileEntity.getWorld().notifyBlockUpdate(this.pos, iblockstate, iblockstate, 3);
   }

   public NBTTagCompound getData() {
      return this.tileEntity.write(new NBTTagCompound());
   }

   public ITextComponent getModifiedMessage() {
      return new TextComponentTranslation("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public ITextComponent getQueryMessage(INBTBase nbt) {
      return new TextComponentTranslation("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), nbt.toFormattedComponent());
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value) {
      return new TextComponentTranslation("commands.data.block.get", pathIn, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), value);
   }
}
