package net.minecraft.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import static net.lax1dude.eaglercraft.sp.relay.RelayManager.logger;

public class EntityList {
	/** Provides a mapping between entity classes and a string */
	private static Map stringToClassMapping = new HashMap();

	/** Provides a mapping between a string and an entity classes */
	private static Map classToStringMapping = new HashMap();

	/** provides a mapping between an entityID and an Entity Class */
	private static Map IDtoClassMapping = new HashMap();

	/** provides a mapping between an Entity Class and an entity ID */
	private static Map classToIDMapping = new HashMap();

	/** Maps entity names to their numeric identifiers */
	private static Map stringToIDMapping = new HashMap();

	/** This is a HashMap of the Creative Entity Eggs/Spawners. */
	public static HashMap entityEggs = new LinkedHashMap();

	/**
	 * adds a mapping between Entity classes and both a string representation and an
	 * ID
	 */
	private static void addMapping(Class par0Class, String par1Str, int par2) {
		stringToClassMapping.put(par1Str, par0Class);
		classToStringMapping.put(par0Class, par1Str);
		IDtoClassMapping.put(Integer.valueOf(par2), par0Class);
		classToIDMapping.put(par0Class, Integer.valueOf(par2));
		stringToIDMapping.put(par1Str, Integer.valueOf(par2));
	}

	/**
	 * Adds a entity mapping with egg info.
	 */
	private static void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4) {
		addMapping(par0Class, par1Str, par2);
		entityEggs.put(Integer.valueOf(par2), new EntityEggInfo(par2, par3, par4));
	}

	/**
	 * Create a new instance of an entity in the world by using the entity name.
	 */
	public static Entity createEntityByName(String par0Str, World par1World) {
		if (par0Str == null)
			return null;
		Integer id = (Integer) stringToIDMapping.get(par0Str);
		return id != null ? createEntityByID(id.intValue(), par1World) : null;
	}


	public static Entity createEntityByClass(Class<? extends Entity> entityClass, World worldIn) {

		Integer entityId = (Integer) classToIDMapping.get(entityClass);
		if (entityId != null) {
			return createEntityByID(entityId.intValue(), worldIn);
		}
		return null;
	}

	/**
	 * create a new instance of an entity from NBT store
	 */
	public static Entity createEntityFromNBT(NBTTagCompound par0NBTTagCompound, World par1World) {
		String name = par0NBTTagCompound.getString("id");
		Entity var2 = createEntityByName(name, par1World);
		if (var2 != null) {
			var2.readFromNBT(par0NBTTagCompound);
		} else {
			}
		return var2;
	}

	/**
	 * Create a new instance of an entity in the world by using an entity ID.
	 */
	public static Entity createEntityByID(int par0, World par1World) {
		switch (par0) {
		case 1:
			return new EntityItem(par1World);
		case 2:
			return new EntityXPOrb(par1World);
		case 9:
			return new EntityPainting(par1World);
		case 10:
			return new EntityArrow(par1World);
		case 11:
			return new EntitySnowball(par1World);
		case 12:
			return new EntityLargeFireball(par1World);
		case 13:
			return new EntitySmallFireball(par1World);
		case 14:
			return new EntityEnderPearl(par1World);
		case 15:
			return new EntityEnderEye(par1World);
		case 16:
			return new EntityPotion(par1World);
		case 17:
			return new EntityExpBottle(par1World);
		case 18:
			return new EntityItemFrame(par1World);
		case 19:
			return new EntityWitherSkull(par1World);
		case 20:
			return new EntityTNTPrimed(par1World);
		case 21:
			return new EntityFallingBlock(par1World);
		case 22:
			return new EntityFireworkRocket(par1World);
		case 41:
			return new EntityBoat(par1World);
		case 48:
			return null;
		case 49:
			return null;
		case 50:
			return new EntityCreeper(par1World);
		case 51:
			return new EntitySkeleton(par1World);
		case 52:
			return new EntitySpider(par1World);
		case 53:
			return new EntityGiantZombie(par1World);
		case 54:
			return new EntityZombie(par1World);
		case 55:
			return new EntitySlime(par1World);
		case 56:
			return new EntityGhast(par1World);
		case 57:
			return new EntityPigZombie(par1World);
		case 58:
			return new EntityEnderman(par1World);
		case 59:
			return new EntityCaveSpider(par1World);
		case 60:
			return new EntitySilverfish(par1World);
		case 61:
			return new EntityBlaze(par1World);
		case 62:
			return new EntityMagmaCube(par1World);
		case 63:
			return new EntityDragon(par1World);
		case 64:
			return new EntityWither(par1World);
		case 65:
			return new EntityBat(par1World);
		case 66:
			return new EntityWitch(par1World);
		case 90:
			return new EntityPig(par1World);
		case 91:
			return new EntitySheep(par1World);
		case 92:
			return new EntityCow(par1World);
		case 93:
			return new EntityChicken(par1World);
		case 94:
			return new EntitySquid(par1World);
		case 95:
			return new EntityWolf(par1World);
		case 96:
			return new EntityMooshroom(par1World);
		case 97:
			return new EntitySnowman(par1World);
		case 98:
			return new EntityOcelot(par1World);
		case 99:
			return new EntityIronGolem(par1World);
		case 120:
			return new EntityVillager(par1World);
		case 200:
			return new EntityEnderCrystal(par1World);
		default:
			return null;
		}
	}

	/**
	 * gets the entityID of a specific entity
	 */
	public static int getEntityID(Entity par0Entity) {
		Class var1 = par0Entity.getClass();
		return classToIDMapping.containsKey(var1) ? ((Integer) classToIDMapping.get(var1)).intValue() : 0;
	}

	/**
	 * Return the class assigned to this entity ID.
	 */
	public static Class getClassFromID(int par0) {
		return (Class) IDtoClassMapping.get(Integer.valueOf(par0));
	}

	/**
	 * Gets the string representation of a specific entity.
	 */
	public static String getEntityString(Entity par0Entity) {
		return (String) classToStringMapping.get(par0Entity.getClass());
	}

	/**
	 * Finds the class using IDtoClassMapping and classToStringMapping
	 */
	public static String getStringFromID(int par0) {
		Class var1 = (Class) IDtoClassMapping.get(Integer.valueOf(par0));
		return var1 != null ? (String) classToStringMapping.get(var1) : null;
	}

	static {
		addMapping(EntityItem.class, "Item", 1);
		addMapping(EntityXPOrb.class, "XPOrb", 2);
		addMapping(EntityPainting.class, "Painting", 9);
		addMapping(EntityArrow.class, "Arrow", 10);
		addMapping(EntitySnowball.class, "Snowball", 11);
		addMapping(EntityLargeFireball.class, "Fireball", 12);
		addMapping(EntitySmallFireball.class, "SmallFireball", 13);
		addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
		addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
		addMapping(EntityPotion.class, "ThrownPotion", 16);
		addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
		addMapping(EntityItemFrame.class, "ItemFrame", 18);
		addMapping(EntityWitherSkull.class, "WitherSkull", 19);
		addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
		addMapping(EntityFallingBlock.class, "FallingSand", 21);
		addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
		addMapping(EntityMinecart.class, "Minecart", 40);
		addMapping(EntityBoat.class, "Boat", 41);
		addMapping(EntityLiving.class, "Mob", 48);
		addMapping(EntityMob.class, "Monster", 49);
		addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
		addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
		addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
		addMapping(EntityGiantZombie.class, "Giant", 53);
		addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
		addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
		addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
		addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
		addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
		addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
		addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
		addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
		addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
		addMapping(EntityDragon.class, "EnderDragon", 63);
		addMapping(EntityWither.class, "WitherBoss", 64);
		addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
		addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
		addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
		addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
		addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
		addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
		addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
		addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
		addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
		addMapping(EntitySnowman.class, "SnowMan", 97);
		addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
		addMapping(EntityIronGolem.class, "VillagerGolem", 99);
		addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
		addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
	}

	public static class EntityEggInfo {
		public final int spawnedID;
		public final int primaryColor;
		public final int secondaryColor;
		public final StatBase field_151512_d;
		public final StatBase field_151513_e;
		private static final String __OBFID = "CL_00001539";

		public EntityEggInfo(int p_i1583_1_, int p_i1583_2_, int p_i1583_3_) {
			this.spawnedID = p_i1583_1_;
			this.primaryColor = p_i1583_2_;
			this.secondaryColor = p_i1583_3_;
			this.field_151512_d = StatList.func_151182_a(this);
			this.field_151513_e = StatList.func_151176_b(this);
		}
	}

	public static Set func_151515_b() {
		return Collections.unmodifiableSet(stringToIDMapping.keySet());
	}

	public static void func_151514_a() {

	}
}
