package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class SummonCommand {
   private static final SimpleCommandExceptionType SUMMON_FAILED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.summon.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("summon").requires((p_198740_0_) -> {
         return p_198740_0_.hasPermissionLevel(2);
      }).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
         return summonEntity(p_198738_0_.getSource(), EntitySummonArgument.getEntityId(p_198738_0_, "entity"), p_198738_0_.getSource().getPos(), new NBTTagCompound(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
         return summonEntity(p_198735_0_.getSource(), EntitySummonArgument.getEntityId(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new NBTTagCompound(), true);
      }).then(Commands.argument("nbt", NBTArgument.nbt()).executes((p_198739_0_) -> {
         return summonEntity(p_198739_0_.getSource(), EntitySummonArgument.getEntityId(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTArgument.getNBT(p_198739_0_, "nbt"), false);
      })))));
   }

   private static int summonEntity(CommandSource source, ResourceLocation type, Vec3d pos, NBTTagCompound nbt, boolean randomizeProperties) throws CommandSyntaxException {
      NBTTagCompound nbttagcompound = nbt.copy();
      nbttagcompound.putString("id", type.toString());
      if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(type)) {
         Entity entity1 = new EntityLightningBolt(source.getWorld(), pos.x, pos.y, pos.z, false);
         source.getWorld().addWeatherEffect(entity1);
         source.sendFeedback(new TextComponentTranslation("commands.summon.success", entity1.getDisplayName()), true);
         return 1;
      } else {
         Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, source.getWorld(), pos.x, pos.y, pos.z, true);
         if (entity == null) {
            throw SUMMON_FAILED.create();
         } else {
            entity.setLocationAndAngles(pos.x, pos.y, pos.z, entity.rotationYaw, entity.rotationPitch);
            if (randomizeProperties && entity instanceof EntityLiving) {
               ((EntityLiving)entity).onInitialSpawn(source.getWorld().getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null, (NBTTagCompound)null);
            }

            source.sendFeedback(new TextComponentTranslation("commands.summon.success", entity.getDisplayName()), true);
            return 1;
         }
      }
   }
}
