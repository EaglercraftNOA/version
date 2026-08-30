package net.minecraft.command.impl.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public interface IDataAccessor {
   void mergeData(NBTTagCompound other) throws CommandSyntaxException;

   NBTTagCompound getData() throws CommandSyntaxException;

   ITextComponent getModifiedMessage();

   ITextComponent getQueryMessage(INBTBase nbt);

   ITextComponent getGetMessage(NBTPathArgument.NBTPath pathIn, double scale, int value);
}
