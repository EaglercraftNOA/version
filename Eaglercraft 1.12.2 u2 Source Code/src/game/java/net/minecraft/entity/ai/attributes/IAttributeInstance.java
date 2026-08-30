package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import javax.annotation.Nullable;

public interface IAttributeInstance {
	/**
	 * Get the Attribute this is an instance of
	 */
	IAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double baseValue);

	Collection<AttributeModifier> getModifiersByOperation(int operation);

	Collection<AttributeModifier> getModifiers();

	boolean hasModifier(AttributeModifier modifier);

	@Nullable

	/**
	 * Returns attribute modifier, if any, by the given UUID
	 */
	AttributeModifier getModifier(EaglercraftUUID uuid);

	void applyModifier(AttributeModifier modifier);

	void removeModifier(AttributeModifier modifier);

	void removeModifier(EaglercraftUUID p_188479_1_);

	void removeAllModifiers();

	double getAttributeValue();
}
