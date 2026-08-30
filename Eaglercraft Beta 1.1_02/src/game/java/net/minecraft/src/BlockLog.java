package net.minecraft.src;

import net.lax1dude.eaglercraft.Random;

public class BlockLog extends Block {
	protected BlockLog(int var1) {
		super(var1, Material.wood);
		this.blockIndexInTexture = 20;
	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public int idDropped(int var1, Random var2) {
		return Block.wood.blockID;
	}

	public void onBlockRemoval(World var1, int var2, int var3, int var4) {
		byte var5 = 4;
		int var6 = var5 + 1;
		if(var1.checkChunksExist(var2 - var6, var3 - var6, var4 - var6, var2 + var6, var3 + var6, var4 + var6)) {
			for(int var7 = -var5; var7 <= var5; ++var7) {
				for(int var8 = -var5; var8 <= var5; ++var8) {
					for(int var9 = -var5; var9 <= var5; ++var9) {
						int var10 = var1.getBlockId(var2 + var7, var3 + var8, var4 + var9);
						if(var10 == Block.leaves.blockID && var1.getBlockMetadata(var2 + var7, var3 + var8, var4 + var9) != 7) {
							var1.setBlockMetadata(var2 + var7, var3 + var8, var4 + var9, 7);
						}
					}
				}
			}
		}

	}

	public int getBlockTextureFromSide(int var1) {
		return var1 == 1 ? 21 : (var1 == 0 ? 21 : 20);
	}
}
