package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
	/**
	 * Contains the bottom-most Y block represented by this ExtendedBlockStorage.
	 * Typically a multiple of 16.
	 */
	private final int yBase;

	/**
	 * A total count of the number of non-air blocks in this block storage's Chunk.
	 */
	private int blockRefCount;

	/**
	 * Contains the number of blocks in this block storage's parent chunk that
	 * require random ticking. Used to cull the Chunk from random tick updates for
	 * performance reasons.
	 */
	private int tickRefCount;
	private final BlockStateContainer data;

	/** The NibbleArray containing a block of Block-light data. */
	private NibbleArray blocklightArray;

	/** The NibbleArray containing a block of Sky-light data. */
	private NibbleArray skylightArray;
	
	private int alfheim$lightRefCount = -1;

	public ExtendedBlockStorage(int y, boolean storeSkylight) {
		this.yBase = y;
		this.data = new BlockStateContainer();
		this.blocklightArray = new NibbleArray();

		if (storeSkylight) {
			this.skylightArray = new NibbleArray();
		}
	}

	public IBlockState get(int x, int y, int z) {
		return this.data.get(x, y, z);
	}

	public void set(int x, int y, int z, IBlockState state) {
		IBlockState iblockstate = this.get(x, y, z);
		Block block = iblockstate.getBlock();
		Block block1 = state.getBlock();

		if (block != Blocks.AIR) {
			--this.blockRefCount;

			if (block.getTickRandomly()) {
				--this.tickRefCount;
			}
		}

		if (block1 != Blocks.AIR) {
			++this.blockRefCount;

			if (block1.getTickRandomly()) {
				++this.tickRefCount;
			}
		}

		this.data.set(x, y, z, state);
	}
	
	public Block getBlockByExtId(int x, int y, int z) {
		return this.get(x, y, z).getBlock();
	}

	/**
	 * Returns whether or not this block storage's Chunk is fully empty, based on
	 * its internal reference count.
	 */
	public boolean isEmpty() {
		if (blockRefCount != 0)
			return false;

		// -1 indicates the lightRefCount needs to be re-calculated
		if (alfheim$lightRefCount == -1) {
			if (alfheim$checkLightArrayEqual(skylightArray, (byte) 255)
					&& alfheim$checkLightArrayEqual(blocklightArray, (byte) 0))
				alfheim$lightRefCount = 0; // Lighting is trivial, don't send to clients
			else
				alfheim$lightRefCount = 1; // Lighting is not trivial, send to clients
		}

		return alfheim$lightRefCount == 0;
	}

	/**
	 * Returns whether or not this block storage's Chunk will require random
	 * ticking, used to avoid looping through random block ticks when there are no
	 * blocks that would randomly tick.
	 */
	public boolean getNeedsRandomTick() {
		return this.tickRefCount > 0;
	}

	/**
	 * Returns the Y location of this ExtendedBlockStorage.
	 */
	public int getYLocation() {
		return this.yBase;
	}

	/**
	 * Sets the saved Sky-light value in the extended block storage structure.
	 */
	public void setExtSkylightValue(int x, int y, int z, int value) {
		this.skylightArray.set(x, y, z, value);
		alfheim$lightRefCount = -1;
	}

	/**
	 * Gets the saved Sky-light value in the extended block storage structure.
	 */
	public int getExtSkylightValue(int x, int y, int z) {
		if(this.skylightArray == null) {
			this.skylightArray = new NibbleArray();
		}
		return this.skylightArray.get(x, y, z);
	}

	/**
	 * Sets the saved Block-light value in the extended block storage structure.
	 */
	public void setExtBlocklightValue(int x, int y, int z, int value) {
		this.blocklightArray.set(x, y, z, value);
		alfheim$lightRefCount = -1;
	}

	/**
	 * Gets the saved Block-light value in the extended block storage structure.
	 */
	public int getExtBlocklightValue(int x, int y, int z) {
		return this.blocklightArray.get(x, y, z);
	}

	public void removeInvalidBlocks() {
		this.blockRefCount = 0;
		this.tickRefCount = 0;

		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				for (int k = 0; k < 16; ++k) {
					Block block = this.get(i, j, k).getBlock();

					if (block != Blocks.AIR) {
						++this.blockRefCount;

						if (block.getTickRandomly()) {
							++this.tickRefCount;
						}
					}
				}
			}
		}
	}

	public BlockStateContainer getData() {
		return this.data;
	}

	/**
	 * Returns the NibbleArray instance containing Block-light data.
	 */
	public NibbleArray getBlocklightArray() {
		return this.blocklightArray;
	}

	/**
	 * Returns the NibbleArray instance containing Sky-light data.
	 */
	public NibbleArray getSkylightArray() {
		return this.skylightArray;
	}

	/**
	 * Sets the NibbleArray instance used for Block-light values in this particular
	 * storage block.
	 */
	public void setBlocklightArray(NibbleArray newBlocklightArray) {
		this.blocklightArray = newBlocklightArray;
		alfheim$lightRefCount = -1;
	}

	/**
	 * Sets the NibbleArray instance used for Sky-light values in this particular
	 * storage block.
	 */
	public void setSkylightArray(NibbleArray newSkylightArray) {
		this.skylightArray = newSkylightArray;
		alfheim$lightRefCount = -1;
	}
	
	private boolean alfheim$checkLightArrayEqual(final NibbleArray storage, final byte targetValue) {
		if (storage == null)
			return true;

		for (final byte currentByte : storage.getData())
			if (currentByte != targetValue)
				return false;

		return true;
	}
}