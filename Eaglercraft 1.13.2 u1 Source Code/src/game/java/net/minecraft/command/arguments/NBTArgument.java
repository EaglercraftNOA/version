package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

public class NBTArgument implements ArgumentType<NBTTagCompound> {
   private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");
   public static final DynamicCommandExceptionType NBT_INVALID = new DynamicCommandExceptionType((p_208664_0_) -> {
      return new TextComponentTranslation("argument.nbt.invalid", p_208664_0_);
   });

   public static NBTArgument nbt() {
      return new NBTArgument();
   }

   public static <S> NBTTagCompound getNBT(CommandContext<S> context, String name) {
      return context.getArgument(name, NBTTagCompound.class);
   }

   public NBTTagCompound parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (new JsonToNBT(p_parse_1_)).readStruct();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
