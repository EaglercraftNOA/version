package net.optifine.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class RenderEnv {

	private IBlockState blockState;
	private BlockPos blockPos;

	public RenderEnv(IBlockState blockState, BlockPos blockPos) {
		this.blockState = blockState;
		this.blockPos = blockPos;
	}

	public void reset(IBlockState blockState, BlockPos blockPos) {
		this.blockState = blockState;
		this.blockPos = blockPos;
	}

	public IBlockState getBlockState() {
		return this.blockState;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}
}
