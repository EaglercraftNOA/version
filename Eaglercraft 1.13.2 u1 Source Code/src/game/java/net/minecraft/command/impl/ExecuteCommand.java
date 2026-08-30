package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.RangeArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType TOO_MANY_BLOCKS = new Dynamic2CommandExceptionType((p_208885_0_, p_208885_1_) -> {
      return new TextComponentTranslation("commands.execute.blocks.toobig", p_208885_0_, p_208885_1_);
   });
   private static final SimpleCommandExceptionType TEST_FAILED = new SimpleCommandExceptionType(new TextComponentTranslation("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType TEST_FAILED_COUNT = new DynamicCommandExceptionType((p_210446_0_) -> {
      return new TextComponentTranslation("commands.execute.conditional.fail_count", p_210446_0_);
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> COMBINE_ON_RESULT_COMPLETE = (p_209937_0_, p_209937_1_) -> {
      return (p_209939_2_, p_209939_3_, p_209939_4_) -> {
         p_209937_0_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
         p_209937_1_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
      };
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("execute").requires((p_198409_0_) -> {
         return p_198409_0_.hasPermissionLevel(2);
      }));
      dispatcher.register(Commands.literal("execute").requires((p_198387_0_) -> {
         return p_198387_0_.hasPermissionLevel(2);
      }).then(Commands.literal("run").redirect(dispatcher.getRoot())).then(makeIfCommand(literalcommandnode, Commands.literal("if"), true)).then(makeIfCommand(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_198385_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_198385_0_, "targets")) {
            list.add(p_198385_0_.getSource().withEntity(entity));
         }

         return list;
      }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_198384_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_198384_0_, "targets")) {
            list.add(p_198384_0_.getSource().withWorld((WorldServer)entity.world).withPos(entity.getPositionVector()).withRotation(entity.getPitchYaw()));
         }

         return list;
      }))).then(Commands.literal("store").then(makeStoreSubcommand(literalcommandnode, Commands.literal("result"), true)).then(makeStoreSubcommand(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_198382_0_) -> {
         return p_198382_0_.getSource().withPos(Vec3Argument.getVec3(p_198382_0_, "pos"));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_201092_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201092_0_, "targets")) {
            list.add(p_201092_0_.getSource().withPos(entity.getPositionVector()));
         }

         return list;
      })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (p_201100_0_) -> {
         return p_201100_0_.getSource().withRotation(RotationArgument.getRotation(p_201100_0_, "rot").getRotation(p_201100_0_.getSource()));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_201087_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201087_0_, "targets")) {
            list.add(p_201087_0_.getSource().withRotation(entity.getPitchYaw()));
         }

         return list;
      })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).fork(literalcommandnode, (p_201083_0_) -> {
         List<CommandSource> list = Lists.newArrayList();
         EntityAnchorArgument.Type entityanchorargument$type = EntityAnchorArgument.getEntityAnchor(p_201083_0_, "anchor");

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201083_0_, "targets")) {
            list.add(p_201083_0_.getSource().withRotation(entity, entityanchorargument$type));
         }

         return list;
      })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_201090_0_) -> {
         return p_201090_0_.getSource().withRotation(Vec3Argument.getVec3(p_201090_0_, "pos"));
      }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (p_198381_0_) -> {
         return p_198381_0_.getSource().withPos(p_198381_0_.getSource().getPos().align(SwizzleArgument.getSwizzle(p_198381_0_, "axes")));
      }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).redirect(literalcommandnode, (p_201091_0_) -> {
         return p_201091_0_.getSource().withEntityAnchorType(EntityAnchorArgument.getEntityAnchor(p_201091_0_, "anchor"));
      }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.getDimension()).redirect(literalcommandnode, (p_201089_0_) -> {
         return p_201089_0_.getSource().withWorld(p_201089_0_.getSource().getServer().getWorld(DimensionArgument.func_212592_a(p_201089_0_, "dimension")));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> makeStoreSubcommand(LiteralCommandNode<CommandSource> parent, LiteralArgumentBuilder<CommandSource> literal, boolean storingResult) {
      literal.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(parent, (p_198412_1_) -> {
         return storeIntoScore(p_198412_1_.getSource(), ScoreHolderArgument.getScoreHolder(p_198412_1_, "targets"), ObjectiveArgument.getObjective(p_198412_1_, "objective"), storingResult);
      }))));
      literal.then(Commands.literal("bossbar").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(BossBarCommand.SUGGESTIONS_PROVIDER).then(Commands.literal("value").redirect(parent, (p_201468_1_) -> {
         return storeIntoBossbar(p_201468_1_.getSource(), BossBarCommand.getBossbar(p_201468_1_), true, storingResult);
      })).then(Commands.literal("max").redirect(parent, (p_201457_1_) -> {
         return storeIntoBossbar(p_201457_1_.getSource(), BossBarCommand.getBossbar(p_201457_1_), false, storingResult);
      }))));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.DATA_PROVIDERS) {
         datacommand$idataprovider.createArgument(literal, (p_198408_3_) -> {
            return p_198408_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_201106_2_) -> {
               return storeIntoNBT(p_201106_2_.getSource(), datacommand$idataprovider.createAccessor(p_201106_2_), NBTPathArgument.getNBTPath(p_201106_2_, "path"), (p_198379_1_) -> {
                  return new NBTTagInt((int)((double)p_198379_1_ * DoubleArgumentType.getDouble(p_201106_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_198375_2_) -> {
               return storeIntoNBT(p_198375_2_.getSource(), datacommand$idataprovider.createAccessor(p_198375_2_), NBTPathArgument.getNBTPath(p_198375_2_, "path"), (p_198410_1_) -> {
                  return new NBTTagFloat((float)((double)p_198410_1_ * DoubleArgumentType.getDouble(p_198375_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_198390_2_) -> {
               return storeIntoNBT(p_198390_2_.getSource(), datacommand$idataprovider.createAccessor(p_198390_2_), NBTPathArgument.getNBTPath(p_198390_2_, "path"), (p_198386_1_) -> {
                  return new NBTTagShort((short)((int)((double)p_198386_1_ * DoubleArgumentType.getDouble(p_198390_2_, "scale"))));
               }, storingResult);
            }))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_198420_2_) -> {
               return storeIntoNBT(p_198420_2_.getSource(), datacommand$idataprovider.createAccessor(p_198420_2_), NBTPathArgument.getNBTPath(p_198420_2_, "path"), (p_198414_1_) -> {
                  return new NBTTagLong((long)((double)p_198414_1_ * DoubleArgumentType.getDouble(p_198420_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_198367_2_) -> {
               return storeIntoNBT(p_198367_2_.getSource(), datacommand$idataprovider.createAccessor(p_198367_2_), NBTPathArgument.getNBTPath(p_198367_2_, "path"), (p_198393_1_) -> {
                  return new NBTTagDouble((double)p_198393_1_ * DoubleArgumentType.getDouble(p_198367_2_, "scale"));
               }, storingResult);
            }))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_198405_2_) -> {
               return storeIntoNBT(p_198405_2_.getSource(), datacommand$idataprovider.createAccessor(p_198405_2_), NBTPathArgument.getNBTPath(p_198405_2_, "path"), (p_198418_1_) -> {
                  return new NBTTagByte((byte)((int)((double)p_198418_1_ * DoubleArgumentType.getDouble(p_198405_2_, "scale"))));
               }, storingResult);
            }))));
         });
      }

      return literal;
   }

   private static CommandSource storeIntoScore(CommandSource source, Collection<String> targets, ScoreObjective objective, boolean storingResult) {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      return source.withResultConsumer((p_198404_4_, p_198404_5_, p_198404_6_) -> {
         for(String s : targets) {
            Score score = scoreboard.getOrCreateScore(s, objective);
            int i = storingResult ? p_198404_6_ : (p_198404_5_ ? 1 : 0);
            score.setScorePoints(i);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoBossbar(CommandSource source, CustomBossEvent bar, boolean storingValue, boolean storingResult) {
      return source.withResultConsumer((p_201459_3_, p_201459_4_, p_201459_5_) -> {
         int i = storingResult ? p_201459_5_ : (p_201459_4_ ? 1 : 0);
         if (storingValue) {
            bar.setValue(i);
         } else {
            bar.setMax(i);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoNBT(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn, IntFunction<INBTBase> tagConverter, boolean storingResult) {
      return source.withResultConsumer((p_198372_4_, p_198372_5_, p_198372_6_) -> {
         try {
            NBTTagCompound nbttagcompound = accessor.getData();
            int i = storingResult ? p_198372_6_ : (p_198372_5_ ? 1 : 0);
            pathIn.set(nbttagcompound, tagConverter.apply(i));
            accessor.mergeData(nbttagcompound);
         } catch (CommandSyntaxException var9) {
            ;
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static ArgumentBuilder<CommandSource, ?> makeIfCommand(CommandNode<CommandSource> parent, LiteralArgumentBuilder<CommandSource> literal, boolean isIf) {
      return literal.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(buildIfResult(parent, Commands.argument("block", BlockPredicateArgument.blockPredicate()), isIf, (p_210434_0_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_210434_0_, "block").test(new BlockWorldState(((CommandSource)p_210434_0_.getSource()).getWorld(), BlockPosArgument.getLoadedBlockPos(p_210434_0_, "pos"), true));
      })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_210438_0_) -> {
         return compareScores(p_210438_0_, Integer::equals);
      })))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_210442_0_) -> {
         return compareScores(p_210442_0_, (p_199669_0_, p_199669_1_) -> {
            return p_199669_0_ < p_199669_1_;
         });
      })))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_210418_0_) -> {
         return compareScores(p_210418_0_, (p_199672_0_, p_199672_1_) -> {
            return p_199672_0_ <= p_199672_1_;
         });
      })))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_210422_0_) -> {
         return compareScores(p_210422_0_, (p_199651_0_, p_199651_1_) -> {
            return p_199651_0_ > p_199651_1_;
         });
      })))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_210424_0_) -> {
         return compareScores(p_210424_0_, (p_199650_0_, p_199650_1_) -> {
            return p_199650_0_ >= p_199650_1_;
         });
      })))).then(Commands.literal("matches").then(buildIfResult(parent, Commands.argument("range", RangeArgument.intRange()), isIf, (p_201088_0_) -> {
         return checkScore(p_201088_0_, RangeArgument.IntRange.getIntRange(p_201088_0_, "range"));
      })))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).then(buildIfBlocks(parent, Commands.literal("all"), isIf, false)).then(buildIfBlocks(parent, Commands.literal("masked"), isIf, true)))))).then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).fork(parent, (p_210428_1_) -> {
         return checkIfMatches(p_210428_1_, isIf, !EntityArgument.getEntitiesAllowingNone(p_210428_1_, "entities").isEmpty());
      }).executes(isIf ? (p_198380_0_) -> {
         int i = EntityArgument.getEntitiesAllowingNone(p_198380_0_, "entities").size();
         if (i > 0) {
            p_198380_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass_count", i), false);
            return i;
         } else {
            throw TEST_FAILED.create();
         }
      } : (p_210451_0_) -> {
         int i = EntityArgument.getEntitiesAllowingNone(p_210451_0_, "entities").size();
         if (i == 0) {
            p_210451_0_.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw TEST_FAILED_COUNT.create(i);
         }
      })));
   }

   private static boolean compareScores(CommandContext<CommandSource> context, BiPredicate<Integer, Integer> comparison) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(context, "targetObjective");
      String s1 = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "source");
      ScoreObjective scoreobjective1 = ObjectiveArgument.getObjective(context, "sourceObjective");
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      if (scoreboard.entityHasObjective(s, scoreobjective) && scoreboard.entityHasObjective(s1, scoreobjective1)) {
         Score score = scoreboard.getOrCreateScore(s, scoreobjective);
         Score score1 = scoreboard.getOrCreateScore(s1, scoreobjective1);
         return comparison.test(score.getScorePoints(), score1.getScorePoints());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext<CommandSource> context, MinMaxBounds.IntBound bounds) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(context, "targetObjective");
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      return !scoreboard.entityHasObjective(s, scoreobjective) ? false : bounds.test(scoreboard.getOrCreateScore(s, scoreobjective).getScorePoints());
   }

   private static Collection<CommandSource> checkIfMatches(CommandContext<CommandSource> context, boolean actual, boolean expected) {
      return (Collection<CommandSource>)(expected == actual ? Collections.singleton(context.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfResult(CommandNode<CommandSource> context, ArgumentBuilder<CommandSource, ?> builder, boolean value, ExecuteCommand.ExecuteTest test) {
      return builder.fork(context, (p_210448_2_) -> {
         return checkIfMatches(p_210448_2_, value, test.test(p_210448_2_));
      }).executes((p_210436_2_) -> {
         if (value == test.test(p_210436_2_)) {
            ((CommandSource)p_210436_2_.getSource()).sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw TEST_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfBlocks(CommandNode<CommandSource> parent, ArgumentBuilder<CommandSource, ?> literal, boolean isIf, boolean isMasked) {
      return literal.fork(parent, (p_212171_2_) -> {
         return checkIfMatches(p_212171_2_, isIf, countMatchingBlocks(p_212171_2_, isMasked).isPresent());
      }).executes(isIf ? (p_212176_1_) -> {
         return checkBlockCountIf(p_212176_1_, isMasked);
      } : (p_212170_1_) -> {
         return checkBlockCountUnless(p_212170_1_, isMasked);
      });
   }

   private static int checkBlockCountIf(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      OptionalInt optionalint = countMatchingBlocks(context, isMasked);
      if (optionalint.isPresent()) {
         context.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass_count", optionalint.getAsInt()), false);
         return optionalint.getAsInt();
      } else {
         throw TEST_FAILED.create();
      }
   }

   private static int checkBlockCountUnless(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      OptionalInt optionalint = countMatchingBlocks(context, isMasked);
      if (!optionalint.isPresent()) {
         context.getSource().sendFeedback(new TextComponentTranslation("commands.execute.conditional.pass"), false);
         return 1;
      } else {
         throw TEST_FAILED_COUNT.create(optionalint.getAsInt());
      }
   }

   private static OptionalInt countMatchingBlocks(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      return countMatchingBlocks(context.getSource().getWorld(), BlockPosArgument.getLoadedBlockPos(context, "start"), BlockPosArgument.getLoadedBlockPos(context, "end"), BlockPosArgument.getLoadedBlockPos(context, "destination"), isMasked);
   }

   private static OptionalInt countMatchingBlocks(WorldServer worldIn, BlockPos begin, BlockPos end, BlockPos destination, boolean isMasked) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(begin, end);
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(destination, destination.add(mutableboundingbox.getLength()));
      BlockPos blockpos = new BlockPos(mutableboundingbox1.minX - mutableboundingbox.minX, mutableboundingbox1.minY - mutableboundingbox.minY, mutableboundingbox1.minZ - mutableboundingbox.minZ);
      int i = mutableboundingbox.getXSize() * mutableboundingbox.getYSize() * mutableboundingbox.getZSize();
      if (i > 32768) {
         throw TOO_MANY_BLOCKS.create(32768, i);
      } else {
         int j = 0;

         for(int k = mutableboundingbox.minZ; k <= mutableboundingbox.maxZ; ++k) {
            for(int l = mutableboundingbox.minY; l <= mutableboundingbox.maxY; ++l) {
               for(int i1 = mutableboundingbox.minX; i1 <= mutableboundingbox.maxX; ++i1) {
                  BlockPos blockpos1 = new BlockPos(i1, l, k);
                  BlockPos blockpos2 = blockpos1.add(blockpos);
                  IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                  if (!isMasked || iblockstate.getBlock() != Blocks.AIR) {
                     if (iblockstate != worldIn.getBlockState(blockpos2)) {
                        return OptionalInt.empty();
                     }

                     TileEntity tileentity = worldIn.getTileEntity(blockpos1);
                     TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);
                     if (tileentity != null) {
                        if (tileentity1 == null) {
                           return OptionalInt.empty();
                        }

                        NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());
                        nbttagcompound.remove("x");
                        nbttagcompound.remove("y");
                        nbttagcompound.remove("z");
                        NBTTagCompound nbttagcompound1 = tileentity1.write(new NBTTagCompound());
                        nbttagcompound1.remove("x");
                        nbttagcompound1.remove("y");
                        nbttagcompound1.remove("z");
                        if (!nbttagcompound.equals(nbttagcompound1)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++j;
                  }
               }
            }
         }

         return OptionalInt.of(j);
      }
   }

   @FunctionalInterface
   interface ExecuteTest {
      boolean test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }
}
