package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class RenderChunk {
	private World world;
	private final RenderGlobal renderGlobal;
	public static int renderChunksUpdated;
	private BlockPos position;
	public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
	private ChunkCompileTaskGenerator compileTask;
	private final Set<TileEntity> setTileEntities = Sets.<TileEntity>newHashSet();
	public AxisAlignedBB boundingBox;
	private int frameIndex = -1;
	private boolean needsUpdate = true;
	private boolean needsUpdateCustom;
	private boolean playerUpdate = false;
	
	private RenderChunk[] renderChunksOfset16 = new RenderChunk[6];
	
	private EnumMap<EnumFacing, BlockPos> field_181702_p = Maps.newEnumMap(EnumFacing.class);

	public RenderChunk(World p_i47120_1_, RenderGlobal p_i47120_2_, BlockPos blockPosIn, int p_i47120_3_) {
		this.world = p_i47120_1_;
		this.renderGlobal = p_i47120_2_;
		if (!blockPosIn.equals(this.getPosition())) {
			this.setPosition(blockPosIn);
		}
	}

	public boolean setFrameIndex(int frameIndexIn) {
		if (this.frameIndex == frameIndexIn) {
			return false;
		} else {
			this.frameIndex = frameIndexIn;
			return true;
		}
	}

	/**
	 * Sets the RenderChunk base position
	 */
	public void setPosition(BlockPos pos) {
		this.stopCompileTask();
		this.position = pos;
		this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));

		EnumFacing[] facings = EnumFacing._VALUES;
		for (int i = 0; i < facings.length; ++i) {
			EnumFacing enumfacing = facings[i];
			this.field_181702_p.put(enumfacing, pos.offset(enumfacing, 16));
			this.renderChunksOfset16[enumfacing.ordinal()] = null;
		}
	}

	public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator) {
		CompiledChunk compiledchunk = generator.getCompiledChunk();

		if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
			this.preRenderBlocks(
					generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT),
					this.position);
			generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT)
					.setVertexState(compiledchunk.getState());
			this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z,
					generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT),
					compiledchunk);
		}
	}

	public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
		if (compiledChunk == CompiledChunk.DUMMY) {
			compiledChunk = new CompiledChunk(this);
		} else {
			compiledChunk.reset();
		}
		int i = 1;
		BlockPos blockpos = this.position;
		BlockPos blockpos1 = blockpos.add(15, 15, 15);

		ChunkCache regionrendercache;
		if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
			return;
		}
		
		regionrendercache = new ChunkCache(this.world, blockpos.add(-1, -1, -1), blockpos1.add(1, 1, 1), 1);
		generator.setCompiledChunk(compiledChunk);

		VisGraph lvt_9_1_ = new VisGraph();
		HashSet lvt_10_1_ = Sets.newHashSet();

		if (!regionrendercache.extendedLevelsInChunkCache()) {
			++renderChunksUpdated;
			boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

			for (BlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1)) {
				IBlockState iblockstate = regionrendercache.getBlockStateFaster(blockpos$mutableblockpos);
				Block block = iblockstate.getBlock();

				if (iblockstate.isOpaqueCube()) {
					lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
				}

				if (block.hasTileEntity()) {
					TileEntity tileentity = regionrendercache.getTileEntity(blockpos$mutableblockpos);

					if (tileentity != null) {
						TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance
								.<TileEntity>getSpecialRenderer(tileentity);

						if (tileentityspecialrenderer != null) {
							compiledChunk.addTileEntity(tileentity);

							if (tileentityspecialrenderer.isGlobalRenderer(tileentity)) {
								lvt_10_1_.add(tileentity);
							}
						}
					}
				}

				BlockRenderLayer blockrenderlayer1 = block.getBlockLayer();
				int j = blockrenderlayer1.ordinal();

				if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE) {
					WorldRenderer bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);

					if (!compiledChunk.isLayerStarted(blockrenderlayer1)) {
						compiledChunk.setLayerStarted(blockrenderlayer1);
						this.preRenderBlocks(bufferbuilder, blockpos);
					}

					aboolean[j] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, regionrendercache, bufferbuilder);
				}
			}

			BlockRenderLayer[] layers = BlockRenderLayer._VALUES;
			for (int ii = 0; ii < layers.length; ++ii) {
				BlockRenderLayer blockrenderlayer = layers[ii];
				if (aboolean[blockrenderlayer.ordinal()]) {
					compiledChunk.setLayerUsed(blockrenderlayer);
				}

				if (compiledChunk.isLayerStarted(blockrenderlayer)) {
					this.postRenderBlocks(blockrenderlayer, x, y, z,
							generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer),
							compiledChunk);
				}
			}
		}

		compiledChunk.setVisibility(lvt_9_1_.computeVisibility());

		Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
		Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
		set.removeAll(this.setTileEntities);
		set1.removeAll(lvt_10_1_);
		this.setTileEntities.clear();
		this.setTileEntities.addAll(lvt_10_1_);
		this.renderGlobal.updateTileEntities(set1, set);
	}

	protected void finishCompileTask() {
		if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
			this.compileTask.finish();
			this.compileTask = null;
		}
	}

	public ChunkCompileTaskGenerator makeCompileTaskChunk() {
		ChunkCompileTaskGenerator chunkcompiletaskgenerator;
		this.finishCompileTask();
		this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
		chunkcompiletaskgenerator = this.compileTask;
		return chunkcompiletaskgenerator;
	}

	@Nullable
	public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
		this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
		this.compileTask.setCompiledChunk(this.compiledChunk);
		return this.compileTask;
	}

	private void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos) {
		worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
		worldRendererIn.setTranslation((double) (-pos.x), (double) (-pos.y), (double) (-pos.z));
	}

	private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, WorldRenderer worldRendererIn,
			CompiledChunk compiledChunkIn) {
		if ((layer == BlockRenderLayer.TRANSLUCENT) && !compiledChunkIn.isLayerEmpty(layer)) {
			worldRendererIn.func_181674_a(x, y, z);
			compiledChunkIn.setState(worldRendererIn.func_181672_a());
		}

		worldRendererIn.finishDrawing();
	}

	public CompiledChunk getCompiledChunk() {
		return this.compiledChunk;
	}

	public void stopCompileTask() {
		this.finishCompileTask();
		if (this.compiledChunk != CompiledChunk.DUMMY) {
			this.compiledChunk.setState(null);
			this.compiledChunk = CompiledChunk.DUMMY;
		}
	}

	public void deleteGlResources() {
		this.stopCompileTask();
		this.world = null;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public void setNeedsUpdate(boolean needsUpdateIn) {
		if (this.needsUpdate) {
			needsUpdateIn |= this.needsUpdateCustom;
		}

		this.needsUpdate = true;
		this.needsUpdateCustom = needsUpdateIn;
		
		if (this.isWorldPlayerUpdate()) {
            this.playerUpdate = true;
        }
	}

	public void clearNeedsUpdate() {
		this.needsUpdate = false;
		this.needsUpdateCustom = false;
		this.playerUpdate = false;
	}

	public boolean isNeedsUpdate() {
		return this.needsUpdate;
	}
	
	public boolean isPlayerUpdate() {
		return this.playerUpdate;
	}
	
	private boolean isWorldPlayerUpdate()	{
        if (this.world instanceof WorldClient)	{
            WorldClient worldclient = (WorldClient)this.world;
            return worldclient.isPlayerUpdate();
        }	else	{
            return false;
        }
    }

	public boolean isNeedsUpdateCustom() {
		return this.needsUpdate && this.needsUpdateCustom;
	}

	public BlockPos getBlockPosOffset16(EnumFacing p_181701_1_) {
		return (BlockPos) this.field_181702_p.get(p_181701_1_);
	}
	
	public RenderChunk getRenderChunkOffset16(ViewFrustum p_getRenderChunkOffset16_1_, EnumFacing p_getRenderChunkOffset16_2_) {
        RenderChunk renderchunk = this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()];

        if (renderchunk == null) {
            BlockPos blockpos = this.getBlockPosOffset16(p_getRenderChunkOffset16_2_);
            renderchunk = p_getRenderChunkOffset16_1_.getRenderChunk(blockpos);
            this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()] = renderchunk;
        }

        return renderchunk;
    }

	public World getWorld() {
		return this.world;
	}
}
