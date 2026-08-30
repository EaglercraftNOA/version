package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class TitleCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("title").requires((p_198847_0_) -> {
         return p_198847_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((p_198838_0_) -> {
         return clear(p_198838_0_.getSource(), EntityArgument.getPlayers(p_198838_0_, "targets"));
      })).then(Commands.literal("reset").executes((p_198841_0_) -> {
         return reset(p_198841_0_.getSource(), EntityArgument.getPlayers(p_198841_0_, "targets"));
      })).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.component()).executes((p_198837_0_) -> {
         return show(p_198837_0_.getSource(), EntityArgument.getPlayers(p_198837_0_, "targets"), ComponentArgument.getComponent(p_198837_0_, "title"), SPacketTitle.Type.TITLE);
      }))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.component()).executes((p_198842_0_) -> {
         return show(p_198842_0_.getSource(), EntityArgument.getPlayers(p_198842_0_, "targets"), ComponentArgument.getComponent(p_198842_0_, "title"), SPacketTitle.Type.SUBTITLE);
      }))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.component()).executes((p_198836_0_) -> {
         return show(p_198836_0_.getSource(), EntityArgument.getPlayers(p_198836_0_, "targets"), ComponentArgument.getComponent(p_198836_0_, "title"), SPacketTitle.Type.ACTIONBAR);
      }))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((p_198843_0_) -> {
         return setTimes(p_198843_0_.getSource(), EntityArgument.getPlayers(p_198843_0_, "targets"), IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"), IntegerArgumentType.getInteger(p_198843_0_, "stay"), IntegerArgumentType.getInteger(p_198843_0_, "fadeOut"));
      })))))));
   }

   private static int clear(CommandSource source, Collection<EntityPlayerMP> targets) {
      SPacketTitle spackettitle = new SPacketTitle(SPacketTitle.Type.CLEAR, (ITextComponent)null);

      for(EntityPlayerMP entityplayermp : targets) {
         entityplayermp.connection.sendPacket(spackettitle);
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.title.cleared.single", targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.title.cleared.multiple", targets.size()), true);
      }

      return targets.size();
   }

   private static int reset(CommandSource source, Collection<EntityPlayerMP> targets) {
      SPacketTitle spackettitle = new SPacketTitle(SPacketTitle.Type.RESET, (ITextComponent)null);

      for(EntityPlayerMP entityplayermp : targets) {
         entityplayermp.connection.sendPacket(spackettitle);
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.title.reset.single", targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.title.reset.multiple", targets.size()), true);
      }

      return targets.size();
   }

   private static int show(CommandSource source, Collection<EntityPlayerMP> targets, ITextComponent message, SPacketTitle.Type type) throws CommandSyntaxException {
      for(EntityPlayerMP entityplayermp : targets) {
         entityplayermp.connection.sendPacket(new SPacketTitle(type, TextComponentUtils.updateForEntity(source, message, entityplayermp)));
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", targets.size()), true);
      }

      return targets.size();
   }

   private static int setTimes(CommandSource source, Collection<EntityPlayerMP> target, int fadeIn, int stay, int fadeOut) {
      SPacketTitle spackettitle = new SPacketTitle(fadeIn, stay, fadeOut);

      for(EntityPlayerMP entityplayermp : target) {
         entityplayermp.connection.sendPacket(spackettitle);
      }

      if (target.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.title.times.single", target.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.title.times.multiple", target.size()), true);
      }

      return target.size();
   }
}
