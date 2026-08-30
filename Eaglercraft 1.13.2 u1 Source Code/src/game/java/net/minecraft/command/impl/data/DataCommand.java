package net.minecraft.command.impl.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class DataCommand {
   private static final SimpleCommandExceptionType NOTHING_CHANGED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((p_208922_0_) -> {
      return new TextComponentTranslation("commands.data.get.invalid", p_208922_0_);
   });
   private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((p_208919_0_) -> {
      return new TextComponentTranslation("commands.data.get.unknown", p_208919_0_);
   });
   public static final List<DataCommand.IDataProvider> DATA_PROVIDERS = Lists.newArrayList(EntityDataAccessor.DATA_PROVIDER, BlockDataAccessor.DATA_PROVIDER);

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("data").requires((p_198939_0_) -> {
         return p_198939_0_.hasPermissionLevel(2);
      });

      for(DataCommand.IDataProvider datacommand$idataprovider : DATA_PROVIDERS) {
         literalargumentbuilder.then(datacommand$idataprovider.createArgument(Commands.literal("merge"), (p_198943_1_) -> {
            return p_198943_1_.then(Commands.argument("nbt", NBTArgument.nbt()).executes((p_198936_1_) -> {
               return merge(p_198936_1_.getSource(), datacommand$idataprovider.createAccessor(p_198936_1_), NBTArgument.getNBT(p_198936_1_, "nbt"));
            }));
         })).then(datacommand$idataprovider.createArgument(Commands.literal("get"), (p_198940_1_) -> {
            return p_198940_1_.executes((p_198944_1_) -> {
               return get(p_198944_1_.getSource(), datacommand$idataprovider.createAccessor(p_198944_1_));
            }).then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198945_1_) -> {
               return get(p_198945_1_.getSource(), datacommand$idataprovider.createAccessor(p_198945_1_), NBTPathArgument.getNBTPath(p_198945_1_, "path"));
            }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_198935_1_) -> {
               return getScaled(p_198935_1_.getSource(), datacommand$idataprovider.createAccessor(p_198935_1_), NBTPathArgument.getNBTPath(p_198935_1_, "path"), DoubleArgumentType.getDouble(p_198935_1_, "scale"));
            })));
         })).then(datacommand$idataprovider.createArgument(Commands.literal("remove"), (p_198934_1_) -> {
            return p_198934_1_.then(Commands.argument("path", NBTPathArgument.nbtPath()).executes((p_198941_1_) -> {
               return remove(p_198941_1_.getSource(), datacommand$idataprovider.createAccessor(p_198941_1_), NBTPathArgument.getNBTPath(p_198941_1_, "path"));
            }));
         }));
      }

      dispatcher.register(literalargumentbuilder);
   }

   private static int remove(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = accessor.getData();
      NBTTagCompound nbttagcompound1 = nbttagcompound.copy();
      pathIn.remove(nbttagcompound);
      if (nbttagcompound1.equals(nbttagcompound)) {
         throw NOTHING_CHANGED.create();
      } else {
         accessor.mergeData(nbttagcompound);
         source.sendFeedback(accessor.getModifiedMessage(), true);
         return 1;
      }
   }

   private static int get(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn) throws CommandSyntaxException {
      INBTBase inbtbase = pathIn.get(accessor.getData());
      int i;
      if (inbtbase instanceof NBTPrimitive) {
         i = MathHelper.floor(((NBTPrimitive)inbtbase).getDouble());
      } else if (inbtbase instanceof NBTTagCollection) {
         i = ((NBTTagCollection)inbtbase).size();
      } else if (inbtbase instanceof NBTTagCompound) {
         i = ((NBTTagCompound)inbtbase).size();
      } else {
         if (!(inbtbase instanceof NBTTagString)) {
            throw GET_UNKNOWN_EXCEPTION.create(pathIn.toString());
         }

         i = ((NBTTagString)inbtbase).getString().length();
      }

      source.sendFeedback(accessor.getQueryMessage(inbtbase), false);
      return i;
   }

   private static int getScaled(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn, double scale) throws CommandSyntaxException {
      INBTBase inbtbase = pathIn.get(accessor.getData());
      if (!(inbtbase instanceof NBTPrimitive)) {
         throw GET_INVALID_EXCEPTION.create(pathIn.toString());
      } else {
         int i = MathHelper.floor(((NBTPrimitive)inbtbase).getDouble() * scale);
         source.sendFeedback(accessor.getGetMessage(pathIn, scale, i), false);
         return i;
      }
   }

   private static int get(CommandSource source, IDataAccessor accessor) throws CommandSyntaxException {
      source.sendFeedback(accessor.getQueryMessage(accessor.getData()), false);
      return 1;
   }

   private static int merge(CommandSource source, IDataAccessor accessor, NBTTagCompound nbt) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = accessor.getData();
      NBTTagCompound nbttagcompound1 = nbttagcompound.copy().merge(nbt);
      if (nbttagcompound.equals(nbttagcompound1)) {
         throw NOTHING_CHANGED.create();
      } else {
         accessor.mergeData(nbttagcompound1);
         source.sendFeedback(accessor.getModifiedMessage(), true);
         return 1;
      }
   }

   public interface IDataProvider {
      IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException;

      ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action);
   }
}
