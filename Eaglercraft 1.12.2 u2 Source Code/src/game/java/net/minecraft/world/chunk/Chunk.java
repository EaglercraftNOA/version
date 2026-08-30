package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dev.redstudio.alfheim.lighting.LightingEngine;
import dev.redstudio.alfheim.utils.EnumBoundaryFacing;
import dev.redstudio.alfheim.utils.WorldChunkSlice;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.sp.server.EaglerMinecraftServer;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ExtendedBlockStorage NULL_BLOCK_STORAGE = null;

	/**
	 * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and
	 * metadata. Each entry corresponds to a logical segment of 16x16x16 blocks,
	 * stacked vertically.
	 */
	private final ExtendedBlockStorage[] storageArrays;

	/**
	 * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum
	 * belongs.
	 */
	private final byte[] blockBiomeArray;

	/**
	 * A map, similar to heightMap, that tracks how far down precipitation can fall.
	 */
	private final int[] precipitationHeightMap;

	/** Which columns need their skylightMaps updated. */
	private final boolean[] updateSkylightColumns;

	/** Whether or not this Chunk is currently loaded into the World */
	private boolean isChunkLoaded;

	/** Reference to the World object. */
	private final World worldObj;
	private final int[] heightMap;

	/** The x coordinate of the chunk. */
	public final int xPosition;

	/** The z coordinate of the chunk. */
	public final int zPosition;
	private boolean isGapLightingUpdated;
	private final Map<BlockPos, TileEntity> chunkTileEntityMap;
	private final ClassInheritanceMultiMap<Entity>[] entityLists;

	/** Boolean value indicating if the terrain is populated. */
	private boolean isTerrainPopulated;
	private boolean isLightPopulated;
	private boolean chunkTicked;

	/**
	 * Set to true if the chunk has been modified and needs to be updated
	 * internally.
	 */
	private boolean isModified;

	/**
	 * Whether this Chunk has any Entities and thus requires saving on every tick
	 */
	private boolean hasEntities;

	/** The time according to World.worldTime when this chunk was last saved */
	private long lastSaveTime;

	/** Lowest value in the heightmap. */
	private int heightMapMinimum;

	/** the cumulative number of ticks players have been in this chunk */
	private long inhabitedTime;

	/**
	 * Contains the current round-robin relight check index, and is implied as the
	 * relight check location as well.
	 */
	private int queuedLightChecks;
	private final List<BlockPos> tileEntityPosQueue;
	private final ChunkPos coordsCache;
	public boolean unloaded;
	
	private LightingEngine alfheim$lightingEngine;
	private boolean alfheim$isLightInitialized;
	public short[] alfheim$neighborLightChecks;

	public Chunk(World worldIn, int x, int z) {
		this.storageArrays = new ExtendedBlockStorage[16];
		this.blockBiomeArray = new byte[256];
		this.precipitationHeightMap = new int[256];
		this.updateSkylightColumns = new boolean[256];
		this.chunkTileEntityMap = Maps.<BlockPos, TileEntity>newHashMap();
		this.queuedLightChecks = 4096;
		this.tileEntityPosQueue = new LinkedList<BlockPos>();
		this.entityLists = (ClassInheritanceMultiMap[]) (new ClassInheritanceMultiMap[16]);
		this.worldObj = worldIn;
		this.xPosition = x;
		this.zPosition = z;
		this.heightMap = new int[256];
		this.coordsCache = new ChunkPos(x, z);

		for (int i = 0; i < this.entityLists.length; ++i) {
			this.entityLists[i] = new ClassInheritanceMultiMap(Entity.class);
		}

		Arrays.fill(this.precipitationHeightMap, -999);
		Arrays.fill(this.blockBiomeArray, (byte) -1);
		
		alfheim$lightingEngine = worldIn != null ? worldIn.alfheim$getLightingEngine() : null;
	}

	public Chunk(World worldIn, ChunkPrimer primer, int x, int z) {
		this(worldIn, x, z);
		int i = 256;
		boolean flag = worldIn.provider.func_191066_m();

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				for (int l = 0; l < 256; ++l) {
					IBlockState iblockstate = primer.getBlockState(j, l, k);

					if (iblockstate.getMaterial() != Material.AIR) {
						int i1 = l >> 4;

						if (this.storageArrays[i1] == NULL_BLOCK_STORAGE) {
							this.storageArrays[i1] = new ExtendedBlockStorage(i1 << 4, flag);
						}

						this.storageArrays[i1].set(j, l & 15, k, iblockstate);
					}
				}
			}
		}
	}

	/**
	 * Checks whether the chunk is at the X/Z location specified
	 */
	public boolean isAtLocation(int x, int z) {
		return x == this.xPosition && z == this.zPosition;
	}

	public int getHeight(BlockPos pos) {
		return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
	}

	/**
	 * Returns the value in the height map at this x, z coordinate in the chunk
	 */
	public int getHeightValue(int x, int z) {
		return this.heightMap[z << 4 | x];
	}

	@Nullable
	private ExtendedBlockStorage getLastExtendedBlockStorage() {
		for (int i = this.storageArrays.length - 1; i >= 0; --i) {
			if (this.storageArrays[i] != NULL_BLOCK_STORAGE) {
				return this.storageArrays[i];
			}
		}

		return null;
	}

	/**
	 * Returns the topmost ExtendedBlockStorage instance for this Chunk that
	 * actually contains a block.
	 */
	public int getTopFilledSegment() {
		ExtendedBlockStorage extendedblockstorage = this.getLastExtendedBlockStorage();
		return extendedblockstorage == null ? 0 : extendedblockstorage.getYLocation();
	}

	/**
	 * Returns the ExtendedBlockStorage array for this Chunk.
	 */
	public ExtendedBlockStorage[] getBlockStorageArray() {
		return this.storageArrays;
	}

	/**
	 * Generates the height map for a chunk from scratch
	 */
	protected void generateHeightMap() {
		int i = this.getTopFilledSegment();
		this.heightMapMinimum = Integer.MAX_VALUE;

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				this.precipitationHeightMap[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; --l) {
					IBlockState iblockstate = this.getBlockState(j, l - 1, k);

					if (iblockstate.getLightOpacity() != 0) {
						this.heightMap[k << 4 | j] = l;

						if (l < this.heightMapMinimum) {
							this.heightMapMinimum = l;
						}

						break;
					}
				}
			}
		}

		this.isModified = true;
	}

	/**
	 * Generates the initial skylight map for the chunk upon generation or load.
	 */
	public void generateSkylightMap() {
		int i = this.getTopFilledSegment();
		this.heightMapMinimum = Integer.MAX_VALUE;

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				this.precipitationHeightMap[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; --l) {
					if (this.getBlockLightOpacity(j, l - 1, k) != 0) {
						this.heightMap[k << 4 | j] = l;

						if (l < this.heightMapMinimum) {
							this.heightMapMinimum = l;
						}

						break;
					}
				}

				if (this.worldObj.provider.func_191066_m()) {
					int k1 = 15;
					int i1 = i + 16 - 1;

					while (true) {
						int j1 = this.getBlockLightOpacity(j, i1, k);

						if (j1 == 0 && k1 != 15) {
							j1 = 1;
						}

						k1 -= j1;

						if (k1 > 0) {
							ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

							if (extendedblockstorage != NULL_BLOCK_STORAGE) {
								extendedblockstorage.setExtSkylightValue(j, i1 & 15, k, k1);
								this.worldObj.notifyLightSet(
										new BlockPos((this.xPosition << 4) + j, i1, (this.zPosition << 4) + k));
							}
						}

						--i1;

						if (i1 <= 0 || k1 <= 0) {
							break;
						}
					}
				}
			}
		}
		
		if (!this.worldObj.isRemote) {
			++EaglerMinecraftServer.counterLightUpdate;
		}
		this.isModified = true;
	}

	/**
	 * Propagates a given sky-visible block's light value downward and upward to
	 * neighboring blocks as necessary.
	 */
	private void propagateSkylightOcclusion(int x, int z) {
		this.updateSkylightColumns[x + z * 16] = true;
		this.isGapLightingUpdated = true;
	}

	private void recheckGaps(boolean p_150803_1_) {
		if (!worldObj.isAreaLoaded(new BlockPos((xPosition << 4) + 8, 0, (zPosition << 4) + 8), 16)) {
			return;
		}
		
		if (!this.worldObj.isRemote) {
			++EaglerMinecraftServer.counterLightUpdate;
		}

		final WorldChunkSlice slice = new WorldChunkSlice(worldObj.getChunkProvider(), xPosition, zPosition);

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				if (!alfheim$recheckGapsForColumn(slice, x, z))
					continue;

				if (p_150803_1_)
					return;
			}
		}

		isGapLightingUpdated = false;
	}

	/**
	 * Checks the height of a block next to a sky-visible block and schedules a
	 * lighting update as necessary.
	 */
	private void checkSkylightNeighborHeight(int x, int z, int maxValue) {
		int i = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();

		if (i > maxValue) {
			this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
		} else if (i < maxValue) {
			this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
		}
	}

	private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
		if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
			for (int i = startY; i < endY; ++i) {
				this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
			}

			this.isModified = true;
		}
	}

	/**
	 * Initiates the recalculation of both the block-light and sky-light for a given
	 * block inside a chunk.
	 */
	private void relightBlock(int x, int y, int z) {
		int heightMapY = heightMap[z << 4 | x] & 255;
		int newHeightMapY = Math.max(y, heightMapY);

		while (newHeightMapY > 0 && getBlockLightOpacity(x, newHeightMapY - 1, z) == 0)
			--newHeightMapY;

		if (newHeightMapY == heightMapY)
			return;
		
		if (!this.worldObj.isRemote) {
			++EaglerMinecraftServer.counterLightUpdate;
		}

		heightMap[z << 4 | x] = newHeightMapY;

		if (!worldObj.provider.getHasNoSky())
			alfheim$relightSkylightColumn(x, z, heightMapY, newHeightMapY);

		final int heightMapY1 = heightMap[z << 4 | x];

		if (heightMapY1 < heightMapMinimum) {
			heightMapMinimum = heightMapY1;
		}
	}

	public int getBlockLightOpacity(BlockPos pos) {
		return this.getBlockState(pos).getLightOpacity();
	}

	private int getBlockLightOpacity(int x, int y, int z) {
		return this.getBlockState(x, y, z).getLightOpacity();
	}

	public IBlockState getBlockState(BlockPos pos) {
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public IBlockState getBlockState(final int x, final int y, final int z) {
		if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
			IBlockState iblockstate = null;

			if (y == 60) {
				iblockstate = Blocks.BARRIER.getDefaultState();
			}

			if (y == 70) {
				iblockstate = ChunkGeneratorDebug.getBlockStateFor(x, z);
			}

			return iblockstate == null ? Blocks.AIR.getDefaultState() : iblockstate;
		} else {
			try {
				if (y >= 0 && y >> 4 < this.storageArrays.length) {
					ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

					if (extendedblockstorage != NULL_BLOCK_STORAGE) {
						return extendedblockstorage.get(x & 15, y & 15, z & 15);
					}
				}

				return Blocks.AIR.getDefaultState();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
				crashreportcategory.setDetail("Location", new ICrashReportDetail<String>() {
					public String call() throws Exception {
						return CrashReportCategory.getCoordinateInfo(x, y, z);
					}
				});
				throw new ReportedException(crashreport);
			}
		}
	}

	@Nullable
	public IBlockState setBlockState(BlockPos pos, IBlockState state) {
		int i = pos.x & 15;
		int j = pos.y;
		int k = pos.z & 15;
		int l = k << 4 | i;
		if (j >= this.precipitationHeightMap[l] - 1) {
			this.precipitationHeightMap[l] = -999;
		}

		int i1 = this.heightMap[l];
		IBlockState iblockstate = this.getBlockState(pos);
		if (iblockstate == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block1 = iblockstate.getBlock();
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
			boolean flag = false;
			if (extendedblockstorage == null) {
				if (block == Blocks.AIR) {
					return null;
				}

				extendedblockstorage = this.storageArrays[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4,
						!this.worldObj.provider.getHasNoSky());
				alfheim$initSkylightForSection(extendedblockstorage);
			}

			extendedblockstorage.set(i, j & 15, k, state);
			if (block1 != block) {
				if (!this.worldObj.isRemote) {
					block1.breakBlock(this.worldObj, pos, iblockstate);
				} else if (block1 instanceof ITileEntityProvider) {
					this.worldObj.removeTileEntity(pos);
				}
			}

			if (extendedblockstorage.getBlockByExtId(i, j & 15, k) != block) {
				return null;
			} else {
				if (flag) {
					this.generateSkylightMap();
				} else {
					int j1 = state.getLightOpacity();
					if (j1 > 0) {
						if (j >= i1) {
							this.relightBlock(i, j + 1, k);
						}
					} else if (j == i1 - 1) {
						this.relightBlock(i, j, k);
					}
				}

				if (block1 instanceof ITileEntityProvider) {
					TileEntity tileentity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
					if (tileentity != null) {
						tileentity.updateContainingBlockInfo();
					}
				}

				if (!this.worldObj.isRemote && block1 != block) {
					block.onBlockAdded(this.worldObj, pos, state);
				}

				if (block instanceof ITileEntityProvider) {
					TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
					if (tileentity1 == null) {
						tileentity1 = ((ITileEntityProvider) block).createNewTileEntity(this.worldObj,
								block.getMetaFromState(state));
						this.worldObj.setTileEntity(pos, tileentity1);
					}

					if (tileentity1 != null) {
						tileentity1.updateContainingBlockInfo();
					}
				}

				this.isModified = true;
				return iblockstate;
			}
		}
	}

	public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos) {
		alfheim$lightingEngine.processLightUpdatesForType(p_177413_1_);
		return alfheim$getCachedLightFor(p_177413_1_, pos);
	}

	public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value) {
		int j = pos.x & 15;
		int k = pos.y;
		int l = pos.z & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[k >> 4];
		if (extendedblockstorage == null) {
			extendedblockstorage = this.storageArrays[k >> 4] = new ExtendedBlockStorage(k >> 4 << 4,
					!this.worldObj.provider.getHasNoSky());
			alfheim$initSkylightForSection(storageArrays[k >> 4]);
		}

		this.isModified = true;
		if (p_177431_1_ == EnumSkyBlock.SKY) {
			if (!this.worldObj.provider.getHasNoSky()) {
				extendedblockstorage.setExtSkylightValue(j, k & 15, l, value);
			}
		} else if (p_177431_1_ == EnumSkyBlock.BLOCK) {
			extendedblockstorage.setExtBlocklightValue(j, k & 15, l, value);
		}
	}

	public int getLightSubtracted(BlockPos pos, int amount) {
		alfheim$lightingEngine.processLightUpdates();
		int j = pos.x & 15;
		int k = pos.y;
		int l = pos.z & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[k >> 4];
		if (extendedblockstorage == null) {
			return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue
					? EnumSkyBlock.SKY.defaultLightValue - amount
					: 0;
		} else {
			int i1 = this.worldObj.provider.getHasNoSky() ? 0
					: extendedblockstorage.getExtSkylightValue(j, k & 15, l);
			i1 = i1 - amount;
			int j1 = extendedblockstorage.getExtBlocklightValue(j, k & 15, l);
			if (j1 > i1) {
				i1 = j1;
			}

			return i1;
		}
	}

	/**
	 * Adds an entity to the chunk.
	 */
	public void addEntity(Entity entityIn) {
		this.hasEntities = true;
		int i = MathHelper.floor(entityIn.posX / 16.0D);
		int j = MathHelper.floor(entityIn.posZ / 16.0D);

		if (i != this.xPosition || j != this.zPosition) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", Integer.valueOf(i), Integer.valueOf(j),
					Integer.valueOf(this.xPosition), Integer.valueOf(this.zPosition), entityIn);
			entityIn.setDead();
		}

		int k = MathHelper.floor(entityIn.posY / 16.0D);

		if (k < 0) {
			k = 0;
		}

		if (k >= this.entityLists.length) {
			k = this.entityLists.length - 1;
		}

		entityIn.addedToChunk = true;
		entityIn.chunkCoordX = this.xPosition;
		entityIn.chunkCoordY = k;
		entityIn.chunkCoordZ = this.zPosition;
		this.entityLists[k].add(entityIn);
	}

	/**
	 * removes entity using its y chunk coordinate as its index
	 */
	public void removeEntity(Entity entityIn) {
		this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
	}

	/**
	 * Removes entity at the specified index from the entity array.
	 */
	public void removeEntityAtIndex(Entity entityIn, int index) {
		if (index < 0) {
			index = 0;
		}

		if (index >= this.entityLists.length) {
			index = this.entityLists.length - 1;
		}

		this.entityLists[index].remove(entityIn);
	}

	public boolean canSeeSky(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		return j >= this.heightMap[k << 4 | i];
	}

	@Nullable
	private TileEntity createNewTileEntity(BlockPos pos) {
		IBlockState iblockstate = this.getBlockState(pos);
		Block block = iblockstate.getBlock();
		return !block.hasTileEntity() ? null
				: ((ITileEntityProvider) block).createNewTileEntity(this.worldObj,
						iblockstate.getBlock().getMetaFromState(iblockstate));
	}

	@Nullable
	public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_) {
		TileEntity tileentity = this.chunkTileEntityMap.get(pos);

		if (tileentity == null) {
			if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE) {
				tileentity = this.createNewTileEntity(pos);
				this.worldObj.setTileEntity(pos, tileentity);
			} else if (p_177424_2_ == Chunk.EnumCreateEntityType.QUEUED) {
				this.tileEntityPosQueue.add(pos);
			}
		} else if (tileentity.isInvalid()) {
			this.chunkTileEntityMap.remove(pos);
			return null;
		}

		return tileentity;
	}

	public void addTileEntity(TileEntity tileEntityIn) {
		this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);

		if (this.isChunkLoaded) {
			this.worldObj.addTileEntity(tileEntityIn);
		}
	}

	public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
		tileEntityIn.setWorldObj(this.worldObj);
		tileEntityIn.setPos(pos);

		if (this.getBlockState(pos).getBlock() instanceof ITileEntityProvider) {
			if (this.chunkTileEntityMap.containsKey(pos)) {
				((TileEntity) this.chunkTileEntityMap.get(pos)).invalidate();
			}

			tileEntityIn.validate();
			this.chunkTileEntityMap.put(pos, tileEntityIn);
		}
	}

	public void removeTileEntity(BlockPos pos) {
		if (this.isChunkLoaded) {
			TileEntity tileentity = this.chunkTileEntityMap.remove(pos);

			if (tileentity != null) {
				tileentity.invalidate();
			}
		}
	}

	/**
	 * Called when this Chunk is loaded by the ChunkProvider
	 */
	public void onChunkLoad() {
		this.isChunkLoaded = true;
		this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

		for (int i = 0; i < this.entityLists.length; ++i) {
			this.worldObj.loadEntities(this.entityLists[i]);
		}
		
		for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
			final int xOffset = facing.getFrontOffsetX();
			final int zOffset = facing.getFrontOffsetZ();

			final Chunk nChunk = worldObj.getChunkProvider().getLoadedChunk(xPosition + xOffset, zPosition + zOffset);

			if (nChunk == null)
				continue;

			EnumSkyBlock[] lightTypes = EnumSkyBlock._VALUES;
			EnumFacing.AxisDirection[] axisDirections = EnumFacing.AxisDirection._VALUES;
			for (int ii = 0, ll = lightTypes.length; ii < ll; ++ii) {
				final EnumSkyBlock lightType = lightTypes[ii];
				for (int jj = 0, mm = axisDirections.length; jj < mm; ++jj) {
					final EnumFacing.AxisDirection axisDir = axisDirections[jj];
					// Merge flags upon loading of a chunk. This ensures that all flags are always
					// already on the IN boundary below
					alfheim$mergeFlags(lightType, this, nChunk, facing, axisDir);
					alfheim$mergeFlags(lightType, nChunk, this, facing.getOpposite(), axisDir);

					// Check everything that might have been canceled due to this chunk not being
					// loaded.
					// Also, pass in chunks if already known
					// The boundary to the neighbor chunk (both ways)
					alfheim$scheduleRelightChecksForBoundary(this, nChunk, null, lightType, xOffset, zOffset, axisDir);
					alfheim$scheduleRelightChecksForBoundary(nChunk, this, null, lightType, -xOffset, -zOffset,
							axisDir);
					// The boundary to the diagonal neighbor (since the checks in that chunk were
					// aborted if this chunk wasn't loaded, see
					// alfheim$scheduleRelightChecksForBoundary)
					alfheim$scheduleRelightChecksForBoundary(nChunk, null, this, lightType,
							(zOffset != 0 ? axisDir.getOffset() : 0), (xOffset != 0 ? axisDir.getOffset() : 0),
							facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE
									? EnumFacing.AxisDirection.NEGATIVE
									: EnumFacing.AxisDirection.POSITIVE);
				}
			}
		}
	}

	/**
	 * Called when this Chunk is unloaded by the ChunkProvider
	 */
	public void onChunkUnload() {
		this.isChunkLoaded = false;

		for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
			this.worldObj.markTileEntityForRemoval(tileentity);
		}

		for (int i = 0; i < this.entityLists.length; ++i) {
			this.worldObj.unloadEntities(this.entityLists[i]);
		}
	}

	/**
	 * Sets the isModified flag for this Chunk
	 */
	public void setChunkModified() {
		this.isModified = true;
	}

	/**
	 * Fills the given list of all entities that intersect within the given bounding
	 * box that aren't the passed entity.
	 */
	public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill,
			Predicate<? super Entity> p_177414_4_) {
		int i = MathHelper.floor((aabb.minY - 2.0D) / 16.0D);
		int j = MathHelper.floor((aabb.maxY + 2.0D) / 16.0D);
		i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
		j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

		for (int k = i; k <= j; ++k) {
			if (!this.entityLists[k].isEmpty()) {
				for (Entity entity : this.entityLists[k]) {
					if (entity.getEntityBoundingBox().intersectsWith(aabb) && entity != entityIn) {
						if (p_177414_4_ == null || p_177414_4_.apply(entity)) {
							listToFill.add(entity);
						}

						Entity[] aentity = entity.getParts();

						if (aentity != null) {
							for (Entity entity1 : aentity) {
								if (entity1 != entityIn && entity1.getEntityBoundingBox().intersectsWith(aabb)
										&& (p_177414_4_ == null || p_177414_4_.apply(entity1))) {
									listToFill.add(entity1);
								}
							}
						}
					}
				}
			}
		}
	}

	public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> entityClass, AxisAlignedBB aabb,
			List<T> listToFill, Predicate<? super T> filter) {
		int i = MathHelper.floor((aabb.minY - 2.0D) / 16.0D);
		int j = MathHelper.floor((aabb.maxY + 2.0D) / 16.0D);
		i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
		j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

		for (int k = i; k <= j; ++k) {
			for (T t : this.entityLists[k].getByClass(entityClass)) {
				if (t.getEntityBoundingBox().intersectsWith(aabb) && (filter == null || filter.apply(t))) {
					listToFill.add(t);
				}
			}
		}
	}

	/**
	 * Returns true if this Chunk needs to be saved
	 */
	public boolean needsSaving(boolean p_76601_1_) {
		if (p_76601_1_) {
			if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
				return true;
			}
		} else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
			return true;
		}

		return this.isModified;
	}

	public EaglercraftRandom getRandomWithSeed(long seed) {
		return new EaglercraftRandom(this.worldObj.getSeed() + (long) (this.xPosition * this.xPosition * 4987142)
				+ (long) (this.xPosition * 5947611) + (long) (this.zPosition * this.zPosition) * 4392871L
				+ (long) (this.zPosition * 389711) ^ seed, !this.worldObj.getWorldInfo().isOldEaglercraftRandom());
	}

	public boolean isEmpty() {
		return false;
	}

	public void populateChunk(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator) {
		Chunk chunk = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition - 1);
		Chunk chunk1 = chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition);
		Chunk chunk2 = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition + 1);
		Chunk chunk3 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition);

		if (chunk1 != null && chunk2 != null
				&& chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition + 1) != null) {
			this.populateChunk(chunkGenrator);
		}

		if (chunk3 != null && chunk2 != null
				&& chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition + 1) != null) {
			chunk3.populateChunk(chunkGenrator);
		}

		if (chunk != null && chunk1 != null
				&& chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition - 1) != null) {
			chunk.populateChunk(chunkGenrator);
		}

		if (chunk != null && chunk3 != null) {
			Chunk chunk4 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition - 1);

			if (chunk4 != null) {
				chunk4.populateChunk(chunkGenrator);
			}
		}
	}

	protected void populateChunk(IChunkGenerator generator) {
		if (this.isTerrainPopulated()) {
			if (generator.generateStructures(this, this.xPosition, this.zPosition)) {
				this.setChunkModified();
			}
		} else {
			this.checkLight();
			generator.populate(this.xPosition, this.zPosition);
			this.setChunkModified();
		}
	}

	public BlockPos getPrecipitationHeight(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = i | j << 4;
		BlockPos blockpos = new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());

		if (blockpos.getY() == -999) {
			int l = this.getTopFilledSegment() + 15;
			blockpos = new BlockPos(pos.getX(), l, pos.getZ());
			int i1 = -1;

			while (blockpos.getY() > 0 && i1 == -1) {
				IBlockState iblockstate = this.getBlockState(blockpos);
				Material material = iblockstate.getMaterial();

				if (!material.blocksMovement() && !material.isLiquid()) {
					blockpos = blockpos.down();
				} else {
					i1 = blockpos.getY() + 1;
				}
			}

			this.precipitationHeightMap[k] = i1;
		}

		return new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
	}

	public void onTick(boolean p_150804_1_) {
		if (this.isGapLightingUpdated && this.worldObj.provider.func_191066_m() && !p_150804_1_) {
			this.recheckGaps(this.worldObj.isRemote);
		}

		this.chunkTicked = true;

		if (!this.isLightPopulated && this.isTerrainPopulated) {
			this.checkLight();
		}

		while (!this.tileEntityPosQueue.isEmpty()) {
			BlockPos blockpos = this.tileEntityPosQueue.remove(0);

			if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null
					&& this.getBlockState(blockpos).getBlock().hasTileEntity()) {
				TileEntity tileentity = this.createNewTileEntity(blockpos);
				this.worldObj.setTileEntity(blockpos, tileentity);
				this.worldObj.markBlockRangeForRenderUpdate(blockpos, blockpos);
			}
		}
	}

	public boolean isPopulated() {
		return this.chunkTicked && this.isTerrainPopulated && this.isLightPopulated;
	}

	public boolean isChunkTicked() {
		return this.chunkTicked;
	}

	/**
	 * Gets a ChunkCoordIntPair representing the Chunk's position.
	 */
	public ChunkPos getChunkCoordIntPair() {
		return coordsCache;
	}

	/**
	 * Returns whether the ExtendedBlockStorages containing levels (in blocks) from
	 * arg 1 to arg 2 are fully empty (true) or not (false).
	 */
	public boolean getAreLevelsEmpty(int startY, int endY) {
		if (startY < 0) {
			startY = 0;
		}

		if (endY >= 256) {
			endY = 255;
		}

		for (int i = startY; i <= endY; i += 16) {
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[i >> 4];

			if (extendedblockstorage != NULL_BLOCK_STORAGE && !extendedblockstorage.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
		if (this.storageArrays.length != newStorageArrays.length) {
			LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}",
					Integer.valueOf(newStorageArrays.length), Integer.valueOf(this.storageArrays.length));
		} else {
			for (int i = 0; i < this.storageArrays.length; ++i) {
				this.storageArrays[i] = newStorageArrays[i];
			}
		}
	}

	public void fillChunk(PacketBuffer buf, int p_186033_2_, boolean p_186033_3_) {
		boolean flag = this.worldObj.provider.func_191066_m();

		for (int i = 0; i < this.storageArrays.length; ++i) {
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[i];

			if ((p_186033_2_ & 1 << i) == 0) {
				if (p_186033_3_ && extendedblockstorage != NULL_BLOCK_STORAGE) {
					this.storageArrays[i] = NULL_BLOCK_STORAGE;
				}
			} else {
				if (extendedblockstorage == NULL_BLOCK_STORAGE) {
					extendedblockstorage = new ExtendedBlockStorage(i << 4, flag);
					this.storageArrays[i] = extendedblockstorage;
				}

				extendedblockstorage.getData().read(buf);
				buf.readBytes(extendedblockstorage.getBlocklightArray().getData());

				if (flag) {
					buf.readBytes(extendedblockstorage.getSkylightArray().getData());
				}
			}
		}

		if (p_186033_3_) {
			buf.readBytes(this.blockBiomeArray);
		}

		for (int j = 0; j < this.storageArrays.length; ++j) {
			if (this.storageArrays[j] != NULL_BLOCK_STORAGE && (p_186033_2_ & 1 << j) != 0) {
				this.storageArrays[j].removeInvalidBlocks();
			}
		}

		this.isLightPopulated = true;
		this.isTerrainPopulated = true;
		this.generateHeightMap();

		for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
			tileentity.updateContainingBlockInfo();
		}
	}

	public Biome getBiome(BlockPos pos, BiomeProvider provider) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = this.blockBiomeArray[j << 4 | i] & 255;

		if (k == 255) {
			Biome biome = provider.getBiome(pos, Biomes.PLAINS);
			k = Biome.getIdForBiome(biome);
			this.blockBiomeArray[j << 4 | i] = (byte) (k & 255);
		}

		Biome biome1 = Biome.getBiome(k);
		return biome1 == null ? Biomes.PLAINS : biome1;
	}

	/**
	 * Returns an array containing a 16x16 mapping on the X/Z of block positions in
	 * this Chunk to biome IDs.
	 */
	public byte[] getBiomeArray() {
		return this.blockBiomeArray;
	}

	/**
	 * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane of
	 * block positions in this Chunk to biome IDs.
	 */
	public void setBiomeArray(byte[] biomeArray) {
		if (this.blockBiomeArray.length != biomeArray.length) {
			LOGGER.warn("Could not set level chunk biomes, array length is {} instead of {}",
					Integer.valueOf(biomeArray.length), Integer.valueOf(this.blockBiomeArray.length));
		} else {
			for (int i = 0; i < this.blockBiomeArray.length; ++i) {
				this.blockBiomeArray[i] = biomeArray[i];
			}
		}
	}

	/**
	 * Resets the relight check index to 0 for this Chunk.
	 */
	public void resetRelightChecks() {
		this.queuedLightChecks = 0;
	}

	/**
	 * Called once-per-chunk-per-tick, and advances the round-robin relight check
	 * index by up to 8 blocks at a time. In a worst-case scenario, can potentially
	 * take up to 25.6 seconds, calculated via (4096/8)/20, to re-check all blocks
	 * in a chunk, which may explain lagging light updates on initial world
	 * generation.
	 */
	public void enqueueRelightChecks() {
		EnumFacing[] facings = EnumFacing._VALUES;
		if (this.queuedLightChecks < 4096) {
			BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

			for (int i = 0; i < 8; ++i) {
				if (this.queuedLightChecks >= 4096) {
					return;
				}

				int j = this.queuedLightChecks % 16;
				int k = this.queuedLightChecks / 16 % 16;
				int l = this.queuedLightChecks / 256;
				++this.queuedLightChecks;

				for (int i1 = 0; i1 < 16; ++i1) {
					BlockPos blockpos1 = blockpos.add(k, (j << 4) + i1, l);
					boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

					if (this.storageArrays[j] == NULL_BLOCK_STORAGE && flag
							|| this.storageArrays[j] != NULL_BLOCK_STORAGE
									&& this.storageArrays[j].get(k, i1, l).getMaterial() == Material.AIR) {
						for (int m = 0; m < facings.length; ++m) {
							BlockPos blockpos2 = blockpos1.offset(facings[m]);

							if (this.worldObj.getBlockState(blockpos2).getLightValue() > 0) {
								this.worldObj.checkLight(blockpos2);
							}
						}

						this.worldObj.checkLight(blockpos1);
					}
				}
			}
		}
	}

	public void checkLight() {
		this.isTerrainPopulated = true;

		if (!alfheim$isLightInitialized)
			alfheim$initChunkLighting(this, worldObj);

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0)
					continue;

				final Chunk nChunk = worldObj.getChunkProvider().getLoadedChunk(xPosition + x, zPosition + z);

				if (nChunk == null || !nChunk.alfheim$isLightInitialized())
					return;
			}
		}

		setLightPopulated(true);
	}

	private void setSkylightUpdated() {
		for (int i = 0; i < this.updateSkylightColumns.length; ++i) {
			this.updateSkylightColumns[i] = true;
		}

		this.recheckGaps(false);
	}

	public boolean isLoaded() {
		return this.isChunkLoaded;
	}

	public void setChunkLoaded(boolean loaded) {
		this.isChunkLoaded = loaded;
	}

	public World getWorld() {
		return this.worldObj;
	}

	public int[] getHeightMap() {
		return this.heightMap;
	}

	public void setHeightMap(int[] newHeightMap) {
		if (this.heightMap.length != newHeightMap.length) {
			LOGGER.warn("Could not set level chunk heightmap, array length is {} instead of {}",
					Integer.valueOf(newHeightMap.length), Integer.valueOf(this.heightMap.length));
		} else {
			for (int i = 0; i < this.heightMap.length; ++i) {
				this.heightMap[i] = newHeightMap[i];
			}
		}
	}

	public Map<BlockPos, TileEntity> getTileEntityMap() {
		return this.chunkTileEntityMap;
	}

	public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
		return this.entityLists;
	}

	public boolean isTerrainPopulated() {
		return this.isTerrainPopulated;
	}

	public void setTerrainPopulated(boolean terrainPopulated) {
		this.isTerrainPopulated = terrainPopulated;
	}

	public boolean isLightPopulated() {
		return this.isLightPopulated;
	}

	public void setLightPopulated(boolean lightPopulated) {
		this.isLightPopulated = lightPopulated;
	}

	public void setModified(boolean modified) {
		this.isModified = modified;
	}

	public void setHasEntities(boolean hasEntitiesIn) {
		this.hasEntities = hasEntitiesIn;
	}

	public void setLastSaveTime(long saveTime) {
		this.lastSaveTime = saveTime;
	}

	public int getLowestHeight() {
		return this.heightMapMinimum;
	}

	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	public void setInhabitedTime(long newInhabitedTime) {
		this.inhabitedTime = newInhabitedTime;
	}

	public static enum EnumCreateEntityType {
		IMMEDIATE, QUEUED, CHECK;
	}
	
	private boolean alfheim$recheckGapsForColumn(final WorldChunkSlice slice, final int x, final int z) {
		final int i = x + (z << 4);

		if (updateSkylightColumns[i]) {
			updateSkylightColumns[i] = false;

			final int x1 = (this.xPosition << 4) + x;
			final int z1 = (this.zPosition << 4) + z;

			alfheim$recheckGapsSkylightNeighborHeight(slice, x1, z1, getHeightValue(x, z),
					alfheim$recheckGapsGetLowestHeight(slice, x1, z1));

			return true;
		}

		return false;
	}

	private int alfheim$recheckGapsGetLowestHeight(final WorldChunkSlice slice, final int x, final int z) {
		int max = Integer.MAX_VALUE;

		Chunk chunk = slice.getChunkFromWorldCoords(x + 1, z);

		if (chunk != null)
			max = Math.min(max, chunk.getLowestHeight());

		chunk = slice.getChunkFromWorldCoords(x, z + 1);

		if (chunk != null)
			max = Math.min(max, chunk.getLowestHeight());

		chunk = slice.getChunkFromWorldCoords(x - 1, z);

		if (chunk != null)
			max = Math.min(max, chunk.getLowestHeight());

		chunk = slice.getChunkFromWorldCoords(x, z - 1);

		if (chunk != null)
			max = Math.min(max, chunk.getLowestHeight());

		return max;
	}

	private void alfheim$recheckGapsSkylightNeighborHeight(final WorldChunkSlice slice, final int x, final int z,
			final int height, final int max) {
		alfheim$checkSkylightNeighborHeight(slice, x, z, max);
		alfheim$checkSkylightNeighborHeight(slice, x + 1, z, height);
		alfheim$checkSkylightNeighborHeight(slice, x, z + 1, height);
		alfheim$checkSkylightNeighborHeight(slice, x - 1, z, height);
		alfheim$checkSkylightNeighborHeight(slice, x, z - 1, height);
	}

	private void alfheim$checkSkylightNeighborHeight(final WorldChunkSlice slice, final int x, final int z,
			final int maxValue) {
		Chunk c = slice.getChunkFromWorldCoords(x, z);
		if (c == null)
			return;

		final int y = c.getHeightValue(x & 15, z & 15);

		if (y > maxValue)
			alfheim$updateSkylightNeighborHeight(slice, x, z, maxValue, y + 1);
		else if (y < maxValue)
			alfheim$updateSkylightNeighborHeight(slice, x, z, y, maxValue + 1);
	}

	private void alfheim$updateSkylightNeighborHeight(final WorldChunkSlice slice, final int x, final int z,
			final int startY, final int endY) {
		if (endY < startY)
			return;

		if (!slice.isLoaded(x, z, 16))
			return;

		for (int y = startY; y < endY; ++y)
			worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, y, z));

		isModified = true;
	}

	private static void alfheim$mergeFlags(final EnumSkyBlock lightType, final Chunk inChunk, final Chunk outChunk,
			final EnumFacing dir, final EnumFacing.AxisDirection axisDirection) {
		if (outChunk.alfheim$neighborLightChecks == null)
			return;

		inChunk.alfheim$initNeighborLightChecks();

		final int inIndex = alfheim$getFlagIndex(lightType, dir, axisDirection, EnumBoundaryFacing.IN);
		final int outIndex = alfheim$getFlagIndex(lightType, dir.getOpposite(), axisDirection, EnumBoundaryFacing.OUT);

		inChunk.alfheim$neighborLightChecks[inIndex] |= outChunk.alfheim$neighborLightChecks[outIndex];
		// No need to call Chunk.setModified() since checks are not deleted from
		// outChunk
	}

	private void alfheim$scheduleRelightChecksForBoundary(final Chunk chunk, Chunk nChunk, Chunk sChunk,
			final EnumSkyBlock lightType, final int xOffset, final int zOffset,
			final EnumFacing.AxisDirection axisDirection) {
		if (chunk.alfheim$neighborLightChecks == null)
			return;

		final int flagIndex = alfheim$getFlagIndex(lightType, xOffset, zOffset, axisDirection, EnumBoundaryFacing.IN); // OUT
																														// checks
																														// from
																														// neighbor
																														// are
																														// already
																														// merged

		final int flags = chunk.alfheim$neighborLightChecks[flagIndex];

		if (flags == 0)
			return;

		if (nChunk == null) {
			nChunk = worldObj.getChunkProvider().getLoadedChunk(chunk.xPosition + xOffset, chunk.zPosition + zOffset);

			if (nChunk == null)
				return;
		}

		if (sChunk == null) {
			sChunk = worldObj.getChunkProvider().getLoadedChunk(
					chunk.xPosition + (zOffset != 0 ? axisDirection.getOffset() : 0),
					chunk.zPosition + (xOffset != 0 ? axisDirection.getOffset() : 0));

			if (sChunk == null)
				return; // Cancel, since the checks in the corner columns require the corner column of
						// sChunk
		}

		final int reverseIndex = alfheim$getFlagIndex(lightType, -xOffset, -zOffset, axisDirection,
				EnumBoundaryFacing.OUT);

		chunk.alfheim$neighborLightChecks[flagIndex] = 0;

		if (alfheim$neighborLightChecks != null)
			nChunk.alfheim$neighborLightChecks[reverseIndex] = 0; // Clear only now that it's clear that the checks
																	// are processed

		chunk.setChunkModified();
		nChunk.setChunkModified();

		// Get the area to check
		// Start in the corner...
		int xMin = chunk.xPosition << 4;
		int zMin = chunk.zPosition << 4;

		// Move to other side of chunk if the direction is positive
		if ((xOffset | zOffset) > 0) {
			xMin += 15 * xOffset;
			zMin += 15 * zOffset;
		}

		// Shift to other half if necessary (shift perpendicular to dir)
		if (axisDirection == EnumFacing.AxisDirection.POSITIVE) {
			xMin += 8 * (zOffset & 1); // x & 1 is same as abs(x) for x=-1,0,1
			zMin += 8 * (xOffset & 1);
		}

		// Get maximal values (shift perpendicular to dir)
		final int xMax = xMin + 7 * (zOffset & 1);
		final int zMax = zMin + 7 * (xOffset & 1);

		for (int y = 0; y < 16; ++y)
			if ((flags & (1 << y)) != 0)
				for (int x = xMin; x <= xMax; ++x)
					for (int z = zMin; z <= zMax; ++z)
						alfheim$scheduleRelightChecksForColumn(lightType, x, z, y << 4, (y << 4) + 15);
	}

	private void alfheim$initSkylightForSection(final ExtendedBlockStorage extendedBlockStorage) {
		if (worldObj.provider.getHasNoSky())
			return;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				if (getHeightValue(x, z) > extendedBlockStorage.getYLocation())
					continue;

				for (int y = 0; y < 16; ++y)
					extendedBlockStorage.setExtSkylightValue(x, y, z, EnumSkyBlock.SKY.defaultLightValue);
			}
		}
	}

	private void alfheim$scheduleRelightChecksForColumn(final EnumSkyBlock lightType, final int x, final int z,
			final int yMin, final int yMax) {
		final BlockPos mutableBlockPos = new BlockPos();

		for (int y = yMin; y <= yMax; ++y)
			worldObj.checkLightFor(lightType, mutableBlockPos.setPos(x, y, z));
	}

	private static int alfheim$getFlagIndex(final EnumSkyBlock lightType, final int xOffset, final int zOffset,
			final EnumFacing.AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing) {
		return (lightType == EnumSkyBlock.BLOCK ? 0 : 16) | ((xOffset + 1) << 2) | ((zOffset + 1) << 1)
				| (axisDirection.getOffset() + 1) | boundaryFacing.ordinal();
	}

	private static int alfheim$getFlagIndex(final EnumSkyBlock lightType, final EnumFacing facing,
			final EnumFacing.AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing) {
		return alfheim$getFlagIndex(lightType, facing.getFrontOffsetX(), facing.getFrontOffsetZ(), axisDirection,
				boundaryFacing);
	}

	private static void alfheim$initChunkLighting(final Chunk chunk, final World world) {
		final int xBase = chunk.xPosition << 4;
		final int zBase = chunk.zPosition << 4;

		final BlockPos mutableBlockPos = new BlockPos(xBase, 0, zBase);

		if (world.isAreaLoaded(mutableBlockPos.add(-16, 0, -16), mutableBlockPos.add(31, 255, 31), false)) {
			final ExtendedBlockStorage[] extendedBlockStorage = chunk.getBlockStorageArray();

			for (int i = 0; i < extendedBlockStorage.length; ++i) {
				final ExtendedBlockStorage storage = extendedBlockStorage[i];

				if (storage == null)
					continue;

				int yBase = i * 16;

				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							if (storage.getBlockByExtId(x, y, z).getBlockState().getBaseState().getLightValue() > 0) {
								mutableBlockPos.setPos(xBase + x, yBase + y, zBase + z);
								world.checkLightFor(EnumSkyBlock.BLOCK, mutableBlockPos);
							}
						}
					}
				}
			}

			if (!world.provider.getHasNoSky())
				chunk.alfheim$setSkylightUpdatedPublic();

			chunk.alfheim$setLightInitialized(true);
		}
	}

	private void alfheim$relightSkylightColumn(final int x, final int z, final int height1, final int height2) {
		final int yMin = Math.min(height1, height2);
		final int yMax = Math.max(height1, height2) - 1;

		final ExtendedBlockStorage[] sections = getBlockStorageArray();

		final int xBase = (xPosition << 4) + x;
		final int zBase = (zPosition << 4) + z;

		alfheim$scheduleRelightChecksForColumn(EnumSkyBlock.SKY, xBase, zBase, yMin, yMax);

		if (sections[yMin >> 4] == null && yMin > 0) {
			worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase, yMin - 1, zBase));
		}

		short emptySections = 0;

		for (int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
			if (sections[sec] == null) {
				emptySections |= (short) (1 << sec);
			}
		}

		if (emptySections != 0) {
			for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
				final int xOffset = facing.getFrontOffsetX();
				final int zOffset = facing.getFrontOffsetZ();

				final boolean neighborColumnExists = (((x + xOffset) | (z + zOffset)) & 16) == 0
						// Checks whether the position is at the specified border (the 16 bit is set for
						// both 15+1 and 0-1)
						|| worldObj.getChunkProvider().getLoadedChunk(xPosition + xOffset, zPosition + zOffset) != null;

				if (neighborColumnExists) {
					for (int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
						if ((emptySections & (1 << sec)) != 0)
							alfheim$scheduleRelightChecksForColumn(EnumSkyBlock.SKY, xBase + xOffset, zBase + zOffset,
									sec << 4, (sec << 4) + 15);
					}
				} else {
					alfheim$initNeighborLightChecks();

					final EnumFacing.AxisDirection axisDirection = ((facing.getAxis() == EnumFacing.Axis.X ? z : x)
							& 15) < 8 ? EnumFacing.AxisDirection.NEGATIVE : EnumFacing.AxisDirection.POSITIVE;
					alfheim$neighborLightChecks[alfheim$getFlagIndex(EnumSkyBlock.SKY, facing, axisDirection,
							EnumBoundaryFacing.OUT)] |= emptySections;

					setChunkModified();
				}
			}
		}
	}

	public LightingEngine alfheim$getLightingEngine() {
		return alfheim$lightingEngine;
	}

	public boolean alfheim$isLightInitialized() {
		return alfheim$isLightInitialized;
	}

	public void alfheim$setLightInitialized(final boolean lightInitialized) {
		alfheim$isLightInitialized = lightInitialized;
	}

	public void alfheim$setSkylightUpdatedPublic() {
		setSkylightUpdated();
	}

	public void alfheim$initNeighborLightChecks() {
		if (alfheim$neighborLightChecks == null) {
			alfheim$neighborLightChecks = new short[32];
		}
	}

	public byte alfheim$getCachedLightFor(final EnumSkyBlock lightType, final BlockPos blockPos) {
		final int x = blockPos.x & 15;
		final int y = blockPos.y;
		final int z = blockPos.z & 15;

		final ExtendedBlockStorage extendedblockstorage = storageArrays[y >> 4];

		if (extendedblockstorage == null)
			return canSeeSky(blockPos) ? (byte) lightType.defaultLightValue : 0;
		else if (lightType == EnumSkyBlock.SKY)
			return !worldObj.provider.getHasNoSky() ? (byte) extendedblockstorage.getExtSkylightValue(x, y & 15, z) : 0;
		else
			return lightType == EnumSkyBlock.BLOCK ? (byte) extendedblockstorage.getExtBlocklightValue(x, y & 15, z)
					: (byte) lightType.defaultLightValue;
	}
}
