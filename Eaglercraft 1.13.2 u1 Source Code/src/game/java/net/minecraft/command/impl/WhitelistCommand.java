package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.server.management.UserListWhitelistEntry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ALREADY_ON = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOn"));
   private static final SimpleCommandExceptionType ALREADY_OFF = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOff"));
   private static final SimpleCommandExceptionType PLAYER_ALREADY_WHITELISTED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.add.failed"));
   private static final SimpleCommandExceptionType PLAYER_NOT_WHITELISTED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.remove.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("whitelist").requires((p_198877_0_) -> {
         return p_198877_0_.hasPermissionLevel(3);
      }).then(Commands.literal("on").executes((p_198872_0_) -> {
         return enableWhiteList(p_198872_0_.getSource());
      })).then(Commands.literal("off").executes((p_198874_0_) -> {
         return disableWhiteList(p_198874_0_.getSource());
      })).then(Commands.literal("list").executes((p_198878_0_) -> {
         return listWhitelistedPlayers(p_198878_0_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198879_0_, p_198879_1_) -> {
         PlayerList playerlist = p_198879_0_.getSource().getServer().getPlayerList();
         return ISuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_198871_1_) -> {
            return !playerlist.getWhitelistedPlayers().isWhitelisted(p_198871_1_.getGameProfile());
         }).map((p_200567_0_) -> {
            return p_200567_0_.getGameProfile().getName();
         }), p_198879_1_);
      }).executes((p_198875_0_) -> {
         return addPlayers(p_198875_0_.getSource(), GameProfileArgument.getGameProfiles(p_198875_0_, "targets"));
      }))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198881_0_, p_198881_1_) -> {
         return ISuggestionProvider.suggest(p_198881_0_.getSource().getServer().getPlayerList().getWhitelistedPlayerNames(), p_198881_1_);
      }).executes((p_198870_0_) -> {
         return removePlayers(p_198870_0_.getSource(), GameProfileArgument.getGameProfiles(p_198870_0_, "targets"));
      }))).then(Commands.literal("reload").executes((p_198882_0_) -> {
         return reload(p_198882_0_.getSource());
      })));
   }

   private static int reload(CommandSource source) {
      source.getServer().getPlayerList().reloadWhitelist();
      source.sendFeedback(new TextComponentTranslation("commands.whitelist.reloaded"), true);
      source.getServer().kickPlayersNotWhitelisted(source);
      return 1;
   }

   private static int addPlayers(CommandSource source, Collection<GameProfile> players) throws CommandSyntaxException {
      UserListWhitelist userlistwhitelist = source.getServer().getPlayerList().getWhitelistedPlayers();
      int i = 0;

      for(GameProfile gameprofile : players) {
         if (!userlistwhitelist.isWhitelisted(gameprofile)) {
            UserListWhitelistEntry userlistwhitelistentry = new UserListWhitelistEntry(gameprofile);
            userlistwhitelist.addEntry(userlistwhitelistentry);
            source.sendFeedback(new TextComponentTranslation("commands.whitelist.add.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw PLAYER_ALREADY_WHITELISTED.create();
      } else {
         return i;
      }
   }

   private static int removePlayers(CommandSource source, Collection<GameProfile> players) throws CommandSyntaxException {
      UserListWhitelist userlistwhitelist = source.getServer().getPlayerList().getWhitelistedPlayers();
      int i = 0;

      for(GameProfile gameprofile : players) {
         if (userlistwhitelist.isWhitelisted(gameprofile)) {
            UserListWhitelistEntry userlistwhitelistentry = new UserListWhitelistEntry(gameprofile);
            userlistwhitelist.removeEntry(userlistwhitelistentry);
            source.sendFeedback(new TextComponentTranslation("commands.whitelist.remove.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw PLAYER_NOT_WHITELISTED.create();
      } else {
         source.getServer().kickPlayersNotWhitelisted(source);
         return i;
      }
   }

   private static int enableWhiteList(CommandSource source) throws CommandSyntaxException {
      PlayerList playerlist = source.getServer().getPlayerList();
      if (playerlist.isWhiteListEnabled()) {
         throw ALREADY_ON.create();
      } else {
         playerlist.setWhiteListEnabled(true);
         source.sendFeedback(new TextComponentTranslation("commands.whitelist.enabled"), true);
         source.getServer().kickPlayersNotWhitelisted(source);
         return 1;
      }
   }

   private static int disableWhiteList(CommandSource source) throws CommandSyntaxException {
      PlayerList playerlist = source.getServer().getPlayerList();
      if (!playerlist.isWhiteListEnabled()) {
         throw ALREADY_OFF.create();
      } else {
         playerlist.setWhiteListEnabled(false);
         source.sendFeedback(new TextComponentTranslation("commands.whitelist.disabled"), true);
         return 1;
      }
   }

   private static int listWhitelistedPlayers(CommandSource source) {
      String[] astring = source.getServer().getPlayerList().getWhitelistedPlayerNames();
      if (astring.length == 0) {
         source.sendFeedback(new TextComponentTranslation("commands.whitelist.none"), false);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.whitelist.list", astring.length, String.join(", ", astring)), false);
      }

      return astring.length;
   }
}
