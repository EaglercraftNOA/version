package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockNote extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTE_BLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE_0_24;

   public BlockNote(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(INSTRUMENT, NoteBlockInstrument.HARP).with(NOTE, Integer.valueOf(0)).with(POWERED, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(INSTRUMENT, NoteBlockInstrument.byState(context.getWorld().getBlockState(context.getPos().down())));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == EnumFacing.DOWN ? stateIn.with(INSTRUMENT, NoteBlockInstrument.byState(facingState)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean flag = worldIn.isBlockPowered(pos);
      if (flag != state.get(POWERED)) {
         if (flag) {
            this.triggerNote(worldIn, pos);
         }

         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
      }

   }

   private void triggerNote(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos.up()).isAir()) {
         worldIn.addBlockEvent(pos, this, 0, 0);
      }

   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         state = state.cycle(NOTE);
         worldIn.setBlockState(pos, state, 3);
         this.triggerNote(worldIn, pos);
         player.addStat(StatList.TUNE_NOTEBLOCK);
         return true;
      }
   }

   public void onBlockClicked(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      if (!worldIn.isRemote) {
         this.triggerNote(worldIn, pos);
         player.addStat(StatList.PLAY_NOTEBLOCK);
      }
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      int i = state.get(NOTE);
      float f = (float)Math.pow(2.0D, (double)(i - 12) / 12.0D);
      worldIn.playSound((EntityPlayer)null, pos, state.get(INSTRUMENT).getSound(), SoundCategory.RECORDS, 3.0F, f);
      worldIn.addParticle(Particles.NOTE, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.2D, (double)pos.getZ() + 0.5D, (double)i / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(INSTRUMENT, POWERED, NOTE);
   }
}
