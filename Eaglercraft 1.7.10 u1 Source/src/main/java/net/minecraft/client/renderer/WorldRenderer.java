package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.shader.TesselatorVertexState;

public class WorldRenderer {
	/** Reference to the World object. */
	public World worldObj;
	private int glRenderList = -1;
	private static Tessellator tessellator = Tessellator.instance;
	public static int chunksUpdated = 0;
	public static int chunksGeometryUpdated = 0;
	public int posX;
	public int posY;
	public int posZ;

	public int chunkX;
	public int chunkY;
	public int chunkZ;

	/** Pos X minus */
	public int posXMinus;

	/** Pos Y minus */
	public int posYMinus;

	/** Pos Z minus */
	public int posZMinus;

	/** Pos X clipped */
	public int posXClip;

	/** Pos Y clipped */
	public int posYClip;

	/** Pos Z clipped */
	public int posZClip;
	public boolean isInFrustum = false;

	/** Should this renderer skip this render pass */
	public boolean[] skipRenderPass = new boolean[2];

	/** Pos X plus */
	public int posXPlus;

	/** Pos Y plus */
	public int posYPlus;

	/** Pos Z plus */
	public int posZPlus;

	/** Boolean for whether this renderer needs to be updated or not */
	public boolean needsUpdate;

	/** Axis aligned bounding box */
	public AxisAlignedBB rendererBoundingBox;

	/** Chunk index */
	public int chunkIndex;

	/** Is this renderer visible according to the occlusion query */
	public boolean isVisible = true;

	/** Is this renderer waiting on the result of the occlusion query */
	public boolean isWaitingOnOcclusionQuery;

	/** Is the chunk lit */
	public boolean isChunkLit;
	private boolean isInitialized = false;

	/** All the tile entities that have special rendering code for this chunk */
	public List tileEntityRenderers = new ArrayList();
	private List tileEntities;

	/** Bytes sent to the GPU */
	private int bytesDrawn;

	private TesselatorVertexState vertexState;
	public int glOcclusionQuery;

	private void preRenderBlocks(int pass) {
		GL11.glNewList(this.glRenderList + pass, GL11.GL_COMPILE);
		GL11.glPushMatrix();
		tessellator.startDrawingQuads();
		tessellator.setTranslationD((double) (this.posXClip - this.posX), (double) (this.posYClip - this.posY),
				(double) (this.posZClip - this.posZ));
	}

	private void postRenderBlocks(int pass, EntityLivingBase entity) {
		this.bytesDrawn += tessellator.draw();
		GL11.glPopMatrix();
		GL11.glEndList();
		tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
	}

	public WorldRenderer(World par1World, List par2List, int par3, int par4, int par5, int par6) {
		this.worldObj = par1World;
		this.tileEntities = par2List;
		this.glRenderList = -1;
		if (this.glRenderList == -1 || this.glRenderList == 0) {
			this.glRenderList = GL11.glGenLists(2);
			if (this.glRenderList == 0) {
				throw new IllegalStateException("Failed to allocate GL display list!");
			}
		}
		this.posX = -999;
		this.setPosition(par3, par4, par5);
		this.needsUpdate = false;
	}

	/**
	 * Sets a new position for the renderer and setting it up so it can be reloaded
	 * with the new data for that position
	 */
	public void setPosition(int par1, int par2, int par3) {
		if (par1 != this.posX || par2 != this.posY || par3 != this.posZ) {
			this.setDontDraw();
			this.posX = par1;
			this.posY = par2;
			this.posZ = par3;
			this.chunkX = par1 >> 4;
			this.chunkY = par2 >> 4;
			this.chunkZ = par3 >> 4;
			this.posXPlus = par1 + 8;
			this.posYPlus = par2 + 8;
			this.posZPlus = par3 + 8;
			this.posXClip = par1 & 1023;
			this.posYClip = par2;
			this.posZClip = par3 & 1023;
			this.posXMinus = par1 - this.posXClip;
			this.posYMinus = par2 - this.posYClip;
			this.posZMinus = par3 - this.posZClip;
			float var4 = 0.0F;
			this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double) ((float) par1 - var4),
					(double) ((float) par2 - var4), (double) ((float) par3 - var4),
					(double) ((float) (par1 + 16) + var4), (double) ((float) (par2 + 16) + var4),
					(double) ((float) (par3 + 16) + var4));

			this.markDirty();
		}
	}

	private void setupGLTranslation() {
		GL11.glTranslatef((float) this.posXClip, (float) this.posYClip, (float) this.posZClip);
	}

	/**
	 * Will update this chunk renderer
	 */
	public void updateRenderer() {
		if (this.glRenderList == -1 || this.glRenderList == 0) {
			this.glRenderList = GL11.glGenLists(2);
			if (this.glRenderList == 0) {
				throw new IllegalStateException("Failed to allocate GL display list!");
			}
		}
		if (this.needsUpdate) {
			this.needsUpdate = false;
			int var1 = this.posX;
			int var2 = this.posY;
			int var3 = this.posZ;
			int var4 = this.posX + 16;
			int var5 = this.posY + 16;
			int var6 = this.posZ + 16;

			for (int var7 = 0; var7 < 2; ++var7) {
				this.skipRenderPass[var7] = true;
			}

			Chunk.isLit = false;
			HashSet var21 = new HashSet();
			var21.addAll(this.tileEntityRenderers);
			this.tileEntityRenderers.clear();
			byte var8 = 1;
			ChunkCache var9 = new ChunkCache(this.worldObj, var1 - var8, var2 - var8, var3 - var8, var4 + var8,
					var5 + var8, var6 + var8, 1);
			if (!var9.extendedLevelsInChunkCache()) {
				++chunksUpdated;
				RenderBlocks var10 = new RenderBlocks(var9);
				this.bytesDrawn = 0;

				for (int var11 = 0; var11 < 2; ++var11) {
					boolean var12 = false;
					boolean var13 = false;
					boolean var14 = false;

					for (int var15 = var2; var15 < var5; ++var15) {
						for (int var16 = var3; var16 < var6; ++var16) {
							for (int var17 = var1; var17 < var4; ++var17) {
								Block var23 = var9.getBlock(var17, var15, var16);
								if (var23 != Blocks.air) {
									if (!var14) {
										var14 = true;
										GL11.glNewList(this.glRenderList + var11, GL11.GL_COMPILE);
										tessellator.startDrawingQuads();
										tessellator.setTranslationD((double) (this.posXClip - this.posX),
												(double) (this.posYClip - this.posY),
												(double) (this.posZClip - this.posZ));
									}
									if (var23 != null) {
										if (var11 == 0 && var23.hasTileEntity()) {
											TileEntity var20 = var9.getTileEntity(var17, var15, var16);
											if (TileEntityRendererDispatcher.instance.hasSpecialRenderer(var20)) {
												this.tileEntityRenderers.add(var20);
											}
										}
										int var24 = var23.getRenderBlockPass();
										if (var24 != var11) {
											var12 = true;
										} else if (var24 == var11) {
											var13 |= var10.renderBlockByRenderType(var23, var17, var15, var16);
										}
									}
								}
							}
						}
					}

					if (var14) {
						tessellator.draw();

						GL11.glEndList();
						tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
					} else {
						var13 = false;
					}

					if (var13) {
						this.skipRenderPass[var11] = false;
					}

					if (!var12) {
						break;
					}
				}

				if (!(skipRenderPass[0] && skipRenderPass[1])) {
					++chunksGeometryUpdated;
				}
			}

			HashSet var22 = new HashSet();
			var22.addAll(this.tileEntityRenderers);
			var22.removeAll(var21);
			this.tileEntities.addAll(var22);
			var21.removeAll(this.tileEntityRenderers);
			this.tileEntities.removeAll(var21);
			this.isChunkLit = Chunk.isLit;
			this.isInitialized = true;

			if (skipRenderPass[0]) {
				GL11.flushDisplayList(glRenderList, true);
			}
			if (skipRenderPass[1]) {
				GL11.flushDisplayList(glRenderList + 1, true);
			}
		}
	}

	public void updateRendererSort(EntityLivingBase entity) {
		if (this.vertexState != null && !this.skipRenderPass[1]) {
			this.preRenderBlocks(1);
			tessellator.setVertexState(this.vertexState);
			this.postRenderBlocks(1, entity);
		}
	}

	/**
	 * Returns the distance of this chunk renderer to the entity without performing
	 * the final normalizing square root, for performance reasons.
	 */
	public float distanceToEntitySquared(Entity par1Entity) {
		float var2 = (float) (par1Entity.posX - (double) this.posXPlus);
		float var3 = (float) (par1Entity.posY - (double) this.posYPlus);
		float var4 = (float) (par1Entity.posZ - (double) this.posZPlus);
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	/**
	 * When called this renderer won't draw anymore until its gets initialized again
	 */
	public void setDontDraw() {
		for (int var1 = 0; var1 < 2; ++var1) {
			this.skipRenderPass[var1] = true;
			GL11.flushDisplayList(glRenderList, true);
			GL11.flushDisplayList(glRenderList + 1, true);
		}

		this.isInFrustum = false;
		this.isInitialized = false;
	}

	public void stopRendering() {
		this.setDontDraw();
		this.worldObj = null;
	}

	/**
	 * Takes in the pass the call list is being requested for. Args: renderPass
	 */
	public int getGLCallListForPass(int par1) {
		return !this.isInFrustum ? -1 : (!this.skipRenderPass[par1] ? this.glRenderList + par1 : -1);
	}

	public void updateInFrustum(ICamera par1ICamera) {
		this.isInFrustum = par1ICamera.isBoundingBoxInFrustum(this.rendererBoundingBox);
	}

	/**
	 * Renders the occlusion query GL List
	 */
	public void callOcclusionQueryList() {
		GL11.glCallList(this.glRenderList + 2);
	}

	public boolean shouldTryOcclusionQuery() {
		return !this.isInitialized ? true : !this.skipRenderPass[0] || !this.skipRenderPass[1];
	}

	/**
	 * Checks if all render passes are to be skipped. Returns false if the renderer
	 * is not initialized
	 */
	public boolean skipAllRenderPasses() {
		return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
	}

	/**
	 * Marks the current renderer data as dirty and needing to be updated.
	 */
	public void markDirty() {
		this.needsUpdate = true;
	}

	/**
	 * Returns true if the entire chunk is completely dark (skylight = 0 everywhere)
	 */
	public boolean isCompletelyDark() {
		int startX = this.posX;
		int startY = this.posY;
		int startZ = this.posZ;
		int endX = this.posX + 16;
		int endY = this.posY + 16;
		int endZ = this.posZ + 16;
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				for (int z = startZ; z < endZ; ++z) {
					int skylight = worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
					if (skylight > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
}