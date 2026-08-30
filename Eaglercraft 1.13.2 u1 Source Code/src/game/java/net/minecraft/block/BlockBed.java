package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBed extends BlockHorizontal implements ITileEntityProvider {
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   private final EnumDyeColor color;

   public BlockBed(EnumDyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
      this.setDefaultState(this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, Boolean.valueOf(false)));
   }

   public MaterialColor getMaterialColor(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(PART) == BedPart.FOOT ? this.color.getMapColor() : MaterialColor.WOOL;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         if (state.get(PART) != BedPart.HEAD) {
            pos = pos.offset(state.get(HORIZONTAL_FACING));
            state = worldIn.getBlockState(pos);
            if (state.getBlock() != this) {
               return true;
            }
         }

         if (worldIn.dimension.canRespawnHere() && worldIn.getBiome(pos) != Biomes.NETHER) {
            if (state.get(OCCUPIED)) {
               EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);
               if (entityplayer != null) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.occupied"), true);
                  return true;
               }

               state = state.with(OCCUPIED, Boolean.valueOf(false));
               worldIn.setBlockState(pos, state, 4);
            }

            EntityPlayer.SleepResult entityplayer$sleepresult = player.trySleep(pos);
            if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK) {
               state = state.with(OCCUPIED, Boolean.valueOf(true));
               worldIn.setBlockState(pos, state, 4);
               return true;
            } else {
               if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.no_sleep"), true);
               } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.not_safe"), true);
               } else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                  player.sendStatusMessage(new TextComponentTranslation("block.minecraft.bed.too_far_away"), true);
               }

               return true;
            }
         } else {
            worldIn.removeBlock(pos);
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
            if (worldIn.getBlockState(blockpos).getBlock() == this) {
               worldIn.removeBlock(blockpos);
            }

            worldIn.createExplosion((Entity)null, DamageSource.netherBedExplosion(), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, true);
            return true;
         }
      }
   }

   @Nullable
   private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos) {
      for(EntityPlayer entityplayer : worldIn.playerEntities) {
         if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
            return entityplayer;
         }
      }

      return null;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.5F);
   }

   public void onLanded(IBlockReader worldIn, Entity entityIn) {
      if (entityIn.isSneaking()) {
         super.onLanded(worldIn, entityIn);
      } else if (entityIn.motionY < 0.0D) {
         entityIn.motionY = -entityIn.motionY * (double)0.66F;
         if (!(entityIn instanceof EntityLivingBase)) {
            entityIn.motionY *= 0.8D;
         }
      }

   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
         return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn.with(OCCUPIED, facingState.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
      } else {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   private static EnumFacing getDirectionToOther(BedPart p_208070_0_, EnumFacing p_208070_1_) {
      return p_208070_0_ == BedPart.FOOT ? p_208070_1_ : p_208070_1_.getOpposite();
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         worldIn.removeTileEntity(pos);
      }
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BedPart bedpart = state.get(PART);
      boolean flag = bedpart == BedPart.HEAD;
      BlockPos blockpos = pos.offset(getDirectionToOther(bedpart, state.get(HORIZONTAL_FACING)));
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (iblockstate.getBlock() == this && iblockstate.get(PART) != bedpart) {
         worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         worldIn.playEvent(player, 2001, blockpos, Block.getStateId(iblockstate));
         if (!worldIn.isRemote && !player.isCreative()) {
            if (flag) {
               state.dropBlockAsItem(worldIn, pos, 0);
            } else {
               iblockstate.dropBlockAsItem(worldIn, blockpos, 0);
            }
         }

         player.addStat(StatList.BLOCK_MINED.get(this));
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      EnumFacing enumfacing = context.getPlacementHorizontalFacing();
      BlockPos blockpos = context.getPos();
      BlockPos blockpos1 = blockpos.offset(enumfacing);
      return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, enumfacing) : null;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return (IItemProvider)(state.get(PART) == BedPart.FOOT ? Items.AIR : super.getItemDropped(state, worldIn, pos, fortune));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   @Nullable
   public static BlockPos getSafeExitLocation(IBlockReader worldIn, BlockPos pos, int tries) {
      EnumFacing enumfacing = worldIn.getBlockState(pos).get(HORIZONTAL_FACING);
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();

      for(int l = 0; l <= 1; ++l) {
         int i1 = i - enumfacing.getXOffset() * l - 1;
         int j1 = k - enumfacing.getZOffset() * l - 1;
         int k1 = i1 + 2;
         int l1 = j1 + 2;

         for(int i2 = i1; i2 <= k1; ++i2) {
            for(int j2 = j1; j2 <= l1; ++j2) {
               BlockPos blockpos = new BlockPos(i2, j, j2);
               if (hasRoomForPlayer(worldIn, blockpos)) {
                  if (tries <= 0) {
                     return blockpos;
                  }

                  --tries;
               }
            }
         }
      }

      return null;
   }

   protected static boolean hasRoomForPlayer(IBlockReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid() && !worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getMaterial().isSolid();
   }

   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING, PART, OCCUPIED);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityBed(this.color);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (!worldIn.isRemote) {
         BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING));
         worldIn.setBlockState(blockpos, state.with(PART, BedPart.HEAD), 3);
         worldIn.notifyNeighbors(pos, Blocks.AIR);
         state.updateNeighbors(worldIn, pos, 3);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public EnumDyeColor getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(IBlockState state, BlockPos pos) {
      BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
      return MathHelper.getCoordinateRandom(blockpos.getX(), pos.getY(), blockpos.getZ());
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
