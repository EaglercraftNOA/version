package net.minecraft.world;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements IEntityReader, IWorld, IWorldReader, AutoCloseable {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final EnumFacing[] FACING_VALUES = EnumFacing._VALUES;
   private int seaLevel = 63;
   public final List<Entity> loadedEntityList = Lists.newArrayList();
   protected final List<Entity> unloadedEntityList = Lists.newArrayList();
   public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
   public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
   private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
   private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
   public final List<EntityPlayer> playerEntities = Lists.newArrayList();
   public final List<Entity> weatherEffects = Lists.newArrayList();
   protected final IntHashMap<Entity> entitiesById = new IntHashMap<>();
   private final long cloudColour = 16777215L;
   private int skylightSubtracted;
   protected int updateLCG = (new net.lax1dude.eaglercraft.v1_8.Random()).nextInt();
   protected final int DIST_HASH_MAGIC = 1013904223;
   protected float prevRainingStrength;
   protected float rainingStrength;
   protected float prevThunderingStrength;
   protected float thunderingStrength;
   private int lastLightningBolt;
   public final Random rand = new net.lax1dude.eaglercraft.v1_8.Random();
   public final Dimension dimension;
   protected PathWorldListener pathListener = new PathWorldListener();
   protected List<IWorldEventListener> eventListeners = Lists.newArrayList(this.pathListener);
   protected IChunkProvider chunkProvider;
   protected final ISaveHandler saveHandler;
   protected WorldInfo worldInfo;
   @Nullable
   private final WorldSavedDataStorage savedDataStorage;
   protected VillageCollection villageCollection;
   public final Profiler profiler;
   public final boolean isRemote;
   protected boolean spawnHostileMobs = true;
   protected boolean spawnPeacefulMobs = true;
   private boolean processingLoadedTiles;
   private final WorldBorder worldBorder;
   int[] lightUpdateBlockList = new int['\u8000'];

   protected World(ISaveHandler p_i49813_1_, @Nullable WorldSavedDataStorage p_i49813_2_, WorldInfo p_i49813_3_, Dimension p_i49813_4_, Profiler p_i49813_5_, boolean p_i49813_6_) {
      this.saveHandler = p_i49813_1_;
      this.savedDataStorage = p_i49813_2_;
      this.profiler = p_i49813_5_;
      this.worldInfo = p_i49813_3_;
      this.dimension = p_i49813_4_;
      this.isRemote = p_i49813_6_;
      this.worldBorder = p_i49813_4_.createWorldBorder();
   }

   public Biome getBiome(BlockPos pos) {
      if (this.isBlockLoaded(pos)) {
         return this.getChunk(pos).getBiome(pos);
      } else {
         return this.chunkProvider.getChunkGenerator().getBiomeProvider().getBiome(pos, Biomes.PLAINS);
      }
   }

   protected abstract IChunkProvider createChunkProvider();

   public void initialize(WorldSettings settings) {
      this.worldInfo.setInitialized(true);
   }

   public boolean isRemote() {
      return this.isRemote;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public IBlockState getGroundAboveSeaLevel(BlockPos pos) {
      BlockPos blockpos;
      for(blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ()); !this.isAirBlock(blockpos.up()); blockpos = blockpos.up()) {
         ;
      }

      return this.getBlockState(blockpos);
   }

   public static boolean isValid(BlockPos p_175701_0_) {
      return !isOutsideBuildHeight(p_175701_0_) && p_175701_0_.getX() >= -30000000 && p_175701_0_.getZ() >= -30000000 && p_175701_0_.getX() < 30000000 && p_175701_0_.getZ() < 30000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos p_189509_0_) {
      return p_189509_0_.getY() < 0 || p_189509_0_.getY() >= 256;
   }

   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).isAir();
   }

   public Chunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   public Chunk getChunk(int chunkX, int chunkZ) {
      Chunk chunk = this.chunkProvider.getChunk(chunkX, chunkZ, true, true);
      if (chunk == null) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return chunk;
      }
   }

   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      if (isOutsideBuildHeight(pos)) {
         return false;
      } else if (!this.isRemote && this.worldInfo.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         Chunk chunk = this.getChunk(pos);
         Block block = newState.getBlock();
         IBlockState iblockstate = chunk.setBlockState(pos, newState, (flags & 64) != 0);
         if (iblockstate == null) {
            return false;
         } else {
            IBlockState iblockstate1 = this.getBlockState(pos);
            if (iblockstate1.getOpacity(this, pos) != iblockstate.getOpacity(this, pos) || iblockstate1.getLightValue() != iblockstate.getLightValue()) {
               this.profiler.startSection("checkLight");
               this.checkLight(pos);
               this.profiler.endSection();
            }

            if (iblockstate1 == newState) {
               if (iblockstate != iblockstate1) {
                  this.markBlockRangeForRenderUpdate(pos, pos);
               }

               if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
                  this.notifyBlockUpdate(pos, iblockstate, newState, flags);
               }

               if (!this.isRemote && (flags & 1) != 0) {
                  this.notifyNeighbors(pos, iblockstate.getBlock());
                  if (newState.hasComparatorInputOverride()) {
                     this.updateComparatorOutputLevel(pos, block);
                  }
               }

               if ((flags & 16) == 0) {
                  int i = flags & -2;
                  iblockstate.updateDiagonalNeighbors(this, pos, i);
                  newState.updateNeighbors(this, pos, i);
                  newState.updateDiagonalNeighbors(this, pos, i);
               }
            }

            return true;
         }
      }
   }

   public boolean removeBlock(BlockPos pos) {
      IFluidState ifluidstate = this.getFluidState(pos);
      return this.setBlockState(pos, ifluidstate.getBlockState(), 3);
   }

   public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
      IBlockState iblockstate = this.getBlockState(pos);
      if (iblockstate.isAir()) {
         return false;
      } else {
         IFluidState ifluidstate = this.getFluidState(pos);
         this.playEvent(2001, pos, Block.getStateId(iblockstate));
         if (dropBlock) {
            iblockstate.dropBlockAsItem(this, pos, 0);
         }

         return this.setBlockState(pos, ifluidstate.getBlockState(), 3);
      }
   }

   public boolean setBlockState(BlockPos pos, IBlockState state) {
      return this.setBlockState(pos, state, 3);
   }

   public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).notifyBlockUpdate(this, pos, oldState, newState, flags);
      }

   }

   public void notifyNeighbors(BlockPos pos, Block blockIn) {
      if (this.worldInfo.getGenerator() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.notifyNeighborsOfStateChange(pos, blockIn);
      }

   }

   public void markBlocksDirtyVertical(int x, int z, int y1, int y2) {
      if (y1 > y2) {
         int i = y2;
         y2 = y1;
         y1 = i;
      }

      if (this.dimension.hasSkyLight()) {
         for(int j = y1; j <= y2; ++j) {
            this.checkLightFor(EnumLightType.SKY, new BlockPos(x, j, z));
         }
      }

      this.markBlockRangeForRenderUpdate(x, y1, z, x, y2, z);
   }

   public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
      this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
      }

   }

   public void notifyNeighborsOfStateChange(BlockPos pos, Block blockIn) {
      this.neighborChanged(pos.west(), blockIn, pos);
      this.neighborChanged(pos.east(), blockIn, pos);
      this.neighborChanged(pos.down(), blockIn, pos);
      this.neighborChanged(pos.up(), blockIn, pos);
      this.neighborChanged(pos.north(), blockIn, pos);
      this.neighborChanged(pos.south(), blockIn, pos);
   }

   public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
      if (skipSide != EnumFacing.WEST) {
         this.neighborChanged(pos.west(), blockType, pos);
      }

      if (skipSide != EnumFacing.EAST) {
         this.neighborChanged(pos.east(), blockType, pos);
      }

      if (skipSide != EnumFacing.DOWN) {
         this.neighborChanged(pos.down(), blockType, pos);
      }

      if (skipSide != EnumFacing.UP) {
         this.neighborChanged(pos.up(), blockType, pos);
      }

      if (skipSide != EnumFacing.NORTH) {
         this.neighborChanged(pos.north(), blockType, pos);
      }

      if (skipSide != EnumFacing.SOUTH) {
         this.neighborChanged(pos.south(), blockType, pos);
      }

   }

   public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.isRemote) {
         IBlockState iblockstate = this.getBlockState(pos);

         try {
            iblockstate.neighborChanged(this, pos, blockIn, fromPos);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
            crashreportcategory.addDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", IRegistry.BLOCK.getKey(blockIn), blockIn.getTranslationKey(), blockIn.getClass().getCanonicalName());
               } catch (Throwable var2) {
                  return "ID #" + IRegistry.BLOCK.getKey(blockIn);
               }
            });
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
            throw new ReportedException(crashreport);
         }
      }
   }

   public boolean canSeeSky(BlockPos pos) {
      return this.getChunk(pos).canSeeSky(pos);
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
         if (pos.getY() < 0) {
            return 0;
         } else {
            if (pos.getY() >= 256) {
               pos = new BlockPos(pos.getX(), 255, pos.getZ());
            }

            return this.getChunk(pos).getLightSubtracted(pos, amount);
         }
      } else {
         return 15;
      }
   }

   public int getHeight(Heightmap.Type heightmapType, int x, int z) {
      int i;
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         if (this.isChunkLoaded(x >> 4, z >> 4, true)) {
            i = this.getChunk(x >> 4, z >> 4).getTopBlockY(heightmapType, x & 15, z & 15) + 1;
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return i;
   }

   @Deprecated
   public int getChunksLowestHorizon(int x, int z) {
      if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
         if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
            return 0;
         } else {
            Chunk chunk = this.getChunk(x >> 4, z >> 4);
            return chunk.getLowestHeight();
         }
      } else {
         return this.getSeaLevel() + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getLightFromNeighborsFor(EnumLightType type, BlockPos pos) {
      if (!this.dimension.hasSkyLight() && type == EnumLightType.SKY) {
         return 0;
      } else {
         if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
         }

         if (!isValid(pos)) {
            return type.defaultLightValue;
         } else if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
         } else if (this.getBlockState(pos).useNeighborBrightness(this, pos)) {
            int i = this.getLightFor(type, pos.up());
            int j = this.getLightFor(type, pos.east());
            int k = this.getLightFor(type, pos.west());
            int l = this.getLightFor(type, pos.south());
            int i1 = this.getLightFor(type, pos.north());
            if (j > i) {
               i = j;
            }

            if (k > i) {
               i = k;
            }

            if (l > i) {
               i = l;
            }

            if (i1 > i) {
               i = i1;
            }

            return i;
         } else {
            return this.getChunk(pos).getLightFor(type, pos);
         }
      }
   }

   public int getLightFor(EnumLightType type, BlockPos pos) {
      if (pos.getY() < 0) {
         pos = new BlockPos(pos.getX(), 0, pos.getZ());
      }

      if (!isValid(pos)) {
         return type.defaultLightValue;
      } else {
         return !this.isBlockLoaded(pos) ? type.defaultLightValue : this.getChunk(pos).getLightFor(type, pos);
      }
   }

   public void setLightFor(EnumLightType type, BlockPos pos, int lightValue) {
      if (isValid(pos)) {
         if (this.isBlockLoaded(pos)) {
            this.getChunk(pos).setLightFor(type, pos, lightValue);
            this.notifyLightSet(pos);
         }
      }
   }

   public void notifyLightSet(BlockPos pos) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).notifyLightSet(pos);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int i = this.getLightFromNeighborsFor(EnumLightType.SKY, pos);
      int j = this.getLightFromNeighborsFor(EnumLightType.BLOCK, pos);
      if (j < lightValue) {
         j = lightValue;
      }

      return i << 20 | j << 4;
   }

   public IBlockState getBlockState(BlockPos pos) {
      if (isOutsideBuildHeight(pos)) {
         return Blocks.VOID_AIR.getDefaultState();
      } else {
         Chunk chunk = this.getChunk(pos);
         return chunk.getBlockState(pos);
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      if (isOutsideBuildHeight(pos)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         Chunk chunk = this.getChunk(pos);
         return chunk.getFluidState(pos);
      }
   }

   public boolean isDaytime() {
      return this.skylightSubtracted < 4;
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
      return this.rayTraceBlocks(start, end, RayTraceFluidMode.NEVER, false, false);
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, RayTraceFluidMode fluidMode) {
      return this.rayTraceBlocks(start, end, fluidMode, false, false);
   }

   @Nullable
   public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, RayTraceFluidMode fluidMode, boolean checkCollision, boolean returnMisses) {
      double d0 = start.x;
      double d1 = start.y;
      double d2 = start.z;
      if (!Double.isNaN(d0) && !Double.isNaN(d1) && !Double.isNaN(d2)) {
         if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
            int i = MathHelper.floor(end.x);
            int j = MathHelper.floor(end.y);
            int k = MathHelper.floor(end.z);
            int l = MathHelper.floor(d0);
            int i1 = MathHelper.floor(d1);
            int j1 = MathHelper.floor(d2);
            BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(l, i1, j1);
            IBlockState iblockstate = this.getBlockState(blockpos);
            IFluidState ifluidstate = this.getFluidState(blockpos);
            if (!checkCollision || !iblockstate.getCollisionShape(this, blockpos).isEmpty()) {
               boolean flag = iblockstate.getBlock().isCollidable(iblockstate);
               boolean flag1 = fluidMode.predicate.test(ifluidstate);
               if (flag || flag1) {
                  RayTraceResult raytraceresult = null;
                  if (flag) {
                     raytraceresult = Block.collisionRayTrace(iblockstate, this, blockpos.toImmutable(), start, end);
                  }

                  if (raytraceresult == null && flag1) {
                     raytraceresult = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate.getHeight(), 1.0D).rayTrace(start, end, blockpos.toImmutable());
                  }

                  if (raytraceresult != null) {
                     return raytraceresult;
                  }
               }
            }

            RayTraceResult raytraceresult2 = null;
            int k1 = 200;

            while(k1-- >= 0) {
               if (Double.isNaN(d0) || Double.isNaN(d1) || Double.isNaN(d2)) {
                  return null;
               }

               if (l == i && i1 == j && j1 == k) {
                  return returnMisses ? raytraceresult2 : null;
               }

               boolean flag4 = true;
               boolean flag5 = true;
               boolean flag6 = true;
               double d3 = 999.0D;
               double d4 = 999.0D;
               double d5 = 999.0D;
               if (i > l) {
                  d3 = (double)l + 1.0D;
               } else if (i < l) {
                  d3 = (double)l + 0.0D;
               } else {
                  flag4 = false;
               }

               if (j > i1) {
                  d4 = (double)i1 + 1.0D;
               } else if (j < i1) {
                  d4 = (double)i1 + 0.0D;
               } else {
                  flag5 = false;
               }

               if (k > j1) {
                  d5 = (double)j1 + 1.0D;
               } else if (k < j1) {
                  d5 = (double)j1 + 0.0D;
               } else {
                  flag6 = false;
               }

               double d6 = 999.0D;
               double d7 = 999.0D;
               double d8 = 999.0D;
               double d9 = end.x - d0;
               double d10 = end.y - d1;
               double d11 = end.z - d2;
               if (flag4) {
                  d6 = (d3 - d0) / d9;
               }

               if (flag5) {
                  d7 = (d4 - d1) / d10;
               }

               if (flag6) {
                  d8 = (d5 - d2) / d11;
               }

               if (d6 == -0.0D) {
                  d6 = -1.0E-4D;
               }

               if (d7 == -0.0D) {
                  d7 = -1.0E-4D;
               }

               if (d8 == -0.0D) {
                  d8 = -1.0E-4D;
               }

               EnumFacing enumfacing;
               if (d6 < d7 && d6 < d8) {
                  enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                  d0 = d3;
                  d1 += d10 * d6;
                  d2 += d11 * d6;
               } else if (d7 < d8) {
                  enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                  d0 += d9 * d7;
                  d1 = d4;
                  d2 += d11 * d7;
               } else {
                  enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  d0 += d9 * d8;
                  d1 += d10 * d8;
                  d2 = d5;
               }

               l = MathHelper.floor(d0) - (enumfacing == EnumFacing.EAST ? 1 : 0);
               i1 = MathHelper.floor(d1) - (enumfacing == EnumFacing.UP ? 1 : 0);
               j1 = MathHelper.floor(d2) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
               blockpos.setPos(l, i1, j1);
               IBlockState iblockstate1 = this.getBlockState(blockpos);
               IFluidState ifluidstate1 = this.getFluidState(blockpos);
               if (!checkCollision || iblockstate1.getMaterial() == Material.PORTAL || !iblockstate1.getCollisionShape(this, blockpos).isEmpty()) {
                  boolean flag2 = iblockstate1.getBlock().isCollidable(iblockstate1);
                  boolean flag3 = fluidMode.predicate.test(ifluidstate1);
                  if (!flag2 && !flag3) {
                     raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(d0, d1, d2), enumfacing, blockpos.toImmutable());
                  } else {
                     RayTraceResult raytraceresult1 = null;
                     if (flag2) {
                        raytraceresult1 = Block.collisionRayTrace(iblockstate1, this, blockpos.toImmutable(), start, end);
                     }

                     if (raytraceresult1 == null && flag3) {
                        raytraceresult1 = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)ifluidstate1.getHeight(), 1.0D).rayTrace(start, end, blockpos.toImmutable());
                     }

                     if (raytraceresult1 != null) {
                        return raytraceresult1;
                     }
                  }
               }
            }

            return returnMisses ? raytraceresult2 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      this.playSound(player, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundIn, category, volume, pitch);
   }

   public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
      }

   }

   public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
   }

   public void playRecord(BlockPos pos, @Nullable SoundEvent soundEventIn) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).playRecord(soundEventIn, pos);
      }

   }

   public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(particleData, particleData.getType().getAlwaysShow(), x, y, z, xSpeed, ySpeed, zSpeed);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addParticle(IParticleData particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(particleData, particleData.getType().getAlwaysShow() || forceAlwaysRender, x, y, z, xSpeed, ySpeed, zSpeed);
      }

   }

   public void addOptionalParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).addParticle(particleData, false, true, x, y, z, xSpeed, ySpeed, zSpeed);
      }

   }

   public boolean addWeatherEffect(Entity entityIn) {
      this.weatherEffects.add(entityIn);
      return true;
   }

   public boolean spawnEntity(Entity entityIn) {
      int i = MathHelper.floor(entityIn.posX / 16.0D);
      int j = MathHelper.floor(entityIn.posZ / 16.0D);
      boolean flag = entityIn.forceSpawn;
      if (entityIn instanceof EntityPlayer) {
         flag = true;
      }

      if (!flag && !this.isChunkLoaded(i, j, false)) {
         return false;
      } else {
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            this.playerEntities.add(entityplayer);
            this.updateAllPlayersSleepingFlag();
         }

         this.getChunk(i, j).addEntity(entityIn);
         this.loadedEntityList.add(entityIn);
         this.onEntityAdded(entityIn);
         return true;
      }
   }

   protected void onEntityAdded(Entity entityIn) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).onEntityAdded(entityIn);
      }

   }

   protected void onEntityRemoved(Entity entityIn) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).onEntityRemoved(entityIn);
      }

   }

   public void removeEntity(Entity entityIn) {
      if (entityIn.isBeingRidden()) {
         entityIn.removePassengers();
      }

      if (entityIn.isPassenger()) {
         entityIn.stopRiding();
      }

      entityIn.remove();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
         this.onEntityRemoved(entityIn);
      }

   }

   public void removeEntityDangerously(Entity entityIn) {
      entityIn.setDropItemsWhenDead(false);
      entityIn.remove();
      if (entityIn instanceof EntityPlayer) {
         this.playerEntities.remove(entityIn);
         this.updateAllPlayersSleepingFlag();
      }

      int i = entityIn.chunkCoordX;
      int j = entityIn.chunkCoordZ;
      if (entityIn.addedToChunk && this.isChunkLoaded(i, j, true)) {
         this.getChunk(i, j).removeEntity(entityIn);
      }

      this.loadedEntityList.remove(entityIn);
      this.onEntityRemoved(entityIn);
   }

   public void addEventListener(IWorldEventListener listener) {
      this.eventListeners.add(listener);
   }

   @OnlyIn(Dist.CLIENT)
   public void removeEventListener(IWorldEventListener listener) {
      this.eventListeners.remove(listener);
   }

   public int calculateSkylightSubtracted(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
      f1 = 1.0F - f1;
      return (int)(f1 * 11.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getSunBrightness(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
      return f1 * 0.8F + 0.2F;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getSkyColor(Entity entityIn, float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      int i = MathHelper.floor(entityIn.posX);
      int j = MathHelper.floor(entityIn.posY);
      int k = MathHelper.floor(entityIn.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      Biome biome = this.getBiome(blockpos);
      float f2 = biome.getTemperature(blockpos);
      int l = biome.getSkyColorByTemp(f2);
      float f3 = (float)(l >> 16 & 255) / 255.0F;
      float f4 = (float)(l >> 8 & 255) / 255.0F;
      float f5 = (float)(l & 255) / 255.0F;
      f3 = f3 * f1;
      f4 = f4 * f1;
      f5 = f5 * f1;
      float f6 = this.getRainStrength(partialTicks);
      if (f6 > 0.0F) {
         float f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
         float f8 = 1.0F - f6 * 0.75F;
         f3 = f3 * f8 + f7 * (1.0F - f8);
         f4 = f4 * f8 + f7 * (1.0F - f8);
         f5 = f5 * f8 + f7 * (1.0F - f8);
      }

      float f10 = this.getThunderStrength(partialTicks);
      if (f10 > 0.0F) {
         float f11 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
         float f9 = 1.0F - f10 * 0.75F;
         f3 = f3 * f9 + f11 * (1.0F - f9);
         f4 = f4 * f9 + f11 * (1.0F - f9);
         f5 = f5 * f9 + f11 * (1.0F - f9);
      }

      if (this.lastLightningBolt > 0) {
         float f12 = (float)this.lastLightningBolt - partialTicks;
         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         f12 = f12 * 0.45F;
         f3 = f3 * (1.0F - f12) + 0.8F * f12;
         f4 = f4 * (1.0F - f12) + 0.8F * f12;
         f5 = f5 * (1.0F - f12) + 1.0F * f12;
      }

      return new Vec3d((double)f3, (double)f4, (double)f5);
   }

   public float getCelestialAngleRadians(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      return f * ((float)Math.PI * 2F);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getCloudColour(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = this.getRainStrength(partialTicks);
      if (f5 > 0.0F) {
         float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         float f7 = 1.0F - f5 * 0.95F;
         f2 = f2 * f7 + f6 * (1.0F - f7);
         f3 = f3 * f7 + f6 * (1.0F - f7);
         f4 = f4 * f7 + f6 * (1.0F - f7);
      }

      f2 = f2 * (f1 * 0.9F + 0.1F);
      f3 = f3 * (f1 * 0.9F + 0.1F);
      f4 = f4 * (f1 * 0.85F + 0.15F);
      float f9 = this.getThunderStrength(partialTicks);
      if (f9 > 0.0F) {
         float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.95F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vec3d((double)f2, (double)f3, (double)f4);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      return this.dimension.getFogColor(f, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   public float getStarBrightness(float partialTicks) {
      float f = this.getCelestialAngle(partialTicks);
      float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      return f1 * f1 * 0.5F;
   }

   public void tickEntities() {
      this.profiler.startSection("entities");
      this.profiler.startSection("global");

      for(int i = 0; i < this.weatherEffects.size(); ++i) {
         Entity entity = this.weatherEffects.get(i);

         try {
            ++entity.ticksExisted;
            entity.tick();
         } catch (Throwable throwable2) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
            if (entity == null) {
               crashreportcategory.addDetail("Entity", "~~NULL~~");
            } else {
               entity.fillCrashReport(crashreportcategory);
            }

            throw new ReportedException(crashreport);
         }

         if (entity.removed) {
            this.weatherEffects.remove(i--);
         }
      }

      this.profiler.endStartSection("remove");
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for(int k = 0; k < this.unloadedEntityList.size(); ++k) {
         Entity entity1 = this.unloadedEntityList.get(k);
         int j = entity1.chunkCoordX;
         int k1 = entity1.chunkCoordZ;
         if (entity1.addedToChunk && this.isChunkLoaded(j, k1, true)) {
            this.getChunk(j, k1).removeEntity(entity1);
         }
      }

      for(int l = 0; l < this.unloadedEntityList.size(); ++l) {
         this.onEntityRemoved(this.unloadedEntityList.get(l));
      }

      this.unloadedEntityList.clear();
      this.tickPlayers();
      this.profiler.endStartSection("regular");

      for(int i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
         Entity entity2 = this.loadedEntityList.get(i1);
         Entity entity3 = entity2.getRidingEntity();
         if (entity3 != null) {
            if (!entity3.removed && entity3.isPassenger(entity2)) {
               continue;
            }

            entity2.stopRiding();
         }

         this.profiler.startSection("tick");
         if (!entity2.removed && !(entity2 instanceof EntityPlayerMP)) {
            try {
               this.tickEntity(entity2);
            } catch (Throwable throwable1) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
               CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Entity being ticked");
               entity2.fillCrashReport(crashreportcategory1);
               throw new ReportedException(crashreport1);
            }
         }

         this.profiler.endSection();
         this.profiler.startSection("remove");
         if (entity2.removed) {
            int l1 = entity2.chunkCoordX;
            int i2 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(l1, i2, true)) {
               this.getChunk(l1, i2).removeEntity(entity2);
            }

            this.loadedEntityList.remove(i1--);
            this.onEntityRemoved(entity2);
         }

         this.profiler.endSection();
      }

      this.profiler.endStartSection("blockEntities");
      if (!this.tileEntitiesToBeRemoved.isEmpty()) {
         this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
         this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
         this.tileEntitiesToBeRemoved.clear();
      }

      this.processingLoadedTiles = true;
      Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();

      while(iterator.hasNext()) {
         TileEntity tileentity = iterator.next();
         if (!tileentity.isRemoved() && tileentity.hasWorld()) {
            BlockPos blockpos = tileentity.getPos();
            if (this.isBlockLoaded(blockpos) && this.worldBorder.contains(blockpos)) {
               try {
                  this.profiler.startSection(() -> {
                     return String.valueOf((Object)TileEntityType.getId(tileentity.getType()));
                  });
                  ((ITickable)tileentity).tick();
                  this.profiler.endSection();
               } catch (Throwable throwable) {
                  CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                  CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                  tileentity.addInfoToCrashReport(crashreportcategory2);
                  throw new ReportedException(crashreport2);
               }
            }
         }

         if (tileentity.isRemoved()) {
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (this.isBlockLoaded(tileentity.getPos())) {
               this.getChunk(tileentity.getPos()).removeTileEntity(tileentity.getPos());
            }
         }
      }

      this.processingLoadedTiles = false;
      this.profiler.endStartSection("pendingBlockEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for(int j1 = 0; j1 < this.addedTileEntityList.size(); ++j1) {
            TileEntity tileentity1 = this.addedTileEntityList.get(j1);
            if (!tileentity1.isRemoved()) {
               if (!this.loadedTileEntityList.contains(tileentity1)) {
                  this.addTileEntity(tileentity1);
               }

               if (this.isBlockLoaded(tileentity1.getPos())) {
                  Chunk chunk = this.getChunk(tileentity1.getPos());
                  IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                  chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                  this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
               }
            }
         }

         this.addedTileEntityList.clear();
      }

      this.profiler.endSection();
      this.profiler.endSection();
   }

   protected void tickPlayers() {
   }

   public boolean addTileEntity(TileEntity tile) {
      boolean flag = this.loadedTileEntityList.add(tile);
      if (flag && tile instanceof ITickable) {
         this.tickableTileEntities.add(tile);
      }

      if (this.isRemote) {
         BlockPos blockpos = tile.getPos();
         IBlockState iblockstate = this.getBlockState(blockpos);
         this.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 2);
      }

      return flag;
   }

   public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
      if (this.processingLoadedTiles) {
         this.addedTileEntityList.addAll(tileEntityCollection);
      } else {
         for(TileEntity tileentity : tileEntityCollection) {
            this.addTileEntity(tileentity);
         }
      }

   }

   public void tickEntity(Entity ent) {
      this.tickEntity(ent, true);
   }

   public void tickEntity(Entity entityIn, boolean forceUpdate) {
      if (!(entityIn instanceof EntityPlayer)) {
         int i = MathHelper.floor(entityIn.posX);
         int j = MathHelper.floor(entityIn.posZ);
         int k = 32;
         if (forceUpdate && !this.isAreaLoaded(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
            return;
         }
      }

      entityIn.lastTickPosX = entityIn.posX;
      entityIn.lastTickPosY = entityIn.posY;
      entityIn.lastTickPosZ = entityIn.posZ;
      entityIn.prevRotationYaw = entityIn.rotationYaw;
      entityIn.prevRotationPitch = entityIn.rotationPitch;
      if (forceUpdate && entityIn.addedToChunk) {
         ++entityIn.ticksExisted;
         if (entityIn.isPassenger()) {
            entityIn.updateRidden();
         } else {
            this.profiler.startSection(() -> {
               return IRegistry.ENTITY_TYPE.getKey(entityIn.getType()).toString();
            });
            entityIn.tick();
            this.profiler.endSection();
         }
      }

      this.profiler.startSection("chunkCheck");
      if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
         entityIn.posX = entityIn.lastTickPosX;
      }

      if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
         entityIn.posY = entityIn.lastTickPosY;
      }

      if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
         entityIn.posZ = entityIn.lastTickPosZ;
      }

      if (Double.isNaN((double)entityIn.rotationPitch) || Double.isInfinite((double)entityIn.rotationPitch)) {
         entityIn.rotationPitch = entityIn.prevRotationPitch;
      }

      if (Double.isNaN((double)entityIn.rotationYaw) || Double.isInfinite((double)entityIn.rotationYaw)) {
         entityIn.rotationYaw = entityIn.prevRotationYaw;
      }

      int l = MathHelper.floor(entityIn.posX / 16.0D);
      int i1 = MathHelper.floor(entityIn.posY / 16.0D);
      int j1 = MathHelper.floor(entityIn.posZ / 16.0D);
      if (!entityIn.addedToChunk || entityIn.chunkCoordX != l || entityIn.chunkCoordY != i1 || entityIn.chunkCoordZ != j1) {
         if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
            this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
         }

         if (!entityIn.setPositionNonDirty() && !this.isChunkLoaded(l, j1, true)) {
            entityIn.addedToChunk = false;
         } else {
            this.getChunk(l, j1).addEntity(entityIn);
         }
      }

      this.profiler.endSection();
      if (forceUpdate && entityIn.addedToChunk) {
         for(Entity entity : entityIn.getPassengers()) {
            if (!entity.removed && entity.getRidingEntity() == entityIn) {
               this.tickEntity(entity);
            } else {
               entity.stopRiding();
            }
         }
      }

   }

   public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      if (shape.isEmpty()) {
         return true;
      } else {
         List<Entity> list = this.getEntitiesWithinAABBExcludingEntity((Entity)null, shape.getBoundingBox());

         for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!entity.removed && entity.preventEntitySpawning && entity != entityIn && (entityIn == null || !entity.isRidingSameEntity(entityIn)) && VoxelShapes.compare(shape, VoxelShapes.create(entity.getBoundingBox()), IBooleanFunction.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean checkBlockCollision(AxisAlignedBB bb) {
      int i = MathHelper.floor(bb.minX);
      int j = MathHelper.ceil(bb.maxX);
      int k = MathHelper.floor(bb.minY);
      int l = MathHelper.ceil(bb.maxY);
      int i1 = MathHelper.floor(bb.minZ);
      int j1 = MathHelper.ceil(bb.maxZ);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                  if (!iblockstate.isAir()) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean isFlammableWithin(AxisAlignedBB bb) {
      int i = MathHelper.floor(bb.minX);
      int j = MathHelper.ceil(bb.maxX);
      int k = MathHelper.floor(bb.minY);
      int l = MathHelper.ceil(bb.maxY);
      int i1 = MathHelper.floor(bb.minZ);
      int j1 = MathHelper.ceil(bb.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     Block block = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)).getBlock();
                     if (block == Blocks.FIRE || block == Blocks.LAVA) {
                        boolean flag = true;
                        return flag;
                     }
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public IBlockState findBlockstateInArea(AxisAlignedBB area, Block blockIn) {
      int i = MathHelper.floor(area.minX);
      int j = MathHelper.ceil(area.maxX);
      int k = MathHelper.floor(area.minY);
      int l = MathHelper.ceil(area.maxY);
      int i1 = MathHelper.floor(area.minZ);
      int j1 = MathHelper.ceil(area.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     IBlockState iblockstate = this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2));
                     if (iblockstate.getBlock() == blockIn) {
                        IBlockState iblockstate1 = iblockstate;
                        return iblockstate1;
                     }
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
      int i = MathHelper.floor(bb.minX);
      int j = MathHelper.ceil(bb.maxX);
      int k = MathHelper.floor(bb.minY);
      int l = MathHelper.ceil(bb.maxY);
      int i1 = MathHelper.floor(bb.minZ);
      int j1 = MathHelper.ceil(bb.maxZ);
      BlockMaterialMatcher blockmaterialmatcher = BlockMaterialMatcher.forMaterial(materialIn);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  if (blockmaterialmatcher.test(this.getBlockState(blockpos$pooledmutableblockpos.setPos(k1, l1, i2)))) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }

         return false;
      }
   }

   public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean damagesTerrain) {
      return this.createExplosion(entityIn, (DamageSource)null, x, y, z, strength, false, damagesTerrain);
   }

   public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      return this.createExplosion(entityIn, (DamageSource)null, x, y, z, strength, causesFire, damagesTerrain);
   }

   public Explosion createExplosion(@Nullable Entity entityIn, @Nullable DamageSource damageSourceIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, causesFire, damagesTerrain);
      if (damageSourceIn != null) {
         explosion.setDamageSource(damageSourceIn);
      }

      explosion.doExplosionA();
      explosion.doExplosionB(true);
      return explosion;
   }

   public float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
      double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
      double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
      double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
            for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
               for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                  double d5 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                  double d6 = bb.minY + (bb.maxY - bb.minY) * (double)f1;
                  double d7 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f2;
                  if (this.rayTraceBlocks(new Vec3d(d5 + d3, d6, d7 + d4), vec) == null) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float)i / (float)j;
      } else {
         return 0.0F;
      }
   }

   public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side) {
      pos = pos.offset(side);
      if (this.getBlockState(pos).getBlock() == Blocks.FIRE) {
         this.playEvent(player, 1009, pos, 0);
         this.removeBlock(pos);
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugLoadedEntities() {
      return "All: " + this.loadedEntityList.size();
   }

   @OnlyIn(Dist.CLIENT)
   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      if (isOutsideBuildHeight(pos)) {
         return null;
      } else {
         TileEntity tileentity = null;
         if (this.processingLoadedTiles) {
            tileentity = this.getPendingTileEntityAt(pos);
         }

         if (tileentity == null) {
            tileentity = this.getChunk(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
         }

         if (tileentity == null) {
            tileentity = this.getPendingTileEntityAt(pos);
         }

         return tileentity;
      }
   }

   @Nullable
   private TileEntity getPendingTileEntityAt(BlockPos pos) {
      for(int i = 0; i < this.addedTileEntityList.size(); ++i) {
         TileEntity tileentity = this.addedTileEntityList.get(i);
         if (!tileentity.isRemoved() && tileentity.getPos().equals(pos)) {
            return tileentity;
         }
      }

      return null;
   }

   public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
      if (!isOutsideBuildHeight(pos)) {
         if (tileEntityIn != null && !tileEntityIn.isRemoved()) {
            if (this.processingLoadedTiles) {
               tileEntityIn.setPos(pos);
               Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();

               while(iterator.hasNext()) {
                  TileEntity tileentity = iterator.next();
                  if (tileentity.getPos().equals(pos)) {
                     tileentity.remove();
                     iterator.remove();
                  }
               }

               this.addedTileEntityList.add(tileEntityIn);
            } else {
               this.getChunk(pos).addTileEntity(pos, tileEntityIn);
               this.addTileEntity(tileEntityIn);
            }
         }

      }
   }

   public void removeTileEntity(BlockPos pos) {
      TileEntity tileentity = this.getTileEntity(pos);
      if (tileentity != null && this.processingLoadedTiles) {
         tileentity.remove();
         this.addedTileEntityList.remove(tileentity);
      } else {
         if (tileentity != null) {
            this.addedTileEntityList.remove(tileentity);
            this.loadedTileEntityList.remove(tileentity);
            this.tickableTileEntities.remove(tileentity);
         }

         this.getChunk(pos).removeTileEntity(pos);
      }

   }

   public void markTileEntityForRemoval(TileEntity tileEntityIn) {
      this.tileEntitiesToBeRemoved.add(tileEntityIn);
   }

   public boolean isBlockFullCube(BlockPos pos) {
      return Block.isOpaque(this.getBlockState(pos).getCollisionShape(this, pos));
   }

   public boolean isBlockPresent(BlockPos pos) {
      if (isOutsideBuildHeight(pos)) {
         return false;
      } else {
         Chunk chunk = this.chunkProvider.getChunk(pos.getX() >> 4, pos.getZ() >> 4, false, false);
         return chunk != null && !chunk.isEmpty();
      }
   }

   public boolean isTopSolid(BlockPos pos) {
      return this.isBlockPresent(pos) && this.getBlockState(pos).isTopSolid();
   }

   public void calculateInitialSkylight() {
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.skylightSubtracted) {
         this.skylightSubtracted = i;
      }

   }

   public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
      this.spawnHostileMobs = hostile;
      this.spawnPeacefulMobs = peaceful;
   }

   public void tick(BooleanSupplier hasTimeLeft) {
      this.worldBorder.tick();
      this.tickWeather();
   }

   protected void calculateInitialWeather() {
      if (this.worldInfo.isRaining()) {
         this.rainingStrength = 1.0F;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = 1.0F;
         }
      }

   }

   public void close() {
      this.chunkProvider.close();
   }

   protected void tickWeather() {
      if (this.dimension.hasSkyLight()) {
         if (!this.isRemote) {
            boolean flag = this.getGameRules().getBoolean("doWeatherCycle");
            if (flag) {
               int i = this.worldInfo.getClearWeatherTime();
               if (i > 0) {
                  --i;
                  this.worldInfo.setClearWeatherTime(i);
                  this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                  this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
               }

               int j = this.worldInfo.getThunderTime();
               if (j <= 0) {
                  if (this.worldInfo.isThundering()) {
                     this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                  } else {
                     this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                  }
               } else {
                  --j;
                  this.worldInfo.setThunderTime(j);
                  if (j <= 0) {
                     this.worldInfo.setThundering(!this.worldInfo.isThundering());
                  }
               }

               int k = this.worldInfo.getRainTime();
               if (k <= 0) {
                  if (this.worldInfo.isRaining()) {
                     this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                  } else {
                     this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                  }
               } else {
                  --k;
                  this.worldInfo.setRainTime(k);
                  if (k <= 0) {
                     this.worldInfo.setRaining(!this.worldInfo.isRaining());
                  }
               }
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.isThundering()) {
               this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            } else {
               this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.isRaining()) {
               this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            } else {
               this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn) {
      chunkIn.enqueueRelightChecks();
   }

   protected void tickBlocks() {
   }

   public boolean checkLight(BlockPos pos) {
      boolean flag = false;
      if (this.dimension.hasSkyLight()) {
         flag |= this.checkLightFor(EnumLightType.SKY, pos);
      }

      flag = flag | this.checkLightFor(EnumLightType.BLOCK, pos);
      return flag;
   }

   private int getRawLight(BlockPos pos, EnumLightType lightType) {
      if (lightType == EnumLightType.SKY && this.canSeeSky(pos)) {
         return 15;
      } else {
         IBlockState iblockstate = this.getBlockState(pos);
         int i = lightType == EnumLightType.SKY ? 0 : iblockstate.getLightValue();
         int j = iblockstate.getOpacity(this, pos);
         if (j >= 15 && iblockstate.getLightValue() > 0) {
            j = 1;
         }

         if (j < 1) {
            j = 1;
         }

         if (j >= 15) {
            return 0;
         } else if (i >= 14) {
            return i;
         } else {
            try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
               for(EnumFacing enumfacing : FACING_VALUES) {
                  blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
                  int k = this.getLightFor(lightType, blockpos$pooledmutableblockpos) - j;
                  if (k > i) {
                     i = k;
                  }

                  if (i >= 14) {
                     int l = i;
                     return l;
                  }
               }

               return i;
            }
         }
      }
   }

   public boolean checkLightFor(EnumLightType lightType, BlockPos pos) {
      if (!this.isAreaLoaded(pos, 17, false)) {
         return false;
      } else {
         int i = 0;
         int j = 0;
         this.profiler.startSection("getBrightness");
         int k = this.getLightFor(lightType, pos);
         int l = this.getRawLight(pos, lightType);
         int i1 = pos.getX();
         int j1 = pos.getY();
         int k1 = pos.getZ();
         if (l > k) {
            this.lightUpdateBlockList[j++] = 133152;
         } else if (l < k) {
            this.lightUpdateBlockList[j++] = 133152 | k << 18;

            while(i < j) {
               int l1 = this.lightUpdateBlockList[i++];
               int i2 = (l1 & 63) - 32 + i1;
               int j2 = (l1 >> 6 & 63) - 32 + j1;
               int k2 = (l1 >> 12 & 63) - 32 + k1;
               int l2 = l1 >> 18 & 15;
               BlockPos blockpos = new BlockPos(i2, j2, k2);
               int i3 = this.getLightFor(lightType, blockpos);
               if (i3 == l2) {
                  this.setLightFor(lightType, blockpos, 0);
                  if (l2 > 0) {
                     int j3 = MathHelper.abs(i2 - i1);
                     int k3 = MathHelper.abs(j2 - j1);
                     int l3 = MathHelper.abs(k2 - k1);
                     if (j3 + k3 + l3 < 17) {
                        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
                           for(EnumFacing enumfacing : FACING_VALUES) {
                              int i4 = i2 + enumfacing.getXOffset();
                              int j4 = j2 + enumfacing.getYOffset();
                              int k4 = k2 + enumfacing.getZOffset();
                              blockpos$pooledmutableblockpos.setPos(i4, j4, k4);
                              int l4 = Math.max(1, this.getBlockState(blockpos$pooledmutableblockpos).getOpacity(this, blockpos$pooledmutableblockpos));
                              i3 = this.getLightFor(lightType, blockpos$pooledmutableblockpos);
                              if (i3 == l2 - l4 && j < this.lightUpdateBlockList.length) {
                                 this.lightUpdateBlockList[j++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l2 - l4 << 18;
                              }
                           }
                        }
                     }
                  }
               }
            }

            i = 0;
         }

         this.profiler.endSection();
         this.profiler.startSection("checkedPosition < toCheckCount");

         while(i < j) {
            int i5 = this.lightUpdateBlockList[i++];
            int j5 = (i5 & 63) - 32 + i1;
            int k5 = (i5 >> 6 & 63) - 32 + j1;
            int l5 = (i5 >> 12 & 63) - 32 + k1;
            BlockPos blockpos1 = new BlockPos(j5, k5, l5);
            int i6 = this.getLightFor(lightType, blockpos1);
            int j6 = this.getRawLight(blockpos1, lightType);
            if (j6 != i6) {
               this.setLightFor(lightType, blockpos1, j6);
               if (j6 > i6) {
                  int k6 = Math.abs(j5 - i1);
                  int l6 = Math.abs(k5 - j1);
                  int i7 = Math.abs(l5 - k1);
                  boolean flag = j < this.lightUpdateBlockList.length - 6;
                  if (k6 + l6 + i7 < 17 && flag) {
                     if (this.getLightFor(lightType, blockpos1.west()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(lightType, blockpos1.east()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(lightType, blockpos1.down()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(lightType, blockpos1.up()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(lightType, blockpos1.north()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
                     }

                     if (this.getLightFor(lightType, blockpos1.south()) < j6) {
                        this.lightUpdateBlockList[j++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
                     }
                  }
               }
            }
         }

         this.profiler.endSection();
         return true;
      }
   }

   public Stream<VoxelShape> getCollisionBoxes(@Nullable Entity movingEntity, VoxelShape area, VoxelShape p_212392_3_, Set<Entity> entitiesToIgnore) {
      Stream<VoxelShape> stream = IWorld.super.getCollisionBoxes(movingEntity, area, p_212392_3_, entitiesToIgnore);
      return movingEntity == null ? stream : Stream.concat(stream, this.getCollisionBoxes(movingEntity, area, entitiesToIgnore));
   }

   public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
      List<Entity> list = Lists.newArrayList();
      int i = MathHelper.floor((boundingBox.minX - 2.0D) / 16.0D);
      int j = MathHelper.floor((boundingBox.maxX + 2.0D) / 16.0D);
      int k = MathHelper.floor((boundingBox.minZ - 2.0D) / 16.0D);
      int l = MathHelper.floor((boundingBox.maxZ + 2.0D) / 16.0D);

      for(int i1 = i; i1 <= j; ++i1) {
         for(int j1 = k; j1 <= l; ++j1) {
            if (this.isChunkLoaded(i1, j1, true)) {
               this.getChunk(i1, j1).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
      List<T> list = Lists.newArrayList();

      for(Entity entity : this.loadedEntityList) {
         if (entityType.isAssignableFrom(entity.getClass()) && filter.test((T)entity)) {
            list.add((T)entity);
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
      List<T> list = Lists.newArrayList();

      for(Entity entity : this.playerEntities) {
         if (playerType.isAssignableFrom(entity.getClass()) && filter.test((T)entity)) {
            list.add((T)entity);
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
      return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
      int i = MathHelper.floor((aabb.minX - 2.0D) / 16.0D);
      int j = MathHelper.ceil((aabb.maxX + 2.0D) / 16.0D);
      int k = MathHelper.floor((aabb.minZ - 2.0D) / 16.0D);
      int l = MathHelper.ceil((aabb.maxZ + 2.0D) / 16.0D);
      List<T> list = Lists.newArrayList();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            if (this.isChunkLoaded(i1, j1, true)) {
               this.getChunk(i1, j1).getEntitiesOfTypeWithinAABB(clazz, aabb, list, filter);
            }
         }
      }

      return list;
   }

   @Nullable
   public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
      List<T> list = this.getEntitiesWithinAABB(entityType, aabb);
      T t = null;
      double d0 = Double.MAX_VALUE;

      for(int i = 0; i < list.size(); ++i) {
         T t1 = list.get(i);
         if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.test(t1)) {
            double d1 = closestTo.getDistanceSq(t1);
            if (!(d1 > d0)) {
               t = t1;
               d0 = d1;
            }
         }
      }

      return t;
   }

   @Nullable
   public Entity getEntityByID(int id) {
      return this.entitiesById.lookup(id);
   }

   @OnlyIn(Dist.CLIENT)
   public int getLoadedEntities() {
      return this.loadedEntityList.size();
   }

   public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
      if (this.isBlockLoaded(pos)) {
         this.getChunk(pos).markDirty();
      }

   }

   public int countEntities(Class<?> entityType, int p_72907_2_) {
      int i = 0;
      Iterator iterator = this.loadedEntityList.iterator();

      while(true) {
         if (!iterator.hasNext()) {
            return i;
         }

         Entity entity = (Entity)iterator.next();
         if (!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) {
            if (entityType.isAssignableFrom(entity.getClass())) {
               ++i;
            }

            if (i > p_72907_2_) {
               break;
            }
         }
      }

      return i;
   }

   public void addEntities(Stream<Entity> entities) {
      entities.forEach((p_212418_1_) -> {
         this.loadedEntityList.add(p_212418_1_);
         this.onEntityAdded(p_212418_1_);
      });
   }

   public void unloadEntities(Collection<Entity> entityCollection) {
      this.unloadedEntityList.addAll(entityCollection);
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public World getWorld() {
      return this;
   }

   public void setSeaLevel(int seaLevelIn) {
      this.seaLevel = seaLevelIn;
   }

   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      return this.getBlockState(pos).getStrongPower(this, pos, direction);
   }

   public WorldType getWorldType() {
      return this.worldInfo.getGenerator();
   }

   public int getStrongPower(BlockPos pos) {
      int i = 0;
      i = Math.max(i, this.getStrongPower(pos.down(), EnumFacing.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getStrongPower(pos.up(), EnumFacing.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getStrongPower(pos.north(), EnumFacing.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getStrongPower(pos.south(), EnumFacing.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getStrongPower(pos.west(), EnumFacing.WEST));
                  if (i >= 15) {
                     return i;
                  } else {
                     i = Math.max(i, this.getStrongPower(pos.east(), EnumFacing.EAST));
                     return i >= 15 ? i : i;
                  }
               }
            }
         }
      }
   }

   public boolean isSidePowered(BlockPos pos, EnumFacing side) {
      return this.getRedstonePower(pos, side) > 0;
   }

   public int getRedstonePower(BlockPos pos, EnumFacing facing) {
      IBlockState iblockstate = this.getBlockState(pos);
      return iblockstate.isNormalCube() ? this.getStrongPower(pos) : iblockstate.getWeakPower(this, pos, facing);
   }

   public boolean isBlockPowered(BlockPos pos) {
      if (this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0) {
         return true;
      } else if (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0) {
         return true;
      } else if (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0) {
         return true;
      } else if (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0) {
         return true;
      } else if (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0) {
         return true;
      } else {
         return this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0;
      }
   }

   public int getRedstonePowerFromNeighbors(BlockPos pos) {
      int i = 0;

      for(EnumFacing enumfacing : FACING_VALUES) {
         int j = this.getRedstonePower(pos.offset(enumfacing), enumfacing);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   @Nullable
   public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (predicate.test(entityplayer1)) {
            double d1 = entityplayer1.getDistanceSq(x, y, z);
            if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer)) {
            double d0 = entityplayer.getDistanceSq(x, y, z);
            if (range < 0.0D || d0 < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isLivingPlayerWithinRange(double x, double y, double z, double range) {
      for(EntityPlayer entityplayer : this.playerEntities) {
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer) && EntitySelectors.IS_LIVING_ALIVE.test(entityplayer)) {
            double d0 = entityplayer.getDistanceSq(x, y, z);
            if (range < 0.0D || d0 < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   public EntityPlayer func_212817_a(double p_212817_1_, double p_212817_3_, double p_212817_5_) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (EntitySelectors.NOT_SPECTATING.test(entityplayer1)) {
            double d1 = entityplayer1.getDistanceSq(p_212817_1_, entityplayer1.posY, p_212817_3_);
            if ((p_212817_5_ < 0.0D || d1 < p_212817_5_ * p_212817_5_) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance) {
      return this.getNearestAttackablePlayer(entityIn.posX, entityIn.posY, entityIn.posZ, maxXZDistance, maxYDistance, (Function<EntityPlayer, Double>)null, (Predicate<EntityPlayer>)null);
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance) {
      return this.getNearestAttackablePlayer((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), maxXZDistance, maxYDistance, (Function<EntityPlayer, Double>)null, (Predicate<EntityPlayer>)null);
   }

   @Nullable
   public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> predicate) {
      double d0 = -1.0D;
      EntityPlayer entityplayer = null;

      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer1 = this.playerEntities.get(i);
         if (!entityplayer1.abilities.disableDamage && entityplayer1.isAlive() && !entityplayer1.isSpectator() && (predicate == null || predicate.test(entityplayer1))) {
            double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
            double d2 = maxXZDistance;
            if (entityplayer1.isSneaking()) {
               d2 = maxXZDistance * (double)0.8F;
            }

            if (entityplayer1.isInvisible()) {
               float f = entityplayer1.getArmorVisibility();
               if (f < 0.1F) {
                  f = 0.1F;
               }

               d2 *= (double)(0.7F * f);
            }

            if (playerToDouble != null) {
               d2 *= MoreObjects.firstNonNull(playerToDouble.apply(entityplayer1), 1.0D);
            }

            if ((maxYDistance < 0.0D || Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) && (maxXZDistance < 0.0D || d1 < d2 * d2) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               entityplayer = entityplayer1;
            }
         }
      }

      return entityplayer;
   }

   @Nullable
   public EntityPlayer getPlayerEntityByName(String name) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (name.equals(entityplayer.getName().getString())) {
            return entityplayer;
         }
      }

      return null;
   }

   @Nullable
   public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
      for(int i = 0; i < this.playerEntities.size(); ++i) {
         EntityPlayer entityplayer = this.playerEntities.get(i);
         if (uuid.equals(entityplayer.getUniqueID())) {
            return entityplayer;
         }
      }

      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() throws SessionLockException {
      this.saveHandler.checkSessionLock();
   }

   @OnlyIn(Dist.CLIENT)
   public void setGameTime(long worldTime) {
      this.worldInfo.setGameTime(worldTime);
   }

   public long getSeed() {
      return this.worldInfo.getSeed();
   }

   public long getGameTime() {
      return this.worldInfo.getGameTime();
   }

   public long getDayTime() {
      return this.worldInfo.getDayTime();
   }

   public void setDayTime(long time) {
      this.worldInfo.setDayTime(time);
   }

   public BlockPos getSpawnPoint() {
      BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
      if (!this.getWorldBorder().contains(blockpos)) {
         blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public void setSpawnPoint(BlockPos pos) {
      this.worldInfo.setSpawn(pos);
   }

   @OnlyIn(Dist.CLIENT)
   public void joinEntityInSurroundings(Entity entityIn) {
      int i = MathHelper.floor(entityIn.posX / 16.0D);
      int j = MathHelper.floor(entityIn.posZ / 16.0D);
      int k = 2;

      for(int l = -2; l <= 2; ++l) {
         for(int i1 = -2; i1 <= 2; ++i1) {
            this.getChunk(i + l, j + i1);
         }
      }

      if (!this.loadedEntityList.contains(entityIn)) {
         this.loadedEntityList.add(entityIn);
      }

   }

   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return true;
   }

   public void setEntityState(Entity entityIn, byte state) {
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      this.getBlockState(pos).onBlockEventReceived(this, pos, eventID, eventParam);
   }

   public ISaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public void updateAllPlayersSleepingFlag() {
   }

   public float getThunderStrength(float delta) {
      return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
   }

   @OnlyIn(Dist.CLIENT)
   public void setThunderStrength(float strength) {
      this.prevThunderingStrength = strength;
      this.thunderingStrength = strength;
   }

   public float getRainStrength(float delta) {
      return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
   }

   @OnlyIn(Dist.CLIENT)
   public void setRainStrength(float strength) {
      this.prevRainingStrength = strength;
      this.rainingStrength = strength;
   }

   public boolean isThundering() {
      if (this.dimension.hasSkyLight() && !this.dimension.isNether()) {
         return (double)this.getThunderStrength(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainStrength(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos position) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(position)) {
         return false;
      } else if (this.getHeight(Heightmap.Type.MOTION_BLOCKING, position).getY() > position.getY()) {
         return false;
      } else {
         return this.getBiome(position).getPrecipitation() == Biome.RainType.RAIN;
      }
   }

   public boolean isBlockinHighHumidity(BlockPos pos) {
      Biome biome = this.getBiome(pos);
      return biome.isHighHumidity();
   }

   @Nullable
   public WorldSavedDataStorage getSavedDataStorage() {
      return this.savedDataStorage;
   }

   public void playBroadcastSound(int id, BlockPos pos, int data) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         this.eventListeners.get(i).broadcastSound(id, pos, data);
      }

   }

   public void playEvent(int type, BlockPos pos, int data) {
      this.playEvent((EntityPlayer)null, type, pos, data);
   }

   public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
      try {
         for(int i = 0; i < this.eventListeners.size(); ++i) {
            this.eventListeners.get(i).playEvent(player, type, pos, data);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
         crashreportcategory.addDetail("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
         crashreportcategory.addDetail("Event source", player);
         crashreportcategory.addDetail("Event type", type);
         crashreportcategory.addDetail("Event data", data);
         throw new ReportedException(crashreport);
      }
   }

   public int getHeight() {
      return 256;
   }

   public int getActualHeight() {
      return this.dimension.isNether() ? 128 : 256;
   }

   @OnlyIn(Dist.CLIENT)
   public double getHorizon() {
      return this.worldInfo.getGenerator() == WorldType.FLAT ? 0.0D : 63.0D;
   }

   public CrashReportCategory fillCrashReport(CrashReport report) {
      CrashReportCategory crashreportcategory = report.makeCategoryDepth("Affected level", 1);
      crashreportcategory.addDetail("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
      crashreportcategory.addDetail("All players", () -> {
         return this.playerEntities.size() + " total; " + this.playerEntities;
      });
      crashreportcategory.addDetail("Chunk stats", () -> {
         return this.chunkProvider.makeString();
      });

      try {
         this.worldInfo.addToCrashReport(crashreportcategory);
      } catch (Throwable throwable) {
         crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
      }

      return crashreportcategory;
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for(int i = 0; i < this.eventListeners.size(); ++i) {
         IWorldEventListener iworldeventlistener = this.eventListeners.get(i);
         iworldeventlistener.sendBlockBreakProgress(breakerId, pos, progress);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockpos = pos.offset(enumfacing);
         if (this.isBlockLoaded(blockpos)) {
            IBlockState iblockstate = this.getBlockState(blockpos);
            if (iblockstate.getBlock() == Blocks.COMPARATOR) {
               iblockstate.neighborChanged(this, blockpos, blockIn, pos);
            } else if (iblockstate.isNormalCube()) {
               blockpos = blockpos.offset(enumfacing);
               iblockstate = this.getBlockState(blockpos);
               if (iblockstate.getBlock() == Blocks.COMPARATOR) {
                  iblockstate.neighborChanged(this, blockpos, blockIn, pos);
               }
            }
         }
      }

   }

   public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
      long i = 0L;
      float f = 0.0F;
      if (this.isBlockLoaded(pos)) {
         f = this.getCurrentMoonPhaseFactor();
         i = this.getChunk(pos).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), i, f);
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void setSkylightSubtracted(int newSkylightSubtracted) {
      this.skylightSubtracted = newSkylightSubtracted;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLastLightningBolt() {
      return this.lastLightningBolt;
   }

   public void setLastLightningBolt(int lastLightningBoltIn) {
      this.lastLightningBolt = lastLightningBoltIn;
   }

   public VillageCollection getVillageCollection() {
      return this.villageCollection;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public boolean isSpawnChunk(int x, int z) {
      BlockPos blockpos = this.getSpawnPoint();
      int i = x * 16 + 8 - blockpos.getX();
      int j = z * 16 + 8 - blockpos.getZ();
      int k = 128;
      return i >= -128 && i <= 128 && j >= -128 && j <= 128;
   }

   public LongSet getForcedChunks() {
      ForcedChunksSaveData forcedchunkssavedata = this.getSavedData(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean isForcedChunk(int x, int z) {
      ForcedChunksSaveData forcedchunkssavedata = this.getSavedData(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      return forcedchunkssavedata != null && forcedchunkssavedata.getChunks().contains(ChunkPos.asLong(x, z));
   }

   public boolean setChunkForced(int x, int z, boolean forced) {
      String s = "chunks";
      ForcedChunksSaveData forcedchunkssavedata = this.getSavedData(this.dimension.getType(), ForcedChunksSaveData::new, "chunks");
      if (forcedchunkssavedata == null) {
         forcedchunkssavedata = new ForcedChunksSaveData("chunks");
         this.setSavedData(this.dimension.getType(), "chunks", forcedchunkssavedata);
      }

      long i = ChunkPos.asLong(x, z);
      boolean flag;
      if (forced) {
         flag = forcedchunkssavedata.getChunks().add(i);
         if (flag) {
            this.getChunk(x, z);
         }
      } else {
         flag = forcedchunkssavedata.getChunks().remove(i);
      }

      forcedchunkssavedata.setDirty(flag);
      return flag;
   }

   public void sendPacketToServer(Packet<?> packetIn) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   @Nullable
   public BlockPos findNearestStructure(String name, BlockPos pos, int radius, boolean p_211157_4_) {
      return null;
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.rand;
   }

   public abstract RecipeManager getRecipeManager();

   public abstract NetworkTagManager getTags();
}
