package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType DATA_ENTITY_INVALID = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.entity.invalid"));
   public static final DataCommand.IDataProvider DATA_PROVIDER = new DataCommand.IDataProvider() {
      public IDataAccessor createAccessor(CommandContext<CommandSource> context) throws CommandSyntaxException {
         return new EntityDataAccessor(EntityArgument.getEntity(context, "target"));
      }

      public ArgumentBuilder<CommandSource, ?> createArgument(ArgumentBuilder<CommandSource, ?> builder, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> action) {
         return builder.then(Commands.literal("entity").then(action.apply(Commands.argument("target", EntityArgument.entity()))));
      }
   };
   private final Entity entity;

   public EntityDataAccessor(Entity entityIn) {
      this.entity = entityIn;
   }

   public void mergeData(NBTTagCompound other) throws CommandSyntaxException {
      if (this.entity instanceof EntityPlayer) {
         throw DATA_ENTITY_INVALID.create();
      } else {
         UUID uuid = this.entity.getUniqueID();
         this.entity.read(other);
         this.entity.setUniqueId(uuid);
      }
   }

   public NBTTagCompound getData() {
      return NBTPredicate.writeToNBTWithSelectedItem(this.entity);
   }

   public ITextComponent getModifiedMessage() {
      return new TextComponentTranslation("commands.data.entity.modified", this.entity.getDisplayName());
   }

   public ITextComponent getQueryMessage(INBTBase nbt) {
      return new TextComponentTranslation("commands.data.entity.query", this.entity.getDisplayName(), nbt.toFormattedComponent());
   }

   public ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value) {
      return new TextComponentTranslation("commands.data.entity.get", pathIn, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), value);
   }
}
