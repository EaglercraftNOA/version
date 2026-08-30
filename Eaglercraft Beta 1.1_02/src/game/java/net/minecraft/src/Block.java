package net.minecraft.src;

import java.util.ArrayList;
import net.lax1dude.eaglercraft.Random;

public class Block {
	public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound soundSandFootstep = new StepSoundSand("sand", 1.0F, 1.0F);
	public static final Block[] blocksList = new Block[256];
	public static final boolean[] tickOnLoad = new boolean[256];
	public static final boolean[] opaqueCubeLookup = new boolean[256];
	public static final boolean[] isBlockContainer = new boolean[256];
	public static final int[] lightOpacity = new int[256];
	public static final boolean[] unusedBooleanArray = new boolean[256];
	public static final int[] lightValue = new int[256];
	public static final Block stone = (new BlockStone(1, 1)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("stone");
	public static final BlockGrass grass = (BlockGrass)(new BlockGrass(2)).setHardness(0.6F).setStepSound(soundGrassFootstep).func_20012_a("grass");
	public static final Block dirt = (new BlockDirt(3, 2)).setHardness(0.5F).setStepSound(soundGravelFootstep).func_20012_a("dirt");
	public static final Block cobblestone = (new Block(4, 16, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("stonebrick");
	public static final Block planks = (new Block(5, 4, Material.wood)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).func_20012_a("wood");
	public static final Block sapling = (new BlockSapling(6, 15)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("sapling");
	public static final Block bedrock = (new Block(7, 17, Material.rock)).setHardness(-1.0F).setResistance(6000000.0F).setStepSound(soundStoneFootstep).func_20012_a("bedrock");
	public static final Block waterStill = (new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).func_20012_a("water");
	public static final Block waterMoving = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).func_20012_a("water");
	public static final Block lavaStill = (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setLightOpacity(255).func_20012_a("lava");
	public static final Block lavaMoving = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setLightOpacity(255).func_20012_a("lava");
	public static final Block sand = (new BlockSand(12, 18)).setHardness(0.5F).setStepSound(soundSandFootstep).func_20012_a("sand");
	public static final Block gravel = (new BlockGravel(13, 19)).setHardness(0.6F).setStepSound(soundGravelFootstep).func_20012_a("gravel");
	public static final Block oreGold = (new BlockOre(14, 32)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreGold");
	public static final Block oreIron = (new BlockOre(15, 33)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreIron");
	public static final Block oreCoal = (new BlockOre(16, 34)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreCoal");
	public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep).func_20012_a("log");
	public static final BlockLeaves leaves = (BlockLeaves)(new BlockLeaves(18, 52)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).func_20012_a("leaves");
	public static final Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep).func_20012_a("sponge");
	public static final Block glass = (new BlockGlass(20, 49, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).func_20012_a("glass");
	public static final Block field_9267_N = null;
	public static final Block field_9266_O = null;
	public static final Block field_9265_P = null;
	public static final Block field_9264_Q = null;
	public static final Block field_9263_R = null;
	public static final Block field_9262_S = null;
	public static final Block field_9261_T = null;
	public static final Block field_9260_U = null;
	public static final Block field_9259_V = null;
	public static final Block field_9258_W = null;
	public static final Block field_9257_X = null;
	public static final Block field_9256_Y = null;
	public static final Block field_9255_Z = null;
	public static final Block field_9269_aa = null;
	public static final Block cloth = (new Block(35, 64, Material.cloth)).setHardness(0.8F).setStepSound(soundClothFootstep).func_20012_a("cloth");
	public static final Block field_9268_ac = null;
	public static final BlockFlower plantYellow = (BlockFlower)(new BlockFlower(37, 13)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("flower");
	public static final BlockFlower plantRed = (BlockFlower)(new BlockFlower(38, 12)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("rose");
	public static final BlockFlower mushroomBrown = (BlockFlower)(new BlockMushroom(39, 29)).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(2.0F / 16.0F).func_20012_a("mushroom");
	public static final BlockFlower mushroomRed = (BlockFlower)(new BlockMushroom(40, 28)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("mushroom");
	public static final Block blockGold = (new BlockOreBlock(41, 39)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).func_20012_a("blockGold");
	public static final Block blockSteel = (new BlockOreBlock(42, 38)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).func_20012_a("blockIron");
	public static final Block stairDouble = (new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("stoneSlab");
	public static final Block stairSingle = (new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("stoneSlab");
	public static final Block brick = (new Block(45, 7, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("brick");
	public static final Block tnt = (new BlockTNT(46, 8)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("tnt");
	public static final Block bookShelf = (new BlockBookshelf(47, 35)).setHardness(1.5F).setStepSound(soundWoodFootstep).func_20012_a("bookshelf");
	public static final Block cobblestoneMossy = (new Block(48, 36, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("stoneMoss");
	public static final Block obsidian = (new BlockObsidian(49, 37)).setHardness(10.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).func_20012_a("obsidian");
	public static final Block torchWood = (new BlockTorch(50, 80)).setHardness(0.0F).setLightValue(15.0F / 16.0F).setStepSound(soundWoodFootstep).func_20012_a("torch");
	public static final BlockFire fire = (BlockFire)(new BlockFire(51, 31)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).func_20012_a("fire");
	public static final Block mobSpawner = (new BlockMobSpawner(52, 65)).setHardness(5.0F).setStepSound(soundMetalFootstep).func_20012_a("mobSpawner");
	public static final Block stairCompactPlanks = (new BlockStairs(53, planks)).func_20012_a("stairsWood");
	public static final Block crate = (new BlockChest(54)).setHardness(2.5F).setStepSound(soundWoodFootstep).func_20012_a("chest");
	public static final Block redstoneWire = (new BlockRedstoneWire(55, 84)).setHardness(0.0F).setStepSound(soundPowderFootstep).func_20012_a("redstoneDust");
	public static final Block oreDiamond = (new BlockOre(56, 50)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreDiamond");
	public static final Block blockDiamond = (new BlockOreBlock(57, 40)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).func_20012_a("blockDiamond");
	public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep).func_20012_a("workbench");
	public static final Block crops = (new BlockCrops(59, 88)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("crops");
	public static final Block tilledField = (new BlockSoil(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).func_20012_a("farmland");
	public static final Block stoneOvenIdle = (new BlockFurnace(61, false)).setHardness(3.5F).setStepSound(soundStoneFootstep).func_20012_a("furnace");
	public static final Block stoneOvenActive = (new BlockFurnace(62, true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(14.0F / 16.0F).func_20012_a("furnace");
	public static final Block signPost = (new BlockSign(63, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).func_20012_a("sign");
	public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).func_20012_a("doorWood");
	public static final Block ladder = (new BlockLadder(65, 83)).setHardness(0.4F).setStepSound(soundWoodFootstep).func_20012_a("ladder");
	public static final Block minecartTrack = (new BlockMinecartTrack(66, 128)).setHardness(0.7F).setStepSound(soundMetalFootstep).func_20012_a("rail");
	public static final Block stairCompactCobblestone = (new BlockStairs(67, cobblestone)).func_20012_a("stairsStone");
	public static final Block signWall = (new BlockSign(68, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).func_20012_a("sign");
	public static final Block lever = (new BlockLever(69, 96)).setHardness(0.5F).setStepSound(soundWoodFootstep).func_20012_a("lever");
	public static final Block pressurePlateStone = (new BlockPressurePlate(70, stone.blockIndexInTexture, EnumMobType.mobs)).setHardness(0.5F).setStepSound(soundStoneFootstep).func_20012_a("pressurePlate");
	public static final Block doorSteel = (new BlockDoor(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).func_20012_a("doorIron");
	public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, planks.blockIndexInTexture, EnumMobType.everything)).setHardness(0.5F).setStepSound(soundWoodFootstep).func_20012_a("pressurePlate");
	public static final Block oreRedstone = (new BlockRedstoneOre(73, 51, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreRedstone");
	public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, 51, true)).setLightValue(10.0F / 16.0F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).func_20012_a("oreRedstone");
	public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, 115, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).func_20012_a("notGate");
	public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, 99, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).func_20012_a("notGate");
	public static final Block button = (new BlockButton(77, stone.blockIndexInTexture)).setHardness(0.5F).setStepSound(soundStoneFootstep).func_20012_a("button");
	public static final Block snow = (new BlockSnow(78, 66)).setHardness(0.1F).setStepSound(soundClothFootstep).func_20012_a("snow");
	public static final Block blockIce = (new BlockIce(79, 67)).setHardness(0.5F).setLightOpacity(3).setStepSound(soundGlassFootstep).func_20012_a("ice");
	public static final Block blockSnow = (new BlockSnowBlock(80, 66)).setHardness(0.2F).setStepSound(soundClothFootstep).func_20012_a("snow");
	public static final Block cactus = (new BlockCactus(81, 70)).setHardness(0.4F).setStepSound(soundClothFootstep).func_20012_a("cactus");
	public static final Block blockClay = (new BlockClay(82, 72)).setHardness(0.6F).setStepSound(soundGravelFootstep).func_20012_a("clay");
	public static final Block reed = (new BlockReed(83, 73)).setHardness(0.0F).setStepSound(soundGrassFootstep).func_20012_a("reeds");
	public static final Block jukebox = (new BlockJukeBox(84, 74)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).func_20012_a("jukebox");
	public static final Block fence = (new BlockFence(85, 4)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).func_20012_a("fence");
	public static final Block pumpkin = (new BlockPumpkin(86, 102, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).func_20012_a("pumpkin");
	public static final Block bloodStone = (new BlockBloodStone(87, 103)).setHardness(0.4F).setStepSound(soundStoneFootstep).func_20012_a("hellrock");
	public static final Block slowSand = (new BlockSlowSand(88, 104)).setHardness(0.5F).setStepSound(soundSandFootstep).func_20012_a("hellsand");
	public static final Block lightStone = (new BlockLightStone(89, 105, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).func_20012_a("lightgem");
	public static final BlockPortal portal = (BlockPortal)(new BlockPortal(90, 14)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(12.0F / 16.0F).func_20012_a("portal");
	public static final Block pumpkinLantern = (new BlockPumpkin(91, 102, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(1.0F).func_20012_a("pumpkin");
	public int blockIndexInTexture;
	public final int blockID;
	protected float blockHardness;
	protected float blockResistance;
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	public StepSound stepSound;
	public float field_357_bm;
	public final Material blockMaterial;
	public float slipperiness;
	private String field_469_a;

	protected Block(int var1, Material var2) {
		this.stepSound = soundPowderFootstep;
		this.field_357_bm = 1.0F;
		this.slipperiness = 0.6F;
		if(blocksList[var1] != null) {
			throw new IllegalArgumentException("Slot " + var1 + " is already occupied by " + blocksList[var1] + " when adding " + this);
		} else {
			this.blockMaterial = var2;
			blocksList[var1] = this;
			this.blockID = var1;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			opaqueCubeLookup[var1] = this.isOpaqueCube();
			lightOpacity[var1] = this.isOpaqueCube() ? 255 : 0;
			unusedBooleanArray[var1] = this.unusedMethod();
			isBlockContainer[var1] = false;
		}
	}

	protected Block(int var1, int var2, Material var3) {
		this(var1, var3);
		this.blockIndexInTexture = var2;
	}

	protected Block setStepSound(StepSound var1) {
		this.stepSound = var1;
		return this;
	}

	protected Block setLightOpacity(int var1) {
		lightOpacity[this.blockID] = var1;
		return this;
	}

	protected Block setLightValue(float var1) {
		lightValue[this.blockID] = (int)(15.0F * var1);
		return this;
	}

	protected Block setResistance(float var1) {
		this.blockResistance = var1 * 3.0F;
		return this;
	}

	private boolean unusedMethod() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public int getRenderType() {
		return 0;
	}

	protected Block setHardness(float var1) {
		this.blockHardness = var1;
		if(this.blockResistance < var1 * 5.0F) {
			this.blockResistance = var1 * 5.0F;
		}

		return this;
	}

	protected void setTickOnLoad(boolean var1) {
		tickOnLoad[this.blockID] = var1;
	}

	public void setBlockBounds(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.minX = (double)var1;
		this.minY = (double)var2;
		this.minZ = (double)var3;
		this.maxX = (double)var4;
		this.maxY = (double)var5;
		this.maxZ = (double)var6;
	}

	public float getBlockBrightness(IBlockAccess var1, int var2, int var3, int var4) {
		return var1.getLightBrightness(var2, var3, var4);
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 0 && this.minY > 0.0D ? true : (var5 == 1 && this.maxY < 1.0D ? true : (var5 == 2 && this.minZ > 0.0D ? true : (var5 == 3 && this.maxZ < 1.0D ? true : (var5 == 4 && this.minX > 0.0D ? true : (var5 == 5 && this.maxX < 1.0D ? true : !var1.isBlockOpaqueCube(var2, var3, var4))))));
	}

	public int getBlockTexture(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return this.getBlockTextureFromSideAndMetadata(var5, var1.getBlockMetadata(var2, var3, var4));
	}

	public int getBlockTextureFromSideAndMetadata(int var1, int var2) {
		return this.getBlockTextureFromSide(var1);
	}

	public int getBlockTextureFromSide(int var1) {
		return this.blockIndexInTexture;
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return AxisAlignedBB.getBoundingBoxFromPool((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ);
	}

	public void getCollidingBoundingBoxes(World var1, int var2, int var3, int var4, AxisAlignedBB var5, ArrayList<AxisAlignedBB> var6) {
		AxisAlignedBB var7 = this.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
		if(var7 != null && var5.intersectsWith(var7)) {
			var6.add(var7);
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return AxisAlignedBB.getBoundingBoxFromPool((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ);
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean canCollideCheck(int var1, boolean var2) {
		return this.isCollidable();
	}

	public boolean isCollidable() {
		return true;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
	}

	public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
	}

	public int tickRate() {
		return 10;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
	}

	public void onBlockRemoval(World var1, int var2, int var3, int var4) {
	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public int idDropped(int var1, Random var2) {
		return this.blockID;
	}

	public float blockStrength(EntityPlayer var1) {
		return this.blockHardness < 0.0F ? 0.0F : (!var1.canHarvestBlock(this) ? 1.0F / this.blockHardness / 100.0F : var1.getCurrentPlayerStrVsBlock(this) / this.blockHardness / 30.0F);
	}

	public void dropBlockAsItem(World var1, int var2, int var3, int var4, int var5) {
		this.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, 1.0F);
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6) {
		if(!var1.multiplayerWorld) {
			int var7 = this.quantityDropped(var1.rand);

			for(int var8 = 0; var8 < var7; ++var8) {
				if(var1.rand.nextFloat() <= var6) {
					int var9 = this.idDropped(var5, var1.rand);
					if(var9 > 0) {
						float var10 = 0.7F;
						double var11 = (double)(var1.rand.nextFloat() * var10) + (double)(1.0F - var10) * 0.5D;
						double var13 = (double)(var1.rand.nextFloat() * var10) + (double)(1.0F - var10) * 0.5D;
						double var15 = (double)(var1.rand.nextFloat() * var10) + (double)(1.0F - var10) * 0.5D;
						EntityItem var17 = new EntityItem(var1, (double)var2 + var11, (double)var3 + var13, (double)var4 + var15, new ItemStack(var9));
						var17.delayBeforeCanPickup = 10;
						var1.entityJoinedWorld(var17);
					}
				}
			}

		}
	}

	public float getExplosionResistance(Entity var1) {
		return this.blockResistance / 5.0F;
	}

	public MovingObjectPosition collisionRayTrace(World var1, int var2, int var3, int var4, Vec3D var5, Vec3D var6) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		var5 = var5.addVector((double)(-var2), (double)(-var3), (double)(-var4));
		var6 = var6.addVector((double)(-var2), (double)(-var3), (double)(-var4));
		Vec3D var7 = var5.getIntermediateWithXValue(var6, this.minX);
		Vec3D var8 = var5.getIntermediateWithXValue(var6, this.maxX);
		Vec3D var9 = var5.getIntermediateWithYValue(var6, this.minY);
		Vec3D var10 = var5.getIntermediateWithYValue(var6, this.maxY);
		Vec3D var11 = var5.getIntermediateWithZValue(var6, this.minZ);
		Vec3D var12 = var5.getIntermediateWithZValue(var6, this.maxZ);
		if(!this.isVecInsideYZBounds(var7)) {
			var7 = null;
		}

		if(!this.isVecInsideYZBounds(var8)) {
			var8 = null;
		}

		if(!this.isVecInsideXZBounds(var9)) {
			var9 = null;
		}

		if(!this.isVecInsideXZBounds(var10)) {
			var10 = null;
		}

		if(!this.isVecInsideXYBounds(var11)) {
			var11 = null;
		}

		if(!this.isVecInsideXYBounds(var12)) {
			var12 = null;
		}

		Vec3D var13 = null;
		if(var7 != null && (var13 == null || var5.distanceTo(var7) < var5.distanceTo(var13))) {
			var13 = var7;
		}

		if(var8 != null && (var13 == null || var5.distanceTo(var8) < var5.distanceTo(var13))) {
			var13 = var8;
		}

		if(var9 != null && (var13 == null || var5.distanceTo(var9) < var5.distanceTo(var13))) {
			var13 = var9;
		}

		if(var10 != null && (var13 == null || var5.distanceTo(var10) < var5.distanceTo(var13))) {
			var13 = var10;
		}

		if(var11 != null && (var13 == null || var5.distanceTo(var11) < var5.distanceTo(var13))) {
			var13 = var11;
		}

		if(var12 != null && (var13 == null || var5.distanceTo(var12) < var5.distanceTo(var13))) {
			var13 = var12;
		}

		if(var13 == null) {
			return null;
		} else {
			byte var14 = -1;
			if(var13 == var7) {
				var14 = 4;
			}

			if(var13 == var8) {
				var14 = 5;
			}

			if(var13 == var9) {
				var14 = 0;
			}

			if(var13 == var10) {
				var14 = 1;
			}

			if(var13 == var11) {
				var14 = 2;
			}

			if(var13 == var12) {
				var14 = 3;
			}

			return new MovingObjectPosition(var2, var3, var4, var14, var13.addVector((double)var2, (double)var3, (double)var4));
		}
	}

	private boolean isVecInsideYZBounds(Vec3D var1) {
		return var1 == null ? false : var1.yCoord >= this.minY && var1.yCoord <= this.maxY && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXZBounds(Vec3D var1) {
		return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXYBounds(Vec3D var1) {
		return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.yCoord >= this.minY && var1.yCoord <= this.maxY;
	}

	public void onBlockDestroyedByExplosion(World var1, int var2, int var3, int var4) {
	}

	public int getRenderBlockPass() {
		return 0;
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		return var5 == 0 || blocksList[var5].blockMaterial.getIsLiquid();
	}

	public boolean blockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		return false;
	}

	public void onEntityWalking(World var1, int var2, int var3, int var4, Entity var5) {
	}

	public void onBlockPlaced(World var1, int var2, int var3, int var4, int var5) {
	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
	}

	public void velocityToAddToEntity(World var1, int var2, int var3, int var4, Entity var5, Vec3D var6) {
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		return 16777215;
	}

	public boolean isPoweringTo(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return false;
	}

	public boolean canProvidePower() {
		return false;
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
	}

	public boolean isIndirectlyPoweringTo(World var1, int var2, int var3, int var4, int var5) {
		return false;
	}

	public void func_237_e() {
	}

	public void harvestBlock(World var1, int var2, int var3, int var4, int var5) {
		this.dropBlockAsItem(var1, var2, var3, var4, var5);
	}

	public boolean canBlockStay(World var1, int var2, int var3, int var4) {
		return true;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5) {
	}

	public Block func_20012_a(String var1) {
		this.field_469_a = "tile." + var1;
		return this;
	}

	public String func_20013_i() {
		return this.field_469_a;
	}

	static {
		for(int var0 = 0; var0 < 256; ++var0) {
			if(blocksList[var0] != null) {
				Item.itemsList[var0] = new ItemBlock(var0 - 256);
			}
		}

	}
}
