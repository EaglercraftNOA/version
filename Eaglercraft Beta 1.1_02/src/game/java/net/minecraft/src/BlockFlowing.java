package net.minecraft.src;

import net.lax1dude.eaglercraft.Random;

public class BlockFlowing extends BlockFluids {
	int field_460_a = 0;
	boolean[] field_459_b = new boolean[4];
	int[] field_461_c = new int[4];

	protected BlockFlowing(int var1, Material var2) {
		super(var1, var2);
	}

	private void func_20015_j(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockMetadata(var2, var3, var4);
		var1.setBlockAndMetadata(var2, var3, var4, this.blockID + 1, var5);
		var1.func_701_b(var2, var3, var4, var2, var3, var4);
		var1.markBlockNeedsUpdate(var2, var3, var4);
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		int var6 = this.func_290_h(var1, var2, var3, var4);
		byte var7 = 1;
		if(this.blockMaterial == Material.lava && !var1.worldProvider.field_6479_d) {
			var7 = 2;
		}

		boolean var8 = true;
		int var10;
		if(var6 > 0) {
			byte var9 = -100;
			this.field_460_a = 0;
			int var12 = this.func_296_f(var1, var2 - 1, var3, var4, var9);
			var12 = this.func_296_f(var1, var2 + 1, var3, var4, var12);
			var12 = this.func_296_f(var1, var2, var3, var4 - 1, var12);
			var12 = this.func_296_f(var1, var2, var3, var4 + 1, var12);
			var10 = var12 + var7;
			if(var10 >= 8 || var12 < 0) {
				var10 = -1;
			}

			if(this.func_290_h(var1, var2, var3 + 1, var4) >= 0) {
				int var11 = this.func_290_h(var1, var2, var3 + 1, var4);
				if(var11 >= 8) {
					var10 = var11;
				} else {
					var10 = var11 + 8;
				}
			}

			if(this.field_460_a >= 2 && this.blockMaterial == Material.water) {
				if(var1.isBlockOpaqueCube(var2, var3 - 1, var4)) {
					var10 = 0;
				} else if(var1.getBlockMaterial(var2, var3 - 1, var4) == this.blockMaterial && var1.getBlockMetadata(var2, var3, var4) == 0) {
					var10 = 0;
				}
			}

			if(this.blockMaterial == Material.lava && var6 < 8 && var10 < 8 && var10 > var6 && var5.nextInt(4) != 0) {
				var10 = var6;
				var8 = false;
			}

			if(var10 != var6) {
				var6 = var10;
				if(var10 < 0) {
					var1.setBlockWithNotify(var2, var3, var4, 0);
				} else {
					var1.setBlockMetadataWithNotify(var2, var3, var4, var10);
					var1.scheduleBlockUpdate(var2, var3, var4, this.blockID);
					var1.notifyBlocksOfNeighborChange(var2, var3, var4, this.blockID);
				}
			} else if(var8) {
				this.func_20015_j(var1, var2, var3, var4);
			}
		} else {
			this.func_20015_j(var1, var2, var3, var4);
		}

		if(this.func_298_m(var1, var2, var3 - 1, var4)) {
			if(var6 >= 8) {
				var1.setBlockAndMetadataWithNotify(var2, var3 - 1, var4, this.blockID, var6);
			} else {
				var1.setBlockAndMetadataWithNotify(var2, var3 - 1, var4, this.blockID, var6 + 8);
			}
		} else if(var6 >= 0 && (var6 == 0 || this.func_295_l(var1, var2, var3 - 1, var4))) {
			boolean[] var13 = this.func_297_k(var1, var2, var3, var4);
			var10 = var6 + var7;
			if(var6 >= 8) {
				var10 = 1;
			}

			if(var10 >= 8) {
				return;
			}

			if(var13[0]) {
				this.func_299_g(var1, var2 - 1, var3, var4, var10);
			}

			if(var13[1]) {
				this.func_299_g(var1, var2 + 1, var3, var4, var10);
			}

			if(var13[2]) {
				this.func_299_g(var1, var2, var3, var4 - 1, var10);
			}

			if(var13[3]) {
				this.func_299_g(var1, var2, var3, var4 + 1, var10);
			}
		}

	}

	private void func_299_g(World var1, int var2, int var3, int var4, int var5) {
		if(this.func_298_m(var1, var2, var3, var4)) {
			int var6 = var1.getBlockId(var2, var3, var4);
			if(var6 > 0) {
				if(this.blockMaterial == Material.lava) {
					this.func_292_i(var1, var2, var3, var4);
				} else {
					Block.blocksList[var6].dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4));
				}
			}

			var1.setBlockAndMetadataWithNotify(var2, var3, var4, this.blockID, var5);
		}

	}

	private int func_300_a(World var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = 1000;

		for(int var8 = 0; var8 < 4; ++var8) {
			if((var8 != 0 || var6 != 1) && (var8 != 1 || var6 != 0) && (var8 != 2 || var6 != 3) && (var8 != 3 || var6 != 2)) {
				int var9 = var2;
				int var11 = var4;
				if(var8 == 0) {
					var9 = var2 - 1;
				}

				if(var8 == 1) {
					++var9;
				}

				if(var8 == 2) {
					var11 = var4 - 1;
				}

				if(var8 == 3) {
					++var11;
				}

				if(!this.func_295_l(var1, var9, var3, var11) && (var1.getBlockMaterial(var9, var3, var11) != this.blockMaterial || var1.getBlockMetadata(var9, var3, var11) != 0)) {
					if(!this.func_295_l(var1, var9, var3 - 1, var11)) {
						return var5;
					}

					if(var5 < 4) {
						int var12 = this.func_300_a(var1, var9, var3, var11, var5 + 1, var8);
						if(var12 < var7) {
							var7 = var12;
						}
					}
				}
			}
		}

		return var7;
	}

	private boolean[] func_297_k(World var1, int var2, int var3, int var4) {
		int var5;
		int var6;
		for(var5 = 0; var5 < 4; ++var5) {
			this.field_461_c[var5] = 1000;
			var6 = var2;
			int var8 = var4;
			if(var5 == 0) {
				var6 = var2 - 1;
			}

			if(var5 == 1) {
				++var6;
			}

			if(var5 == 2) {
				var8 = var4 - 1;
			}

			if(var5 == 3) {
				++var8;
			}

			if(!this.func_295_l(var1, var6, var3, var8) && (var1.getBlockMaterial(var6, var3, var8) != this.blockMaterial || var1.getBlockMetadata(var6, var3, var8) != 0)) {
				if(!this.func_295_l(var1, var6, var3 - 1, var8)) {
					this.field_461_c[var5] = 0;
				} else {
					this.field_461_c[var5] = this.func_300_a(var1, var6, var3, var8, 1, var5);
				}
			}
		}

		var5 = this.field_461_c[0];

		for(var6 = 1; var6 < 4; ++var6) {
			if(this.field_461_c[var6] < var5) {
				var5 = this.field_461_c[var6];
			}
		}

		for(var6 = 0; var6 < 4; ++var6) {
			this.field_459_b[var6] = this.field_461_c[var6] == var5;
		}

		return this.field_459_b;
	}

	private boolean func_295_l(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		if(var5 != Block.doorWood.blockID && var5 != Block.doorSteel.blockID && var5 != Block.signPost.blockID && var5 != Block.ladder.blockID && var5 != Block.reed.blockID) {
			if(var5 == 0) {
				return false;
			} else {
				Material var6 = Block.blocksList[var5].blockMaterial;
				return var6.func_878_a();
			}
		} else {
			return true;
		}
	}

	protected int func_296_f(World var1, int var2, int var3, int var4, int var5) {
		int var6 = this.func_290_h(var1, var2, var3, var4);
		if(var6 < 0) {
			return var5;
		} else {
			if(var6 == 0) {
				++this.field_460_a;
			}

			if(var6 >= 8) {
				var6 = 0;
			}

			return var5 >= 0 && var6 >= var5 ? var5 : var6;
		}
	}

	private boolean func_298_m(World var1, int var2, int var3, int var4) {
		Material var5 = var1.getBlockMaterial(var2, var3, var4);
		return var5 == this.blockMaterial ? false : (var5 == Material.lava ? false : !this.func_295_l(var1, var2, var3, var4));
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		if(var1.getBlockId(var2, var3, var4) == this.blockID) {
			var1.scheduleBlockUpdate(var2, var3, var4, this.blockID);
		}

	}
}
