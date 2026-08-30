package net.minecraft.src;

import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import com.carrotsearch.hppc.ObjectIntMap;

import net.lax1dude.eaglercraft.Random;

public class BlockFire extends Block {
	private ObjectIntMap<Integer> chanceToEncourageFire = new ObjectIntIdentityHashMap<Integer>(256);
	private ObjectIntMap<Integer> abilityToCatchFire = new ObjectIntIdentityHashMap<Integer>(256);

	protected BlockFire(int var1, int var2) {
		super(var1, var2, Material.fire);
		this.setBurnRate(Block.planks.blockID, 5, 20);
		this.setBurnRate(Block.wood.blockID, 5, 5);
		this.setBurnRate(Block.leaves.blockID, 30, 60);
		this.setBurnRate(Block.bookShelf.blockID, 30, 20);
		this.setBurnRate(Block.tnt.blockID, 15, 100);
		this.setBurnRate(Block.cloth.blockID, 30, 60);
		this.setTickOnLoad(true);
	}

	private void setBurnRate(int var1, int var2, int var3) {
		this.chanceToEncourageFire.put(var1, var2);
		this.abilityToCatchFire.put(var1, var3);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 3;
	}

	public int quantityDropped(Random var1) {
		return 0;
	}

	public int tickRate() {
		return 10;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		boolean var6 = var1.getBlockId(var2, var3 - 1, var4) == Block.bloodStone.blockID;
		int var7 = var1.getBlockMetadata(var2, var3, var4);
		if(var7 < 15) {
			var1.setBlockMetadataWithNotify(var2, var3, var4, var7 + 1);
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID);
		}

		if(!var6 && !this.func_263_h(var1, var2, var3, var4)) {
			if(!var1.isBlockOpaqueCube(var2, var3 - 1, var4) || var7 > 3) {
				var1.setBlockWithNotify(var2, var3, var4, 0);
			}

		} else if(!var6 && !this.canBlockCatchFire(var1, var2, var3 - 1, var4) && var7 == 15 && var5.nextInt(4) == 0) {
			var1.setBlockWithNotify(var2, var3, var4, 0);
		} else {
			if(var7 % 2 == 0 && var7 > 2) {
				this.tryToCatchBlockOnFire(var1, var2 + 1, var3, var4, 300, var5);
				this.tryToCatchBlockOnFire(var1, var2 - 1, var3, var4, 300, var5);
				this.tryToCatchBlockOnFire(var1, var2, var3 - 1, var4, 250, var5);
				this.tryToCatchBlockOnFire(var1, var2, var3 + 1, var4, 250, var5);
				this.tryToCatchBlockOnFire(var1, var2, var3, var4 - 1, 300, var5);
				this.tryToCatchBlockOnFire(var1, var2, var3, var4 + 1, 300, var5);

				for(int var8 = var2 - 1; var8 <= var2 + 1; ++var8) {
					for(int var9 = var4 - 1; var9 <= var4 + 1; ++var9) {
						for(int var10 = var3 - 1; var10 <= var3 + 4; ++var10) {
							if(var8 != var2 || var10 != var3 || var9 != var4) {
								int var11 = 100;
								if(var10 > var3 + 1) {
									var11 += (var10 - (var3 + 1)) * 100;
								}

								int var12 = this.getChanceOfNeighborsEncouragingFire(var1, var8, var10, var9);
								if(var12 > 0 && var5.nextInt(var11) <= var12) {
									var1.setBlockWithNotify(var8, var10, var9, this.blockID);
								}
							}
						}
					}
				}
			}

		}
	}

	private void tryToCatchBlockOnFire(World var1, int var2, int var3, int var4, int var5, Random var6) {
		int var7 = this.abilityToCatchFire.get(var1.getBlockId(var2, var3, var4));
		if(var6.nextInt(var5) < var7) {
			boolean var8 = var1.getBlockId(var2, var3, var4) == Block.tnt.blockID;
			if(var6.nextInt(2) == 0) {
				var1.setBlockWithNotify(var2, var3, var4, this.blockID);
			} else {
				var1.setBlockWithNotify(var2, var3, var4, 0);
			}

			if(var8) {
				Block.tnt.onBlockDestroyedByPlayer(var1, var2, var3, var4, 0);
			}
		}

	}

	private boolean func_263_h(World var1, int var2, int var3, int var4) {
		return this.canBlockCatchFire(var1, var2 + 1, var3, var4) ? true : (this.canBlockCatchFire(var1, var2 - 1, var3, var4) ? true : (this.canBlockCatchFire(var1, var2, var3 - 1, var4) ? true : (this.canBlockCatchFire(var1, var2, var3 + 1, var4) ? true : (this.canBlockCatchFire(var1, var2, var3, var4 - 1) ? true : this.canBlockCatchFire(var1, var2, var3, var4 + 1)))));
	}

	private int getChanceOfNeighborsEncouragingFire(World var1, int var2, int var3, int var4) {
		byte var5 = 0;
		if(!var1.func_20084_d(var2, var3, var4)) {
			return 0;
		} else {
			int var6 = this.getChanceToEncourageFire(var1, var2 + 1, var3, var4, var5);
			var6 = this.getChanceToEncourageFire(var1, var2 - 1, var3, var4, var6);
			var6 = this.getChanceToEncourageFire(var1, var2, var3 - 1, var4, var6);
			var6 = this.getChanceToEncourageFire(var1, var2, var3 + 1, var4, var6);
			var6 = this.getChanceToEncourageFire(var1, var2, var3, var4 - 1, var6);
			var6 = this.getChanceToEncourageFire(var1, var2, var3, var4 + 1, var6);
			return var6;
		}
	}

	public boolean isCollidable() {
		return false;
	}

	public boolean canBlockCatchFire(IBlockAccess var1, int var2, int var3, int var4) {
		return this.chanceToEncourageFire.get(var1.getBlockId(var2, var3, var4)) > 0;
	}

	public int getChanceToEncourageFire(World var1, int var2, int var3, int var4, int var5) {
		int var6 = this.chanceToEncourageFire.get(var1.getBlockId(var2, var3, var4));
		return var6 > var5 ? var6 : var5;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		return var1.isBlockOpaqueCube(var2, var3 - 1, var4) || this.func_263_h(var1, var2, var3, var4);
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.isBlockOpaqueCube(var2, var3 - 1, var4) && !this.func_263_h(var1, var2, var3, var4)) {
			var1.setBlockWithNotify(var2, var3, var4, 0);
		}
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		if(var1.getBlockId(var2, var3 - 1, var4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(var1, var2, var3, var4)) {
			if(!var1.isBlockOpaqueCube(var2, var3 - 1, var4) && !this.func_263_h(var1, var2, var3, var4)) {
				var1.setBlockWithNotify(var2, var3, var4, 0);
			} else {
				var1.scheduleBlockUpdate(var2, var3, var4, this.blockID);
			}
		}
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(var5.nextInt(24) == 0) {
			var1.playSoundEffect((double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), "fire.fire", 1.0F + var5.nextFloat(), var5.nextFloat() * 0.7F + 0.3F);
		}

		int var6;
		float var7;
		float var8;
		float var9;
		if(!var1.isBlockOpaqueCube(var2, var3 - 1, var4) && !Block.fire.canBlockCatchFire(var1, var2, var3 - 1, var4)) {
			if(Block.fire.canBlockCatchFire(var1, var2 - 1, var3, var4)) {
				for(var6 = 0; var6 < 2; ++var6) {
					var7 = (float)var2 + var5.nextFloat() * 0.1F;
					var8 = (float)var3 + var5.nextFloat();
					var9 = (float)var4 + var5.nextFloat();
					var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(var1, var2 + 1, var3, var4)) {
				for(var6 = 0; var6 < 2; ++var6) {
					var7 = (float)(var2 + 1) - var5.nextFloat() * 0.1F;
					var8 = (float)var3 + var5.nextFloat();
					var9 = (float)var4 + var5.nextFloat();
					var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(var1, var2, var3, var4 - 1)) {
				for(var6 = 0; var6 < 2; ++var6) {
					var7 = (float)var2 + var5.nextFloat();
					var8 = (float)var3 + var5.nextFloat();
					var9 = (float)var4 + var5.nextFloat() * 0.1F;
					var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(var1, var2, var3, var4 + 1)) {
				for(var6 = 0; var6 < 2; ++var6) {
					var7 = (float)var2 + var5.nextFloat();
					var8 = (float)var3 + var5.nextFloat();
					var9 = (float)(var4 + 1) - var5.nextFloat() * 0.1F;
					var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
				}
			}

			if(Block.fire.canBlockCatchFire(var1, var2, var3 + 1, var4)) {
				for(var6 = 0; var6 < 2; ++var6) {
					var7 = (float)var2 + var5.nextFloat();
					var8 = (float)(var3 + 1) - var5.nextFloat() * 0.1F;
					var9 = (float)var4 + var5.nextFloat();
					var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
				}
			}
		} else {
			for(var6 = 0; var6 < 3; ++var6) {
				var7 = (float)var2 + var5.nextFloat();
				var8 = (float)var3 + var5.nextFloat() * 0.5F + 0.5F;
				var9 = (float)var4 + var5.nextFloat();
				var1.spawnParticle("largesmoke", (double)var7, (double)var8, (double)var9, 0.0D, 0.0D, 0.0D);
			}
		}

	}
}
