package net.minecraft.world;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
	private final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
	private final BlockPos position;
	protected int chunkX;
	protected int chunkZ;
	protected Chunk[][] chunkArray;

	/** set by !chunk.getAreLevelsEmpty */
	private static final IBlockState[] blockStates = new IBlockState[8000];
	protected boolean hasExtendedLevels;

	/** Reference to the World object. */
	protected World worldObj;

	public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
		this.worldObj = worldIn;
		this.chunkX = posFromIn.getX() - subIn >> 4;
		this.chunkZ = posFromIn.getZ() - subIn >> 4;
		int i = posToIn.getX() + subIn >> 4;
		int j = posToIn.getZ() + subIn >> 4;
		this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
		this.hasExtendedLevels = true;

		for (int k = this.chunkX; k <= i; ++k) {
			for (int l = this.chunkZ; l <= j; ++l) {
				this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunkFromChunkCoords(k, l);
			}
		}

		for (int i1 = posFromIn.getX() >> 4; i1 <= posToIn.getX() >> 4; ++i1) {
			for (int j1 = posFromIn.getZ() >> 4; j1 <= posToIn.getZ() >> 4; ++j1) {
				Chunk chunk = this.chunkArray[i1 - this.chunkX][j1 - this.chunkZ];

				if (chunk != null && !chunk.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY())) {
					this.hasExtendedLevels = false;
				}
			}
		}
		
		this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
		Arrays.fill(blockStates, null);
	}

	/**
	 * set by !chunk.getAreLevelsEmpty
	 */
	public boolean extendedLevelsInChunkCache() {
		return this.hasExtendedLevels;
	}

	@Nullable
	public TileEntity getTileEntity(BlockPos pos) {
		return this.getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
	}

	@Nullable
	public TileEntity getTileEntity(BlockPos p_190300_1_, Chunk.EnumCreateEntityType p_190300_2_) {
		int i = (p_190300_1_.getX() >> 4) - this.chunkX;
		int j = (p_190300_1_.getZ() >> 4) - this.chunkZ;
		return this.chunkArray[i][j].getTileEntity(p_190300_1_, p_190300_2_);
	}

	public int getCombinedLight(BlockPos pos, int lightValue) {
		IBlockState state = getBlockState(pos);
		Block b = state.getBlock();
		int j = b.alfheim$getLightFor(state, this, EnumSkyBlock.SKY, pos);
		int k = b.alfheim$getLightFor(state, this, EnumSkyBlock.BLOCK, pos);
		if (k < lightValue) {
			k = lightValue;
		}

		return j << 20 | k << 4;
	}

	public IBlockState getBlockState(BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.chunkX;
			int j = (pos.getZ() >> 4) - this.chunkZ;

			if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length) {
				Chunk chunk = this.chunkArray[i][j];

				if (chunk != null) {
					return chunk.getBlockState(pos);
				}
			}
		}

		return Blocks.AIR.getDefaultState();
	}
	
	public IBlockState getBlockStateFaster(BlockPos blockpos) {
		int i = this.getPositionIndexFaster(blockpos);
		IBlockState iblockstate = blockStates[i];
		if (iblockstate == null) {
			iblockstate = this.getBlockStateRawFaster(blockpos);
			blockStates[i] = iblockstate;
		}

		return iblockstate;
	}
	
	private int getPositionIndexFaster(BlockPos parBlockPos) {
		int i = parBlockPos.x - this.position.x;
		int j = parBlockPos.y - this.position.y;
		int k = parBlockPos.z - this.position.z;
		return i * 400 + k * 20 + j;
	}
	
	private IBlockState getBlockStateRawFaster(BlockPos pos) {
		if (pos.y >= 0 && pos.y < 256) {
			int i = (pos.x >> 4) - this.chunkX;
			int j = (pos.z >> 4) - this.chunkZ;
			return this.chunkArray[i][j].getBlockState(pos);
		} else {
			return DEFAULT_STATE;
		}
	}

	public Biome getBiome(BlockPos pos) {
		int i = (pos.getX() >> 4) - this.chunkX;
		int j = (pos.getZ() >> 4) - this.chunkZ;
		return this.chunkArray[i][j].getBiome(pos, this.worldObj.getBiomeProvider());
	}

	/**
	 * Checks to see if an air block exists at the provided location. Note that this
	 * only checks to see if the blocks material is set to air, meaning it is
	 * possible for non-vanilla blocks to still pass this check.
	 */
	public boolean isAirBlock(BlockPos pos) {
		return this.getBlockState(pos).getMaterial() == Material.AIR;
	}

	public int getLightFor(EnumSkyBlock p_175628_1_, BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.chunkX;
			int j = (pos.getZ() >> 4) - this.chunkZ;
			return this.chunkArray[i][j].getLightFor(p_175628_1_, pos);
		} else {
			return p_175628_1_.defaultLightValue;
		}
	}

	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return this.getBlockState(pos).getStrongPower(this, pos, direction);
	}

	public WorldType getWorldType() {
		return this.worldObj.getWorldType();
	}
}
