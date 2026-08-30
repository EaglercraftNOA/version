package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class TeleportCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("teleport").requires((p_198816_0_) -> {
         return p_198816_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_198807_0_) -> {
         return teleportToPos(p_198807_0_.getSource(), EntityArgument.getEntities(p_198807_0_, "targets"), p_198807_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198807_0_, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
      }).then(Commands.argument("rotation", RotationArgument.rotation()).executes((p_198811_0_) -> {
         return teleportToPos(p_198811_0_.getSource(), EntityArgument.getEntities(p_198811_0_, "targets"), p_198811_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198811_0_, "location"), RotationArgument.getRotation(p_198811_0_, "rotation"), (TeleportCommand.Facing)null);
      })).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.entity()).executes((p_198806_0_) -> {
         return teleportToPos(p_198806_0_.getSource(), EntityArgument.getEntities(p_198806_0_, "targets"), p_198806_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198806_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_198806_0_, "facingEntity"), EntityAnchorArgument.Type.FEET));
      }).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes((p_198812_0_) -> {
         return teleportToPos(p_198812_0_.getSource(), EntityArgument.getEntities(p_198812_0_, "targets"), p_198812_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198812_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_198812_0_, "facingEntity"), EntityAnchorArgument.getEntityAnchor(p_198812_0_, "facingAnchor")));
      })))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_198805_0_) -> {
         return teleportToPos(p_198805_0_.getSource(), EntityArgument.getEntities(p_198805_0_, "targets"), p_198805_0_.getSource().getWorld(), Vec3Argument.getLocation(p_198805_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(Vec3Argument.getVec3(p_198805_0_, "facingLocation")));
      })))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_198814_0_) -> {
         return teleportToEntity(p_198814_0_.getSource(), EntityArgument.getEntities(p_198814_0_, "targets"), EntityArgument.getEntity(p_198814_0_, "destination"));
      }))).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_200560_0_) -> {
         return teleportToPos(p_200560_0_.getSource(), Collections.singleton(p_200560_0_.getSource().assertIsEntity()), p_200560_0_.getSource().getWorld(), Vec3Argument.getLocation(p_200560_0_, "location"), LocationInput.current(), (TeleportCommand.Facing)null);
      })).then(Commands.argument("destination", EntityArgument.entity()).executes((p_200562_0_) -> {
         return teleportToEntity(p_200562_0_.getSource(), Collections.singleton(p_200562_0_.getSource().assertIsEntity()), EntityArgument.getEntity(p_200562_0_, "destination"));
      })));
      dispatcher.register(Commands.literal("tp").requires((p_200556_0_) -> {
         return p_200556_0_.hasPermissionLevel(2);
      }).redirect(literalcommandnode));
   }

   private static int teleportToEntity(CommandSource source, Collection<? extends Entity> targets, Entity destination) {
      for(Entity entity : targets) {
         teleport(source, entity, source.getWorld(), destination.posX, destination.posY, destination.posZ, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class), destination.rotationYaw, destination.rotationPitch, (TeleportCommand.Facing)null);
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.teleport.success.entity.single", targets.iterator().next().getDisplayName(), destination.getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
      }

      return targets.size();
   }

   private static int teleportToPos(CommandSource source, Collection<? extends Entity> targets, WorldServer worldIn, ILocationArgument position, @Nullable ILocationArgument rotationIn, @Nullable TeleportCommand.Facing facing) throws CommandSyntaxException {
      Vec3d vec3d = position.getPosition(source);
      Vec2f vec2f = rotationIn == null ? null : rotationIn.getRotation(source);
      Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);
      if (position.isXRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.X);
      }

      if (position.isYRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.Y);
      }

      if (position.isZRelative()) {
         set.add(SPacketPlayerPosLook.EnumFlags.Z);
      }

      if (rotationIn == null) {
         set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
      } else {
         if (rotationIn.isXRelative()) {
            set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         }

         if (rotationIn.isYRelative()) {
            set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
         }
      }

      for(Entity entity : targets) {
         if (rotationIn == null) {
            teleport(source, entity, worldIn, vec3d.x, vec3d.y, vec3d.z, set, entity.rotationYaw, entity.rotationPitch, facing);
         } else {
            teleport(source, entity, worldIn, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, facing);
         }
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.teleport.success.location.single", targets.iterator().next().getDisplayName(), vec3d.x, vec3d.y, vec3d.z), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.teleport.success.location.multiple", targets.size(), vec3d.x, vec3d.y, vec3d.z), true);
      }

      return targets.size();
   }

   private static void teleport(CommandSource source, Entity entityIn, WorldServer worldIn, double x, double y, double z, Set<SPacketPlayerPosLook.EnumFlags> relativeList, float yaw, float pitch, @Nullable TeleportCommand.Facing facing) {
      if (entityIn instanceof EntityPlayerMP) {
         entityIn.stopRiding();
         if (((EntityPlayerMP)entityIn).isPlayerSleeping()) {
            ((EntityPlayerMP)entityIn).wakeUpPlayer(true, true, false);
         }

         if (worldIn == entityIn.world) {
            ((EntityPlayerMP)entityIn).connection.setPlayerLocation(x, y, z, yaw, pitch, relativeList);
         } else {
            ((EntityPlayerMP)entityIn).teleport(worldIn, x, y, z, yaw, pitch);
         }

         entityIn.setRotationYawHead(yaw);
      } else {
         float f = MathHelper.wrapDegrees(yaw);
         float f1 = MathHelper.wrapDegrees(pitch);
         f1 = MathHelper.clamp(f1, -90.0F, 90.0F);
         if (worldIn == entityIn.world) {
            entityIn.setLocationAndAngles(x, y, z, f, f1);
            entityIn.setRotationYawHead(f);
         } else {
            WorldServer worldserver = (WorldServer)entityIn.world;
            worldserver.removeEntity(entityIn);
            entityIn.dimension = worldIn.dimension.getType();
            entityIn.removed = false;
            Entity entity = entityIn;
            entityIn = entityIn.getType().create(worldIn);
            if (entityIn == null) {
               return;
            }

            entityIn.copyDataFromOld(entity);
            entityIn.setLocationAndAngles(x, y, z, f, f1);
            entityIn.setRotationYawHead(f);
            boolean flag = entityIn.forceSpawn;
            entityIn.forceSpawn = true;
            worldIn.spawnEntity(entityIn);
            entityIn.forceSpawn = flag;
            worldIn.tickEntity(entityIn, false);
            entity.removed = true;
         }
      }

      if (facing != null) {
         facing.updateLook(source, entityIn);
      }

      if (!(entityIn instanceof EntityLivingBase) || !((EntityLivingBase)entityIn).isElytraFlying()) {
         entityIn.motionY = 0.0D;
         entityIn.onGround = true;
      }

   }

   static class Facing {
      private final Vec3d position;
      private final Entity entity;
      private final EntityAnchorArgument.Type anchor;

      public Facing(Entity entityIn, EntityAnchorArgument.Type anchorIn) {
         this.entity = entityIn;
         this.anchor = anchorIn;
         this.position = anchorIn.apply(entityIn);
      }

      public Facing(Vec3d positionIn) {
         this.entity = null;
         this.position = positionIn;
         this.anchor = null;
      }

      public void updateLook(CommandSource source, Entity entityIn) {
         if (this.entity != null) {
            if (entityIn instanceof EntityPlayerMP) {
               ((EntityPlayerMP)entityIn).lookAt(source.getEntityAnchorType(), this.entity, this.anchor);
            } else {
               entityIn.lookAt(source.getEntityAnchorType(), this.position);
            }
         } else {
            entityIn.lookAt(source.getEntityAnchorType(), this.position);
         }

      }
   }
}
