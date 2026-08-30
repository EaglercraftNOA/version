package net.minecraft.src;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.Random;

public class Item {
	protected static Random itemRand = new Random();
	public static Item[] itemsList = new Item[32000];
	public static Item shovelSteel = (new ItemSpade(0, 2)).func_20010_a(2, 5).func_20011_a("shovelIron");
	public static Item pickaxeSteel = (new ItemPickaxe(1, 2)).func_20010_a(2, 6).func_20011_a("pickaxeIron");
	public static Item axeSteel = (new ItemAxe(2, 2)).func_20010_a(2, 7).func_20011_a("hatchetIron");
	public static Item flintAndSteel = (new ItemFlintAndSteel(3)).func_20010_a(5, 0).func_20011_a("flintAndSteel");
	public static Item appleRed = (new ItemFood(4, 4)).func_20010_a(10, 0).func_20011_a("apple");
	public static Item bow = (new ItemBow(5)).func_20010_a(5, 1).func_20011_a("bow");
	public static Item arrow = (new Item(6)).func_20010_a(5, 2).func_20011_a("arrow");
	public static Item coal = (new Item(7)).func_20010_a(7, 0).func_20011_a("coal");
	public static Item diamond = (new Item(8)).func_20010_a(7, 3).func_20011_a("emerald");
	public static Item ingotIron = (new Item(9)).func_20010_a(7, 1).func_20011_a("ingotIron");
	public static Item ingotGold = (new Item(10)).func_20010_a(7, 2).func_20011_a("ingotGold");
	public static Item swordSteel = (new ItemSword(11, 2)).func_20010_a(2, 4).func_20011_a("swordIron");
	public static Item swordWood = (new ItemSword(12, 0)).func_20010_a(0, 4).func_20011_a("swordWood");
	public static Item shovelWood = (new ItemSpade(13, 0)).func_20010_a(0, 5).func_20011_a("shovelWood");
	public static Item pickaxeWood = (new ItemPickaxe(14, 0)).func_20010_a(0, 6).func_20011_a("pickaxeWood");
	public static Item axeWood = (new ItemAxe(15, 0)).func_20010_a(0, 7).func_20011_a("hatchetWood");
	public static Item swordStone = (new ItemSword(16, 1)).func_20010_a(1, 4).func_20011_a("swordStone");
	public static Item shovelStone = (new ItemSpade(17, 1)).func_20010_a(1, 5).func_20011_a("shovelStone");
	public static Item pickaxeStone = (new ItemPickaxe(18, 1)).func_20010_a(1, 6).func_20011_a("pickaxeStone");
	public static Item axeStone = (new ItemAxe(19, 1)).func_20010_a(1, 7).func_20011_a("hatchetStone");
	public static Item swordDiamond = (new ItemSword(20, 3)).func_20010_a(3, 4).func_20011_a("swordDiamond");
	public static Item shovelDiamond = (new ItemSpade(21, 3)).func_20010_a(3, 5).func_20011_a("shovelDiamond");
	public static Item pickaxeDiamond = (new ItemPickaxe(22, 3)).func_20010_a(3, 6).func_20011_a("pickaxeDiamond");
	public static Item axeDiamond = (new ItemAxe(23, 3)).func_20010_a(3, 7).func_20011_a("hatchetDiamond");
	public static Item stick = (new Item(24)).func_20010_a(5, 3).setFull3D().func_20011_a("stick");
	public static Item bowlEmpty = (new Item(25)).func_20010_a(7, 4).func_20011_a("bowl");
	public static Item bowlSoup = (new ItemSoup(26, 10)).func_20010_a(8, 4).func_20011_a("mushroomStew");
	public static Item swordGold = (new ItemSword(27, 0)).func_20010_a(4, 4).func_20011_a("swordGold");
	public static Item shovelGold = (new ItemSpade(28, 0)).func_20010_a(4, 5).func_20011_a("shovelGold");
	public static Item pickaxeGold = (new ItemPickaxe(29, 0)).func_20010_a(4, 6).func_20011_a("pickaxeGold");
	public static Item axeGold = (new ItemAxe(30, 0)).func_20010_a(4, 7).func_20011_a("hatchetGold");
	public static Item silk = (new Item(31)).func_20010_a(8, 0).func_20011_a("string");
	public static Item feather = (new Item(32)).func_20010_a(8, 1).func_20011_a("feather");
	public static Item gunpowder = (new Item(33)).func_20010_a(8, 2).func_20011_a("sulphur");
	public static Item hoeWood = (new ItemHoe(34, 0)).func_20010_a(0, 8).func_20011_a("hoeWood");
	public static Item hoeStone = (new ItemHoe(35, 1)).func_20010_a(1, 8).func_20011_a("hoeStone");
	public static Item hoeSteel = (new ItemHoe(36, 2)).func_20010_a(2, 8).func_20011_a("hoeIron");
	public static Item hoeDiamond = (new ItemHoe(37, 3)).func_20010_a(3, 8).func_20011_a("hoeDiamond");
	public static Item hoeGold = (new ItemHoe(38, 1)).func_20010_a(4, 8).func_20011_a("hoeGold");
	public static Item seeds = (new ItemSeeds(39, Block.crops.blockID)).func_20010_a(9, 0).func_20011_a("seeds");
	public static Item wheat = (new Item(40)).func_20010_a(9, 1).func_20011_a("wheat");
	public static Item bread = (new ItemFood(41, 5)).func_20010_a(9, 2).func_20011_a("bread");
	public static Item helmetLeather = (new ItemArmor(42, 0, 0, 0)).func_20010_a(0, 0).func_20011_a("helmetCloth");
	public static Item plateLeather = (new ItemArmor(43, 0, 0, 1)).func_20010_a(0, 1).func_20011_a("chestplateCloth");
	public static Item legsLeather = (new ItemArmor(44, 0, 0, 2)).func_20010_a(0, 2).func_20011_a("leggingsCloth");
	public static Item bootsLeather = (new ItemArmor(45, 0, 0, 3)).func_20010_a(0, 3).func_20011_a("bootsCloth");
	public static Item helmetChain = (new ItemArmor(46, 1, 1, 0)).func_20010_a(1, 0).func_20011_a("helmetChain");
	public static Item plateChain = (new ItemArmor(47, 1, 1, 1)).func_20010_a(1, 1).func_20011_a("chestplateChain");
	public static Item legsChain = (new ItemArmor(48, 1, 1, 2)).func_20010_a(1, 2).func_20011_a("leggingsChain");
	public static Item bootsChain = (new ItemArmor(49, 1, 1, 3)).func_20010_a(1, 3).func_20011_a("bootsChain");
	public static Item helmetSteel = (new ItemArmor(50, 2, 2, 0)).func_20010_a(2, 0).func_20011_a("helmetIron");
	public static Item plateSteel = (new ItemArmor(51, 2, 2, 1)).func_20010_a(2, 1).func_20011_a("chestplateIron");
	public static Item legsSteel = (new ItemArmor(52, 2, 2, 2)).func_20010_a(2, 2).func_20011_a("leggingsIron");
	public static Item bootsSteel = (new ItemArmor(53, 2, 2, 3)).func_20010_a(2, 3).func_20011_a("bootsIron");
	public static Item helmetDiamond = (new ItemArmor(54, 3, 3, 0)).func_20010_a(3, 0).func_20011_a("helmetDiamond");
	public static Item plateDiamond = (new ItemArmor(55, 3, 3, 1)).func_20010_a(3, 1).func_20011_a("chestplateDiamond");
	public static Item legsDiamond = (new ItemArmor(56, 3, 3, 2)).func_20010_a(3, 2).func_20011_a("leggingsDiamond");
	public static Item bootsDiamond = (new ItemArmor(57, 3, 3, 3)).func_20010_a(3, 3).func_20011_a("bootsDiamond");
	public static Item helmetGold = (new ItemArmor(58, 1, 4, 0)).func_20010_a(4, 0).func_20011_a("helmetGold");
	public static Item plateGold = (new ItemArmor(59, 1, 4, 1)).func_20010_a(4, 1).func_20011_a("chestplateGold");
	public static Item legsGold = (new ItemArmor(60, 1, 4, 2)).func_20010_a(4, 2).func_20011_a("leggingsGold");
	public static Item bootsGold = (new ItemArmor(61, 1, 4, 3)).func_20010_a(4, 3).func_20011_a("bootsGold");
	public static Item flint = (new Item(62)).func_20010_a(6, 0).func_20011_a("flint");
	public static Item porkRaw = (new ItemFood(63, 3)).func_20010_a(7, 5).func_20011_a("porkchopRaw");
	public static Item porkCooked = (new ItemFood(64, 8)).func_20010_a(8, 5).func_20011_a("porkchopCooked");
	public static Item painting = (new ItemPainting(65)).func_20010_a(10, 1).func_20011_a("painting");
	public static Item appleGold = (new ItemFood(66, 42)).func_20010_a(11, 0).func_20011_a("appleGold");
	public static Item sign = (new ItemSign(67)).func_20010_a(10, 2).func_20011_a("sign");
	public static Item doorWood = (new ItemDoor(68, Material.wood)).func_20010_a(11, 2).func_20011_a("doorWood");
	public static Item bucketEmpty = (new ItemBucket(69, 0)).func_20010_a(10, 4).func_20011_a("bucket");
	public static Item bucketWater = (new ItemBucket(70, Block.waterStill.blockID)).func_20010_a(11, 4).func_20011_a("bucketWater");
	public static Item bucketLava = (new ItemBucket(71, Block.lavaStill.blockID)).func_20010_a(12, 4).func_20011_a("bucketLava");
	public static Item minecartEmpty = (new ItemMinecart(72, 0)).func_20010_a(7, 8).func_20011_a("minecart");
	public static Item saddle = (new ItemSaddle(73)).func_20010_a(8, 6).func_20011_a("saddle");
	public static Item doorSteel = (new ItemDoor(74, Material.iron)).func_20010_a(12, 2).func_20011_a("doorIron");
	public static Item redstone = (new ItemRedstone(75)).func_20010_a(8, 3).func_20011_a("redstone");
	public static Item snowball = (new ItemSnowball(76)).func_20010_a(14, 0).func_20011_a("snowball");
	public static Item boat = (new ItemBoat(77)).func_20010_a(8, 8).func_20011_a("boat");
	public static Item leather = (new Item(78)).func_20010_a(7, 6).func_20011_a("leather");
	public static Item bucketMilk = (new ItemBucket(79, -1)).func_20010_a(13, 4).func_20011_a("milk");
	public static Item brick = (new Item(80)).func_20010_a(6, 1).func_20011_a("brick");
	public static Item clay = (new Item(81)).func_20010_a(9, 3).func_20011_a("clay");
	public static Item reed = (new ItemReed(82, Block.reed)).func_20010_a(11, 1).func_20011_a("reeds");
	public static Item paper = (new Item(83)).func_20010_a(10, 3).func_20011_a("paper");
	public static Item book = (new Item(84)).func_20010_a(11, 3).func_20011_a("book");
	public static Item slimeBall = (new Item(85)).func_20010_a(14, 1).func_20011_a("slimeball");
	public static Item minecartCrate = (new ItemMinecart(86, 1)).func_20010_a(7, 9).func_20011_a("minecartChest");
	public static Item minecartPowered = (new ItemMinecart(87, 2)).func_20010_a(7, 10).func_20011_a("minecartFurnace");
	public static Item egg = (new ItemEgg(88)).func_20010_a(12, 0).func_20011_a("egg");
	public static Item compass = (new Item(89)).func_20010_a(6, 3).func_20011_a("compass");
	public static Item fishingRod = (new ItemFishingRod(90)).func_20010_a(5, 4).func_20011_a("fishingRod");
	public static Item pocketSundial = (new Item(91)).func_20010_a(6, 4).func_20011_a("clock");
	public static Item lightStoneDust = (new Item(92)).func_20010_a(9, 4).func_20011_a("yellowDust");
	public static Item fishRaw = (new ItemFood(93, 2)).func_20010_a(9, 5).func_20011_a("fishRaw");
	public static Item fishCooked = (new ItemFood(94, 5)).func_20010_a(10, 5).func_20011_a("fishCooked");
	public static Item record13 = (new ItemRecord(2000, "13")).func_20010_a(0, 15).func_20011_a("record");
	public static Item recordCat = (new ItemRecord(2001, "cat")).func_20010_a(1, 15).func_20011_a("record");
	public final int shiftedIndex;
	protected int maxStackSize = 64;
	protected int maxDamage = 32;
	protected int iconIndex;
	protected boolean bFull3D = false;
	private String healAmount;
	
	private static Logger LOGGER = LogManager.getLogger();

	protected Item(int var1) {
		this.shiftedIndex = 256 + var1;
		if(itemsList[256 + var1] != null) {
			LOGGER.error("CONFLICT @ {}", var1);
		}

		itemsList[256 + var1] = this;
	}

	public Item setIconIndex(int var1) {
		this.iconIndex = var1;
		return this;
	}

	public Item func_20010_a(int var1, int var2) {
		this.iconIndex = var1 + var2 * 16;
		return this;
	}

	public int getIconIndex(ItemStack var1) {
		return this.iconIndex;
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
		return false;
	}

	public float getStrVsBlock(ItemStack var1, Block var2) {
		return 1.0F;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		return var1;
	}

	public int getItemStackLimit() {
		return this.maxStackSize;
	}

	public int getMaxDamage() {
		return this.maxDamage;
	}

	public void hitEntity(ItemStack var1, EntityLiving var2) {
	}

	public void hitBlock(ItemStack var1, int var2, int var3, int var4, int var5) {
	}

	public int getDamageVsEntity(Entity var1) {
		return 1;
	}

	public boolean canHarvestBlock(Block var1) {
		return false;
	}

	public void func_4019_b(ItemStack var1, EntityLiving var2) {
	}

	public Item setFull3D() {
		this.bFull3D = true;
		return this;
	}

	public boolean isFull3D() {
		return this.bFull3D;
	}

	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}

	public Item func_20011_a(String var1) {
		this.healAmount = "item." + var1;
		return this;
	}

	public String func_20009_a() {
		return this.healAmount;
	}
}
