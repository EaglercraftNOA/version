package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Maps;
import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

import java.util.Set;

import org.json.JSONObject;

import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.peyton.eagler.json.JSONUtils;

public class EntityHasScore implements LootCondition {
	private final Map<String, RandomValueRange> scores;
	private final LootContext.EntityTarget target;

	public EntityHasScore(Map<String, RandomValueRange> scoreIn, LootContext.EntityTarget targetIn) {
		this.scores = scoreIn;
		this.target = targetIn;
	}

	public boolean testCondition(EaglercraftRandom rand, LootContext context) {
		Entity entity = context.getEntity(this.target);

		if (entity == null) {
			return false;
		} else {
			Scoreboard scoreboard = entity.world.getScoreboard();

			for (Entry<String, RandomValueRange> entry : this.scores.entrySet()) {
				if (!this.entityScoreMatch(entity, scoreboard, entry.getKey(), entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean entityScoreMatch(Entity entityIn, Scoreboard scoreboardIn, String objectiveStr,
			RandomValueRange rand) {
		ScoreObjective scoreobjective = scoreboardIn.getObjective(objectiveStr);

		if (scoreobjective == null) {
			return false;
		} else {
			String s = entityIn instanceof EntityPlayerMP ? entityIn.getName() : entityIn.getCachedUniqueIdString();
			return !scoreboardIn.entityHasObjective(s, scoreobjective) ? false
					: rand.isInRange(scoreboardIn.getOrCreateScore(s, scoreobjective).getScorePoints());
		}
	}

	public static class Serializer extends LootCondition.Serializer<EntityHasScore> {
		protected Serializer() {
			super(new ResourceLocation("entity_scores"), EntityHasScore.class);
		}

		public void serialize(JSONObject json, EntityHasScore value) {
			JSONObject jsonobject = new JSONObject();

			for (Entry<String, RandomValueRange> entry : value.scores.entrySet()) {
				jsonobject.put(entry.getKey(), (Object) JSONTypeProvider.serialize(entry.getValue()));
			}

			json.put("scores", jsonobject);
			json.put("entity", (Object) JSONTypeProvider.serialize(value.target));
		}

		public EntityHasScore deserialize(JSONObject json) {
			Set<Entry<String, Object>> set = json.getJSONObject("scores").entrySet();
			Map<String, RandomValueRange> map = Maps.<String, RandomValueRange>newLinkedHashMap();

			for (Entry<String, Object> entry : set) {
				map.put(entry.getKey(), JSONTypeProvider.deserialize(entry.getValue(), RandomValueRange.class));
			}

			return new EntityHasScore(map, (LootContext.EntityTarget) JSONTypeProvider.deserialize(json.get("entity"),
					LootContext.EntityTarget.class));
		}
	}
}
