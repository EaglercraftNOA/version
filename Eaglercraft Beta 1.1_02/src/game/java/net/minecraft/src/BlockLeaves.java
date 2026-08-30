package net.minecraft.src;

import net.lax1dude.eaglercraft.Random;

public class BlockLeaves extends BlockLeavesBase {
	private int baseIndexInPNG;
	int[] field_20017_a;

	protected BlockLeaves(int var1, int var2) {
		super(var1, var2, Material.leaves, false);
		this.baseIndexInPNG = var2;
		this.setTickOnLoad(true);
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		var1.func_4075_a().func_4069_a(var2, var4, 1, 1);
		double var5 = var1.func_4075_a().temperature[0];
		double var7 = var1.func_4075_a().humidity[0];
		return ColorizerFoliage.func_4146_a(var5, var7);
	}

	public void onBlockRemoval(World var1, int var2, int var3, int var4) {
		byte var5 = 1;
		int var6 = var5 + 1;
		if(var1.checkChunksExist(var2 - var6, var3 - var6, var4 - var6, var2 + var6, var3 + var6, var4 + var6)) {
			for(int var7 = -var5; var7 <= var5; ++var7) {
				for(int var8 = -var5; var8 <= var5; ++var8) {
					for(int var9 = -var5; var9 <= var5; ++var9) {
						int var10 = var1.getBlockId(var2 + var7, var3 + var8, var4 + var9);
						if(var10 == Block.leaves.blockID) {
							var1.setBlockMetadata(var2 + var7, var3 + var8, var4 + var9, 7);
						}
					}
				}
			}
		}

	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
		if(!var1.multiplayerWorld) {
			if(var1.getBlockMetadata(var2, var3, var4) == 7) {
				byte var6 = 4;
				int var7 = var6 + 1;
				byte var8 = 32;
				int var9 = var8 * var8;
				int var10 = var8 / 2;
				if(this.field_20017_a == null) {
					this.field_20017_a = new int[var8 * var8 * var8];
				}

				int var11;
				if(var1.checkChunksExist(var2 - var7, var3 - var7, var4 - var7, var2 + var7, var3 + var7, var4 + var7)) {
					var11 = -var6;

					label111:
					while(true) {
						int var12;
						int var13;
						int var14;
						if(var11 > var6) {
							var11 = 1;

							while(true) {
								if(var11 > 4) {
									break label111;
								}

								for(var12 = -var6; var12 <= var6; ++var12) {
									for(var13 = -var6; var13 <= var6; ++var13) {
										for(var14 = -var6; var14 <= var6; ++var14) {
											if(this.field_20017_a[(var12 + var10) * var9 + (var13 + var10) * var8 + var14 + var10] == var11 - 1) {
												if(this.field_20017_a[(var12 + var10 - 1) * var9 + (var13 + var10) * var8 + var14 + var10] == -2) {
													this.field_20017_a[(var12 + var10 - 1) * var9 + (var13 + var10) * var8 + var14 + var10] = var11;
												}

												if(this.field_20017_a[(var12 + var10 + 1) * var9 + (var13 + var10) * var8 + var14 + var10] == -2) {
													this.field_20017_a[(var12 + var10 + 1) * var9 + (var13 + var10) * var8 + var14 + var10] = var11;
												}

												if(this.field_20017_a[(var12 + var10) * var9 + (var13 + var10 - 1) * var8 + var14 + var10] == -2) {
													this.field_20017_a[(var12 + var10) * var9 + (var13 + var10 - 1) * var8 + var14 + var10] = var11;
												}

												if(this.field_20017_a[(var12 + var10) * var9 + (var13 + var10 + 1) * var8 + var14 + var10] == -2) {
													this.field_20017_a[(var12 + var10) * var9 + (var13 + var10 + 1) * var8 + var14 + var10] = var11;
												}

												if(this.field_20017_a[(var12 + var10) * var9 + (var13 + var10) * var8 + (var14 + var10 - 1)] == -2) {
													this.field_20017_a[(var12 + var10) * var9 + (var13 + var10) * var8 + (var14 + var10 - 1)] = var11;
												}

												if(this.field_20017_a[(var12 + var10) * var9 + (var13 + var10) * var8 + var14 + var10 + 1] == -2) {
													this.field_20017_a[(var12 + var10) * var9 + (var13 + var10) * var8 + var14 + var10 + 1] = var11;
												}
											}
										}
									}
								}

								++var11;
							}
						}

						for(var12 = -var6; var12 <= var6; ++var12) {
							for(var13 = -var6; var13 <= var6; ++var13) {
								var14 = var1.getBlockId(var2 + var11, var3 + var12, var4 + var13);
								if(var14 == Block.wood.blockID) {
									this.field_20017_a[(var11 + var10) * var9 + (var12 + var10) * var8 + var13 + var10] = 0;
								} else if(var14 == Block.leaves.blockID) {
									this.field_20017_a[(var11 + var10) * var9 + (var12 + var10) * var8 + var13 + var10] = -2;
								} else {
									this.field_20017_a[(var11 + var10) * var9 + (var12 + var10) * var8 + var13 + var10] = -1;
								}
							}
						}

						++var11;
					}
				}

				var11 = this.field_20017_a[var10 * var9 + var10 * var8 + var10];
				if(var11 >= 0) {
					var1.setBlockMetadataWithNotify(var2, var3, var4, 0);
				} else {
					this.func_6360_i(var1, var2, var3, var4);
				}
			}

		}
	}

	private void func_6360_i(World var1, int var2, int var3, int var4) {
		this.dropBlockAsItem(var1, var2, var3, var4, var1.getBlockMetadata(var2, var3, var4));
		var1.setBlockWithNotify(var2, var3, var4, 0);
	}

	public int quantityDropped(Random var1) {
		return var1.nextInt(16) == 0 ? 1 : 0;
	}

	public int idDropped(int var1, Random var2) {
		return Block.sapling.blockID;
	}

	public boolean isOpaqueCube() {
		return !this.graphicsLevel;
	}

	public void setGraphicsLevel(boolean var1) {
		this.graphicsLevel = var1;
		this.blockIndexInTexture = this.baseIndexInPNG + (var1 ? 0 : 1);
	}

	public void onEntityWalking(World var1, int var2, int var3, int var4, Entity var5) {
		super.onEntityWalking(var1, var2, var3, var4, var5);
	}
}
