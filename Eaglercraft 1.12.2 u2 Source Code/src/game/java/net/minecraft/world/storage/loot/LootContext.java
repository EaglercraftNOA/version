package net.minecraft.world.storage.loot;

import com.google.common.collect.Sets;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;

import org.json.*;
import net.lax1dude.eaglercraft.json.*;

public class LootContext {
	private final float luck;
	private final WorldServer worldObj;
	private final LootTableManager lootTableManager;
	@Nullable
	private final Entity lootedEntity;
	@Nullable
	private final EntityPlayer player;
	@Nullable
	private final DamageSource damageSource;
	private final Set<LootTable> lootTables = Sets.<LootTable>newLinkedHashSet();

	public LootContext(float luckIn, WorldServer worldIn, LootTableManager lootTableManagerIn,
			@Nullable Entity lootedEntityIn, @Nullable EntityPlayer playerIn, @Nullable DamageSource damageSourceIn) {
		this.luck = luckIn;
		this.worldObj = worldIn;
		this.lootTableManager = lootTableManagerIn;
		this.lootedEntity = lootedEntityIn;
		this.player = playerIn;
		this.damageSource = damageSourceIn;
	}

	@Nullable
	public Entity getLootedEntity() {
		return this.lootedEntity;
	}

	@Nullable
	public Entity getKillerPlayer() {
		return this.player;
	}

	@Nullable
	public Entity getKiller() {
		return this.damageSource == null ? null : this.damageSource.getEntity();
	}

	public boolean addLootTable(LootTable lootTableIn) {
		return this.lootTables.add(lootTableIn);
	}

	public void removeLootTable(LootTable lootTableIn) {
		this.lootTables.remove(lootTableIn);
	}

	public LootTableManager getLootTableManager() {
		return this.lootTableManager;
	}

	public float getLuck() {
		return this.luck;
	}

	@Nullable
	public Entity getEntity(LootContext.EntityTarget target) {
		switch (target) {
		case THIS:
			return this.getLootedEntity();

		case KILLER:
			return this.getKiller();

		case KILLER_PLAYER:
			return this.getKillerPlayer();

		default:
			return null;
		}
	}

	public static class Builder {
		private final WorldServer worldObj;
		private float luck;
		private Entity lootedEntity;
		private EntityPlayer player;
		private DamageSource damageSource;

		public Builder(WorldServer worldIn) {
			this.worldObj = worldIn;
		}

		public LootContext.Builder withLuck(float luckIn) {
			this.luck = luckIn;
			return this;
		}

		public LootContext.Builder withLootedEntity(Entity entityIn) {
			this.lootedEntity = entityIn;
			return this;
		}

		public LootContext.Builder withPlayer(EntityPlayer playerIn) {
			this.player = playerIn;
			return this;
		}

		public LootContext.Builder withDamageSource(DamageSource dmgSource) {
			this.damageSource = dmgSource;
			return this;
		}

		public LootContext build() {
			return new LootContext(this.luck, this.worldObj, this.worldObj.getLootTableManager(), this.lootedEntity,
					this.player, this.damageSource);
		}
	}

	public static enum EntityTarget {
		THIS("this"), KILLER("killer"), KILLER_PLAYER("killer_player");

		private final String targetType;

		private EntityTarget(String type) {
			this.targetType = type;
		}

		public static LootContext.EntityTarget fromString(String type) {
			for (LootContext.EntityTarget lootcontext$entitytarget : values()) {
				if (lootcontext$entitytarget.targetType.equals(type)) {
					return lootcontext$entitytarget;
				}
			}

			throw new IllegalArgumentException("Invalid entity target " + type);
		}

		public static class Serializer implements JSONTypeDeserializer<String, LootContext.EntityTarget>,
				JSONTypeCodec<LootContext.EntityTarget, String> {

			public EntityTarget deserialize(String json) throws JSONException {
				return LootContext.EntityTarget.fromString(json);
			}

			public String serialize(EntityTarget object) throws JSONException {
				// JSONObject jsonobject = new JSONObject();
				// jsonobject.put("target_type", object.targetType);
				// return jsonobject;
				return object.targetType;
			}

		}
	}
}
