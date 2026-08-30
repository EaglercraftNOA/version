package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   void sendMessage(ITextComponent component);

   boolean shouldReceiveFeedback();

   boolean shouldReceiveErrors();

   boolean allowLogging();
}
