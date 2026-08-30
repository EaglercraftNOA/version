package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunk {
   private static final BlockRenderLayer[] blockRenderLayers = BlockRenderLayer.values();
   private static final EnumFacing[] facings = EnumFacing._VALUES;
   private final Random chunkRandom = new net.lax1dude.eaglercraft.v1_8.Random();
   private volatile World world;
   private final WorldRenderer renderGlobal;
   public static int renderChunksUpdated;
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   private ChunkRenderTask compileTask;
   private Set<TileEntity> setTileEntities;
   private Set<TileEntity> rebuildTileEntities;
   private Set<TileEntity> tileEntitiesToAdd;
   private Set<TileEntity> tileEntitiesToRemove;
   private final boolean[] layerUsedScratch = new boolean[blockRenderLayers.length];
   private final VisGraph rebuildVisGraph = new VisGraph();
   private final BlockPos.MutableBlockPos rebuildBlockPos = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos rebuildCacheFrom = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos rebuildCacheTo = new BlockPos.MutableBlockPos();
   private static final float[] modelviewMatrix = new float[16];
   private static boolean modelviewMatrixLoaded = false;
   private final VertexBuffer[] vertexBuffers = new VertexBuffer[blockRenderLayers.length];
   public AxisAlignedBB boundingBox;
   private int frameIndex = -1;
   private boolean needsUpdate = true;
   private final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);
   private final BlockPos.MutableBlockPos[] mapEnumFacing = makeMapEnumFacing();
   private final RenderChunk[] renderChunksOffset16 = new RenderChunk[facings.length];
   private boolean needsImmediateUpdate;

   public RenderChunk(World p_i49841_1_, WorldRenderer p_i49841_2_) {
      this.world = p_i49841_1_;
      this.renderGlobal = p_i49841_2_;
      if (OpenGlHelper.useVbo()) {
         for(int i = 0; i < blockRenderLayers.length; ++i) {
            this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
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

   public VertexBuffer getVertexBufferByLayer(int layer) {
      return this.vertexBuffers[layer];
   }

   public void setPosition(int x, int y, int z) {
      if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ()) {
         this.stopCompileTask();
         this.position.setPos(x, y, z);
         this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

         for(EnumFacing enumfacing : facings) {
            this.mapEnumFacing[enumfacing.ordinal()].setPos(this.position).move(enumfacing, 16);
         }

         Arrays.fill(this.renderChunksOffset16, null);
         this.initModelviewMatrix();
      }
   }

   private static BlockPos.MutableBlockPos[] makeMapEnumFacing() {
      BlockPos.MutableBlockPos[] blockpos = new BlockPos.MutableBlockPos[6];
      for(int i = 0; i < blockpos.length; ++i) {
         blockpos[i] = new BlockPos.MutableBlockPos();
      }
      return blockpos;
   }

   public void resortTransparency(float x, float y, float z, ChunkRenderTask generator) {
      CompiledChunk compiledchunk = generator.getCompiledChunk();
      if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(BlockRenderLayer.TRANSLUCENT)) {
         this.preRenderBlocks(generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), this.position);
         generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT).setVertexState(compiledchunk.getState());
         this.postRenderBlocks(BlockRenderLayer.TRANSLUCENT, x, y, z, generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), compiledchunk);
      }
   }

   public void rebuildChunk(float x, float y, float z, ChunkRenderTask generator) {
      CompiledChunk compiledchunk = new CompiledChunk();
      int i = this.position.getX();
      int j = this.position.getY();
      int k = this.position.getZ();
      World world = this.world;
      if (world != null) {
         if (generator.getStatus() != ChunkRenderTask.Status.COMPILING) {
            return;
         }

         generator.setCompiledChunk(compiledchunk);

         RenderChunkCache lvt_10_1_ = RenderChunkCache.generateCache(world, this.rebuildCacheFrom.setPos(i - 1, j - 1, k - 1), this.rebuildCacheTo.setPos(i + 16, j + 16, k + 16), 1);
         VisGraph lvt_11_1_ = this.rebuildVisGraph;
         lvt_11_1_.clear();
         Set<TileEntity> lvt_12_1_ = this.rebuildTileEntities;
         if (lvt_12_1_ != null) {
            lvt_12_1_.clear();
         }
         if (lvt_10_1_ != null) {
            ++renderChunksUpdated;
            boolean[] aboolean = this.layerUsedScratch;
            Arrays.fill(aboolean, false);
            BlockModelRenderer.enableCache();
            Random random = this.chunkRandom;
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            for(int l = k; l < k + 16; ++l) {
               for(int i1 = j; i1 < j + 16; ++i1) {
                  for(int j1 = i; j1 < i + 16; ++j1) {
                     BlockPos.MutableBlockPos blockpos$mutableblockpos = this.rebuildBlockPos.setPos(j1, i1, l);
                     IBlockState iblockstate = lvt_10_1_.getBlockState(blockpos$mutableblockpos);
                     Block block = iblockstate.getBlock();
                     if (iblockstate.isOpaqueCube(lvt_10_1_, blockpos$mutableblockpos)) {
                        lvt_11_1_.setOpaqueCube(blockpos$mutableblockpos);
                     }

                     if (block.hasTileEntity()) {
                        TileEntity tileentity = lvt_10_1_.getTileEntity(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);
                        if (tileentity != null) {
                           TileEntityRenderer<TileEntity> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);
                           if (tileentityrenderer != null) {
                              compiledchunk.addTileEntity(tileentity);
                              if (tileentityrenderer.isGlobalRenderer(tileentity)) {
                                 if (lvt_12_1_ == null) {
                                    lvt_12_1_ = Sets.newHashSet();
                                    this.rebuildTileEntities = lvt_12_1_;
                                 }

                                 lvt_12_1_.add(tileentity);
                              }
                           }
                        }
                     }

                     IFluidState ifluidstate = lvt_10_1_.getFluidState(blockpos$mutableblockpos);
                     if (!ifluidstate.isEmpty()) {
                        BlockRenderLayer blockrenderlayer1 = ifluidstate.getRenderLayer();
                        int k1 = blockrenderlayer1.ordinal();
                        BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getBuilder(k1);
                        if (!compiledchunk.isLayerStarted(blockrenderlayer1)) {
                           compiledchunk.setLayerStarted(blockrenderlayer1);
                           this.preRenderBlocks(bufferbuilder, i, j, k);
                        }

                        aboolean[k1] |= blockrendererdispatcher.renderFluid(blockpos$mutableblockpos, lvt_10_1_, bufferbuilder, ifluidstate);
                     }

                     if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                        BlockRenderLayer blockrenderlayer2 = block.getRenderLayer();
                        int l1 = blockrenderlayer2.ordinal();
                        BufferBuilder bufferbuilder1 = generator.getRegionRenderCacheBuilder().getBuilder(l1);
                        if (!compiledchunk.isLayerStarted(blockrenderlayer2)) {
                           compiledchunk.setLayerStarted(blockrenderlayer2);
                           this.preRenderBlocks(bufferbuilder1, i, j, k);
                        }

                        aboolean[l1] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, lvt_10_1_, bufferbuilder1, random);
                     }
                  }
               }
            }

            for(BlockRenderLayer blockrenderlayer : blockRenderLayers) {
               if (aboolean[blockrenderlayer.ordinal()]) {
                  compiledchunk.setLayerUsed(blockrenderlayer);
               }

               if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                  this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), compiledchunk);
               }
            }

            BlockModelRenderer.disableCache();
         }

         compiledchunk.setVisibility(lvt_11_1_.computeVisibility());
         if (lvt_12_1_ != null || this.setTileEntities != null && !this.setTileEntities.isEmpty()) {
            Set<TileEntity> set = this.tileEntitiesToAdd;
            Set<TileEntity> set1 = this.tileEntitiesToRemove;
            if (set == null) {
               set = Sets.newHashSet();
               this.tileEntitiesToAdd = set;
            } else {
               set.clear();
            }

            if (set1 == null) {
               set1 = Sets.newHashSet();
               this.tileEntitiesToRemove = set1;
            } else {
               set1.clear();
            }

            if (lvt_12_1_ != null) {
               set.addAll(lvt_12_1_);
            }

            if (this.setTileEntities != null) {
               set1.addAll(this.setTileEntities);
               set.removeAll(this.setTileEntities);
            }

            if (lvt_12_1_ != null) {
               set1.removeAll(lvt_12_1_);
            }

            if (this.setTileEntities == null) {
               this.setTileEntities = Sets.newHashSet();
            } else {
               this.setTileEntities.clear();
            }

            if (lvt_12_1_ != null) {
               this.setTileEntities.addAll(lvt_12_1_);
            }

            this.renderGlobal.updateTileEntities(set1, set);
            if (lvt_12_1_ != null) {
               lvt_12_1_.clear();
            }

            set.clear();
            set1.clear();
         }

      }
   }

   protected void finishCompileTask() {
      if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
         this.compileTask.finish();
         this.compileTask = null;
      }

   }

   public void clearCompileTask(ChunkRenderTask task) {
      if (this.compileTask == task) {
         this.compileTask = null;
      }

   }

   public ChunkRenderTask makeCompileTaskChunk() {
      this.finishCompileTask();
      this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.REBUILD_CHUNK, this.getDistanceSq());
      return this.compileTask;
   }

   @Nullable
   public ChunkRenderTask makeCompileTaskTransparency() {
      if (this.compileTask == null || this.compileTask.getStatus() != ChunkRenderTask.Status.PENDING) {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkRenderTask.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }

         this.compileTask = new ChunkRenderTask(this, ChunkRenderTask.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
         this.compileTask.setCompiledChunk(this.compiledChunk);
         return this.compileTask;
      }

      return null;
   }

   protected double getDistanceSq() {
      EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
      double d0 = this.boundingBox.minX + 8.0D - entityplayersp.posX;
      double d1 = this.boundingBox.minY + 8.0D - entityplayersp.posY;
      double d2 = this.boundingBox.minZ + 8.0D - entityplayersp.posZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   private void preRenderBlocks(BufferBuilder bufferBuilderIn, BlockPos pos) {
      bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
      bufferBuilderIn.setTranslation((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
   }

   private void preRenderBlocks(BufferBuilder bufferBuilderIn, int x, int y, int z) {
      bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
      bufferBuilderIn.setTranslation((double)(-x), (double)(-y), (double)(-z));
   }

	private void postRenderBlocks(BlockRenderLayer layer, float x, float y, float z, BufferBuilder bufferBuilderIn, CompiledChunk compiledChunkIn) {
		if (layer == BlockRenderLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer) && bufferBuilderIn.getVertexCount() > 0) {
			bufferBuilderIn.sortVertexData(x, y, z);
			if (compiledChunkIn.getState() == null || compiledChunkIn.getState().getVertexCount() != bufferBuilderIn.getVertexCount()) {
				compiledChunkIn.setState(bufferBuilderIn.getVertexState());
			}
		}

		bufferBuilderIn.finishDrawing();
	}

   private void initModelviewMatrix() {
      if (modelviewMatrixLoaded) {
         return;
      }

      modelviewMatrixLoaded = true;
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.translatef(-8.0F, -8.0F, -8.0F);
      GlStateManager.scalef(1.000001F, 1.000001F, 1.000001F);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.getFloat(2982, modelviewMatrix);
      GlStateManager.popMatrix();
   }

   public void multModelviewMatrix() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.multMatrix(modelviewMatrix);
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.compiledChunk = compiledChunkIn;
   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
   }

   public void deleteGlResources() {
      this.stopCompileTask();
      this.world = null;

      for(int i = 0; i < blockRenderLayers.length; ++i) {
         if (this.vertexBuffers[i] != null) {
            this.vertexBuffers[i].deleteGlBuffers();
         }
      }

   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean immediate) {
      if (this.needsUpdate) {
         immediate |= this.needsImmediateUpdate;
      }

      this.needsUpdate = true;
      this.needsImmediateUpdate = immediate;
   }

   public void clearNeedsUpdate() {
      this.needsUpdate = false;
      this.needsImmediateUpdate = false;
   }

   public boolean needsUpdate() {
      return this.needsUpdate;
   }

   public boolean needsImmediateUpdate() {
      return this.needsUpdate && this.needsImmediateUpdate;
   }

   public BlockPos getBlockPosOffset16(EnumFacing facing) {
      return this.mapEnumFacing[facing.ordinal()];
   }

   public RenderChunk getRenderChunkOffset16(ViewFrustum viewFrustum, EnumFacing facing) {
      int i = facing.ordinal();
      RenderChunk renderchunk = this.renderChunksOffset16[i];
      if (renderchunk == null) {
         renderchunk = viewFrustum.getRenderChunk(this.getBlockPosOffset16(facing));
         this.renderChunksOffset16[i] = renderchunk;
      }

      return renderchunk;
   }

   public World getWorld() {
      return this.world;
   }
}
